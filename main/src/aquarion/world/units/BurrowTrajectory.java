package aquarion.world.units;

import arc.math.Mathf;
import arc.math.geom.Vec2;

/** One amphibious-style sine arc, sampled by distance so the worm stays connected. */
public class BurrowTrajectory{
    private static final int samples = 64;
    public final float startX, startY, endX, endY, height, groundLength, length;
    private final float[] distances = new float[samples + 1];
    private final float directionX, directionY;

    public BurrowTrajectory(float startX, float startY, float endX, float endY, float height){
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.height = height;
        groundLength = Math.max(0.001f, Mathf.dst(startX, startY, endX, endY));
        directionX = (endX - startX) / groundLength;
        directionY = (endY - startY) / groundLength;
        float previousHeight = 0f;
        for(int i = 1; i <= samples; i++){
            float nextHeight = Mathf.sin(i / (float)samples * Mathf.PI) * height;
            distances[i] = distances[i - 1] + Mathf.len(groundLength / samples, nextHeight - previousHeight);
            previousHeight = nextHeight;
        }
        length = distances[samples];
    }

    public boolean matches(float sx, float sy, float ex, float ey, float h){
        return startX == sx && startY == sy && endX == ex && endY == ey && height == h;
    }

    public float progress(float distance){
        if(distance <= 0f) return distance / groundLength;
        if(distance >= length) return 1f + (distance - length) / groundLength;
        int low = 0, high = samples;
        while(high - low > 1){
            int mid = (low + high) >>> 1;
            if(distances[mid] <= distance) low = mid;
            else high = mid;
        }
        return (low + (distance - distances[low]) / (distances[high] - distances[low])) / samples;
    }

    public float distanceAt(float progress){
        if(progress <= 0f) return 0f;
        if(progress >= 1f) return length * progress;
        float index = progress * samples;
        int low = Mathf.floor(index);
        return Mathf.lerp(distances[low], distances[low + 1], index - low);
    }

    public float heightAt(float distance){
        if(distance <= 0f || distance >= length) return 0f;
        return Mathf.sin(progress(distance) * Mathf.PI) * height;
    }

    public Vec2 groundAt(float distance, Vec2 out){
        float groundDistance = progress(distance) * groundLength;
        return out.set(startX + directionX * groundDistance, startY + directionY * groundDistance);
    }

}
