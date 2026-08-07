package aquarion.world.AI;

import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.units.AIController;
import mindustry.world.Tile;

import static aquarion.content.AquaBullets.neoplasmGlob;
import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

/** Wanders slowly, never depositing or popping, and constantly flings neoplasm glob bullets in random directions to seed new blobs. */
public class SpewerAI extends AIController {
    public BulletType bullet = neoplasmGlob;
    /** Ticks between shots. */
    public float shootInterval = 60;
    /** Distance in world units the spewer drifts while wandering. */
    public float wanderRadius = 500f;
    /** Ticks between wander direction changes. */
    public float wanderTime = 240f;
    /** Distance in world units from the map edge at which boundary avoidance kicks in. */
    public float boundaryMargin = 24f;
    /** Strength of the push-away-from-map-edge steering. */
    public float boundaryForce = 6f;
    /** Radius in world units within which the spewer steers away from natural walls and blob blocks. */
    public float obstacleRadius = 2f;
    /** Strength of the push-away-from-walls/blobs steering. */
    public float obstacleForce = 4f;
    public float wanderAngle;
    public float wanderTimer;
    public float shootTimer;
    final float[] pushVec = new float[2];

    @Override
    public void updateMovement() {
        if (unit == null || unit.dead()) return;
        wanderTimer += Time.delta;
        if (wanderTimer >= wanderTime) {
            wanderTimer = 0f;
            wanderAngle = Mathf.random(360f);
        }
        float bx = 0f, by = 0f;
        if (unit.x < boundaryMargin) {
            bx += (boundaryMargin - unit.x) / boundaryMargin;
        } else if (unit.x > world.unitWidth() - boundaryMargin) {
            bx -= (unit.x - (world.unitWidth() - boundaryMargin)) / boundaryMargin;
        }
        if (unit.y < boundaryMargin) {
            by += (boundaryMargin - unit.y) / boundaryMargin;
        } else if (unit.y > world.unitHeight() - boundaryMargin) {
            by -= (unit.y - (world.unitHeight() - boundaryMargin)) / boundaryMargin;
        }
        bx *= boundaryForce;
        by *= boundaryForce;
        pushVec[0] = 0f;
        pushVec[1] = 0f;
        obstaclePush(pushVec);
        Tmp.v1.set(unit.x + bx + pushVec[0] + Mathf.cosDeg(wanderAngle) * wanderRadius, unit.y + by + pushVec[1] + Mathf.sinDeg(wanderAngle) * wanderRadius);
        moveTo(Tmp.v1, 0f);
        unit.lookAt(Tmp.v1);

        shootTimer += Time.delta;
        if (shootTimer >= shootInterval && bullet != null) {
            shootTimer = 0f;
            bullet.create(unit, unit.team, unit.x, unit.y, Mathf.random(360f), Mathf.range(0.5f), 0.5f);
        }
    }

    /** Fills out with a soft push-away vector from natural walls and own-team buildings near the unit. Enemy structures are not avoided, so spewers can fly over them. */
    void obstaclePush(float[] out) {
        float sx = 0f, sy = 0f;
        int r = (int) (obstacleRadius / tilesize) + 1;
        int ox = unit.tileX(), oy = unit.tileY();
        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -r; dy <= r; dy++) {
                Tile t = world.tile(ox + dx, oy + dy);
                if (t == null) continue;
                boolean solid = (t.build != null && t.build.team == unit.team) || (t.build == null && t.solid());
                if (!solid) continue;
                float cx = (t.x + 0.5f) * tilesize, cy = (t.y + 0.5f) * tilesize;
                float d = unit.dst(cx, cy);
                if (d < 0.001f || d >= obstacleRadius) continue;
                float w = (1f - d / obstacleRadius) * obstacleForce;
                sx += (unit.x - cx) / d * w;
                sy += (unit.y - cy) / d * w;
            }
        }
        out[0] = sx;
        out[1] = sy;
    }
}