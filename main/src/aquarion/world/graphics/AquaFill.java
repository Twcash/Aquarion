package aquarion.world.graphics;

import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.struct.FloatSeq;

public class AquaFill {

    public static final FloatSeq points = new FloatSeq();

    public static final Vec2 v1 = new Vec2(), v2 = new Vec2(), v3 = new Vec2();

    /**
     * Draws a slice of a circle with center at x and y, a measure (fraction of the circle), a rotation, and a radius to represent its outer curved edge.
     * Due to the use of polygon approximations, side is the number of sides to draw for each curve section.
     * */
    public static void circleSlice(float x, float y,  float measure, float rotation, float radius, int sides){
        arcSlice(x, y, measure, rotation, 0, radius, sides);
    }

    public static void circleSlice(Position center, float measure, float rotation, float radius, int sides){
        arcSlice(center, measure, rotation, 0, radius, sides);
    }

    public static void arcSlice(Position center, float measure, float rotation, float innerRadius, float outerRadius, int sides){
        arcSlice(center.getX(), center.getY(), measure, rotation, innerRadius, outerRadius, sides);
    }

    /**
     * Draws an arc with center at x and y, a measure (fraction of the circle), a rotation, and two radii to represent its inner and outer curved edges.
     * Due to the use of polygon approximations, side is the number of sides to draw for each curve section.
     * */
    public static void arcSlice(float x, float y, float measure, float rotation, float innerRadius, float outerRadius, int sides){
        if (Mathf.zero(outerRadius) || Mathf.zero(measure) || sides == 0) return;
        points.clear();
        float fraction = measure / sides;
        if(Mathf.zero(innerRadius)){
            points.clear();
            points.add(x, y);
            for(int i = 0; i < sides + 1; i++){
                float angle = rotation - (measure / 2) + fraction * i;
                v1.trns(angle, outerRadius).add(x, y);
                points.add(v1.x, v1.y);
            }
            Fill.poly(points);
        } else {
            v2.trns(rotation - (measure / 2), outerRadius).add(x,y);
            v3.trns(rotation - (measure / 2), innerRadius).add(x,y);

            for (int i = 1; i < sides + 1; i++) {
                points.clear();
                points.add(v2.x, v2.y, v3.x, v3.y);
                float angle = rotation - (measure / 2) + fraction * i;
                v1.trns(angle, innerRadius).add(x, y);
                points.add(v1.x, v1.y);
                v3.set(v1);
                v1.trns(angle, outerRadius).add(x, y);
                points.add(v1.x, v1.y);
                v2.set(v1);
                Fill.poly(points);
            }
        }
    }


}
