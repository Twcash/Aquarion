package aquarion.world.blocks.rotPower;

import arc.struct.IntSet;
import mindustry.gen.Building;
import java.util.ArrayList;
import java.util.List;

public class TorqueNetwork {
    private List<TorqueBlock> connectedTorqueBlocks = new ArrayList<>();
    private float sharedTorque = 0f; // Shared torque value

    public List<TorqueBlock> getConnectedTorqueBlocks(Building start) {
        connectedTorqueBlocks.clear();
        collectConnectedTorqueBlocks(start, new IntSet());
        return connectedTorqueBlocks;
    }

    private void collectConnectedTorqueBlocks(Building start, IntSet cameFrom) {
        if (!cameFrom.add(start.id)) return; // Avoid cycles

        if (start instanceof TorqueBlock torqueBlock) {
            connectedTorqueBlocks.add(torqueBlock);
        }

        for (var build : start.proximity) {
            if (build != null && build.team == start.team && build instanceof TorqueBlock torqueE) {
                if (!build.block.rotate || relativeTo(build) == build.rotation) {
                    collectConnectedTorqueBlocks(build, cameFrom);
                }
            }
        }
    }

    public void setSharedTorque(Building start, float torque) {
        this.sharedTorque = torque;
        // Propagate torque to all connected blocks
        for (TorqueBlock block : getConnectedTorqueBlocks(start)) {
            block.setTorque(torque);
        }
    }

    public float getSharedTorque() {
        return sharedTorque;
    }

    // Helper method to determine relative direction
    private int relativeTo(Building build) {
        // Implementation of relativeTo method
        return 0; // Placeholder
    }
}
