package aquarion.world.blocks.distribution;

import aquarion.world.items.AquaItemEntity;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.blocks.distribution.DirectionalUnloader;
import mindustry.world.blocks.storage.CoreBlock.CoreBuild;
import mindustry.world.blocks.storage.StorageBlock.StorageBuild;

/** Directional unloader that transfers per-unit item state from its source inventory. */
public class EntityDirectionalUnloader extends DirectionalUnloader {
    public EntityDirectionalUnloader(String name) {
        super(name);
        buildType = () -> new EntityDirectionalUnloaderBuild(this);
    }

    public static class EntityDirectionalUnloaderBuild extends DirectionalUnloader.DirectionalUnloaderBuild {
        public EntityDirectionalUnloaderBuild(DirectionalUnloader owner) {
            owner.super();
        }

        public static void install(DirectionalUnloader block) {
            block.buildType = () -> new EntityDirectionalUnloaderBuild(block);
        }

        @Override
        public void updateTile(){
            DirectionalUnloader unloader = (DirectionalUnloader)block;
            if((unloadTimer += edelta()) >= unloader.speed){
                Building front = front(), back = back();
                Building itemSource = back instanceof StorageBuild storage && storage.linkedCore != null ? storage.linkedCore : back;
                if(front != null && back != null && itemSource != null && itemSource.items != null && front.team == team && back.team == team && back.canUnload() &&
                    (unloader.allowCoreUnload || !(back instanceof CoreBuild || (back instanceof StorageBuild storage && storage.linkedCore != null)))){
                    Item selected = unloadItem;
                    if(selected == null){
                        var itemseq = Vars.content.items();
                        for(int i = 0; i < itemseq.size; i++){
                            Item item = itemseq.get((i + offset) % itemseq.size);
                            if(itemSource.items.has(item) && front.acceptItem(this, item)){
                                selected = item;
                                offset = item.id + 1;
                                break;
                            }
                        }
                    }else if(!itemSource.items.has(selected) || !front.acceptItem(this, selected)){
                        selected = null;
                    }

                    if(selected != null){
                        AquaItemEntity entity = AquaItemEntity.capture(itemSource, selected);
                        AquaItemEntity.forward(this, front, selected, entity);
                        itemSource.items.remove(selected, 1);
                        itemSource.itemTaken(selected);
                    }
                }
                unloadTimer %= unloader.speed;
            }
        }
    }
}
