package aquarion.world.entities;


import aquarion.world.blocks.power.LightningRod;
import aquarion.world.graphics.AquaFx;
import aquarion.world.graphics.Renderer;
import arc.graphics.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.entities.Units;
import mindustry.entities.bullet.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.world.*;

import static mindustry.Vars.*;

public class AquaLightning {
    private static final Rand random = new Rand();
    private static final Rect rect = new Rect();
    private static final Seq<Unit> entities = new Seq<>();
    private static final Seq<Building> buildings = new Seq<>();
    private static final IntSet hit = new IntSet();
    private static final IntSet hitBuildings = new IntSet();
    private static final int maxChain = 8;
    private static final float hitRange = 30f;
    private static boolean bhit = false;
    private static boolean chainStopped = false;
    private static int lastSeed = 0;

    private static final float ANGLE_RANGE = 30f;

    private static final float BRANCH_CHANCE = 0.3f;

    public static void create(Team team, Color color, float damage, float x, float y, float targetAngle, int length){
        createLightningInternal(null, lastSeed++, team, color, damage, x, y, targetAngle, length);
    }

    public static void create(Bullet bullet, Color color, float damage, float x, float y, float targetAngle, int length){
        createLightningInternal(bullet, lastSeed++, bullet.team, color, damage, x, y, targetAngle, length);
    }

    private static void createLightningInternal(@Nullable Bullet hitter, int seed, Team team, Color color, float damage, float x, float y, float rotation, int length){
        random.setSeed(seed);
        hit.clear();
        hitBuildings.clear();
        chainStopped = false;

        BulletType hitCreate = hitter == null || hitter.type.lightningType == null ? Bullets.damageLightning : hitter.type.lightningType;
        // The damage bullets use normal shield interception rather than bespoke shield geometry checks.
        hitCreate.absorbable = true;
        Seq<Vec2> mainLines = new Seq<>();
        bhit = false;

        createBranch(hitter, hitCreate, team, color, damage, x, y, rotation, length, mainLines);
        Renderer.requestGlitchPass();
        AquaFx.lightning.at(x, y, rotation, color, mainLines);
    }

    /** Recursive branch creation */
    private static void createBranch(@Nullable Bullet hitter, BulletType hitCreate, Team team, Color color, float damage,
                                     float x, float y, float rotation, int length, Seq<Vec2> lines) {
        for (int i = 0; i < length / 2; i++) {
            if(chainStopped) break;

            // Lightning rods attract the strike and terminate all remaining chain links.
            LightningRod.LightningRodBuild rod = LightningRod.find(x, y);
            if(rod != null){
                lines.add(new Vec2(rod.x, rod.y));
                rod.capture();
                chainStopped = true;
                break;
            }

            float startX = x, startY = y;
            Vec2 nextPoint = new Vec2(x + random.range(3f), y + random.range(3f));
            lines.add(nextPoint);

            // Check collision with insulated tiles
            if (lines.size > 1) {
                bhit = false;
                Vec2 from = lines.get(lines.size - 2);
                Vec2 to = lines.get(lines.size - 1);
                World.raycastEach(World.toTile(from.x), World.toTile(from.y), World.toTile(to.x), World.toTile(to.y), (wx, wy) -> {
                    Tile tile = world.tile(wx, wy);
                    if (tile != null && tile.build != null && tile.build.isInsulated() && tile.team() != team) {
                        bhit = true;
                        lines.get(lines.size - 1).set(wx * tilesize, wy * tilesize);
                        return true;
                    }
                    return false;
                });
                if (bhit) break;
            }

            rect.setSize(hitRange).setCenter(x, y);
            entities.clear();
            buildings.clear();
            if (hit.size + hitBuildings.size < maxChain) {
                Units.nearbyEnemies(team, rect, u -> {
                    if (!hit.contains(u.id())) {
                        entities.add(u);
                    }
                });
                Units.nearbyBuildings(x, y, hitRange, b -> {
                    if(b.team != team && b.block.targetable && !b.block.privileged && !hitBuildings.contains(b.pos())){
                        buildings.add(b);
                    }
                });
            }

            Unit furthest = Geometry.findFurthest(x, y, entities);
            Building furthestBuilding = Geometry.findFurthest(x, y, buildings);
            boolean targetBuilding = furthestBuilding != null && (furthest == null || furthestBuilding.dst2(x, y) > furthest.dst2(x, y));

            if(targetBuilding){
                hitBuildings.add(furthestBuilding.pos());
                x = furthestBuilding.x;
                y = furthestBuilding.y;
                if(furthestBuilding instanceof LightningRod.LightningRodBuild lightningRod){
                    lightningRod.capture();
                    chainStopped = true;
                    break;
                }
            }else if(furthest != null){
                hit.add(furthest.id());
                x = furthest.x();
                y = furthest.y();
            } else {
                // Randomize rotation within a range
                rotation += random.range(ANGLE_RANGE);
                x += Angles.trnsx(rotation, hitRange / 2f);
                y += Angles.trnsy(rotation, hitRange / 2f);
            }

            // Use moving, absorbable projectiles so shields can intercept damage normally.
            // Defer creation one tick so the shield scan runs before the bolt can hit its target.
            if(!net.client()){
                float distance = Mathf.dst(startX, startY, x, y);
                float hitDamage = damage * (hitter == null ? 1f : hitter.damageMultiplier());
                float targetAngle = Angles.angle(startX, startY, x, y);
                float targetX = x, targetY = y;
                Time.run(1f, () -> {
                    Bullet damageBullet = hitCreate.create(null, team, startX, startY, targetAngle, hitDamage, 1f, 1f, hitter);
                    if(damageBullet != null && distance > 0.001f){
                        damageBullet.vel.set(targetX - startX, targetY - startY).setLength(Mathf.clamp(distance / 2f, 1f, 8f));
                    }
                });
            }

            // Chance to create a branch
            if (random.chance(BRANCH_CHANCE)) {
                float branchRot = rotation + random.range(ANGLE_RANGE);
                Seq<Vec2> branchLines = new Seq<>();
                createBranch(hitter, hitCreate, team, color, damage, x, y, branchRot, length / 2, branchLines);
                lines.addAll(branchLines);
            }
        }
    }

}
