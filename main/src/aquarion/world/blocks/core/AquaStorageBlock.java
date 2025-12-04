package aquarion.world.blocks.core;

import aquarion.world.type.AquaBlock;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.struct.EnumSet;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.Nullable;
import mindustry.content.Fx;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.logic.LAccess;
import mindustry.type.Item;
import mindustry.world.Edges;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock.CoreBuild;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Env;

import static mindustry.Vars.state;
public class AquaStorageBlock extends AquaBlock {
        public boolean coreMerge = true;

        public AquaStorageBlock(String name){
            super(name);
            hasItems = true;
            solid = true;
            update = false;
            sync = true;
            destructible = true;
            separateItemCapacity = true;
            group = BlockGroup.transportation;
            flags = EnumSet.of(BlockFlag.storage);
            allowResupply = true;
            envEnabled = Env.any;
        }
        public DrawBlock drawer = new DrawDefault();
    @Override
    public TextureRegion[] icons(){
        return drawer.finalIcons(this);
    }
    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        drawer.drawPlan(this, plan, list);
    }
    @Override
    public void load(){
        super.load();

        drawer.load(this);
    }
        @Override
        public boolean outputsItems(){
            return false;
        }

        public static void incinerateEffect(Building self, Building source){
            if(Mathf.chance(0.3)){
                Tile edge = Edges.getFacingEdge(source, self);
                Tile edge2 = Edges.getFacingEdge(self, source);
                if(edge != null && edge2 != null && self.wasVisible){
                    Fx.coreBurn.at((edge.worldx() + edge2.worldx())/2f, (edge.worldy() + edge2.worldy())/2f);
                }
            }
        }

        public class AStorageBuild extends Building{
            public @Nullable Building linkedCore;

            @Override
            public boolean acceptItem(Building source, Item item){
                return linkedCore != null ? linkedCore.acceptItem(source, item) : items.get(item) < getMaximumAccepted(item);
            }

            @Override
            public void handleItem(Building source, Item item){
                if(linkedCore != null){
                    if(linkedCore.items.get(item) >= ((CoreBuild)linkedCore).storageCapacity){
                        incinerateEffect(this, source);
                    }
                    ((CoreBuild)linkedCore).noEffect = true;
                    linkedCore.handleItem(source, item);
                }else{
                    super.handleItem(source, item);
                }
            }

            @Override
            public void itemTaken(Item item){
                if(linkedCore != null){
                    linkedCore.itemTaken(item);
                }
            }

            @Override
            public int removeStack(Item item, int amount){
                int result = super.removeStack(item, amount);

                if(linkedCore != null && team == state.rules.defaultTeam && state.isCampaign()){
                    state.rules.sector.info.handleCoreItem(item, -result);
                }

                return result;
            }

            @Override
            public int getMaximumAccepted(Item item){
                return linkedCore != null ? linkedCore.getMaximumAccepted(item) : itemCapacity;
            }

            @Override
            public int explosionItemCap(){
                //when linked to a core, containers/vaults are made significantly less explosive.
                return linkedCore != null ? Math.min(itemCapacity/60, 6) : itemCapacity;
            }

            @Override
            public void drawSelect(){
                if(linkedCore != null){
                    linkedCore.drawSelect();
                }
            }

            @Override
            public double sense(LAccess sensor){
                if(sensor == LAccess.itemCapacity && linkedCore != null) return linkedCore.sense(sensor);
                return super.sense(sensor);
            }

            @Override
            public void overwrote(Seq<Building> previous){
                //only add prev items when core is not linked
                if(linkedCore == null){
                    for(Building other : previous){
                        if(other.items != null && other.items != items && !(other instanceof AStorageBuild b && b.linkedCore != null)){
                            items.add(other.items);
                        }
                    }

                    items.each((i, a) -> items.set(i, Math.min(a, itemCapacity)));
                }
            }
            @Override
            public void draw(){
                drawer.draw(this);
            }
            @Override
            public boolean canPickup(){
                return linkedCore == null;
            }
        }
    }