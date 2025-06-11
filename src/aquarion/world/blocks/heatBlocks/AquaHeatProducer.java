package aquarion.world.blocks.heatBlocks;

import arc.Core;
import arc.math.*;
import arc.struct.*;
import arc.util.io.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.blocks.production.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;

public class AquaHeatProducer extends GenericCrafter{
    public float visualMaxHeat, visualMinHeat = 15;
    public float heatOutput = 10f;
    public float warmupRate = 0.15f;

    public AquaHeatProducer(String name){
        super(name);

        drawer = new DrawMulti(new DrawDefault());
        rotateDraw = false;
        drawArrow = true;
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.add(Stat.output, heatOutput, StatUnit.heatUnits);
    }

    @Override
    public void setBars(){
        super.setBars();

        addBar("temperature", (AquaheatMover.HeatMoverBuild entity) -> new Bar(() -> Core.bundle.format("bar.heatamount", (int) (entity.heat + 0.001f)), () -> Pal.techBlue.lerp(Pal.lightOrange, entity.heat / visualMaxHeat), () -> entity.heat / visualMaxHeat));
    }

    public class AquaHeatProducerBuild extends GenericCrafterBuild implements AquaHeatBlock{
        public float heat;

        @Override
        public void updateTile(){
            super.updateTile();

            //heat approaches target at the same speed regardless of efficiency
            heat = Mathf.approachDelta(heat, heatOutput * efficiency, warmupRate * delta());
        }

        @Override
        public float heat(){
            return heat;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(heat);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            heat = read.f();
        }
    }
}