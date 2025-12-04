package aquarion.world.drawers;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.world.Block;
import mindustry.world.draw.DrawRegion;

public class DrawOrbitRegions extends DrawRegion {
    public TextureRegion region;
    public String suffix = "";
    public boolean drawPlan = true;
    //how many thingies are in orbit
    public int regionCount = 1;
    public float radius = 8f;
    //How fast this thing orbits.
    public float orbitSpeed;
    //offset of the entire orbit
    public float rotationOffset = 0f;
    public float layer = -1;
    public float countRotOffset = 0;
    public boolean alternateRot = false;
    public DrawOrbitRegions(String suffix, int regionCount, float radius, float orbitSpeed) {
        this.suffix = suffix;
        this.regionCount = regionCount;
        this.radius = radius;
        this.orbitSpeed = orbitSpeed;
    }

    public DrawOrbitRegions() {
    }

    @Override
    public void draw(Building build) {
        float z = Draw.z();
        if (layer > 0) Draw.z(layer);

        float baseRotation = build.totalProgress() * orbitSpeed + rotationOffset * build.warmup();

        for (int i = 0; i < regionCount; i++) {
            float angle = baseRotation + (360f / regionCount) * i;
            float rad = Mathf.degRad * angle;
            float offset = countRotOffset * i;
            float rotDir = 1;
            if(alternateRot && i % 2 == 0) rotDir = -1;
            float cx = build.x + Mathf.cos(rad) * radius;
            float cy = build.y + Mathf.sin(rad) * radius;
            if(spinSprite) {
                Drawf.spinSprite(region, cx, cy, (build.totalProgress() * rotDir) * rotateSpeed + rotation + (buildingRotate ? build.rotdeg() : 0) + offset);
            } else {
                Draw.rect(region, cx, cy, (build.totalProgress() * rotDir) * rotateSpeed + rotation + (buildingRotate ? build.rotdeg() : 0) + offset);
            }
        }
        Draw.z(z);
    }

    @Override
    public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list) {
        if (!drawPlan) return;
        float originalZ = Draw.z();
        //had some layering issues so i uhhh idk but it works :thumbsup:
        if (layer > 0) {
            Draw.z(layer);
        } else {
            Draw.z(Layer.plans);
        }
        for (int i = 0; i < regionCount; i++) {
            float angle = rotationOffset + (360f / regionCount) * i;
            float rad = Mathf.degRad * angle;
            float cx = plan.drawx() + Mathf.cos(rad) * radius;
            float cy = plan.drawy() + Mathf.sin(rad) * radius;
            if (region != null) {
                Draw.rect(region, cx, cy, rotation);
            }
        }
        Draw.z(originalZ);
    }

    @Override
    public TextureRegion[] icons(Block block) {
        return new TextureRegion[]{region};
    }

    @Override
    public void load(Block block) {
        region = Core.atlas.find(block.name + suffix);
    }
}