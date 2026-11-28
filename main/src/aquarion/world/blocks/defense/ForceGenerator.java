package aquarion.world.blocks.defense;

import aquarion.world.graphics.Renderer;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Intersector;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.blocks.defense.ForceProjector;

import static mindustry.Vars.renderer;
import static mindustry.Vars.tilesize;

public class ForceGenerator extends ForceProjector {
    public ForceGenerator(String name) {
        super(name);
        buildType = ForceGeneratorBuild::new;
    }
    public float rotateBoostAmount = 0;

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        Draw.color(Pal.gray);
        Lines.stroke(3f);
        Lines.poly(x * tilesize + offset, y * tilesize + offset, sides, radius + phaseRadiusBoost, shieldRotation + rotateBoostAmount);
        Draw.color(Pal.accentBack);
        Lines.stroke(1f);
        Lines.poly(x * tilesize + offset, y * tilesize + offset, sides, radius + phaseRadiusBoost, shieldRotation + rotateBoostAmount);
        Draw.color();
    }

    public class ForceGeneratorBuild extends ForceBuild{
        @Override
        public void drawShield(){
            if(!broken){
                float radius = realRadius();
                float rotation = shieldRotation + (rotateBoostAmount * phaseHeat);

                if(radius > 0.001f){
                    if(renderer.animateShields){
                        Renderer.requestForceGeneratorPass();
                        Draw.z(Renderer.Layer.forceGenerator);
                        Draw.color(team.color, Color.white, Mathf.clamp(hit));
                        Fill.poly(x, y, sides, radius, rotation);
                    }else{
                        Draw.color(team.color, Color.white, Mathf.clamp(hit));

                        Draw.z(Layer.shields);
                        Lines.stroke(1.5f);
                        Draw.alpha(0.09f + Mathf.clamp(0.08f * hit));

                        Fill.poly(x, y, sides, radius, rotation);
                        Draw.alpha(1f);
                        Lines.poly(x, y, sides, radius, rotation);
                        Draw.reset();
                    }
                }
            }

            Draw.reset();
        }
        @Override
        public boolean absorbExplosion(float ex, float ey, float damage){
            boolean absorb = !broken && Intersector.isInRegularPolygon(sides, x, y, realRadius(), shieldRotation + (rotateBoostAmount * phaseHeat), ex, ey);
            if(absorb){
                absorbEffect.at(ex, ey);
                hit = 1f;
                buildup += damage * crashDamageMultiplier;
            }
            return absorb;
        }
    }
}
