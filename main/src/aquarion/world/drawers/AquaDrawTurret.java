package aquarion.world.drawers;

import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import mindustry.entities.part.DrawPart;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.draw.DrawTurret;

public class AquaDrawTurret extends DrawTurret {
    @Override
    public void draw(Building build){
        Turret turret = (Turret)build.block;
        Turret.TurretBuild tb = (Turret.TurretBuild)build;

        Draw.rect(base, build.x, build.y);
        Draw.color();

        Draw.z(shadowLayer);

        Drawf.shadow(preview, build.x + tb.recoilOffset.x - turret.elevation, build.y + tb.recoilOffset.y - turret.elevation, tb.drawrot());

        Draw.z(turretLayer);

        drawTurret(turret, tb);
        drawHeat(turret, tb);

        if(parts.size > 0){
            if(outline.found()){
                //draw outline under everything when parts are involved
                Draw.z(turretLayer - 0.01f);
                Draw.rect(outline, build.x + tb.recoilOffset.x, build.y + tb.recoilOffset.y, tb.drawrot());
                Draw.z(turretLayer);
            }

            float progress = tb.progress();

            var params = DrawPart.params.set(
                    build.warmup(),
                    1f - progress,
                    1f - progress,
                    tb.heat,
                    tb.curRecoil,
                    tb.charge,
                    tb.x + tb.recoilOffset.x,
                    tb.y + tb.recoilOffset.y,
                    tb.rotation
            );
                float curShots = (float) tb.totalAmmo / turret.ammoPerShot;
                params.life = Mathf.clamp(curShots);
            for(var part : parts){
                params.setRecoil(part.recoilIndex >= 0 && tb.curRecoils != null
                        ? tb.curRecoils[part.recoilIndex]
                        : tb.curRecoil
                );

                part.draw(params);
            }
        }
    }
}
