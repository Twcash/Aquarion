package aquarion.world.blocks.defense;

import aquarion.world.graphics.AquaFx;
import aquarion.world.graphics.Renderer;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.gen.Groups;
import mindustry.world.blocks.defense.ForceProjector;

import java.util.concurrent.atomic.AtomicBoolean;

public class deflectorShield extends ForceProjector {
    public deflectorShield(String name) {
        super(name);
    }

    public class deflectorShieldBuild extends ForceBuild {
        private static final float detectionMultiplier = 2f;

        public float deflectWarmup = 0f;

        @Override
        public void updateTile() {
            super.updateTile();

            deflectBullets();
        }

        public void deflectBullets() {
            float realRadius = realRadius();
            if(realRadius <= 0 || broken) return;

            float detectRadius = realRadius * detectionMultiplier;
            AtomicBoolean bulletNearby = new AtomicBoolean(false);

            Groups.bullet.intersect(x - detectRadius, y - detectRadius, detectRadius * 2f, detectRadius * 2f, b -> {
                if(b.team != this.team){
                    bulletNearby.set(true);
                }
            });

            if(bulletNearby.get() && Mathf.chanceDelta(0.05f)){
                AquaFx.deflectorSteam.at(x, y);
            }
            deflectWarmup = Mathf.lerpDelta(deflectWarmup, bulletNearby.get() ? 1f : 0f, 0.01f);

            if(deflectWarmup < 0.1f) return;

            Groups.bullet.intersect(x - realRadius, y - realRadius, realRadius * 2f, realRadius * 2f, b -> {
                if(b.team != this.team){
                    float dx = b.x - this.x;
                    float dy = b.y - this.y;
                    float distance = Mathf.sqrt(dx * dx + dy * dy) + 0.001f;

                    float nx = dx / distance;
                    float ny = dy / distance;

                    float radialStrength = Mathf.clamp(1f - distance / realRadius, 0f, 0.9f) * 0.1f * deflectWarmup;

                    float cross = dx * b.vel.y - dy * b.vel.x;
                    float tangentDir = Mathf.sign(cross);

                    float tangentStrength = 0.2f * deflectWarmup;
                    float tx = -ny * tangentStrength * tangentDir;
                    float ty = nx * tangentStrength * tangentDir;

                    float targetVx = b.vel.x * (1 - radialStrength) + nx * radialStrength + tx;
                    float targetVy = b.vel.y * (1 - radialStrength) + ny * radialStrength + ty;

                    float lerpFactor = 0.07f;
                    b.vel.x = Mathf.lerp(b.vel.x, targetVx, lerpFactor);
                    b.vel.y = Mathf.lerp(b.vel.y, targetVy, lerpFactor);

                    paramEntity = this;
                    paramEffect = absorbEffect;
                }
            });
        }

        @Override
        public void draw() {
            super.draw();
            drawShield();
        }

        @Override
        public void drawShield() {
            if (broken || deflectWarmup <= 0.01f) return;

            float radius = this.realRadius();
            if (radius <= 0.001F) return;

            Draw.color(Color.gray.a(0.4f * deflectWarmup), Color.white, Mathf.clamp(hit));
            if (Vars.renderer.animateShields) {
                Draw.z(Renderer.Layer.deflector);
                Fill.poly(this.x, this.y, sides, radius, shieldRotation);
            } else {
                Draw.z(Renderer.Layer.deflector + 1);
                Lines.stroke(1.5F);
                Draw.alpha((0.09F + Mathf.clamp(0.08F * this.hit)) * deflectWarmup);
                Fill.poly(this.x, this.y, sides, radius, shieldRotation);
                Draw.alpha(1.0F);
                Lines.poly(this.x, this.y, sides, radius, shieldRotation);
                Draw.reset();
            }

            Draw.reset();
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(deflectWarmup);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            deflectWarmup = read.f();
        }

        public float radius() {
            return realRadius();
        }
    }

}