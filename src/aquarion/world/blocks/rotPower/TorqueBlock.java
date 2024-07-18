package aquarion.world.blocks.rotPower;

public interface TorqueBlock {
    float torque();
    /** @return rotPower as a fraction of max heat */
    float torqueFract();
}