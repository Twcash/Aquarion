package aquarion.world.entities.parts;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.*;
import mindustry.entities.Effect;
import mindustry.entities.Lightning;
import mindustry.entities.part.DrawPart;
import mindustry.game.Team;
//Draws lightning from the origin to a target.
public class LightningPart extends DrawPart {
    public float x, y;
    public float moveX, moveY;
    public float interval = 12f;
    public int segments = 6;
    public float length = 60f;
    public float segmentJitter = 3f;
    public float spread = 15f;

    public Color color = Color.white;
    public float layerOffset = 0f;
    public boolean clampProgress = true;
    public boolean mirror = false;
    public float stroke = 3f;

    public float targetX = 0f, targetY = 0f;
    public boolean useRotation = false;

    public PartProgress progress = PartProgress.warmup;

    private float timer = 0f;

    @Override
    public void draw(PartParams params){
        float z = Draw.z();
        Draw.z(z + layerOffset);

        timer += Time.delta;
        if(timer < interval){
            Draw.z(z);
            return;
        }
        timer = 0f;

        float prog = progress.getClamp(params, clampProgress);

        int sides = mirror && params.sideOverride == -1 ? 2 : 1;
        for(int s = 0; s < sides; s++){
            int i = params.sideOverride == -1 ? s : params.sideOverride;
            float sign = (i == 0 ? 1f : -1f) * params.sideMultiplier;

            // Compute origin with mirroring and progress
            Tmp.v1.set((x + moveX * prog) * sign, (y + moveY * prog)).rotate(params.rotation - 90f);
            float rx = params.x + Tmp.v1.x;
            float ry = params.y + Tmp.v1.y;

            // Determine direction
            float angle = useRotation ?
                    (params.rotation + params.rotation * sign + Mathf.range(spread)) :
                    (Angles.angle(rx, ry, targetX, targetY) + Mathf.range(spread));

            // Build lightning line
            Seq<Vec2> points = new Seq<>();
            float segLen = length / (float)segments;
            float px = rx, py = ry;

            for(int j = 0; j < segments; j++){
                float nx = px + Angles.trnsx(angle, segLen) + Mathf.range(segmentJitter);
                float ny = py + Angles.trnsy(angle, segLen) + Mathf.range(segmentJitter);
                points.add(new Vec2(px, py));
                px = nx;
                py = ny;
            }
            points.add(new Vec2(px, py));
            lightningVisual.at(rx, ry, angle, color, points);
        }

        Draw.z(z);
    }
    // identical visual to Fx.lightning
    public static final Effect lightningVisual = new Effect(10f, 500f, e -> {
        if(!(e.data instanceof Seq<?> seq)) return;

        @SuppressWarnings("unchecked")
        Seq<Vec2> lines = (Seq<Vec2>)seq;

        Lines.stroke(3f * e.fout());
        Draw.color(e.color, Color.white, e.fin());

        for(int i = 0; i < lines.size - 1; i++){
            Vec2 a = lines.get(i), b = lines.get(i + 1);
            Lines.line(a.x, a.y, b.x, b.y, false);
        }

        for(Vec2 p : lines){
            Fill.circle(p.x, p.y, Lines.getStroke() / 2f);
        }

        Draw.reset();
    });

    @Override
    public void load(String name){
        // no-op
    }
}