package aquarion.world.entities;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Intersector;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Teams;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import net.jpountz.util.Utils;


public class MeltingLaserBulletType extends BulletType {

    // Beam appearance
    public Color[] colors = {Pal.lancerLaser.cpy().a(0.3f), Pal.lancerLaser, Color.white};
    public float length = 400f;
    public float width = 18f;
    public float lengthFalloff = 0.7f;

    // Status and environmental effects
    public float slagSpacing = 8f;
    public float statusDuration = 120f;

    public MeltingLaserBulletType(float speed, float lifetime){
        super(speed, 0f);
        this.lifetime = lifetime;

        collides = false;
        hittable = false;
        pierce = true;
        absorbable = false;
        removeAfterPierce = false;
        keepVelocity = false;
        impact = false;

        hitEffect = Fx.none;
        despawnEffect = Fx.none;
    }

    public MeltingLaserBulletType(){
        this(0f, 60f);
    }

    @Override
    public void update(Bullet b){
        float bx = b.x;
        float by = b.y;
        float rot = b.rotation();

        Tmp.v1.trns(rot, length).add(bx, by);
        float ex = Tmp.v1.x;
        float ey = Tmp.v1.y;

        // Number of samples along the beam
        int samples = (int)(length / slagSpacing);

        for(int i = 0; i <= samples; i++){
            float dx = Angles.trnsx(rot, i * slagSpacing);
            float dy = Angles.trnsy(rot, i * slagSpacing);
            float px = bx + dx;
            float py = by + dy;

            if(Vars.state != null){
                for(Teams.TeamData data : Vars.state.teams.present){
                    if(data.team == b.team) continue;

                }
            }
        }
    }

    /** Draw the beam and white silhouettes efficiently */
    @Override
    public void draw(Bullet b){
        float bx = b.x;
        float by = b.y;
        float rot = b.rotation();
        float halfWidth = width / 2f;

        // End of laser
        Tmp.v1.trns(rot, length).add(bx, by);
        float ex = Tmp.v1.x;
        float ey = Tmp.v1.y;

        // White silhouettes for units and buildings
        if(Vars.state != null){
            for(Teams.TeamData data : Vars.state.teams.present){
                if(data.team == b.team) continue;

                // --- Units ---
                if(data.unitTree != null){
                    Rect beamRect = new Rect(bx, by - halfWidth, length, width);
                    Seq<Unit> candidates = new Seq<>();
                    data.unitTree.intersect(beamRect, candidates);

                    Vec2 start = Tmp.v2.set(bx, by);
                    Vec2 end = Tmp.v3.trns(rot, length).add(bx, by);

                    for(Unit u : candidates){
                        if(u.dead()) continue;

                        if(Intersector.intersectSegmentCircle(start, end, Tmp.v4.set(u.x, u.y), u.hitSize / 2f)){
                            Draw.color(Color.white, 0.7f * Mathf.clamp(1f - b.dst(u)/length));
                            Draw.rect(u.type.fullIcon, u.x, u.y);
                        }
                    }
                }

                // --- Buildings ---
                if(data.buildingTree != null){
                    Rect beamRect = new Rect(bx, by - halfWidth, length, width);
                    Seq<Building> candidates = new Seq<>();
                    data.buildingTree.intersect(beamRect, candidates);

                    Vec2 start = Tmp.v2.set(bx, by);
                    Vec2 end = Tmp.v3.trns(rot, length).add(bx, by);

                    for(Building bl : candidates){
                        float hw = bl.block.size * 4f / 2f;
                        float hh = bl.block.size * 4f / 2f;

                        if(Intersector.intersectSegmentRectangle(start.x, start.y, end.x, end.y, bl.x - hw, bl.y - hh, bl.x + hw, bl.y + hh)){
                            Draw.color(Color.white, 0.5f * Mathf.clamp(1f - b.dst(bl)/length));
                            Draw.rect(bl.block.region, bl.x, bl.y);
                        }
                    }
                }
            }
        }

        float cwidth = width;
        for(Color color : colors){
            Draw.color(color);
            Lines.stroke(cwidth * Interp.pow2In.apply(b.fin()));
            Lines.lineAngle(bx, by, rot, length * Interp.pow2In.apply(b.fin()));
            cwidth *= lengthFalloff;
        }

        // Beam light
        Drawf.light(bx, by, ex, ey, width * 2f, colors[0], 0.5f);

        Draw.reset();
    }


    @Override
    public void drawLight(Bullet b){
        // optionally add extra glow if needed
    }
}
