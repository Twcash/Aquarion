package aquarion.world.AI;

import aquarion.world.blocks.neoplasia.GenericNeoplasiaBlock;
import arc.func.Cons;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.Units;
import mindustry.entities.units.AIController;
import mindustry.gen.Unit;
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
    /** Distance in world units from its spawn point a popper must travel before it can deposit. Prevents poppers from instantly self-destructing on a tile right next to the tree. */
    public float minDepositDistance = 60f;
    public float spawnX, spawnY;
    public boolean hasSpawn;
    final float[] steeringSum = new float[5];
    final float[] pushVec = new float[2];
    final Cons<Unit> separationScan = other -> {
        if (other == unit) return;
        float d = unit.dst(other);
        if (d < 0.001f || d >= separationRadius + other.hitSize / 2f) return;
        float w = (1f - d / (separationRadius + other.hitSize / 2f)) * separationForce;
        steeringSum[0] += (unit.x - other.x) / d * w;
        steeringSum[1] += (unit.y - other.y) / d * w;
        steeringSum[2] += other.vel.x;
        steeringSum[3] += other.vel.y;
        steeringSum[4]++;
    };

    public Tile target;
    public float timer = rescanTime;
    public float wanderAngle;
    public float wanderDist;
    public float wanderTimer;
    public float lastX, lastY;
    public float stuckTimer;
    public float pushDisableTimer;
    public boolean hasLastPos;

    @Override
    public void updateMovement() {
        if (unit == null || unit.dead()) return;
        if (!hasSpawn) {
            hasSpawn = true;
            spawnX = unit.x;
            spawnY = unit.y;
        }
        lifetimeTimer += Time.delta;
        if (lifetimeTimer >= lifetime) {
            unit.kill();
            return;
        }
        if (!hasLastPos) {
            hasLastPos = true;
            lastX = unit.x;
            lastY = unit.y;
        }
        float moveDist = Mathf.dst(unit.x - lastX, unit.y - lastY);
        lastX = unit.x;
        lastY = unit.y;
        if (moveDist < 0.15f) {
            stuckTimer += Time.delta;
        } else {
            stuckTimer = 0f;
        }
        if (stuckTimer >= 60f) {
            stuckTimer = 0f;
            pushDisableTimer = 90f;
            target = null;
        }
        if (pushDisableTimer > 0f) pushDisableTimer -= Time.delta;
        timer += Time.delta;
        if (timer >= rescanTime && pushDisableTimer <= 0f) {
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
            steeringSum[0] = 0f;
            steeringSum[1] = 0f;
            steeringSum[2] = 0f;
            steeringSum[3] = 0f;
            steeringSum[4] = 0f;
            Units.nearby(unit.x - separationRadius, unit.y - separationRadius, separationRadius * 2f, separationRadius * 2f, separationScan);
            float sx = steeringSum[0], sy = steeringSum[1], ax = steeringSum[2], ay = steeringSum[3], count = steeringSum[4];
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
            pushVec[0] = 0f;
            pushVec[1] = 0f;
            if (pushDisableTimer <= 0f) obstaclePush(pushVec);
            Tmp.v1.set(unit.x + sx + ax + bx + pushVec[0] + Mathf.cosDeg(wanderAngle) * wanderDist, unit.y + sy + ay + by + pushVec[1] + Mathf.sinDeg(wanderAngle) * wanderDist);
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
        if (pushDisableTimer <= 0f) {
            pushVec[0] = 0f;
            pushVec[1] = 0f;
            obstaclePush(pushVec);
            if (pushVec[0] != 0f || pushVec[1] != 0f) {
                float fade = Mathf.clamp((unit.dst(target) - depositRange) / obstacleRadius, 0f, 1f);
                float toX = target.worldx() - unit.x, toY = target.worldy() - unit.y;
                float toLen = Mathf.len(toX, toY);
                if (toLen > 0.001f) {
                    float dx = toX / toLen, dy = toY / toLen;
                    float side = (-dy * pushVec[0] + dx * pushVec[1]) * fade;
                    vec.add(-dy * side, dx * side).limit(unit.speed());
                    unit.movePref(vec);
                }
            }
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
                if (Mathf.dst(t.worldx() - spawnX, t.worldy() - spawnY) < minDepositDistance) continue;
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
        if (Mathf.dst(tile.worldx() - spawnX, tile.worldy() - spawnY) < minDepositDistance) return false;
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
