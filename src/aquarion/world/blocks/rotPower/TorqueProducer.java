package aquarion.world.blocks.rotPower;

import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.util.io.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.blocks.production.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;

public class TorqueProducer extends GenericCrafter {
    public float torqueOutput = 10f;
    public float warmupRate = 0.15f;

    public TorqueProducer(String name) {
        super(name);

        drawer = new DrawMulti(new DrawDefault());
        rotateDraw = false;
        rotate = true;
        canOverdrive = false;
        drawArrow = true;
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.output, torqueOutput, StatUnit.heatUnits);
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("torque", (TorqueProducerBuild entity) -> new Bar("bar.torque", Pal.thoriumPink, () -> entity.getTorque() / torqueOutput));
    }

    public class TorqueProducerBuild extends GenericCrafterBuild implements TorqueBlock {
        private float torque;

        @Override
        public void updateTile() {
            super.updateTile();

            // Update the torque and propagate it to connected TorqueBlocks
            torque = Mathf.approachDelta(torque, torqueOutput * efficiency, warmupRate * delta());
            setTorque(torque);
        }

        @Override
        public float torque() {
            return torque;
        }

        @Override
        public float getTorque() {
            return torque;
        }

        @Override
        public void setTorque(float newTorque) {
            this.torque = newTorque;
            // Optionally, update connected TorqueBlocks if needed
            // In this case, TorqueProducer does not inherently connect to other blocks
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(torque);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            torque = read.f();
        }
    }
}