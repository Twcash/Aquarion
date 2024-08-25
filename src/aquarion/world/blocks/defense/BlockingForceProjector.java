package aquarion.world.blocks.defense;

import arc.math.*;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Fx;
import mindustry.entities.Units;
import mindustry.gen.*;
import mindustry.type.UnitType;
import mindustry.world.*;
import mindustry.world.blocks.defense.ForceProjector;

public class BlockingForceProjector extends ForceProjector {
    public BlockingForceProjector(String name) {
        super(name);
    }

    @Override
    public void init() {
        super.init();
    }

    public class BlockingForceBuild extends ForceBuild {
        @Override
        public void updateTile() {
            super.updateTile();
            float effectiveRadius = radius(); // Get the correct shield radius

            if (!broken && effectiveRadius > 1) {
                Units.nearbyEnemies(team, x, y, effectiveRadius + 10f, unit -> {
                    float overlapDst = (unit.hitSize / 2f + effectiveRadius) - unit.dst(x, y);
                    if (!unit.isGrounded()){
                        if (overlapDst > 0) {
                            unit.vel.setZero(); // Stop units
                            unit.move(Tmp.v1.set(unit).sub(x, y).setLength(overlapDst + 0.01f)); // Push units out
                            if (Mathf.chanceDelta(0.12f * Time.delta)) {
                                Fx.circleColorSpark.at(unit.x, unit.y, team.color);
                            }
                        }
                    }
                });
            }
        }

        @Override
        public void draw() {
            super.draw();
            drawShield(); // Draw the shield
        }

        @Override
        public void write(Writes write) {
            super.write(write);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
        }

        public float radius() {
            // Override to ensure it returns the effective shield radius
            return realRadius(); // Ensure this matches the logic for effective radius in ForceProjector
        }
    }
}