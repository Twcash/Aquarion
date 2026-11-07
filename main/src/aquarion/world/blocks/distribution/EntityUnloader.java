package aquarion.world.blocks.distribution;

import aquarion.world.items.AquaItemEntity;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.blocks.storage.Unloader;
import mindustry.world.blocks.storage.CoreBlock.CoreBuild;
import mindustry.world.blocks.storage.StorageBlock.StorageBuild;

/** Vanilla unloader build that preserves per-item state when moving items between inventories. */
public class EntityUnloader extends Unloader {
    public EntityUnloader(String name) {
        super(name);
        buildType = () -> new EntityUnloaderBuild(this);
    }

    public static class EntityUnloaderBuild extends Unloader.UnloaderBuild {
        public EntityUnloaderBuild(Unloader owner) {
            owner.super();
        }

        public static void install(Unloader block) {
            block.buildType = () -> new EntityUnloaderBuild(block);
        }

        @Override
        public void updateTile() {
            Unloader unloader = (Unloader)block;
            unloadTimer += delta();
            if(unloadTimer < unloader.speed || proximity.size < 2) return;

            var candidates = Vars.content.items();
            int candidateCount = sortItem == null ? candidates.size : 1;
            if(candidateCount == 0) return;

            Item selected = null;
            Building selectedFrom = null, selectedTo = null;
            for(int offset = 0; offset < candidateCount && selected == null; offset++){
                int index = sortItem == null ? (rotations + offset + 1) % candidateCount : 0;
                Item item = sortItem == null ? candidates.get(index) : sortItem;
                Building from = null, to = null;
                float fromLoad = -1f, toLoad = Float.MAX_VALUE;

                for(Building other : proximity){
                    if(!other.interactable(team) || other.team != team || other.items == null) continue;
                    boolean storage = other instanceof CoreBuild || other instanceof StorageBuild;
                    boolean canProvide = other.canUnload() && other.items.has(item) &&
                        (unloader.allowCoreUnload || !storage || (other instanceof StorageBuild store && store.linkedCore == null));
                    boolean canReceive = !storage && other.acceptItem(this, item);

                    if(canProvide){
                        int maximum = other.getMaximumAccepted(item);
                        float load = maximum == 0 ? 0f : other.items.get(item) / (float)maximum;
                        if(load > fromLoad){
                            from = other;
                            fromLoad = load;
                        }
                    }
                    if(canReceive){
                        int maximum = other.getMaximumAccepted(item);
                        float load = maximum == 0 ? 0f : other.items.get(item) / (float)maximum;
                        if(load < toLoad){
                            to = other;
                            toLoad = load;
                        }
                    }
                }

                if(from != null && to != null && from != to && (fromLoad != toLoad || !from.acceptItem(this, item))){
                    selected = item;
                    selectedFrom = from;
                    selectedTo = to;
                }
            }

            if(selected != null){
                AquaItemEntity entity = AquaItemEntity.capture(selectedFrom, selected);
                AquaItemEntity.forward(this, selectedTo, selected, entity);
                selectedFrom.removeStack(selected, 1);
                rotations = selected.id;
                unloadTimer -= unloader.speed;
            }else{
                unloadTimer = Math.min(unloadTimer, unloader.speed);
            }
        }
    }
}
