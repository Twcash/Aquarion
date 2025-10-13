package aquarion.world.blocks.power;

import arc.Events;
import arc.util.Nullable;
import mindustry.type.ItemStack;
import mindustry.world.blocks.power.ConsumeGenerator;
import mindustry.world.consumers.ConsumeItemFlammable;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;

public class GenericGenerator extends ConsumeGenerator {
    /** Written to outputItems as a single-element array if outputItems is null. */
    public @Nullable ItemStack outputItem;
    /** Overwrites outputItem if not null. */
    public @Nullable ItemStack[] outputItems;
    public GenericGenerator(String name) {
        super(name);
    }

    @Override
    public void setStats(){
        stats.timePeriod = itemDuration;
        super.setStats();
        if((hasItems && itemCapacity > 0) || outputItems != null){
            stats.add(Stat.productionTime, itemDuration / 60f, StatUnit.seconds);
        }

        if(outputItems != null){
            stats.add(Stat.output, StatValues.items(itemDuration, outputItems));
        }

    }
    @Override
    public void init(){
        if(outputItems == null && outputItem != null){
            outputItems = new ItemStack[]{outputItem};
        }

        if(outputItems != null) hasItems = true;

        super.init();
    }
    @Override
    public boolean outputsItems(){
        return outputItems != null;
    }
    public class genConBuild extends ConsumeGeneratorBuild {
        @Override
        public void updateTile() {;
            super.updateTile();
            boolean valid = efficiency > 0;
            if(hasItems && valid && generateTime <= 0f){
                craft();
            }
            dumpOutputs();
        }
        @Override
        public boolean shouldConsume(){
            if(outputItems != null){
                for(var output : outputItems){
                    if(items.get(output.item) + output.amount > itemCapacity){
                        return false;
                    }
                }
            }
            return super.shouldConsume();
        }
        public void craft(){
            consume();

            if(outputItems != null){
                for(var output : outputItems){
                    for(int i = 0; i < output.amount; i++){
                        offload(output.item);
                    }
                }
            }
        }
        public void dumpOutputs(){
            if(outputItems != null && timer(timerDump, dumpTime / timeScale)){
                for(ItemStack output : outputItems){
                    dump(output.item);
                }
            }
        }
    }

}
