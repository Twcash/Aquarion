package aquarion.world.blocks.distribution;

import aquarion.world.content.LiquidReactions;
import aquarion.world.content.LiquidUtil;
import arc.Core;
import arc.math.Mathf;
import arc.scene.ui.Tooltip;
import arc.scene.ui.layout.Table;
import arc.util.Strings;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.gen.Teamc;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.ui.Bar;
import mindustry.ui.Styles;
import mindustry.world.Tile;
import mindustry.world.blocks.distribution.Router;
import mindustry.world.blocks.liquid.LiquidJunction;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.modules.LiquidModule;

/**
 * A junction with 4 independent side buffers (one per input direction). Each side
 * has its own liquid capacity; liquids stored on different sides never mix or react.
 * Liquids within a single side react with each other, and the whole side drains to the
 * opposite output using the conduit flow formula.
 */
public class ModifiedLiquidJunction extends LiquidJunction implements LiquidUtil.Rated {
    public float speed = 2.1f;
    public int capacity = 6;
    public float displayedSpeed = 13f;
    /** Liquid capacity of each of the 4 independent side buffers. */
    public float sideLiquidCapacity = 30f;
    /** Throughput in units/second; <= 0 falls back to {@code liquidCapacity * FLOW_RATE}. */
    public float liquidSpeed = -1f;

    @Override
    public float flowRate(){
        return liquidSpeed > 0 ? liquidSpeed : liquidCapacity * LiquidUtil.FLOW_RATE;
    }

    public ModifiedLiquidJunction(String name) {
        super(name);
        update = true;
        solid = false;
        underBullets = true;
        hasItems = true;
        unloadable = false;
        noUpdateDisabled = true;
        liquidCapacity = sideLiquidCapacity * 4f;
    }
    @Override
    public void setStats(){
        super.setStats();

        stats.add(Stat.itemsMoved, displayedSpeed, StatUnit.itemsSecond);
        stats.add(Stat.itemCapacity, table -> {
            table.add(Strings.autoFixed(capacity, 2) + " " + StatUnit.items.localized() + " " + StatUnit.perSide.localized());
        });
    }

    @Override
    public void setBars(){
        super.setBars();
        removeBar("liquid");
    }
    @Override
    public boolean outputsItems(){
        return true;
    }
    public class wtfBuild extends LiquidJunctionBuild{
        public Item lastItem;
        public Tile lastInput;
        public float time;
        public LiquidModule[] sides = new LiquidModule[4];

        {
            for(int i = 0; i < 4; i++){
                sides[i] = new LiquidModule();
            }
        }

        @Override
        public void displayBars(Table bars){
            super.displayBars(bars);
            for(int i = 0; i < 4; i++){
                final LiquidModule side = sides[i];
                if(side == null || LiquidUtil.total(side) <= 0.001f) continue;

                Bar bar = new Bar(
                    () -> sideLabel(side),
                    () -> LiquidUtil.mixedColor(side, Tmp.c1),
                    () -> Mathf.clamp(LiquidUtil.total(side) / sideLiquidCapacity)
                );
                bar.addListener(new Tooltip(table -> {
                    table.background(Styles.black6);
                    table.margin(4f);
                    table.label(() -> sideTooltip(side)).style(Styles.outlineLabel);
                }));
                bars.add(bar).row();
            }
        }

        private CharSequence sideLabel(LiquidModule side){
            StringBuilder sb = new StringBuilder();
            side.each((liquid, amount) -> {
                if(amount > 0.001f){
                    if(sb.length() > 0) sb.append(" + ");
                    sb.append(liquid.localizedName);
                }
            });
            return sb.length() == 0 ? Core.bundle.get("bar.liquid") : sb;
        }

        private CharSequence sideTooltip(LiquidModule side){
            StringBuilder sb = new StringBuilder();
            side.each((liquid, amount) -> {
                if(amount > 0.001f){
                    if(sb.length() > 0) sb.append("\n");
                    sb.append(liquid.localizedName).append(": ").append((int)amount).append("/").append((int)sideLiquidCapacity);
                }
            });
            return sb.length() == 0 ? Core.bundle.get("bar.liquid") : sb;
        }

        @Override
        public void updateTile(){
            if(lastItem == null && items.any()){
                lastItem = items.first();
            }

            if(lastItem != null){
                time += 1f / speed * delta();
                Building target = getTileTarget(lastItem, lastInput, false);

                if(target != null && (time >= 1f || !(target.block instanceof Router || target.block.instantTransfer))){
                    getTileTarget(lastItem, lastInput, true);
                    target.handleItem(this, lastItem);
                    items.remove(lastItem, 1);
                    lastItem = null;
                }
            }

            boolean any = false;
            for(int i = 0; i < 4; i++){
                final int dir = i;
                LiquidModule side = sides[i];
                if(side == null || LiquidUtil.total(side) <= 0.001f) continue;
                any = true;

                //liquids on the same side react with each other, but never with other sides
                LiquidReactions.react(side, self());

                //push this side's liquid out the opposite side using the conduit flow formula
                side.each((liquid, amount) -> {
                    if(amount <= 0.001f) return;

                    if(liquid.temperature > 0.5f){
                        damageContinuous(liquid.temperature/100f);
                        if(Mathf.chanceDelta(0.01f)){
                            Fx.steam.at(x, y);
                        }
                    }

                    Building target = nearby(Mathf.mod(dir + 2, 4));
                    if(target != null && target.team == team && target.acceptLiquid(this, liquid)){
                        float flow = Math.min(amount, LiquidUtil.flow(sides[dir], sideLiquidCapacity, this, target, liquid) * delta());
                        if(flow > 0.01f){
                            target.handleLiquid(this, liquid, flow);
                            sides[dir].remove(liquid, flow);
                        }
                    }
                });
            }

            if(any){
                noSleep();
            }else{
                sleep();
            }
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid){
            noSleep();
            int dir = inputSide(source);
            return enabled && sides[dir] != null && LiquidUtil.freeSpace(sides[dir], sideLiquidCapacity) > 0.01f;
        }

        @Override
        public void handleLiquid(Building source, Liquid liquid, float amount){
            int dir = inputSide(source);
            if(sides[dir] == null) sides[dir] = new LiquidModule();
            sides[dir].add(liquid, amount);
        }

        /** @return the physical side of this junction that {@code source} is on. */
        private int inputSide(Building source){
            int travel = source.relativeToEdge(tile);
            return travel == -1 ? 0 : Mathf.mod(travel + 2, 4);
        }

        /** Amount of free space on the side buffer facing {@code source}; what this junction will actually store from {@code source}. */
        public float freeSpaceFor(Building source){
            if(source == null) return sideLiquidCapacity * 4f;
            int dir = inputSide(source);
            return sides[dir] == null ? sideLiquidCapacity : LiquidUtil.freeSpace(sides[dir], sideLiquidCapacity);
        }

        @Override
        public Building getLiquidDestination(Building source, Liquid liquid){
            //the junction is always the buffering endpoint: it stores liquid per side and throttles output
            return this;
        }

        public Building getTileTarget(Item item, Tile from, boolean set){

            int counter = rotation;
            for(int i = 0; i < proximity.size; i++){
                Building other = proximity.get((i + counter) % proximity.size);
                if(set) rotation = ((byte)((rotation + 1) % proximity.size));
                if(other.tile == from && from.block() == Blocks.overflowGate) continue;
                if(other.acceptItem(this, item)){
                    return other;
                }
            }
            return null;
        }

        @Override
        public int acceptStack(Item item, int amount, Teamc source){
            return 0;
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            return team == source.team && lastItem == null && items.total() == 0;
        }

        @Override
        public void handleItem(Building source, Item item){
            items.add(item, 1);
            lastItem = item;
            time = 0f;
            lastInput = source.tile;
        }

        @Override
        public int removeStack(Item item, int amount){
            int result = super.removeStack(item, amount);
            if(result != 0 && item == lastItem){
                lastItem = null;
            }
            return result;
        }

        @Override
        public byte version(){
            return 1;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            for(int i = 0; i < 4; i++){
                sides[i].write(write);
            }
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            if(revision >= 1){
                for(int i = 0; i < 4; i++){
                    sides[i].read(read, false);
                }
            }
        }
    }
}
