package aquarion.world.graphics.drawers;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.world.Block;
import mindustry.world.draw.DrawCells;

public class DrawCellsNew extends DrawCells {
    public TextureRegion middle;
    public String suffix = "-middle";
    public Color color = Color.white.cpy(), particleColorFrom = Color.black.cpy(), particleColorTo = Color.black.cpy();
    public int particles = 12;
    public float range = 4f, recurrence = 2f, radius = 1.8f, lifetime = 60f * 3f;
    public float offsetX, offsetY = 0;

    @Override
    public void draw(Building build){
        Drawf.liquid(middle, build.x, build.y, build.warmup(), color);

        if(build.warmup() > 0.001f){
            rand.setSeed(build.id);
            for(int i = 0; i < particles; i++){
                float offset = rand.nextFloat() * 999999f;
                float x = rand.range(range), y = rand.range(range);
                float fin = 1f - (((Time.time + offset) / lifetime) % recurrence);
                float ca = rand.random(0.1f, 1f);
                float fslope = Mathf.slope(fin);

                if(fin > 0){
                    Draw.color(particleColorFrom, particleColorTo, ca);
                    Draw.alpha(build.warmup());

                    Fill.circle(build.x + x+ offsetX, build.y + y + offsetY, fslope * radius);
                }
            }
        }

        Draw.color();
    }

    @Override
    //BEGONE HARDCODED HARDCODE
    public void load(Block block){
        middle = Core.atlas.find(block.name + suffix);
    }
}