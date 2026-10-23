package aquarion.world.entities;

import arc.math.Mathf;
import arc.math.geom.Vec2;
import mindustry.entities.Leg;

/** Per-leg articulated chain. The normal leg gait still controls its planted foot. */
public class SegmentedAquaLeg extends Leg{
    public final Vec2[] points;

    public SegmentedAquaLeg(int segments){
        points = new Vec2[Math.max(1, segments) + 1];
        for(int i = 0; i < points.length; i++) points[i] = new Vec2();
    }

    public void solve(Vec2 root, float length, boolean bendLeft){
        int count = points.length - 1;
        if(count == 1){
            points[0].set(root);
            points[1].set(base);
            return;
        }
        float distance = root.dst(base);
        float section = Math.max(length, distance) / count;
        // Seed a consistent bend, avoiding the straight-chain singularity and knee flips.
        float dx = base.x - root.x, dy = base.y - root.y;
        float normalX = distance > 0.001f ? -dy / distance : 0f;
        float normalY = distance > 0.001f ? dx / distance : 1f;
        float bend = Mathf.sqrt(Math.max(0f, length * length - distance * distance)) * 0.5f;
        float side = bendLeft ? 1f : -1f;
        for(int i = 0; i <= count; i++){
            float t = (float)i / count;
            float offset = Mathf.sin(t * Mathf.PI) * bend * side;
            points[i].set(root.x + dx * t + normalX * offset, root.y + dy * t + normalY * offset);
        }
        for(int iteration = 0; iteration < 24; iteration++){
            points[count].set(base);
            for(int i = count - 1; i >= 0; i--){
                points[i].sub(points[i + 1]).setLength(section).add(points[i + 1]);
            }
            points[0].set(root);
            for(int i = 1; i <= count; i++){
                points[i].sub(points[i - 1]).setLength(section).add(points[i - 1]);
            }
            if(points[count].dst2(base) < 0.000001f) break;
        }
        // Always attach to the planted foot, including stretched/unreachable poses.
        points[count].set(base);
    }
}
