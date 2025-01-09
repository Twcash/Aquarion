package aquarion.world.graphics;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.CacheLayer;
import mindustry.graphics.Drawf;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;

public class DrawShaderRegion extends DrawBlock {
    public TextureRegion region;
    public String suffix = "";
    public boolean spinSprite = false;
    public boolean drawPlan = true;
    public boolean buildingRotate = false;
    public float rotateSpeed, x, y, rotation;
    /** Any number <=0 disables layer changes. */
    public float layer = -1;
    public CacheLayer cacheLayer; // New cacheLayer field

    public DrawShaderRegion(String suffix) {
        this.suffix = suffix;
    }

    public DrawShaderRegion(String suffix, float rotateSpeed) {
        this.suffix = suffix;
        this.rotateSpeed = rotateSpeed;
    }

    public DrawShaderRegion(String suffix, float rotateSpeed, boolean spinSprite) {
        this.suffix = suffix;
        this.spinSprite = spinSprite;
        this.rotateSpeed = rotateSpeed;
    }

    public DrawShaderRegion() {
    }

    // Setter for cacheLayer
    public void setCacheLayer(CacheLayer cacheLayer) {
        this.cacheLayer = cacheLayer;
    }

    @Override
    public void draw(Building build) {
        if (cacheLayer != null && build.floor().cacheLayer != cacheLayer) {
            return; // Skip drawing if the cacheLayer doesn't match
        }

        float z = Draw.z();
        if (layer > 0) Draw.z(layer);
        if (spinSprite) {
            Drawf.spinSprite(region, build.x + x, build.y + y, build.totalProgress() * rotateSpeed + rotation + (buildingRotate ? build.rotdeg() : 0));
        } else {
            Draw.rect(region, build.x + x, build.y + y, build.totalProgress() * rotateSpeed + rotation + (buildingRotate ? build.rotdeg() : 0));
        }
        Draw.z(z);
    }

    @Override
    public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list) {
        if (!drawPlan) return;
        Draw.rect(region, plan.drawx(), plan.drawy(), (buildingRotate ? plan.rotation * 90f : 0));
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