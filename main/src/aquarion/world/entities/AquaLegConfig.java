package aquarion.world.entities;

import arc.func.Cons;
import arc.func.Cons2;
import arc.math.geom.Vec2;
import mindustry.world.blocks.environment.Floor;

public class AquaLegConfig {

    public static class JointConfig {
        public float baseX, baseY;      // base offset relative to unit
        public float baseRot;           // initial rotation
        public float minLen, maxLen;    // joint length limits
        public float rotationLimit;     // max rotation relative to parent joint

        public JointConfig(float baseX, float baseY, float baseRot,
                           float minLen, float maxLen, float rotationLimit){
            this.baseX = baseX;
            this.baseY = baseY;
            this.baseRot = baseRot;
            this.minLen = minLen;
            this.maxLen = maxLen;
            this.rotationLimit = rotationLimit;
        }
    }

    public JointConfig[] jointConfigs;
    public Vec2[] defaultPositions;  // starting offsets
    public Vec2[] jointPositions;    // live positions updated by IK

    public AquaLegConfig(JointConfig... jointConfigs){
        this.jointConfigs = jointConfigs;
        this.defaultPositions = new Vec2[jointConfigs.length];
        this.jointPositions = new Vec2[jointConfigs.length];

        for(int i = 0; i < jointConfigs.length; i++){
            this.defaultPositions[i] = new Vec2(jointConfigs[i].baseX, jointConfigs[i].baseY);
            this.jointPositions[i] = new Vec2(this.defaultPositions[i]);
        }
    }
}