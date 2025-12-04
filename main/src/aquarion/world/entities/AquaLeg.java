package aquarion.world.entities;

import arc.math.geom.Vec2;

public class AquaLeg {
    public Vec2[] joints;
    public Vec2 base = new Vec2();
    public int group;
    public boolean moving;

    public AquaLeg(int jointCount){
        joints = new Vec2[jointCount];
        for(int i = 0; i < jointCount; i++){
            joints[i] = new Vec2();
        }
    }
}