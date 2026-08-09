package aquarion.world.blocks.distribution;

import aquarion.world.content.LiquidReactions;
import aquarion.world.content.LiquidUtil;
import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.util.Eachable;
import arc.util.Nullable;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.type.Liquid;
import mindustry.world.Tile;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.blocks.liquid.LiquidBlock;

import static mindustry.Vars.content;
import static mindustry.Vars.headless;
import static mindustry.Vars.renderer;
import static mindustry.Vars.tilesize;

/**
 * Liquid counterpart to the vanilla item Sorter: set a liquid to sort.
 * The configured liquid passes straight through, everything else is pushed out the sides.
 */
public class SiphonSorter extends LiquidBlock{
    public TextureRegion cross;
    public boolean invert;

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
        cross = Core.atlas.find("cross-full");
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
        /** direction (from source to this block) the liquid is travelling in; used to route buffered liquid. */
        public int inputDir = 0;

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
            //reactions between mixed liquids
            LiquidReactions.react(self());

            if(LiquidUtil.total(liquids) > 0.001f){
                noSleep();

                //the block the buffered liquid travelled in from, used for chain checks
                Building source = nearby(Mathf.mod(inputDir + 2, 4));

                liquids.each((liquid, amount) -> {
                    if(amount <= 0.001f) return;

                    Building target = source == null ? null : getTileTarget(liquid, source, false);
                    if(target != null && target.acceptLiquid(this, liquid)){
                        float flow = Math.min(amount, LiquidUtil.freeSpace(target));
                        if(flow > 0.01f){
                            target.handleLiquid(this, liquid, flow);
                            liquids.remove(liquid, flow);
                        }
                    }
                });
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
            return LiquidUtil.freeSpace(self()) > 0.01f;
        }

        @Override
        public void handleLiquid(Building source, Liquid liquid, float amount){
            inputDir = source.relativeToEdge(tile);
            if(inputDir == -1) inputDir = 0;

            Building target = getTileTarget(liquid, source, true);
            if(target != null && target.acceptLiquid(this, liquid)){
                float flow = Math.min(amount, LiquidUtil.freeSpace(target));
                if(flow > 0.01f){
                    target.handleLiquid(this, liquid, flow);
                    amount -= flow;
                }
            }

            //buffer any overflow until an output opens up
            if(amount > 0.01f){
                liquids.add(liquid, Math.min(amount, LiquidUtil.freeSpace(self())));
            }
        }

        public boolean isSame(Building other){
            return other != null && other.block.instantTransfer;
        }

        public Building getTileTarget(Liquid liquid, Building source, boolean flip){
            if(source == null) return null;
            int dir = source.relativeToEdge(tile);
            if(dir == -1) return null;
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
            return 1;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.s(sortLiquid == null ? -1 : sortLiquid.id);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            sortLiquid = content.liquid(read.s());
        }
    }
}
