package aquarion.world.blocks.neoplasia;

import aquarion.gen.NeoplasiaUpdater;
import aquarion.gen.NeoplasiaUpdaterc;
import aquarion.world.entities.NeoplasiaManager;
import arc.util.Nullable;
import arc.util.Time;
import mindustry.gen.Building;
import mindustry.world.Block;


    public class NeoplasiaSource extends Block {

        public float initialAmount = 20f;
        public float productionPerSecond = 5f;

        public NeoplasiaSource(String name) {
            super(name);
            update = true;
            solid = true;
        }

        public class NeoplasiaSourceBuild extends Building {

            @Override
            public void created() {
                super.created();
                NeoplasiaManager.instance.add(tile, initialAmount);
            }

            @Override
            public void updateTile() {
                NeoplasiaManager.instance.add(tile, productionPerSecond * Time.delta);
            }
        }
    }