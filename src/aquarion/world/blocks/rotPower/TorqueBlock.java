package aquarion.world.blocks.rotPower;

public interface TorqueBlock {
    /**
     * Returns the current amount of torque.
     */
    float torque();

    /**
     * Sets the current amount of torque.
     */
    void setTorque(float newTorque);

    /**
     * Returns the current amount of torque.
     */
    float getTorque();
}