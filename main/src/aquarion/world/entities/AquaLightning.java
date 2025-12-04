package aquarion.world.entities;


import aquarion.world.graphics.AquaFx;
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
    private static final IntSet hit = new IntSet();
    private static final int maxChain = 8;
    private static final float hitRange = 30f;
    private static boolean bhit = false;
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

        BulletType hitCreate = hitter == null || hitter.type.lightningType == null ? Bullets.damageLightning : hitter.type.lightningType;
        Seq<Vec2> mainLines = new Seq<>();
        bhit = false;

        createBranch(hitter, hitCreate, team, color, damage, x, y, rotation, length, mainLines);
        AquaFx.lightning.at(x, y, rotation, color, mainLines);
    }

    /** Recursive branch creation */
    private static void createBranch(@Nullable Bullet hitter, BulletType hitCreate, Team team, Color color, float damage,
                                     float x, float y, float rotation, int length, Seq<Vec2> lines) {
        for (int i = 0; i < length / 2; i++) {
            hitCreate.create(null, team, x, y, rotation, damage * (hitter == null ? 1f : hitter.damageMultiplier()), 1f, 1f, hitter);
            Vec2 nextPoint = new Vec2(x + Mathf.range(3f), y + Mathf.range(3f));
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
            if (hit.size < maxChain) {
                Units.nearbyEnemies(team, rect, u -> {
                    if (!hit.contains(u.id()) && (hitter == null || u.checkTarget(hitter.type.collidesAir, hitter.type.collidesGround))) {
                        entities.add(u);
                    }
                });
            }

            Unit furthest = Geometry.findFurthest(x, y, entities);

            if (furthest != null) {
                hit.add(furthest.id());
                x = furthest.x();
                y = furthest.y();
            } else {
                // Randomize rotation within a range
                rotation += random.range(ANGLE_RANGE);
                x += Angles.trnsx(rotation, hitRange / 2f);
                y += Angles.trnsy(rotation, hitRange / 2f);
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
