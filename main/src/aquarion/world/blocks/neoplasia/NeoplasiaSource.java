package aquarion.world.blocks.neoplasia;

import aquarion.gen.NeoplasiaCell;
import arc.util.Nullable;
import arc.util.Time;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.world.Block;
import mindustry.world.Tile;


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
                infect(tile, initialAmount);
            }

            @Override
            public void updateTile() {
                infect(tile, productionPerSecond * Time.delta);
            }
            private void infect(Tile tile, float amt){
                NeoplasiaCell cell = NeoplasiaCell.cells.get(tile);
                if(cell == null){
                    cell = NeoplasiaCell.createCell(tile, 0f);
                }
                cell.amount += amt;
            }
        }
    }