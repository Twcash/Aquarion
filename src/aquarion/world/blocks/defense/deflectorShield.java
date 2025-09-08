package aquarion.world.blocks.defense;

import aquarion.world.graphics.Renderer;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.*;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Fx;
import mindustry.entities.Units;
import mindustry.gen.Groups;
import mindustry.graphics.Pal;
import mindustry.world.blocks.defense.ForceProjector;

public class deflectorShield extends ForceProjector {
    public deflectorShield(String name) {
        super(name);
    }

    public class deflectorShieldBuild extends ForceBuild {
        @Override
        public void updateTile() {
            super.updateTile();
        }
        public void deflectBullets(){
            float realRadius = realRadius();

            if(realRadius > 0 && !broken){
                Groups.bullet.intersect(x - realRadius, y - realRadius, realRadius * 2f, realRadius * 2f, b -> {
                    if(b.team != this.team){
                        float dx = b.x - this.x;
                        float dy = b.y - this.y;
                        float distance = Mathf.sqrt(dx*dx + dy*dy) + 0.001f;

                        float nx = dx / distance;
                        float ny = dy / distance;

                        float radialStrength = Mathf.clamp(1f - distance / realRadius, 0f, 0.9f) * 0.1f;

                        float tangentStrength = 0.7f;
                        float tx = -ny * tangentStrength;
                        float ty = nx * tangentStrength;

                        float targetVx = b.vel.x * (1 - radialStrength) + nx * radialStrength + tx;
                        float targetVy = b.vel.y * (1 - radialStrength) + ny * radialStrength + ty;

                        float lerpFactor = 0.121f; // smaller = slower turn
                        b.vel.x = Mathf.lerp(b.vel.x, targetVx, lerpFactor);
                        b.vel.y = Mathf.lerp(b.vel.y, targetVy, lerpFactor);

                        paramEntity = this;
                        paramEffect = absorbEffect;
                    }
                });
            }
        }

            @Override
        public void draw() {
            super.draw();
            drawShield();
        }
        @Override
        public void drawShield() {
            if (!this.broken) {
                float radius = this.realRadius();

                if (radius > 0.001F) {
                    Draw.color(Color.gray.a(0.1f), Color.white, Mathf.clamp(hit));
                    if (Vars.renderer.animateShields) {
                        Draw.z(Renderer.Layer.deflector);
                        Fill.poly(this.x, this.y, sides, radius, shieldRotation);
                    } else {
                        Draw.z(Renderer.Layer.deflector+1);
                        Lines.stroke(1.5F);
                        Draw.alpha(0.09F + Mathf.clamp(0.08F * this.hit));
                        Fill.poly(this.x, this.y, sides, radius, shieldRotation);
                        Draw.alpha(1.0F);
                        Lines.poly(this.x, this.y, sides, radius, shieldRotation);
                        Draw.reset();
                    }
                }
            }

            Draw.reset();
        }
        @Override
        public void write(Writes write) {
            super.write(write);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
        }

        public float radius() {
            // Override to ensure it returns the effective shield radius
            return realRadius();
        }
    }
}