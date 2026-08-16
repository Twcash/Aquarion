package aquarion.world.blocks.distribution;

import aquarion.world.content.LiquidReactions;
import aquarion.world.content.LiquidUtil;
import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.ui.Tooltip;
import arc.scene.ui.layout.Table;
import arc.util.Eachable;
import arc.util.Nullable;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.type.Liquid;
import mindustry.ui.Bar;
import mindustry.ui.Styles;
import mindustry.world.Tile;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.blocks.liquid.LiquidBlock;
import mindustry.world.modules.LiquidModule;

import static mindustry.Vars.content;
import static mindustry.Vars.headless;
import static mindustry.Vars.renderer;
import static mindustry.Vars.tilesize;

/**
 * Liquid counterpart to the vanilla item Sorter: set a liquid to sort.
 * The configured liquid passes straight through, everything else is pushed out the sides.
 * Has 4 independent side buffers (one per input direction), each with its own capacity;
 * liquids on different sides never mix or react with each other.
 */
public class SiphonSorter extends LiquidBlock implements LiquidUtil.Rated {
    public TextureRegion cross;
    public boolean invert;
    /** Liquid capacity of each of the 4 independent side buffers. */
    public float sideLiquidCapacity = 30f;
    /** Throughput in units/second; <= 0 falls back to {@code liquidCapacity * FLOW_RATE}. */
    public float liquidSpeed = -1f;

    @Override
    public float flowRate(){
        return liquidSpeed > 0 ? liquidSpeed : liquidCapacity * LiquidUtil.FLOW_RATE;
    }

    public SiphonSorter(String name){
        super(name);
        solid = false;
        destructible = true;
        underBullets = true;
        instantTransfer = true;
        configurable = true;
        saveConfig = true;
        clearOnDoubleTap = true;
        floating = true;
        liquidCapacity = 30f;

        config(Liquid.class, (SiphonSorterBuild tile, Liquid liquid) -> tile.sortLiquid = liquid);
        configClear((SiphonSorterBuild tile) -> tile.sortLiquid = null);
    }

    @Override
    public void load(){
        super.load();
        cross = Core.atlas.find(name + "-cross", Core.atlas.find("cross-full"));
    }

    @Override
    public void setBars(){
        super.setBars();
        removeBar("liquid");
    }

    @Override
    public void drawPlanConfig(BuildPlan plan, Eachable<BuildPlan> list){
        drawPlanConfigCenter(plan, plan.config, "center", true);
    }

    @Override
    public int minimapColor(Tile tile){
        var build = (SiphonSorterBuild)tile.build;
        return build == null || build.sortLiquid == null ? 0 : build.sortLiquid.color.rgba();
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{Core.atlas.find("source-bottom"), region};
    }

    public class SiphonSorterBuild extends LiquidBuild{
        public @Nullable Liquid sortLiquid;
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
        public void configured(Unit player, Object value){
            super.configured(player, value);

            if(!headless){
                renderer.minimap.update(tile);
            }
        }

        @Override
        public void draw(){
            if(sortLiquid == null){
                Draw.rect(cross, x, y);
            }else{
                Draw.color(sortLiquid.color);
                Fill.square(x, y, tilesize/2f - 0.00003f);
                Draw.color();
            }
            Draw.rect(region, x, y, 0);
        }

        @Override
        public void drawSelect(){
            super.drawSelect();
            drawItemSelection(sortLiquid);
        }

        @Override
        public void updateTile(){
            boolean any = false;
            for(int i = 0; i < 4; i++){
                final int dir = i;
                LiquidModule side = sides[dir];
                if(side == null || LiquidUtil.total(side) <= 0.001f) continue;
                any = true;

                //liquids on the same side react with each other, but never with other sides
                LiquidReactions.react(side, self());

                //the block the buffered liquid travelled in from, used for chain checks
                Building source = nearby(dir);
                if(source == null) continue;

                int travel = Mathf.mod(dir + 2, 4);
                side.each((liquid, amount) -> {
                    if(amount <= 0.001f) return;

                    Building target = getTileTarget(liquid, travel, source, false);
                    if(target != null && target.acceptLiquid(this, liquid)){
                        float flow = Math.min(amount, LiquidUtil.flow(side, sideLiquidCapacity, this, target, liquid) * delta());
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
        public Building getLiquidDestination(Building from, Liquid liquid){
            //the sorter is always the destination, so it stays in control of routing and buffering
            return this;
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid){
            noSleep();
            int dir = inputSide(source);
            return enabled && sides[dir] != null && LiquidUtil.freeSpace(sides[dir], sideLiquidCapacity) > 0.01f;
        }

        @Override
        public void handleLiquid(Building source, Liquid liquid, float amount){
            int input = inputSide(source);
            if(sides[input] == null) sides[input] = new LiquidModule();
            sides[input].add(liquid, amount);

            int travel = Mathf.mod(input + 2, 4);
            Building target = getTileTarget(liquid, travel, source, true);
            if(target != null && target.acceptLiquid(this, liquid)){
                float flow = Math.min(amount, LiquidUtil.flow(sides[input], sideLiquidCapacity, this, target, liquid) * delta());
                if(flow > 0.01f){
                    target.handleLiquid(this, liquid, flow);
                    sides[input].remove(liquid, flow);
                }
            }
        }

        /** @return the physical side of this sorter that {@code source} is on. */
        private int inputSide(Building source){
            int travel = source.relativeToEdge(tile);
            return travel == -1 ? 0 : Mathf.mod(travel + 2, 4);
        }

        /** Amount of free space on the side buffer facing {@code source}; what this sorter will actually store from {@code source}. */
        public float freeSpaceFor(Building source){
            if(source == null) return sideLiquidCapacity * 4f;
            int dir = inputSide(source);
            return sides[dir] == null ? sideLiquidCapacity : LiquidUtil.freeSpace(sides[dir], sideLiquidCapacity);
        }

        public boolean isSame(Building other){
            return other != null && other.block.instantTransfer;
        }

        public Building getTileTarget(Liquid liquid, int dir, Building source, boolean flip){
            if(source == null) return null;
            Building to;

            if(((liquid == sortLiquid) != invert) == enabled){
                //prevent 3-chains
                if(isSame(source) && isSame(nearby(dir))){
                    return null;
                }
                to = nearby(dir);
            }else{
                Building a = nearby(Mathf.mod(dir - 1, 4));
                Building b = nearby(Mathf.mod(dir + 1, 4));
                boolean ac = a != null && !(a.block.instantTransfer && source.block.instantTransfer) && a.acceptLiquid(this, liquid);
                boolean bc = b != null && !(b.block.instantTransfer && source.block.instantTransfer) && b.acceptLiquid(this, liquid);

                if(ac && !bc){
                    to = a;
                }else if(bc && !ac){
                    to = b;
                }else if(!bc){
                    return null;
                }else{
                    to = (rotation & (1 << dir)) == 0 ? a : b;
                    if(flip) rotation ^= (1 << dir);
                }
            }

            return to;
        }

        @Override
        public void buildConfiguration(Table table){
            ItemSelection.buildTable(SiphonSorter.this, table, content.liquids(), () -> sortLiquid, this::configure, selectionRows, selectionColumns);
        }

        @Override
        public Liquid config(){
            return sortLiquid;
        }

        @Override
        public byte version(){
            return 2;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.s(sortLiquid == null ? -1 : sortLiquid.id);
            for(int i = 0; i < 4; i++){
                sides[i].write(write);
            }
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            sortLiquid = content.liquid(read.s());
            if(revision >= 2){
                for(int i = 0; i < 4; i++){
                    sides[i].read(read, false);
                }
            }
        }
    }
}
