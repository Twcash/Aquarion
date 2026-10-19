package aquarion.world.graphics;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.struct.FloatSeq;
import arc.util.Time;
import mindustry.graphics.Trail;

/** A normal engine trail with an age gradient and a wave that grows towards its tail. */
public class StyledTrail extends Trail{
    public Color tailColor;
    public float tailAlpha = 0f;
    public float waveAmplitude = 0f;
    /** Frames per radian of wave animation. Zero keeps the wave stationary. */
    public float waveScale = 12f;
    /** Number of complete waves along the trail. */
    public float waveCycles = 2f;
    public float wavePhase;

    private final FloatSeq displaced = new FloatSeq();
    private final Color tint = new Color();

    public StyledTrail(int length){
        super(length);
    }

    public float waveOffset(float fraction, float time){
        float age = 1f - Mathf.clamp(fraction);
        return Mathf.sin((waveScale == 0f ? 0f : time / waveScale) + fraction * Mathf.PI2 * waveCycles + wavePhase)
            * waveAmplitude * age;
    }

    public Color colorAt(Color head, float fraction, Color out){
        float f = Mathf.clamp(fraction);
        out.set(tailColor == null ? head : tailColor).lerp(head, f);
        out.a *= Mathf.lerp(Mathf.clamp(tailAlpha), 1f, f);
        return out;
    }

    @Override
    public StyledTrail copy(){
        StyledTrail out = new StyledTrail(length);
        out.points.addAll(points);
        out.lastX = lastX;
        out.lastY = lastY;
        out.lastAngle = lastAngle;
        out.lastW = lastW;
        out.counter = counter;
        out.tailColor = tailColor == null ? null : tailColor.cpy();
        out.tailAlpha = tailAlpha;
        out.waveAmplitude = waveAmplitude;
        out.waveScale = waveScale;
        out.waveCycles = waveCycles;
        out.wavePhase = wavePhase;
        return out;
    }

    @Override
    public void draw(Color color, float width){
        int count = size();
        if(count == 0 || width <= 0f) return;
        displaced.clear();
        for(int i = 0; i <= count; i++){
            float x = i == count ? lastX : points.items[i * 3];
            float y = i == count ? lastY : points.items[i * 3 + 1];
            float w = i == count ? lastW : points.items[i * 3 + 2];
            if(i == 0 && count > 1 && count >= length - 1){
                x = Mathf.lerp(x, points.items[3], counter);
                y = Mathf.lerp(y, points.items[4], counter);
                w = Mathf.lerp(w, points.items[5], counter);
            }
            int previous = Math.max(0, i - 1) * 3;
            int next = (i + 1) * 3;
            float px = points.items[previous], py = points.items[previous + 1];
            float nx = i + 1 >= count ? lastX : points.items[next];
            float ny = i + 1 >= count ? lastY : points.items[next + 1];
            float dx = nx - px, dy = ny - py, distance = Mathf.dst(px, py, nx, ny);
            float offset = waveOffset(i / (float)count, Time.time);
            if(distance > 0.0001f){
                x -= dy / distance * offset;
                y += dx / distance * offset;
            }
            displaced.add(x, y, w);
        }

        Draw.color();
        float previousNx = 0f, previousNy = 0f;
        for(int i = 0; i < count; i++){
            int index = i * 3;
            float x1 = displaced.items[index], y1 = displaced.items[index + 1];
            float x2 = displaced.items[index + 3], y2 = displaced.items[index + 4];
            float dx = x2 - x1, dy = y2 - y1, distance = Mathf.dst(x1, y1, x2, y2);
            if(distance <= 0.0001f) continue;
            float nx = -dy / distance, ny = dx / distance;
            float f1 = i / (float)count, f2 = (i + 1f) / count;
            float w1 = width * f1 * displaced.items[index + 2];
            float w2 = width * f2 * displaced.items[index + 5];
            float sx = i == 0 ? nx : previousNx, sy = i == 0 ? ny : previousNy;
            float c1 = colorAt(color, f1, tint).toFloatBits();
            float c2 = colorAt(color, f2, tint).toFloatBits();
            Fill.quad(x1 - sx * w1, y1 - sy * w1, c1,
                x1 + sx * w1, y1 + sy * w1, c1,
                x2 + nx * w2, y2 + ny * w2, c2,
                x2 - nx * w2, y2 - ny * w2, c2);
            previousNx = nx;
            previousNy = ny;
        }
        Draw.reset();
    }
}
