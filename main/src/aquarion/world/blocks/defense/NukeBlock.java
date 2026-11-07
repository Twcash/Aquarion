package aquarion.world.blocks.defense;

import aquarion.content.blocks.EnvironmentBlocks;
import arc.math.geom.Geometry;
import mindustry.game.Team;
import aquarion.world.graphics.AquaFx;
import aquarion.world.graphics.NuclearTerrainImpacts;
import aquarion.world.blocks.environment.CraterFloods;
import arc.struct.IntSeq;
import arc.graphics.Color;
import arc.math.Angles;
import arc.math.Mathf;
import arc.struct.IntSet;
import arc.struct.Seq;
import mindustry.content.Blocks;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.entities.Fires;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.*;
import mindustry.world.Block;
import mindustry.world.Tile;

import static mindustry.Vars.*;

/** A disposable, immediate-detonation warhead; never creates a missile unit. */
public class NukeBlock extends MissileBlock{
    public float blastRadius = 480f;
    public float blastDamage = 40000f;
    public float shockwaveRadius = 960f;
    public float waveDelay = 18f;
    public float waveDuration = 240f;
    public float falloutDuration = 3600f;
    public float buildingBurnDuration = 600f;
    public float buildingBurnDamage = 20f;
    public int cloudParticles = 720;
    public float cloudDuration = 1800f;

    public final BulletType explosion = new BulletType(0f, 0f){
        {
            lifetime = 3858f;
            collides = false;
            hittable = absorbable = reflectable = false;
            keepVelocity = false;
            // Keep the camera whiteout visible even when ground zero is off-screen.
            drawSize = 100000000f;
            despawnEffect = shootEffect = smokeEffect = mindustry.content.Fx.none;
        }

        @Override
        public void update(Bullet b){
            NuclearTerrainImpacts.watch(b, shockwaveRadius, waveDelay + waveDuration + 10f);
            // Cosmetic emissions run locally, including on multiplayer clients.
            // Sample tile positions rather than adding an update entity to every tile.
            if(!headless && b.time >= waveDelay + waveDuration && b.timer(2, 12f)){
                int extent = Mathf.ceil(blastRadius / tilesize);
                long pulse = b.id + Mathf.floor(b.time / 12f) * 65537L;
                for(int i = 0; i < 10; i++){
                    long seed = pulse + i * 127L;
                    int tx = world.toTile(b.x) + Mathf.round(Mathf.randomSeed(seed, -extent, extent));
                    int ty = world.toTile(b.y) + Mathf.round(Mathf.randomSeed(seed + 1, -extent, extent));
                    Tile tile = world.tile(tx, ty);
                    if(tile != null && inFallout(tile.worldx() - b.x, tile.worldy() - b.y, b.id)){
                        AquaFx.nuclearFalloutSmoke.at(tile.worldx(), tile.worldy());
                    }
                }
            }
            if(net.client()){
                if(b.time >= waveDelay + waveDuration && b.timer(3, 30f)) placeCraterRim(b);
                return;
            }
            if(!(b.data instanceof BlastState)) b.data = new BlastState();
            BlastState blast = (BlastState)b.data;
            float radius = shockwaveRadius * Mathf.clamp((b.time - waveDelay) / waveDuration);
            if(blast.radius < shockwaveRadius){
                int tiles = Mathf.ceil(radius / tilesize);
                int cx = world.toTile(b.x), cy = world.toTile(b.y);
                for(int tx = Math.max(0, cx - tiles); tx <= Math.min(world.width() - 1, cx + tiles); tx++){
                    for(int ty = Math.max(0, cy - tiles); ty <= Math.min(world.height() - 1, cy + tiles); ty++){
                        Tile tile = world.tile(tx, ty);
                        float dst = Mathf.dst(b.x, b.y, tile.worldx(), tile.worldy());
                        if(dst > radius || (blast.radius >= 0f && dst <= blast.radius)) continue;
                        float dx = tile.worldx() - b.x, dy = tile.worldy() - b.y;
                        float exposure = blastShape(dx, dy, b.id);
                        if(dst > shockwaveRadius * exposure) continue;
                        Building target = tile.build;
                        if(target != null && blast.buildings.add(target.id)){
                            Fires.create(target.tile);
                            float damage = waveDamage(dst / exposure);
                            if(target.health <= damage){
                                Call.effect(AquaFx.debris, target.x, target.y, Angles.angle(b.x, b.y, target.x, target.y), Color.white, target.block);
                            }
                            target.damage(damage);
                            if(target.isValid()){
                                blast.burning.add(new BurningBuilding(target, b.time));
                                Call.effect(AquaFx.hitIncend2, target.x, target.y, target.block.size * tilesize, Color.white, target.id);
                            }
                        }else if(target == null && tile.block() != Blocks.air && tile.block().solid
                            && dst < blastRadius * exposure * (0.65f + 0.35f * noise(dx / 70f, dy / 70f, b.id + 41))){
                            Call.effect(AquaFx.debris, tile.worldx(), tile.worldy(), Angles.angle(b.x, b.y, tile.worldx(), tile.worldy()), Color.white, tile.block());
                            tile.setNet(Blocks.air);
                        }
                        if(dst <= blastRadius * exposure && Mathf.chance(0.075)) Fires.create(tile);
                        // Save actual terrain changes, rather than a temporary effect decal.
                        // Water and slag remain as flood sources; other liquid terrain evaporates.
                        float scarNoise = noise(dx / 34f, dy / 34f, b.id);
                        float angle = Angles.angle(dx, dy);
                        float gouge = Math.abs(Mathf.sinDeg(angle * 19f + scarNoise * 85f + dst * 0.09f));
                        float reach = blastRadius * exposure * (0.85f + noise(dx / 115f, dy / 115f, b.id + 9) * 0.35f);
                        if(dst < reach && tile.floor().isLiquid && !CraterFloods.allowed(tile.floor())){
                            tile.setFloorNet(Blocks.basalt, Blocks.air);
                        }
                        if(dst < reach && !tile.floor().isLiquid && tile.overlay() == Blocks.air){
                            float innerDistance = dst / (blastRadius * exposure * (0.88f + scarNoise * 0.24f));
                            if(innerDistance < 0.16f){
                                tile.setFloorNet(Blocks.magmarock, Blocks.air);
                            }else if(innerDistance < 0.32f){
                                tile.setFloorNet(Blocks.hotrock, Blocks.air);
                            }else if(innerDistance < 0.58f){
                                tile.setFloorNet(Blocks.basalt, Blocks.air);
                            }else if(dst < blastRadius * exposure * (0.48f + scarNoise * 0.3f) || (gouge > 0.78f && scarNoise > 0.2f)){
                                Block overlay = Mathf.randomSeed(tile.pos() * 31L + b.id) < 0.2f ? EnvironmentBlocks.scorche : Blocks.air;
                                tile.setFloorNet(scarNoise > 0.3f ? Blocks.craters : Blocks.charr, overlay);
                            }else if(scarNoise > 0.42f || gouge > 0.78f){
                                if(Mathf.randomSeed(tile.pos() * 31L + b.id) < 0.35f) tile.setOverlayNet(EnvironmentBlocks.scorche);
                            }
                        }
                    }
                }
                Units.nearby(null, b.x, b.y, radius, unit -> {
                    float exposure = blastShape(unit.x - b.x, unit.y - b.y, b.id);
                    if(unit.dst(b) > shockwaveRadius * exposure) return;
                    if(blast.units.add(unit.id)){
                        float dst = unit.dst(b);
                        unit.damage(waveDamage(dst / exposure));
                        unit.apply(StatusEffects.burning, 600f);
                        unit.impulse(Angles.trnsx(unit.angleTo(b) + 180f, 120f), Angles.trnsy(unit.angleTo(b) + 180f, 120f));
                    }
                });
                blast.radius = radius;
            }
            if(!blast.rimPlaced && b.time >= waveDelay + waveDuration){
                blast.rimPlaced = true;
                registerCraterBasin(b);
                placeCraterRim(b);
            }
            if(b.timer(1, 30f)){
                for(int i = blast.burning.size - 1; i >= 0; i--){
                    BurningBuilding burning = blast.burning.get(i);
                    if(!burning.build.isValid() || b.time - burning.started >= buildingBurnDuration){
                        blast.burning.remove(i);
                        continue;
                    }
                    burning.build.damage(buildingBurnDamage * 0.5f);
                    Fires.create(burning.build.tile);
                }
            }
            if(b.time >= waveDelay + waveDuration && b.timer(0, 60f)){
                Units.nearby(null, b.x, b.y, blastRadius, unit -> {
                    if(!inFallout(unit.x - b.x, unit.y - b.y, b.id)) return;
                    unit.damage(12f);
                    unit.apply(StatusEffects.corroded, 120f);
                });
            }
        }

        @Override
        public void draw(Bullet b){
            AquaFx.Flash(b.time);
            AquaFx.BigassExplosionCloud(b.id, b.x, b.y, b.time, blastRadius, cloudParticles, cloudDuration);
            AquaFx.ShockwaveDust(b.id, b.x, b.y, b.time, shockwaveRadius, waveDelay, waveDuration);
            if(b.time < waveDelay) return;
            if(b.time < waveDelay + waveDuration){
                AquaFx.drawNuclearShockwave(b.x, b.y, (b.time - waveDelay) / waveDuration, shockwaveRadius);
            }else{
                AquaFx.nuclearFallout(b.id, b.x, b.y, b.time, b.lifetime, blastRadius, waveDelay, waveDuration);
            }
        }
    };
    // I wonder if this is client friendly.
    private float craterDepth(float dx, float dy, int seed){
        return Mathf.dst(dx, dy) / (blastRadius * blastShape(dx, dy, seed)
            * (0.88f + noise(dx / 34f, dy / 34f, seed) * 0.24f));
    }

    private void registerCraterBasin(Bullet b){
        IntSeq positions = new IntSeq();
        int extent = Mathf.ceil(blastRadius / tilesize);
        int cx = world.toTile(b.x), cy = world.toTile(b.y);
        for(int tx = Math.max(0, cx - extent); tx <= Math.min(world.width() - 1, cx + extent); tx++){
            for(int ty = Math.max(0, cy - extent); ty <= Math.min(world.height() - 1, cy + extent); ty++){
                Tile tile = world.tile(tx, ty);
                if(tile.overlay() != Blocks.spawn && craterDepth(tile.worldx() - b.x, tile.worldy() - b.y, b.id) < 0.58f){
                    positions.add(tile.pos());
                }
            }
        }
        CraterFloods.track(positions);
    }

    private void placeCraterRim(Bullet b){
        int extent = Mathf.ceil(blastRadius / tilesize);
        int cx = world.toTile(b.x), cy = world.toTile(b.y);
        for(int tx = Math.max(0, cx - extent); tx <= Math.min(world.width() - 1, cx + extent); tx++){
            for(int ty = Math.max(0, cy - extent); ty <= Math.min(world.height() - 1, cy + extent); ty++){
                Tile tile = world.tile(tx, ty);
                boolean existingCliff = tile.block() == Blocks.cliff;
                if(tile.build != null || (!existingCliff && tile.block() != Blocks.air) || tile.floor().isLiquid
                    || tile.overlay().itemDrop != null || tile.overlay() == Blocks.spawn) continue;
                float dx = tile.worldx() - b.x, dy = tile.worldy() - b.y;
                float depth = craterDepth(dx, dy, b.id);
                if(depth < 0.58f || depth > 0.7f) continue;
                float angle = Angles.angle(dx, dy);
                if(Math.abs(Mathf.sinDeg(angle * 2f + particleRandom(b.id, 360f))) < 0.15f
                    || noise(dx / 70f, dy / 70f, b.id + 7) < 0.25f) continue;
                int mask = 0;
                for(int i = 0; i < 8; i++){
                    if(craterDepth(dx + Geometry.d8[i].x * tilesize, dy + Geometry.d8[i].y * tilesize, b.id) < 0.58f){
                        mask |= 1 << i;
                    }
                }
                if(mask == 0) continue;
                if(!net.client()){
                    tile.setFloorNet(Blocks.basalt, Blocks.air);
                    if(!existingCliff) tile.setNet(Blocks.cliff, Team.derelict, 0);
                }else if(!existingCliff){
                    continue;
                }
                // Vanilla cliffs use tile data, not building configuration, for facing.
                // Clients reconstruct the same mask after the tile update arrives.
                if(tile.data != (byte)mask){
                    tile.data = (byte)mask;
                    tile.recache();
                }
            }
        }
    }

    private static float particleRandom(long seed){
        seed = (seed ^ (seed >>> 30)) * 0xbf58476d1ce4e5b9L;
        seed = (seed ^ (seed >>> 27)) * 0x94d049bb133111ebL;
        seed ^= seed >>> 31;
        return (seed >>> 40) / 16777216f;
    }

    private static float particleRandom(long seed, float max){
        return particleRandom(seed) * max;
    }

    private static float particleRandom(long seed, float min, float max){
        return Mathf.lerp(min, max, particleRandom(seed));
    }

    // Smooth seeded fields keep terrain damage coherent rather than random tile speckle.
    private static float noise(float x, float y, int seed){
        int ix = Mathf.floor(x), iy = Mathf.floor(y);
        float fx = x - ix, fy = y - iy;
        fx = fx * fx * (3f - 2f * fx);
        fy = fy * fy * (3f - 2f * fy);
        float a = Mathf.randomSeed(seed + ix * 734287L + iy * 912271L);
        float b = Mathf.randomSeed(seed + (ix + 1) * 734287L + iy * 912271L);
        float c = Mathf.randomSeed(seed + ix * 734287L + (iy + 1) * 912271L);
        float d = Mathf.randomSeed(seed + (ix + 1) * 734287L + (iy + 1) * 912271L);
        return Mathf.lerp(Mathf.lerp(a, b, fx), Mathf.lerp(c, d, fx), fy);
    }

    private static float blastShape(float dx, float dy, int seed){
        float angle = Angles.angle(dx, dy);
        float phase = Mathf.randomSeed(seed, 360f);
        return 0.68f + 0.13f * Mathf.sinDeg(angle * 5f + phase)
            + 0.09f * Mathf.sinDeg(angle * 11f - phase)
            + 0.18f * noise(dx / 95f, dy / 95f, seed);
    }

    private boolean inFallout(float dx, float dy, int seed){
        return AquaFx.nuclearFalloutContains(dx, dy, blastRadius, seed);
    }

    private float waveDamage(float distance){
        return distance <= blastRadius ? blastDamage * (1f - 0.75f * distance / blastRadius)
            : 250f + 4000f * (1f - distance / shockwaveRadius);
    }

    private static class BlastState{
        float radius = -1f;
        boolean rimPlaced;
        final IntSet buildings = new IntSet(), units = new IntSet();
        final Seq<BurningBuilding> burning = new Seq<>();
    }

    private static class BurningBuilding{
        final Building build;
        final float started;

        BurningBuilding(Building build, float started){
            this.build = build;
            this.started = started;
        }
    }

    public NukeBlock(String name){
        super(name);
        CraterFloods.register();
        size = 1;
        spawn = null;
        buildType = NukeBuild::new;
    }


    public class NukeBuild extends MissileBuild{
        private boolean detonated;

        @Override
        public void updateTile(){
            if(detonated || net.client() || tile == emptyTile) return;
            detonated = true;
            float bx = x, by = y;
            explosion.createNet(team, bx, by, 0f, 0f, 1f, (waveDelay + waveDuration + falloutDuration) / explosion.lifetime);
            Call.soundAt(Sounds.explosionMissile, bx, by, 0.5f, 2f);
            Call.effect(AquaFx.nuclearFireball, bx, by, blastRadius, Color.white);
            Effect.shake(100f, 240f, bx, by);
            kill();
        }
    }
}
