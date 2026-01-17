package aquarion.world.entities.parts;

import aquarion.world.graphics.Renderer;
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
    public float baseRot;
    public boolean under = true, turretShading = true;
    public Color color = Color.valueOf("8beded");

    @Override
    public void draw(PartParams params) {
        float prog = progress.getClamp(params, clampProgress);

        Draw.z(Renderer.Layer.flyingUnitLow+layerOffset-1.2f);

        int len = mirror && params.sideOverride == -1 ? 2 : 1;

        for(int s = 0; s < len; s++){
            int i = params.sideOverride == -1 ? s : params.sideOverride;
            float sign = (i == 0 ? 1f : -1f) * params.sideMultiplier;

            Tmp.v1
                    .set((x + moveX * prog) * sign, y + moveY * prog)
                    .rotate(params.rotation-90);

            float rx = params.x + Tmp.v1.x;
            float ry = params.y + Tmp.v1.y;

            float rad = (radius + Mathf.absin(Time.time, 2f, radius / 4f)) * prog;

            Draw.color(color);
            Fill.circle(rx, ry, rad);

            float ang =
                    params.rotation  +
                            (rotation + baseRot + moveRot * prog) * sign;
            Draw.z(Renderer.Layer.flyingUnitLow+layerOffset-1);

            Draw.color(Color.white);
            Fill.circle(
                    rx - Angles.trnsx(ang, rad / 4f),
                    ry - Angles.trnsy(ang, rad / 4f),
                    rad / 2f
            );

            Draw.reset();
        }

        Draw.z();
    }
}
