package aquarion.world.blocks.rotPower;

import aquarion.blocks.TorqueBlocks;
import arc.math.Mathf;
import arc.struct.IntSet;
import arc.util.Nullable;
import mindustry.gen.Building;

import java.util.Arrays;

import static mindustry.Vars.tilesize;

public class TorqueBuildingComp extends Building {
    public float calculateTorque(float[] torqueSide) {
        return calculateTorque(torqueSide, null);
    }

    public float calculateTorque(float[] torqueSide, @Nullable IntSet cameFrom) {
        Arrays.fill(torqueSide, 0f);
        if (cameFrom != null) cameFrom.clear();

        float heat = 0f;

        for (var build : proximity) {
            if (build != null && build.team == team && build instanceof TorqueBlock torqueE) {


                boolean split = build.block instanceof TorqueShaft cond && cond.splitTorque;
                // non-routers must face us, routers must face away - next to a redirector, they're forced to face away due to cycles anyway
                if (!build.block.rotate || (!split && (relativeTo(build) + 2) % 4 == build.rotation) || (split && relativeTo(build) != build.rotation)) { //TODO hacky

                    //if there's a cycle, ignore its heat
                    if (!(build instanceof TorqueShaft.TorqueShaftBuild hc && hc.cameFrom.contains(id()))) {
                        //x/y coordinate difference across point of contact
                        float diff = (Math.min(Math.abs(build.x - x), Math.abs(build.y - y)) / tilesize);
                        //number of points that this block had contact with
                        int contactPoints = Math.min((int) (block.size / 2f + build.block.size / 2f - diff), Math.min(build.block.size, block.size));

                        //heat is distributed across building size
                        float add = torqueE.torque() / build.block.size * contactPoints;
                        if (split) {
                            //heat routers split heat across 3 surfaces
                            add /= 3f;
                        }

                        torqueSide[Mathf.mod(relativeTo(build), 4)] += add;
                        heat += add;
                    }

                    //register traversed cycles
                    if (cameFrom != null) {
                        cameFrom.add(build.id);
                        if (build instanceof TorqueShaft.TorqueShaftBuild hc) {
                            cameFrom.addAll(hc.cameFrom);
                        }
                    }

                    //hehe haha eeh eeh ooh ooh
                    if (torqueE instanceof TorqueShaft.TorqueShaftBuild cond) {
                        cond.updateTorque();
                    }
                }
            }
        }
        return heat;
    }
}
