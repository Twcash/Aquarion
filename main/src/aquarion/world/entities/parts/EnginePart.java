package aquarion.world.entities.parts;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Log;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.part.RegionPart;

public class EnginePart extends RegionPart {
    public float radius = 4f;
    public Color color = Color.valueOf("8beded");
    @Override
    public void draw(PartParams params){
        float z = Draw.z();
        if(layer > 0) Draw.z(layer);
        if(under && turretShading) Draw.z(z - 0.0001f);

        Draw.z(Draw.z() + layerOffset);

        int i = params.sideOverride;
        float prog = progress.getClamp(params, clampProgress);

        float sign = (i == 0 ? 1 : -1) * params.sideMultiplier;

        Tmp.v1.set(params.x * sign, params.y).rotate(params.rotation - 90);
        float rx = params.x+ Tmp.v1.x;
        float ry = params.y + Tmp.v1.y;

        float rad = radius + Mathf.absin(Time.time, 2f, radius / 4f);

        Draw.color(color);
        Draw.alpha(prog);
        Fill.circle(rx, ry, rad * prog);

        float angle = params.rotation - 90 + rotation;
        float ox = Angles.trnsx(angle, rad / 4f);
        float oy = Angles.trnsy(angle, rad / 4f);

        ox *= sign;

        Draw.color(Color.white);
        Fill.circle(rx + ox, ry + oy, rad/2f * prog);

        Draw.reset();
    }
}
