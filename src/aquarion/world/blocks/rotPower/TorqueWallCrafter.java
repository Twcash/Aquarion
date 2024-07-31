package aquarion.world.blocks.rotPower;

import arc.Core;
import arc.math.Mathf;
import arc.struct.IntSet;
import arc.util.Nullable;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.blocks.production.WallCrafter;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import java.util.Arrays;

import static mindustry.Vars.tilesize;

/** A crafter that requires contact from torque blocks to craft like heatCrafter. */
public class TorqueWallCrafter extends WallCrafter {
    /** Base torque requirement for 100% efficiency. */
    public float torqueRequirement = 5f;
    /** After torque meets this requirement, excess torque will be scaled by this number. */
    public float overEfficiencyScale = 1f;
    /** Maximum possible efficiency. */
    public float maxEfficiency = 2f;

    public TorqueWallCrafter(String name) {
        super(name);
    }

    @Override
    public void setStats() {
        super.setStats();

        stats.add(Stat.input, torqueRequirement, StatUnit.heatUnits);
        stats.add(Stat.maxEfficiency, (int) (maxEfficiency * 100f), StatUnit.percent);
    }

    @Override
    public void setBars() {
        super.setBars();

        addBar("torque", (TorqueWallCrafterBuild entity) ->
                new Bar(() ->
                        Core.bundle.format("bar.TorquePercent", (int) (entity.torque + 0.01f), (int) (entity.efficiencyScale() * 100 + 0.01f)),
                        () -> Pal.thoriumPink,
                        () -> entity.torque / torqueRequirement));
    }

    public class TorqueWallCrafterBuild extends WallCrafterBuild implements TorqueConsumer {
        public float[] sideTorque = new float[4];
        public float torque = 0f;

        @Override
        public void updateTile() {
            updateTorque();
            super.updateTile();
        }

        public void updateTorque() {
            torque = calculateTorque(sideTorque);
            // Deduct the required torque from the network
            float requiredTorque = torqueRequirement();
            if (torque >= requiredTorque) {
                torque -= requiredTorque;
                // Update any connected components to reflect the reduced torque
                updateConnectedTorque(-requiredTorque);
            }
        }

        @Override
        public float torqueRequirement() {
            return torqueRequirement;
        }

        @Override
        public float[] torqueSide() {
            return sideTorque;
        }

        public float efficiencyScale() {
            float over = Math.max(torque - torqueRequirement, 0f);
            return Math.min(Mathf.clamp(torque / torqueRequirement) + over / torqueRequirement * overEfficiencyScale, maxEfficiency);
        }

        public float calculateTorque(float[] torqueSide) {
            return calculateTorque(torqueSide, null);
        }

        public float calculateTorque(float[] torqueSide, @Nullable IntSet cameFrom) {
            Arrays.fill(torqueSide, 0f);
            if (cameFrom != null) cameFrom.clear();

            float totalTorque = 0f;

            for (var build : proximity) {
                if (build != null && build.team == team && build instanceof TorqueBlock torqueE) {

                    boolean split = build.block instanceof TorqueShaft cond;
                    if (!build.block.rotate || (!split && (relativeTo(build) + 2) % 4 == build.rotation) || (split && relativeTo(build) != build.rotation)) {

                        if (!(build instanceof TorqueShaft.TorqueShaftBuild hc)) {
                            float diff = (Math.min(Math.abs(build.x - x), Math.abs(build.y - y)) / tilesize);
                            int contactPoints = Math.min((int) (block.size / 2f + build.block.size / 2f - diff), Math.min(build.block.size, block.size));

                            float add = torqueE.torque() / build.block.size * contactPoints;
                            if (split) {
                                add /= 3f;
                            }

                            torqueSide[Mathf.mod(relativeTo(build), 4)] += add;
                            totalTorque += add;
                        }

                        if (cameFrom != null) {
                            cameFrom.add(build.id);
                        }
                    }
                }
            }
            return totalTorque;
        }

        /** Update connected components' torque after deduction. */
        private void updateConnectedTorque(float torqueDelta) {
        }
    }
}