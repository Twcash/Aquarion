package aquarion.world.blocks.rotPower;

import arc.math.*;
import arc.util.io.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.blocks.production.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;

public class TorqueProducer extends GenericCrafter{
    public float torqueOutput = 10f;
    public float warmupRate = 0.15f;

    public TorqueProducer(String name){
        super(name);

        drawer = new DrawMulti(new DrawDefault());
        rotateDraw = false;
        rotate = true;
        canOverdrive = false;
        drawArrow = true;
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.add(Stat.output, torqueOutput, StatUnit.heatUnits);
    }

    @Override
    public void setBars(){
        super.setBars();

        addBar("torque", (TorqueProducerBuild entity) -> new Bar("bar.torque", Pal.thoriumPink, () -> entity.torque / torqueOutput));
    }

    public class TorqueProducerBuild extends GenericCrafterBuild implements TorqueBlock{
        public float torque;

        @Override
        public void updateTile(){
            super.updateTile();

            //heat approaches target at the same speed regardless of efficiency
            torque = Mathf.approachDelta(torque, torqueOutput * efficiency, warmupRate * delta());
        }

        @Override
        public float torqueFract(){
            return torque / torqueOutput;
        }

        @Override
        public float torque(){
            return torque;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(torque);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            torque = read.f();
        }
    }
}