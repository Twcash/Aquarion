package aquarion.world.blocks.rotPower;

import arc.*;
import arc.graphics.g2d.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.draw.*;

public class TorqueShaft extends Block{
    public float maxVisualTorque = 15f;
    public DrawBlock drawer = new DrawDefault();
    public boolean splitTorque = false;

    public TorqueShaft(String name){
        super(name);
        update = solid = rotate = true;
        rotateDraw = false;
        size = 1;
    }

    @Override
    public void setBars(){
        super.setBars();

        //TODO show number
        addBar("torque", (TorqueShaftBuild entity) -> new Bar(() -> Core.bundle.format("bar.TorqueAmount", (int)(entity.torque() + 0.001f)), () -> Pal.thoriumPink, () -> entity.torque() / maxVisualTorque));
    }

    @Override
    public void load(){
        super.load();

        drawer.load(this);
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        drawer.drawPlan(this, plan, list);
    }

    @Override
    public TextureRegion[] icons(){
        return drawer.finalIcons(this);
    }

    public class TorqueShaftBuild extends TorqueBuildingComp implements TorqueBlock, TorqueConsumer{
        public float torque = 0f;
        public float[] torqueSide = new float[4];
        public IntSet cameFrom = new IntSet();
        public long lastTorqueUpdate = -1;

        @Override
        public void draw(){
            drawer.draw(this);
        }

        @Override
        public void drawLight(){
            super.drawLight();
            drawer.drawLight(this);
        }

        @Override
        public float[] torqueSide(){
            return torqueSide;
        }

        @Override
        public float torqueRequirement(){
            return maxVisualTorque;
        }

        @Override
        public void updateTile(){
            updateTorque();
        }

        public void updateTorque(){
            if(lastTorqueUpdate == Vars.state.updateId) return;

            lastTorqueUpdate = Vars.state.updateId;
            torque = calculateTorque(torqueSide, cameFrom);
        }

        @Override
        public float warmup(){
            return torque;
        }

        @Override
        public float torque(){
            return torque;
        }

        @Override
        public float torqueFract(){
            return (torque / maxVisualTorque) / (splitTorque ? 3f : 1);
        }
    }
}