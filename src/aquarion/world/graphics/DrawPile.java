package aquarion.world.graphics;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;
public class DrawPile extends DrawBlock {
    public String suffix = "-pile";
    public float x, y, rotation = 0;
    //How far the pile will move before resetting.
    public float moveX, moveY = 0;
    public float speed = 0.01f;
    public Effect despawnEffect = Fx.mine;
    public float layer = -1f;
    public TextureRegion region;
    @Override
    public void draw(Building build){
        float z = Draw.z();
        Draw.alpha(build.totalProgress()/5);
        float progress = build.progress();
        if(layer > 0) Draw.z(layer);
        float px = Mathf.lerp(build.x + x, build.x +moveX+x, progress);
        float py = Mathf.lerp(build.y + y, build.y + moveY+y, progress);
        Draw.rect(region, px, py, rotation);
        if(progress >= 1){
            despawnEffect.at(build.x +moveX + x, build.y +moveY+y);
        }
        Draw.z(z);
    }
    @Override
    public void load(Block block){
            region = Core.atlas.find(block.name + suffix);
    }
}
