package aquarion.world.blocks.rotPower;

public interface TorqueBlock {
    float torque();
    public boolean hasTorque = true;
    /** @return rotPower as a fraction of max heat */
    float torqueFract();
}