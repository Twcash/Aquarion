package aquarion.world.drawers;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Tmp;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;

//Credit to @Photon-gravity for initial code. I just heavily modified it.
public class DrawWheel extends DrawBlock {
    public String suffix = "";
    public float rotationSpeed = 1f;
    public float rotation = 0f;
    public int sideCount = 12;
    public float layer = -1;
    public  float width = 6f;
    public float height = 10f;
    public float x, y = 0;
    public boolean circular = false;
    //Makes it where there are "gaps" between panels
    public float holeWidth = 1;
    public Color[] wheelColors = {Color.white, Color.gray, Color.black};

    TextureRegion region;

    public DrawWheel(String suffix, float rotateSpeed, float rotation) {
        this.suffix = suffix;
        this.rotation = rotation;
        this.rotationSpeed = rotateSpeed;
    }

    public DrawWheel() {}

    @Override
    public void load(Block block) {
        super.load(block);
        region = Core.atlas.find(block.name + suffix);
    }

    @Override
    public void draw(Building build) {
        float z = Draw.z();
        if (layer > 0) Draw.z(layer);


        float rot = build.totalProgress() * rotationSpeed;

        for (int i = 0; i < sideCount; i++) {
            float mod1 = Mathf.mod(rot + 360f / sideCount * i, 360f);
            float mod2 = Mathf.mod(rot + 360f / sideCount * (i + 1), 360f);

            float cos1 = Mathf.sin(Mathf.degreesToRadians * mod1);
            float cos2 = Mathf.sin(Mathf.degreesToRadians * mod2);
            //Liz code cos3
            float midCos = Mathf.sin(Mathf.degreesToRadians * (rot + 360f / sideCount * (i + 0.5f)), 1, 1);

            // Color blending
            if (midCos > 0f) {
                Tmp.c1.set(wheelColors[1]).lerp(wheelColors[0], Mathf.clamp(midCos)).cpy();
            } else {
                Tmp.c1.set(wheelColors[2]).lerp(wheelColors[1], Mathf.clamp(midCos + 1f)).cpy();
            }
            Draw.color(Tmp.c1, Tmp.c1.a);

            if (circular) {
                if (mod1 > 180) cos1 = 1f;
                if (mod2 > 180) cos2 = -1f;
            }

            if ((cos1 - cos2 >= 0 && !circular) || (!(mod1 >= 180 && mod2 >= 180) && circular)) {
                Tmp.v1.trns(rotation, 0f, height * (cos1 + cos2)/4f);
                Fill.rect(build.x + Tmp.v1.x + x, build.y + Tmp.v1.y+y, width, height/holeWidth * (cos1 - cos2)/2f, rotation);
            }
        }

        Draw.reset();
        Draw.z(z);
    }
}