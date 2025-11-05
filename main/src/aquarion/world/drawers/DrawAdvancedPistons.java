package aquarion.world.drawers;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Eachable;
import arc.util.Tmp;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;
//I only Ever plan on using this for one or two blocks. Ever DO NOT COPY THIS IS USELESS
// I just wanted to be better
public class DrawAdvancedPistons extends DrawBlock {
    public float sinMag = 4f, sinScl = 6f, sinOffset = 50f, sideOffset = 0f, lenOffset = -1f, horiOffset = 0f, angleOffset = 0f;
    public int sides = 4;
    public String suffix = "-piston";
    public TextureRegion region1, region2, regiont, regiont2, iconRegion;

    @Override
    public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list){
        if(iconRegion.found()){
            Draw.rect(iconRegion, plan.drawx(), plan.drawy());
        }
    }

    @Override
    public void draw(Building build) {
        for (int i = 0; i < sides; i++) {

            float progress = Mathf.absin(build.totalProgress() + sinOffset + sideOffset * sinScl * i, sinScl, sinMag) + lenOffset;

            float len = progress * sinMag + lenOffset;
                float angle = angleOffset + i * 360f / sides;
                //TODO find a way to combine t2 and t region into one and flip the sprite without a mess
                TextureRegion reg =
                        regiont2.found() && Mathf.equal(angle, 270) ? regiont2 :
                                regiont.found() && Mathf.equal(angle, 90) ? regiont :
                                        angle >= 90 && angle < 270 ? region2 : region1;

                if (Mathf.equal(angle, 315)) {
                    Draw.yscl = -1f;
                }

                float adjustedLen = len / Mathf.sqrt2;
                Tmp.v1.set(adjustedLen, adjustedLen).rotate(angle);
                Draw.rect(reg, build.x + Tmp.v1.x, build.y + Tmp.v1.y, angle);
        }
    }

    @Override
    public void load(Block block){
        super.load(block);

        region1 = Core.atlas.find(block.name + suffix + "0", block.name + suffix);
        region2 = Core.atlas.find(block.name + suffix + "1", block.name + suffix);
        regiont = Core.atlas.find(block.name + suffix + "-t");
        regiont2 = Core.atlas.find(block.name + suffix + "-t2");
        iconRegion = Core.atlas.find(block.name + suffix + "-icon");
    }

    @Override
    public TextureRegion[] icons(Block block){
        return new TextureRegion[]{iconRegion};
    }
}
