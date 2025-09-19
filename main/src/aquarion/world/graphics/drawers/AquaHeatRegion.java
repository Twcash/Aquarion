package aquarion.world.graphics.drawers;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.world.Block;
import mindustry.world.blocks.heat.HeatConsumer;
import mindustry.world.draw.DrawBlock;

public class AquaHeatRegion extends DrawBlock {
    public Color color = new Color(1f, 0.22f, 0.22f, 0.8f);
    public float pulse = 0.3f, pulseScl = 10f;
    public float layer = Layer.blockAdditive;

    public TextureRegion heat;
    public String suffix = "-heats";

    public AquaHeatRegion(float layer){
        this.layer = layer;
    }

    public AquaHeatRegion(String suffix){
        this.suffix = suffix;
    }

    public AquaHeatRegion(){
    }

    @Override
    public void draw(Building build){
        Draw.z(Layer.blockAdditive);
        if((build instanceof HeatConsumer hc)){
            float heate = 0f;
            for(int i = 0; i < 4; i++){
                heate += hc.sideHeat()[i];
            }            float z = Draw.z();
            if(layer > 0) Draw.z(layer);
            Draw.blend(Blending.additive);
            Draw.color(color, Mathf.clamp(heate /30f) * (color.a * (1f - pulse + Mathf.absin(pulseScl, pulse))));
            Draw.rect(heat, build.x, build.y);
            Draw.blend();
            Draw.color();
            Draw.z(z);
        }
    }

    @Override
    public void load(Block block){
        heat = Core.atlas.find(block.name + suffix);
    }
}