package aquarion.world.blocks.neoplasia;

import aquarion.world.graphics.AquaShaders;
import aquarion.world.graphics.Renderer;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.entities.Units;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.ConstructBlock;
import mindustry.world.blocks.environment.Prop;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;
import static mindustry.content.Blocks.*;

public class GenericNeoplasiaBlock extends Block {

    public float wscl = 25f, wmag = 0.4f, wtscl = 1f, wmag2 = 1f;
    public float maxAmount = 1000f;
    public float startMass = 0.001f;
    public float selfGrowRate = 0.04f;
    public float oreGrowBonus = 0.25f;

    public Block base;

    public float emptyUpgradeCost = 400;
    public boolean shouldEmptyUpgrade = true;
    public Block emptyUpgrade;

    public boolean hasPods = true;
    public float damage = 2f;

    public float burstThresholdFraction = 0.3f;
    public float burstDelay = 600f;
    public int burstLength = 5;
    public float burstTransfer = 35f;

    public float recentDamageDecay = 0.004f;

    public Block oreUpgrade;
    public Block damageUpgrade;

    public Color colFrom = Color.valueOf("701e1e");
    public Color colTo = Color.valueOf("cf5a3b");

    public float oreUpgradeCost = 300;
    public float damageUpgradeCost = 10;

    public GenericNeoplasiaBlock(String name){
        super(name);
        update = true;
        solid = false;
        destructible = true;
        rebuildable = false;
        drawTeamOverlay = false;
    }

    public class NeoplasiaBuild extends Building {

        int burstDir = -1;
        int burstStep = 0;
        float burstTimer = 0f;
        float burstCooldown = 0f;
        int currentBurstLength = 0;

        Tile burstTile;

        public float amount = 0f;
        public float recentDamage = 0f;
        public float upgradeDamageScale = 0.9f;

        float spawnTime = 0f;
        float spawnDuration = 90f;
        float burstStepDelay = 4f;
        @Override
        public float handleDamage(float amount){
            recentDamage += amount;
            return amount;
        }
        @Override
        public void created(){
            amount = startMass;
            super.created();

        }

        @Override
        public void updateTile(){

            if(tile == null) return;

            health = amount;
            maxHealth = amount;

            recentDamage = Math.max(0f, recentDamage - recentDamageDecay);

            if(spawnTime < spawnDuration){
                spawnTime += delta();
            }

            if(amount <= 0f){
                kill();
                return;
            }

            grow();
            burstSpread();
            damageNearby();

            tryUpgrades();
        }

        void tryUpgrades(){

            if(tile == null) return;

            // Ore upgrade
            if(amount >= oreUpgradeCost && Mathf.chanceDelta(0.05f) && isOre(tile)){
                safeSet(tile, oreUpgrade);
            }

            // Empty upgrade
            if(amount >= emptyUpgradeCost && Mathf.chanceDelta(0.05f) && !isOre(tile) && shouldEmptyUpgrade){
                safeSet(tile, emptyUpgrade);
            }

            float chance = recentDamage * upgradeDamageScale;
            if(amount >= damageUpgradeCost && Mathf.chanceDelta(chance)){
                safeSet(tile, damageUpgrade);
            }
        }

        void grow(){
            float growth = selfGrowRate;
            if(isOre(tile)) growth += oreGrowBonus;
            amount = Math.min(maxAmount, amount + growth * delta());
        }

        void burstSpread(){

            if(tile == null) return;

            float threshold = maxAmount * burstThresholdFraction;

            if(burstDir == -1){

                if(amount < threshold) return;

                if(burstCooldown > 0f){
                    burstCooldown -= delta();
                    return;
                }

                burstTile = tile;
                burstStep = 0;
                burstTimer = 0f;
                currentBurstLength = 1 + (int)(Mathf.pow(Mathf.random(), 0.6f) * burstLength);
                burstDir = 0;
            }

            burstTimer += delta();
            if(burstTimer < burstStepDelay) return;
            burstTimer = 0f;

            if(burstTile == null){
                resetBurst();
                return;
            }

            burstStep++;
            if(burstStep > currentBurstLength){
                resetBurst();
                return;
            }

            Tile next = pickBestNeighbor(burstTile);
            if(next == null){
                resetBurst();
                return;
            }

            float transfer = Math.min(burstTransfer, amount * 0.15f);
            if(transfer <= 0f){
                resetBurst();
                return;
            }

            if(next.build instanceof NeoplasiaBuild n){
                float flow = Math.min(transfer, amount);
                amount -= flow;
                n.amount = Math.min(maxAmount, n.amount + flow);
            }
            else if(next.block() == air){
                if(base != null){
                    next.setBlock(base, team);
                    if(next.build instanceof NeoplasiaBuild nb){
                        float flow = Math.min(transfer, amount);
                        amount -= flow;
                        nb.amount = flow;
                    }
                }
            }

            burstTile = next;

            if(amount <= 1f){
                resetBurst();
            }
        }

        Tile pickBestNeighbor(Tile origin){

            if(origin == null) return null;

            int bestDir = -1;
            float bestScore = -999f;

            for(int i = 0; i < 4; i++){

                Tile check = world.tile(origin.x + Geometry.d4[i].x, origin.y + Geometry.d4[i].y);
                if(check == null || check.solid() || check.floor().isDeep()) continue;

                float score = 0f;

                if(check.build == null){
                    score += 12f;
                    if(check.overlay() != null && check.overlay().itemDrop != null){
                        score += 13f;
                    }
                }

                if(check.build instanceof NeoplasiaBuild){
                    score += 1.5f;
                }

                score += Mathf.random(0.5f);

                if(score > bestScore){
                    bestScore = score;
                    bestDir = i;
                }
            }

            if(bestDir == -1) return null;

            return world.tile(origin.x + Geometry.d4[bestDir].x,
                    origin.y + Geometry.d4[bestDir].y);
        }

        void resetBurst(){
            burstDir = -1;
            burstCooldown = burstDelay;
            burstTile = null;
        }

        boolean isOre(Tile t){
            return t != null &&
                    t.overlay() != null &&
                    t.overlay().itemDrop != null;
        }

        void safeSet(Tile t, Block b){
            if(t == null || b == null) return;
            t.setBlock(b, team);
        }

        void damageNearby(){

            if(tile == null) return;

            for(int i = 0; i < 4; i++){
                Tile t = tile.nearby(i);
                if(t == null) continue;
                if(t.block().unitMoveBreakable){
                    ConstructBlock.deconstructFinish(t, t.block(), null);
                }
                Building other = t.build;
                if(other != null && other.team != team){
                    other.damage(damage * delta());
                }
            }

            Units.closestEnemy(team,
                    x - tilesize,
                    y - tilesize,
                    tilesize * 2f,
                    unit -> {
                        if(!unit.dead()){
                            unit.damage(damage * delta());
                        }
                        return true;
                    });
        }

        @Override public void draw() {
            float scale = 1f; if(spawnTime < spawnDuration){
                float progress = spawnTime / spawnDuration; scale = Interp.smooth.apply(progress);
            }
            Draw.z(Renderer.Layer.neoplasiaBase);
            Draw.scl(scale);
            float radius = tilesize * 1.5f / 2 * scale;

            Draw.color(colFrom,colTo, amount/maxAmount);
            Fill.circle(x, y, radius);
            Draw.z(Renderer.Layer.neoplasiaUnder);
            Fill.circle(x, y, radius);
            Draw.scl(1f);
            Draw.color();
        }
        @Override
        public void write(Writes write){
            super.write(write);
            write.f(amount);
            write.f(spawnTime);
        }
        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            amount = read.f();
            spawnTime = read.f();
        }
    }
}