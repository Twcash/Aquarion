package aquarion.world.AI;

import aquarion.world.blocks.neoplasia.GenericNeoplasiaBlock;
import aquarion.world.blocks.neoplasia.NeoplasmHeart;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.func.Cons;
import mindustry.entities.Units;
import mindustry.gen.Unit;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.units.AIController;
import mindustry.world.Tile;
import mindustry.content.Blocks;

import static aquarion.content.AquaBullets.neoplasmGlob;
import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

/** Seeks uncolonized ground and deliberately seeds it with neoplasm globs. */
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
    public float wanderAngle;
    public float wanderTimer;
    public float shootTimer;
    public float scanRadius = 160f, seedRange = 100f, rescanTime = 60f;
    public float scanTimer = 60f, stuckTimer, progressTimer, lastX, lastY;
    public boolean hasLastPos;
    public Tile target;
    public Unit leader;
    public float leaderScanTime = 60f, leaderTimer = 60f;
    public float followDistance = 40f, separationRadius = 28f, flockInterval = 15f;
    float flockTimer = 15f;
    final Vec2 separation = new Vec2();
    final Vec2 alignment = new Vec2();
    int flockCount;
    final Cons<Unit> flockScan = other -> {
        if(other == unit || !(other.controller() instanceof SpewerAI) || other.dead()) return;
        float distance = unit.dst(other);
        if(distance >= separationRadius) return;
        if(distance < 0.01f){
            long pair = Math.min(unit.id, other.id) * 31L + Math.max(unit.id, other.id);
            float angle = Mathf.randomSeed(pair, 0f, 360f) + (unit.id > other.id ? 180f : 0f);
            separation.add(Mathf.cosDeg(angle) * separationRadius, Mathf.sinDeg(angle) * separationRadius);
        }else{
            float weight = (separationRadius - distance) / distance;
            separation.add((unit.x - other.x) * weight, (unit.y - other.y) * weight);
        }
        alignment.add(other.vel);
        flockCount++;
    };

    @Override
    public void updateMovement() {
        if (unit == null || unit.dead()) return;
        if(!hasLastPos){ hasLastPos = true; lastX = unit.x; lastY = unit.y; }
        progressTimer += Time.delta;
        if(progressTimer >= 60f){
            stuckTimer = (target == null || !unit.within(target, seedRange)) && unit.dst(lastX, lastY) < 1f
                ? stuckTimer + progressTimer : 0f;
            progressTimer = 0f;
            lastX = unit.x;
            lastY = unit.y;
            if(stuckTimer >= 120f){
                wanderAngle = unit.angleTo(world.unitWidth() / 2f, world.unitHeight() / 2f) + Mathf.range(45f);
                wanderTimer = 0f;
                target = null;
                leader = null;
                leaderTimer = 0f;
                scanTimer = 0f;
            }
        }
        scanTimer += Time.delta;
        if(target != null && !validSeedTile(target)) target = null;
        if(scanTimer >= rescanTime){
            scanTimer = 0f;
            target = findTarget();
        }
        shootTimer = Math.min(shootInterval, shootTimer + Time.delta);
        updateFlock();
        if(target != null){
            boolean edge = unit.x < boundaryMargin || unit.y < boundaryMargin
                || unit.x > world.unitWidth() - boundaryMargin || unit.y > world.unitHeight() - boundaryMargin;
            boolean escaping = insideColony() && target.block() == Blocks.air;
            float fade = Mathf.clamp(unit.dst(target) / (separationRadius * 2f));
            Tmp.v1.set(target).mulAdd(separation, fade);
            NeoplasiaUnitMovement.clampDestination(Tmp.v1, boundaryMargin);
            moveTo(Tmp.v1, edge || escaping ? 0f : seedRange * 0.6f);
            unit.lookAt(target);
            if(!escaping && unit.within(target, seedRange) && shootTimer >= shootInterval && bullet != null){
                float distance = unit.dst(target);
                var glob = bullet.create(unit, unit.team, unit.x, unit.y, unit.angleTo(target), 1f, 1f);
                if(glob != null) glob.lifetime = Math.max(1f, distance / Math.max(0.001f, bullet.speed));
                shootTimer = 0f;
            }
            return;
        }
        leaderTimer += Time.delta;
        if(!validLeader(leader)) leader = null;
        if(leaderTimer >= leaderScanTime){
            leaderTimer = 0f;
            leader = findLeader();
        }
        if(leader != null){
            float offset = Mathf.randomSeed(unit.id * 43L, 0f, 360f);
            Tmp.v1.set(leader.x + Mathf.cosDeg(offset) * followDistance,
                leader.y + Mathf.sinDeg(offset) * followDistance)
                .add(separation).mulAdd(alignment, 12f);
            NeoplasiaUnitMovement.clampDestination(Tmp.v1, boundaryMargin);
            moveTo(Tmp.v1, 8f);
            unit.lookAt(Tmp.v1);
            return;
        }
        wanderTimer += Time.delta;
        if(wanderTimer >= wanderTime){
            wanderTimer = 0f;
            wanderAngle = Mathf.random(360f);
        }
        Tmp.v1.trns(wanderAngle, wanderRadius).add(unit.x, unit.y).add(separation).mulAdd(alignment, 12f);
        NeoplasiaUnitMovement.clampDestination(Tmp.v1, boundaryMargin);
        if(unit.within(Tmp.v1, tilesize)) Tmp.v1.set(world.unitWidth() / 2f, world.unitHeight() / 2f);
        moveTo(Tmp.v1, 0f);
        unit.lookAt(Tmp.v1);
    }

    boolean insideColony(){
        var build = world.buildWorld(unit.x, unit.y);
        return build != null && build.team == unit.team
            && (build instanceof GenericNeoplasiaBlock.NeoplasiaBuild || build instanceof NeoplasmHeart.NeoplasmHeartBuild);
    }

    boolean validLeader(Unit other){
        return other != null && other != unit && other.isAdded() && !other.dead() && other.team == unit.team
            && other.controller() instanceof SpewerAI ai && ai.target != null && ai.target.block() == Blocks.air && ai.validSeedTile(ai.target);
    }

    Unit findLeader(){
        Unit nearest = null;
        float distance = Float.MAX_VALUE;
        for(Unit other : unit.team.data().units){
            if(!validLeader(other)) continue;
            float candidate = unit.dst2(other);
            if(candidate < distance){ distance = candidate; nearest = other; }
        }
        return nearest;
    }

    void updateFlock(){
        flockTimer += Time.delta;
        if(flockTimer < flockInterval) return;
        flockTimer = 0f;
        separation.setZero();
        alignment.setZero();
        flockCount = 0;
        Units.nearby(unit.team, unit.x - separationRadius, unit.y - separationRadius,
            separationRadius * 2f, separationRadius * 2f, flockScan);
        if(flockCount > 0){
            separation.scl(1f / flockCount).limit(separationRadius);
            alignment.scl(1f / flockCount);
        }
    }

    boolean validSeedTile(Tile tile){
        return tile != null && (tile.block() == Blocks.air && tile.build == null
            || tile.build instanceof GenericNeoplasiaBlock.NeoplasiaBuild tissue
                && tissue.team == unit.team && tissue.amount < tissue.block().maxAmount * 0.8f)
            && !tile.floor().isDeep() && tile.floor().placeableOn
            && tile.worldx() >= Math.min(boundaryMargin, world.unitWidth() / 2f)
            && tile.worldx() <= world.unitWidth() - Math.min(boundaryMargin, world.unitWidth() / 2f)
            && tile.worldy() >= Math.min(boundaryMargin, world.unitHeight() / 2f)
            && tile.worldy() <= world.unitHeight() - Math.min(boundaryMargin, world.unitHeight() / 2f);
    }

    Tile findTarget(){
        Tile best = null;
        float bestScore = Float.MAX_VALUE;
        int bestPriority = -1;
        int radius = Mathf.ceil(scanRadius / tilesize);
        for(int dx = -radius; dx <= radius; dx++) for(int dy = -radius; dy <= radius; dy++){
            Tile tile = world.tile(unit.tileX() + dx, unit.tileY() + dy);
            if(!validSeedTile(tile) || !unit.within(tile, scanRadius) || unit.within(tile, tilesize * 2f)) continue;
            // Different units prefer different sites instead of converging on one tile.
            float score = unit.dst(tile) + Mathf.randomSeed(unit.id * 31L + tile.pos(), 0f, 64f);
            int priority = tile.block() == Blocks.air ? 1 : 0;
            if(priority > bestPriority || priority == bestPriority && score < bestScore){
                bestPriority = priority; bestScore = score; best = tile;
            }
        }
        if(bestPriority == 1) return best;
        // Do not settle for feeding the interior when another spewer already knows a way out.
        Unit guide = findLeader();
        if(guide != null){ leader = guide; leaderTimer = 0f; return null; }
        Tile frontier = SpewerFrontier.closest(this);
        return frontier != null ? frontier : best;
    }

}
