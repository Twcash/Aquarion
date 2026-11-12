package aquarion.world.blocks.radioloy;

import aquarion.world.items.AquaItemEntity;
import aquarion.world.type.AquaBlock;
import arc.math.Mathf;
import mindustry.content.Items;
import mindustry.gen.Building;

public class RadioactiveBlock extends AquaBlock {
    public float enrichChance = 0.01f;
    public RadioactiveBlock(String name) {
        super(name);
        update = true;
        buildType = RadioactiveBlockBuild::new;
    }
    public class RadioactiveBlockBuild extends Building {
        @Override
        public void updateTile() {
            for(Building other : proximity) {
                if(other.team == team && other.items != null && other.items.has(Items.thorium)
                    && Mathf.chance(1f - Math.pow(1f - Mathf.clamp(enrichChance), delta()))) {
                    AquaItemEntity.enrich(other, Items.thorium);
                }
            }
        }
    }
}
