package aquarion.world.blocks.heatBlocks;

import arc.Core;
import arc.math.*;
import arc.struct.*;
import arc.util.io.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.blocks.heat.HeatBlock;
import mindustry.world.blocks.heat.HeatConsumer;
import mindustry.world.blocks.production.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;

public class CoolingBlock extends AquaHeatCrafter{
    public float heatSubtraction = 10;
    public float maxCooling = -50;
    public float warmupRate = 0.15f;

    public CoolingBlock(String name) {
        super(name);
        heatRequirement = 0;
        maxEfficiency = 1;
        rotateDraw = false;
        rotate = true;
    }
    public class HeatCrafterBuild extends AquaHeatCrafter.AquaHeatCrafterBuild implements HeatConsumer, HeatBlock {
        //TODO sideHeat could be smooth
        public float[] sideHeat = new float[4];
        public float heat = 0f;
        @Override
        public void updateTile(){
            super.updateTile();

            float receivedHeat = calculateHeat(sideHeat);
            if(receivedHeat < heatRequirement) return;
            // Subtract the received heat
            heat = Math.max(Mathf.approachDelta(heat,  receivedHeat-heatSubtraction, warmupRate * delta()) * efficiencyScale(), maxCooling);


        }

        @Override
        public float heatFrac(){
            return heat / heatSubtraction;
        }

        @Override
        public float heat(){
            return heat;
        }
        @Override
        public float heatRequirement(){
            return heatRequirement;
        }

        @Override
        public float[] sideHeat(){
            return sideHeat;
        }

        @Override
        public float warmupTarget(){
            return Mathf.clamp(heat / heatRequirement);
        }

        @Override
        public float efficiencyScale(){
            float over = Math.max(heat - heatRequirement, 0f);
            return Math.min(Mathf.clamp(heat / heatRequirement) + over / heatRequirement * overheatScale, maxEfficiency);
        }
    }
}