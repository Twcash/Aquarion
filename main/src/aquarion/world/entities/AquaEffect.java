package aquarion.world.entities;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import mindustry.graphics.Pal;

import static mindustry.Vars.headless;
import static mindustry.entities.Effect.decal;

public class AquaEffect {
    public static void rubble(float x, float y, int blockSize){
        if(headless) return;

        TextureRegion region = Core.atlas.find("aquarion-block-gore-" + blockSize + "-" + Mathf.random(0,1));
        decal(region, x, y, Mathf.random(4) * 90, 3600, Color.white);
    }
}
