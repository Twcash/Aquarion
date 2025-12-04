package aquarion.world.entities;

import arc.func.Cons;
import arc.func.Cons2;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Vec2;
import mindustry.world.blocks.environment.Floor;

public class AquaLegConfig {
    public float baseX, baseY, baseRot, legLength = 10, minLen, maxLen, legExtension;
    public String suffix;
    public AquaLegConfig(float baseX,float baseY,float baseRot, float legLength){
        this.baseX = baseX;
        this.baseY = baseY;
        this.baseRot = baseRot;
        this.legLength = legLength;
    }
    public TextureRegion footRegion, legRegion, legBaseRegion, jointRegion, baseJointRegion;
}