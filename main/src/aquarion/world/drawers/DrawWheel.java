package aquarion.world.drawers;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.Pixmap;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
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

    public TextureRegion iconRegion;

    public DrawWheel(String suffix, float rotateSpeed, float rotation) {
        this.suffix = suffix;
        this.rotation = rotation;
        this.rotationSpeed = rotateSpeed;
    }

    public DrawWheel() {}

    @Override
    public void load(Block block) {
        super.load(block);
        iconRegion = suffix.isEmpty() ? null : Core.atlas.find(block.name + suffix);
    }

    @Override
    public TextureRegion[] icons(Block block){
        return iconRegion != null && iconRegion.found() ? new TextureRegion[]{iconRegion} : super.icons(block);
    }

    @Override
    public void draw(Building build) {
        float z = Draw.z();
        if (layer > 0) Draw.z(layer);


        float rot = build.totalProgress() * build.warmup() * rotationSpeed;
        eachPanel(rot, (panelX, panelY, panelHeight, color) -> {
            Draw.color(color, color.a);
            Fill.rect(build.x + panelX, build.y + panelY, width, panelHeight, rotation);
        });
        Draw.reset();
        Draw.z(z);
    }

    /** Renders the resting wheel on a block-sized, transparent sprite layer. */
    public Pixmap makeIcon(int iconWidth, int iconHeight){
        Pixmap icon = new Pixmap(iconWidth, iconHeight);
        float cos = Mathf.cosDeg(rotation), sin = Mathf.sinDeg(rotation);
        eachPanel(0f, (panelX, panelY, panelHeight, color) -> {
            int rgba = color.rgba8888();
            for(int py = 0; py < iconHeight; py++){
                float dy = (iconHeight / 2f - py - 0.5f) / 4f - panelY;
                for(int px = 0; px < iconWidth; px++){
                    float dx = (px + 0.5f - iconWidth / 2f) / 4f - panelX;
                    if(Math.abs(dx * cos + dy * sin) < width / 2f &&
                        Math.abs(dy * cos - dx * sin) < Math.abs(panelHeight) / 2f){
                        icon.setRaw(px, py, rgba);
                    }
                }
            }
        });
        return icon;
    }

    private void eachPanel(float rot, PanelConsumer consumer){
        Color color = new Color();
        float sin = Mathf.sinDeg(rotation), cos = Mathf.cosDeg(rotation);
        for (int i = 0; i < sideCount; i++) {
            float mod1 = Mathf.mod(rot + 360f / sideCount * i, 360f);
            float mod2 = Mathf.mod(rot + 360f / sideCount * (i + 1), 360f);

            float cos1 = Mathf.sin(Mathf.degreesToRadians * mod1);
            float cos2 = Mathf.sin(Mathf.degreesToRadians * mod2);
            //Liz code cos3
            float midCos = Mathf.sin(Mathf.degreesToRadians * (rot + 360f / sideCount * (i + 0.5f)), 1, 1);

            // Color blending
            if (midCos > 0f) {
                color.set(wheelColors[1]).lerp(wheelColors[0], Mathf.clamp(midCos));
            } else {
                color.set(wheelColors[2]).lerp(wheelColors[1], Mathf.clamp(midCos + 1f));
            }
            if (circular) {
                if (mod1 > 180) cos1 = 1f;
                if (mod2 > 180) cos2 = -1f;
            }

            if ((cos1 - cos2 >= 0 && !circular) || (!(mod1 >= 180 && mod2 >= 180) && circular)) {
                float offset = height * (cos1 + cos2) / 4f;
                consumer.draw(x - sin * offset, y + cos * offset, height / holeWidth * (cos1 - cos2) / 2f, color);
            }
        }
    }

    @FunctionalInterface
    private interface PanelConsumer {
        void draw(float x, float y, float height, Color color);
    }
}
