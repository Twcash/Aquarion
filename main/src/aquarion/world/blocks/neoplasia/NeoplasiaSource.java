package aquarion.world.blocks.neoplasia;

import aquarion.gen.NeoplasiaUpdater;
import aquarion.gen.NeoplasiaUpdaterc;
import aquarion.world.entities.NeoplasiaManager;
import arc.util.Nullable;
import mindustry.gen.Building;
import mindustry.world.Block;

public class NeoplasiaSource extends Block {

    public float initialAmount = 20f;

    public NeoplasiaSource(String name){
        super(name);

        update = true;
        solid = true;
        destructible = true;
    }

    public class NeoplasiaSourceBuild extends Building {
        private @Nullable NeoplasiaUpdater entity = NeoplasiaUpdater.create();
        boolean injected = false;
        @Override
        public void updateTile(){
            if(entity == null) entity  = NeoplasiaUpdater.create();
            if(entity != null){entity.manager.add(tile, initialAmount);
                //DIE
                kill();
            };

        }
    }
}