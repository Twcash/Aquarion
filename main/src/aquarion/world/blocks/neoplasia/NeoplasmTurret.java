package aquarion.world.blocks.neoplasia;

import aquarion.world.entities.bullet.NeoplasmGlobBulletType;
import aquarion.world.graphics.Renderer;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import mindustry.content.Blocks;
import mindustry.gen.Teamc;
import mindustry.world.Tile;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class NeoplasmTurret extends GenericNeoplasiaBlock {
    public float range = 100f;
    public float reloadTime = 120f;
    public float shootLength = 8f;
    public NeoplasmGlobBulletType shootType;

    public NeoplasmTurret(String name) {
        super(name);
    }
    @Override
    public boolean canUpgradeToThis(NeoplasiaBuild build) {
        for (int dx = -10; dx <= 10; dx++) {
            for (int dy = -10; dy <= 10; dy++) {
                Tile t = world.tile(build.tile.x + dx, build.tile.y + dy);
                if (t != null && t.build instanceof NeoplasmTurretBuild) {
                    return false;
                }
            }
        }
        int emptyCount = 0;
        for (int dx = -6; dx <= 6; dx++) {
            for (int dy = -6; dy <= 6; dy++) {
                Tile t = world.tile(build.tile.x + dx, build.tile.y + dy);
                if (t != null && t.block() == Blocks.air && !t.floor().isDeep()) {
                    emptyCount++;
                }
            }
        }
        return emptyCount >= 3;
    }

    public class NeoplasmTurretBuild extends NeoplasiaBuild {
        float reloadTimer = 0f;
        float rotation = 0f;

        @Override
        public void updateTile() {
            super.updateTile();
            if (shootType == null || shootType.neoplasiaBlock == null) return;

            Tile target = findTarget();
            if (target != null) {
                float targetAngle = Mathf.angle(target.worldx() - x, target.worldy() - y);
                rotation = Mathf.slerp(rotation, targetAngle, 0.1f * delta());

                reloadTimer += delta();
                if (reloadTimer >= reloadTime) {
                    reloadTimer = 0f;
                    float spawnX = x + Mathf.cosDeg(rotation) * shootLength;
                    float spawnY = y + Mathf.sinDeg(rotation) * shootLength;
                    float dist = Mathf.dst(target.worldx() - spawnX, target.worldy() - spawnY);
                    var bullet = shootType.create(this, team, spawnX, spawnY, rotation, 1f, 1f);
                    if (bullet != null) {
                        bullet.lifetime = Math.max(dist / shootType.speed, 4f);
                    }
                }
            }
        }

        @Override
        public void draw() {
            super.draw();
            Draw.z(Renderer.Layer.neoplasiaBase + 0.1f);
            Draw.rect(block.region, x, y, rotation - 90);
        }

        Tile findTarget() {
            int tr = (int)(range / tilesize);
            int bestType = 0;
            Tile bestTile = null;
            float bestDist = Float.MAX_VALUE;
            for (int dx = -tr; dx <= tr; dx++) {
                for (int dy = -tr; dy <= tr; dy++) {
                    float dst = Mathf.dst(dx, dy);
                    if (dst > tr) continue;
                    Tile t = world.tile(tile.x + dx, tile.y + dy);
                    if (t == null || t.solid() || t.floor().isDeep()) continue;
                    int type = 0;
                    if (t.block() == Blocks.air && shootType.neoplasiaBlock.canPlaceOn(t, team, 0)) {
                        type = 2;
                    } else if (t.build instanceof NeoplasiaBuild nb && nb.team == team && nb.amount < nb.block().maxAmount * 0.3f) {
                        type = 1;
                    }
                    if (type > bestType || (type == bestType && dst < bestDist)) {
                        bestType = type;
                        bestTile = t;
                        bestDist = dst;
                    }
                }
            }
            return bestTile;
        }
    }
}
