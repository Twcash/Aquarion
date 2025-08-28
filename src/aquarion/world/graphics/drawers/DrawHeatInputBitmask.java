package aquarion.world.graphics.drawers;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.world.Block;
import mindustry.world.blocks.heat.HeatConsumer;
import mindustry.world.draw.DrawBlock;

public class DrawHeatInputBitmask extends DrawBlock {
    public String suffix = "-heat";
    public Color heatColor = new Color(1f, 0.22f, 0.22f, 0.8f);
    public float heatPulse = 0.3f, heatPulseScl = 10f;

    public TextureRegion[][] heat;

    public DrawHeatInputBitmask(String suffix){
        this.suffix = suffix;
    }

    public DrawHeatInputBitmask(){
    }

    @Override
    public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list){
    }

    @Override
    public void draw(Building build){

        Draw.z(Layer.blockAdditive);
        if(build instanceof HeatConsumer hc){
            float[] side = hc.sideHeat();
            for(int i = 0; i < 4; i++){
                if(side[i] > 0){
                    Draw.blend(Blending.additive);
                    Draw.color(heatColor, side[i] / hc.heatRequirement() * (heatColor.a * (1f - heatPulse + Mathf.absin(heatPulseScl, heatPulse))));
                    Draw.rect(heat[i][0], build.x, build.y, i * 90f);
                    Draw.blend();
                    Draw.color();
                }
            }
        }
        Draw.z(Layer.block);
    }

    @Override
    public void load(Block block){
        heat = Core.atlas.find(block.name + suffix).split(block.size*32, block.size*32);
    }
}