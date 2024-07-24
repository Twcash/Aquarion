package aquarion.world.blocks.rotPower;

import aquarion.utilities.AquaUtil;
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

import static arc.Core.atlas;

public class TorqueShaft extends Block{
    public float maxVisualTorque = 15f;
    public boolean hasTorque = true;
    public boolean splitTorque = false;
    //Thanks again Stabu. It took forever to find this out
    public static final int[][] tiles = new int[][]{
            {},
            {0, 2}, {1, 3}, {0, 1},
            {0, 2}, {0, 2}, {1, 2},
            {0, 1, 2}, {1, 3}, {0, 3},
            {1, 3}, {0, 1, 3}, {2, 3},
            {0, 2, 3}, {1, 2, 3}, {0, 1, 2, 3}
    };
    public TextureRegion[][] topRegion;
    public TorqueShaft(String name){
        super(name);
        update = solid = rotate = true;
        rotateDraw = false;
        hasTorque = true;
        size = 1;
    }
    @Override
    public void load(){
        super.load();
        topRegion = AquaUtil.splitLayers(name + "-sheet", 32, 1);
        uiIcon = atlas.find(name + "-icon");
    }


    @Override
    public void setBars(){
        super.setBars();

        //TODO show number
        addBar("torque", (TorqueShaftBuild entity) -> new Bar(() -> Core.bundle.format("bar.TorqueAmount", (int)(entity.torque() + 0.001f)), () -> Pal.thoriumPink, () -> entity.torque() / maxVisualTorque));
    }


    public class TorqueShaftBuild extends TorqueBuildingComp implements TorqueBlock, TorqueConsumer{
        public float torque = 0f;
        public float[] torqueSide = new float[4];
        public IntSet cameFrom = new IntSet();
        public int tiling = 0;
        public @Nullable Building next;
        public @Nullable TorqueShaftBuild nextc;
        public long lastTorqueUpdate = -1;


        @Override
        public float[] torqueSide(){
            return torqueSide;
        }

        @Override
        public float torqueRequirement(){
            return maxVisualTorque;
        }
        @Override
        public void draw() {
            //the cheddar is gone
            Draw.z(Layer.block - 0.15f);
            Draw.rect(topRegion[0][tiling], x, y, 0);
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
        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();
            noSleep();
            next = front();
            nextc = next instanceof TorqueShaftBuild d ? d : null;

            tiling = 0;
            for(int i = 0; i < 4; i++){
                Building otherblock = nearby(i);
                if (otherblock == null) continue;
                if ((otherblock.block instanceof TorqueShaft ?
                        (rotation == i || (otherblock.rotation + 2) % 4 == i) :
                        (
                                (rotation == i && otherblock.block instanceof TorqueShaft) ||
                                        (rotation != i && otherblock.block instanceof TorqueBlock))
                )){
                    tiling |= (1 << i);
                }
            }
            tiling |= 1 << rotation;
        }
    }
}