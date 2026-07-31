package aquarion.world.AI;

import aquarion.world.blocks.neoplasia.GenericNeoplasiaBlock;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.Units;
import mindustry.entities.units.AIController;
import mindustry.type.ItemStack;
import mindustry.world.Tile;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class PopperAI extends AIController {
    /** Blob block placed when no instance block is configured. Set by content loaders. */
    public static GenericNeoplasiaBlock defaultBlobBlock;
    /** Items seeded into the placed blob when no instance items are configured. */
    public static ItemStack[] defaultBlobItems;

    /** Blob block to deposit. If null, {@link #defaultBlobBlock} is used. */
    public GenericNeoplasiaBlock blobBlock;
    /** Items deposited along with the blob. If null, {@link #defaultBlobItems} is used. */
    public ItemStack[] blobItems;
    /** Starting amount of the deposited blob. */
    public float blobAmount = 300f;
    /** Radius in world units to search for placement tiles. */
    public float scanRadius = 120f;
    /** Distance in world units at which the popper deposits and pops. */
    public float depositRange = 10f;
    /** Ticks between target rescans. */
    public float rescanTime = 30f;
    /** Radius in world units within which the popper avoids crowding other units while searching. */
    public float separationRadius = 22f;
    /** Strength of boid separation steering while no tile is in range. */
    public float separationForce = 3f;
    /** Strength of boid alignment steering while no tile is in range. */
    public float alignmentForce = 0.8f;
    /** Distance in world units the popper drifts while searching for an empty tile. */
    public float wanderRadius = 80f;
    /** Ticks between search wander direction changes. */
    public float wanderTime = 40f;
    /** Distance in world units from the map edge at which boundary avoidance kicks in. */
    public float boundaryMargin = 24f;
    /** Strength of the push-away-from-map-edge steering while searching. */
    public float boundaryForce = 6f;
    /** Radius in world units within which the popper steers away from natural walls and blob blocks. */
    public float obstacleRadius = 28f;
    /** Strength of the push-away-from-walls/blobs steering. */
    public float obstacleForce = 4f;
    /** Seconds a popper may live before it must deposit; idle poppers despawn so they don't accumulate. */
    public float lifetime = 60f;
    public float lifetimeTimer;

    public Tile target;
    public float timer = rescanTime;
    public float wanderAngle;
    public float wanderDist;
    public float wanderTimer;

    @Override
    public void updateMovement() {
        if (unit == null || unit.dead()) return;
        lifetimeTimer += Time.delta;
        if (lifetimeTimer >= lifetime) {
            unit.kill();
            return;
        }
        timer += Time.delta;
        if (timer >= rescanTime) {
            timer = 0f;
            target = findTarget();
            if (target != null) lifetimeTimer = 0f;
        }
        if (target == null) {
            wanderTimer += Time.delta;
            if (wanderTimer >= wanderTime) {
                wanderTimer = 0f;
                wanderAngle = Mathf.random(360f);
                wanderDist = Mathf.random(wanderRadius * 0.4f, wanderRadius);
            }
            float[] sum = {0f, 0f, 0f, 0f, 0f};
            Units.nearby(unit.x - separationRadius, unit.y - separationRadius, separationRadius * 2f, separationRadius * 2f, other -> {
                if (other == unit) return;
                float d = unit.dst(other);
                if (d < 0.001f || d >= separationRadius + other.hitSize / 2f) return;
                float w = (1f - d / (separationRadius + other.hitSize / 2f)) * separationForce;
                sum[0] += (unit.x - other.x) / d * w;
                sum[1] += (unit.y - other.y) / d * w;
                sum[2] += other.vel.x;
                sum[3] += other.vel.y;
                sum[4]++;
            });
            float sx = sum[0], sy = sum[1], ax = sum[2], ay = sum[3], count = sum[4];
            if (count > 0) {
                ax = ax / count * alignmentForce;
                ay = ay / count * alignmentForce;
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
            float[] op = {0f, 0f};
            obstaclePush(op);
            Tmp.v1.set(unit.x + sx + ax + bx + op[0] + Mathf.cosDeg(wanderAngle) * wanderDist, unit.y + sy + ay + by + op[1] + Mathf.sinDeg(wanderAngle) * wanderDist);
            moveTo(Tmp.v1, 0f);
            unit.lookAt(Tmp.v1);
            return;
        }
        if (unit.within(target.worldx(), target.worldy(), depositRange)) {
            if (!deposit(target)) {
                target = findTarget();
            }
            return;
        }
        moveTo(target, depositRange * 0.5f);
        float[] op = {0f, 0f};
        obstaclePush(op);
        if (op[0] != 0f || op[1] != 0f) {
            float fade = Mathf.clamp((unit.dst(target) - depositRange) / obstacleRadius, 0f, 1f);
            vec.add(op[0] * fade, op[1] * fade).limit(unit.speed());
            unit.movePref(vec);
        }
        unit.lookAt(target);
    }

    /** Fills out with a soft push-away vector from natural walls and own-team buildings near the unit. Enemy structures are not avoided, so poppers can fly over them. */
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

    Tile findTarget() {
        int tr = (int) (scanRadius / tilesize);
        Tile bestOre = null, bestEmpty = null;
        float bestOreDst = Float.MAX_VALUE, bestEmptyDst = Float.MAX_VALUE;
        for (int dx = -tr; dx <= tr; dx++) {
            for (int dy = -tr; dy <= tr; dy++) {
                float dst = Mathf.dst(dx, dy);
                if (dst > tr) continue;
                Tile t = world.tile(unit.tileX() + dx, unit.tileY() + dy);
                if (t == null || t.solid() || t.floor().isDeep()) continue;
                if (t.build != null) continue;
                float d = unit.dst(t.worldx(), t.worldy());
                if (t.overlay() != null && t.overlay().itemDrop != null) {
                    if (d < bestOreDst) {
                        bestOreDst = d;
                        bestOre = t;
                    }
                } else if (d < bestEmptyDst) {
                    bestEmptyDst = d;
                    bestEmpty = t;
                }
            }
        }
        return bestOre != null ? bestOre : bestEmpty;
    }

    boolean deposit(Tile tile) {
        if (tile == null || tile.build != null) return false;
        GenericNeoplasiaBlock block = blobBlock != null ? blobBlock : defaultBlobBlock;
        if (block == null) return false;
        tile.setBlock(block, unit.team);
        if (tile.build instanceof GenericNeoplasiaBlock.NeoplasiaBuild nb) {
            nb.amount = Math.min(nb.block().maxAmount, blobAmount);
            ItemStack[] stacks = blobItems != null ? blobItems : defaultBlobItems;
            if (stacks != null) {
                for (ItemStack stack : stacks) {
                    if (nb.items.has(stack.item, stack.amount)) continue;
                    nb.items.add(stack.item, stack.amount);
                }
            }
        }
        unit.kill();
        return true;
    }
}
