package aquarion.world.blocks.production;


import arc.math.Mathf;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.world.blocks.production.Incinerator;
import mindustry.world.meta.BlockStatus;

public class DelayIncinerator extends Incinerator {
    public float incinTime = 60;
    public DelayIncinerator(String name){
        super(name);
        hasPower = true;
        hasItems = true;
        update = true;
        itemCapacity = 40;
        hasLiquids = false;
        solid = true;
    }

    public class IncineratorBuild extends Incinerator.IncineratorBuild {
        public float heat;
        public float progress = 0;
        @Override
        public void updateTile(){
            heat = Mathf.approachDelta(heat, efficiency, 0.04f);
            if(items.first() != null){
                progress += delta() * heat;
            }
            if(progress >= incinTime){
                progress = 0;
                items.remove(items.first(), 1);
            }
        }

        @Override
        public BlockStatus status(){
            return !enabled ? BlockStatus.logicDisable : heat > 0.5f ? BlockStatus.active : BlockStatus.noInput;
        }
        //Need a new Draw() method
        @Override
        public void draw(){
            super.draw();
        }

        @Override
        public void handleItem(Building source, Item item){
            items.add(item, 1);
            if(Mathf.chance(0.3)){
                effect.at(x, y);
            }
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            return heat > 0.5f && enabled && items.get(item ) < itemCapacity;
        }
        @Override
        public boolean acceptLiquid(Building source, Liquid liquid){
            //Literally just dump it
            return false;
        }
    }
}