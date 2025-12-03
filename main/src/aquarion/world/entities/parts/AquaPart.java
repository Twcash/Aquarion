package aquarion.world.entities.parts;

import arc.math.Mathf;
import arc.util.Log;
import mindustry.entities.part.DrawPart;
import mindustry.gen.Unit;

public abstract class AquaPart {
    public static final AquaPart.AquaParams aquaParams = new AquaPart.AquaParams();

    public static class AquaParams {
        public int team;
        public float fVel, bVel, lVel, rVel;

        public AquaParams set(Unit unit) {
            float max = unit.type.speed;
            float v = unit.vel().len() / max;

            float velAng = unit.vel().angle();
            float rot = unit.rotation;

            float rel = deltaAngle(rot, velAng);

            this.fVel = Mathf.clamp(Mathf.cosDeg(rel) * v);
            this.bVel = Mathf.clamp(Mathf.cosDeg(rel - 180f) * v);
            this.rVel = Mathf.clamp(Mathf.cosDeg(rel - 90f) * v);
            this.lVel = Mathf.clamp(Mathf.cosDeg(rel + 90f) * v);

            return this;
        }
    }
    public interface APartProg {
        APartProg
                fvel = p -> p.fVel,
                bvel = p -> p.bVel,
                lvel = p -> p.lVel,
                rvel = p -> p.rVel;

        DrawPart.PartProgress
                frontVelocity = p -> aquaParams.fVel,
                backVelocity  = p -> aquaParams.bVel,
                leftVelocity  = p -> aquaParams.lVel,
                rightVelocity = p -> aquaParams.rVel;

        float get(AquaPart.AquaParams p);
    }
    public static float deltaAngle(float a, float b){
        float diff = (b - a) % 360f;
        if(diff > 180f) diff -= 360f;
        if(diff < -180f) diff += 360f;
        return diff;
    }
}
