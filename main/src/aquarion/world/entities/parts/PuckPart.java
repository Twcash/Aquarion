package aquarion.world.entities.parts;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Tmp;
import mindustry.entities.part.DrawPart;

//Useless class basically I only made it so vector wasn't draining my sanity
public class PuckPart extends DrawPart {
    public String suffix = "-puck";
    public float x, y;
    public float drainFeedY = 2f;
    public float recoilFeedY = 2f;
    public float recoilFade = 1f;

    public TextureRegion region;

    public PuckPart(){}

    public PuckPart(String suffix, float x, float y){
        this(suffix, x, y, -1);
    }

    public PuckPart(String suffix, float x, float y, int recoilIndex){
        this.suffix = suffix;
        this.x = x;
        this.y = y;
        this.recoilIndex = recoilIndex;
    }

    @Override
    public void load(String name){
        region = Core.atlas.find(name + suffix, Core.atlas.find(name + "-puck"));
    }

    @Override
    public void getOutlines(Seq<TextureRegion> out){
        if(region != null && region.found()) out.add(region);
    }

    @Override
    public void draw(PartParams params){
        if(region == null || !region.found()) return;

        float z = Draw.z();
        if(under) Draw.z(z - 0.0001f);

        float drain = 1f - Mathf.clamp(params.reload);
        float recoil = Mathf.clamp(params.recoil);
        float feed = drain * drainFeedY + recoil * recoilFeedY;

        Tmp.v1.set(x, y + feed).rotateRadExact((params.rotation - 90) * Mathf.degRad);
        float rx = params.x + Tmp.v1.x, ry = params.y + Tmp.v1.y;
        float rot = params.rotation - 90;
        Draw.alpha(1f - recoil * recoilFade);
        Draw.rect(region, rx, ry, rot);
        Draw.alpha(1f);

        Draw.z(z);
    }
}
