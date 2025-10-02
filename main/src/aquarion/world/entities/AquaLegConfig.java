package aquarion.world.entities;

import arc.func.Cons;
import arc.func.Cons2;
import arc.math.geom.Vec2;
import mindustry.world.blocks.environment.Floor;

public class AquaLegConfig {
    public float baseX, baseY, baseRot, baseLength = 10, legLength = 10, baseMinLen = 0.5f, baseMaxLen = 1.1f, legMinLen = 0.5f, legMaxLen = 1.1f;
    public AquaLegConfig(float baseX,float baseY,float baseRot, float baseLength, float legLength){
        this.baseX = baseX;
        this.baseY = baseY;
        this.baseRot = baseRot;
        this.baseLength = baseLength;
        this.legLength = legLength;
    }
}