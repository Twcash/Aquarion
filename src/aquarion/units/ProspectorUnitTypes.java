package aquarion.units;

import aquarion.world.AI.ProspectorBaseBuilderAI;
import mindustry.ai.BaseBuilderAI;
import mindustry.ai.types.MinerAI;
import mindustry.game.Teams;
import mindustry.gen.UnitEntity;
import mindustry.type.UnitType;

import static mindustry.Vars.state;

public class ProspectorUnitTypes {
    public static UnitType synthesis, dissect, inquire, employ;

    public static void loadContent() {
        synthesis = new UnitType("synthsis"){{
            constructor = UnitEntity::create;
            hitSize = 11;
            speed = 6.5f;
            flying = true;
            buildSpeed = 1;
            isEnemy = true;
            canAttack = false;
            rotateMoveFirst = true;
            controller = u -> new ProspectorBaseBuilderAI();
            aiController = ProspectorBaseBuilderAI::new;
        }};
    }
}
