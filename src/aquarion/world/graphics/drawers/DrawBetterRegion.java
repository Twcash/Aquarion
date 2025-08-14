package aquarion.world.graphics.drawers;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.world.Block;
import mindustry.world.draw.DrawRegion;

public class DrawBetterRegion extends DrawRegion {
    public TextureRegion region;
    public String suffix = "";
    public boolean spinSprite = false;
    public boolean drawPlan = false;
    public boolean drawIcon = false;

    public boolean buildingRotate = false;
    public float rotateSpeed, x, y, rotation;
    /** Any number <=0 disables layer changes. */
    public float layer = -1;

    public DrawBetterRegion(String suffix){
        this.suffix = suffix;
    }

    public DrawBetterRegion(String suffix, float rotateSpeed){
        this.suffix = suffix;
        this.rotateSpeed = rotateSpeed;
    }

    public DrawBetterRegion(String suffix, float rotateSpeed, boolean spinSprite){
        this.suffix = suffix;
        this.spinSprite = spinSprite;
        this.rotateSpeed = rotateSpeed;
    }

    public DrawBetterRegion(){
    }

    @Override
    public void draw(Building build){
        float z = Draw.z();
        if(layer > 0) Draw.z(layer);
        if(spinSprite){
            Drawf.spinSprite(region, build.x + x, build.y + y, build.totalProgress() * rotateSpeed + rotation + (buildingRotate ? build.rotdeg() : 0));
        }else{
            Draw.rect(region, build.x + x, build.y + y, build.totalProgress() * rotateSpeed + rotation + (buildingRotate ? build.rotdeg() : 0));
        }
        Draw.z(z);
    }

    @Override
    public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list){
        if(!drawPlan) return;
        if(spinSprite){
            Drawf.spinSprite(region, plan.drawx() + x, plan.drawy() + y, (buildingRotate ? plan.rotation * 90f : 0 + rotation));
        }else{
            Draw.rect(region, plan.drawx()+ x, plan.drawy() + y, (buildingRotate ? plan.rotation * 90f : 0 + rotation));
        }
    }

    @Override
    public TextureRegion[] icons(Block block){
        if(!drawIcon) return new TextureRegion[0];
        return new TextureRegion[]{region};
    }

    @Override
    public void load(Block block){
        region = Core.atlas.find(block.name + suffix);
    }
}