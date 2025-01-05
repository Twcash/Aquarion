package aquarion.world.graphics;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.util.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.draw.DrawBlock;

public class DrawOrbitRegions extends DrawBlock {
    public TextureRegion region;
    public String suffix = "";
    public boolean drawPlan = true;
    public int regionCount = 1;
    public float radius = 8f;
    public float rotateSpeed = 1f;
    public float rotationOffset = 0f;
    public float layer = -1;
//the one thing that works. I am useless
    public DrawOrbitRegions(String suffix, int regionCount, float radius, float rotateSpeed) {
        this.suffix = suffix;
        this.regionCount = regionCount;
        this.radius = radius;
        this.rotateSpeed = rotateSpeed;
    }

    public DrawOrbitRegions() {
    }

    @Override
    public void draw(Building build) {
        float z = Draw.z();
        if (layer > 0) Draw.z(layer);

        float baseRotation = build.totalProgress() * rotateSpeed + rotationOffset * build.warmup();

        for (int i = 0; i < regionCount; i++) {
            float angle = baseRotation + (360f / regionCount) * i;
            float rad = Mathf.degRad * angle;

            float cx = build.x + Mathf.cos(rad) * radius;
            float cy = build.y + Mathf.sin(rad) * radius;

            Draw.rect(region, cx, cy, 0); // Draw the orbiting region
        }

        Draw.z(z);
    }

    @Override
    public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list) {
        if (!drawPlan) return;

        for (int i = 0; i < regionCount; i++) {
            float angle = rotationOffset + (360f / regionCount) * i;
            float rad = Mathf.degRad * angle;

            float cx = plan.drawx() + Mathf.cos(rad) * radius;
            float cy = plan.drawy() + Mathf.sin(rad) * radius;

            Draw.rect(region, cx, cy, 0); // Draw the orbiting regions in the plan
        }
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