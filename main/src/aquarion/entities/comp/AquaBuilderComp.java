package aquarion.entities.comp;

import aquarion.annotations.Annotations.*;
import aquarion.gen.AquaBuilderc;
import aquarion.gen.AquaUnitc;
import mindustry.gen.Builderc;
import mindustry.graphics.Layer;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.Queue;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.units.*;
import mindustry.game.EventType.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.ConstructBlock.*;

import java.util.*;

import static mindustry.Vars.*;
@EntityComponent
abstract class AquaBuilderComp implements Unitc {
    @Import float x, y, rotation, buildSpeedMultiplier;
    @Import UnitType type;
    @Import Team team;
    @Import private transient float buildCounter;
    @Import private transient BuildPlan lastActive;
    @Import private transient int lastSize;
    @Import transient float buildAlpha = 0f;
    @Import @SyncLocal Queue<BuildPlan> plans = new Queue<>(1);
    @Import @SyncLocal boolean updateBuilding = true;

    @Override
    public void validatePlans(){

    }
    @Override
    public void updateBuildLogic(){
        if(type.buildSpeed <= 0f) return;
    }

}