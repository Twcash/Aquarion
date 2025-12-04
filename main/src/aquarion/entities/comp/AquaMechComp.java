package aquarion.entities.comp;

import aquarion.annotations.Annotations.*;
import aquarion.gen.AquaMechc;
import arc.math.Mathf;
import mindustry.gen.ElevationMovec;
import mindustry.gen.Healthc;
import mindustry.gen.Posc;
import mindustry.gen.Unitc;
import mindustry.type.UnitType;
@EntityComponent
abstract class AquaMechComp implements Unitc, Posc, Healthc, AquaMechc, ElevationMovec {
    @Import float x, y, hitSize;
    @Import
    UnitType type;

    @SyncField(false) @SyncLocal
    float baseRotation;
    transient float walkTime, walkExtension;
    transient private boolean walked;

    public float walkExtend(float offset, boolean scaled){
        // cycle advances based on walkTime + stride length
        float cycle = walkTime / type.mechStride;

        // sine-based step: smooth oscillation
        float step = Mathf.sin((cycle + offset) * Mathf.PI2);

        if(scaled){
            // -1..1 normalized stride
            return step;
        }else{
            // scale stride to actual stride length
            return step * type.mechStride;
        }
    }
}
