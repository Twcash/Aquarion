package aquarion.world.blocks.neoplasia;

import aquarion.annotations.Annotations;
import aquarion.gen.JetUnitc;
import aquarion.gen.NeoplasiaCellc;
import arc.util.Nullable;
import arc.util.Time;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.gen.Unitc;
import mindustry.type.UnitType;
import mindustry.world.Block;
import mindustry.world.Tile;


public class NeoplasiaSource extends Block {
    public static @Annotations.EntityDef(value = {Unitc.class, JetUnitc.class}) UnitType neo;

    public float initialAmount = 20f;
        public float productionPerSecond = 5f;

        public NeoplasiaSource(String name) {
            super(name);
            update = true;
            solid = true;
            neo = new UnitType("neo"){{
                health = 200;
            }};
        }

        public class NeoplasiaSourceBuild extends Building {
             @Override
            public void created(){
                 neo.create(Team.crux);
             }
        }
    }