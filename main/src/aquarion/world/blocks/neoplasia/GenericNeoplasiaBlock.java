package aquarion.world.blocks.neoplasia;

import aquarion.content.blocks.CoreBlocks;
import aquarion.world.graphics.AquaFx;
import aquarion.world.graphics.Renderer;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.entities.Units;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.world.Block;
import mindustry.world.Tile;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class GenericNeoplasiaBlock extends Block {
    public float wscl = 25f, wmag = 0.4f, wtscl = 1f, wmag2 = 1f;
    public float maxAmount = 1000f;
    public float startMass = 0.001f;
    public float selfGrowRate = 0.04f;
    public float oreGrowBonus = 0.25f;
    public Block base = CoreBlocks.neoplasiaMass;

    public float damage = 2f;

    public float burstThresholdFraction = 0.3f;
    public float burstDelay = 600f;
    public int burstLength = 5;
    public float burstTransfer = 35f;
    public float recentDamageDecay = 0.0008f;
    public Block oreUpgrade = Blocks.router;
    public Block damageUpgrade = Blocks.router;

    public float oreUpgradeCost = 300;
    public float damageUpgradeCost = 10;

    public GenericNeoplasiaBlock(String name){
        super(name);
        update = true;
        solid = false;
        destroyEffect = Fx.neoplasmHeal;
        destructible = true;
        rebuildable = false;
        drawTeamOverlay = false;
    }

    public class NeoplasiaBuild extends Building {
        int burstDir = -1;
        int burstStep = 0;
        float burstTimer = 0f;
        float burstStepDelay = 4f;
        public float amount = 0f;
        float burstCooldown = 0f;
        int currentBurstLength = 0;
        public float recentDamage = 0f;
        public float upgradeDamageScale = 0.9f;
        Tile burstTile;

        @Override
        public void created(){
            amount = startMass;
            super.created();
        }

        @Override
        public void updateTile(){
            health = amount;
            maxHealth = amount;
            recentDamage *= Mathf.pow(recentDamageDecay, delta());
            if(amount <= 0f){
                kill();
                return;
            }

            grow();
            burstSpread();
            damageNearby();
            if(amount >= oreUpgradeCost && Mathf.chanceDelta(0.05f) && isOre(this.tile)){
                if(oreUpgrade != null) {
                    AquaFx.neoplasiaPlace.at(x, y);
                    this.tile.setBlock(oreUpgrade, this.team);
                }
            }
            float upgradeChance = recentDamage * upgradeDamageScale;
            if(Mathf.chanceDelta(upgradeChance) && amount >= damageUpgradeCost){
                AquaFx.neoplasiaPlace.at(x, y);
                this.tile.setBlock(damageUpgrade, this.team);
            }
        }

        @Override
        public float handleDamage(float amount){
            this.amount -= amount;
            recentDamage += amount * 3;
            return amount;
        }

        void grow(){
            float growth = selfGrowRate;

            if(isOre(tile)){
                growth += oreGrowBonus;
            }

            amount = Math.min(maxAmount, amount + growth * delta());
        }
        void burstSpread(){

            float threshold = maxAmount * burstThresholdFraction;

            // Start burst
            if(burstDir == -1){

                if(amount < threshold) return;

                if(burstCooldown > 0f){
                    burstCooldown -= delta();
                    return;
                }

                burstStep = 0;
                currentBurstLength = 1 + (int)(Mathf.pow(Mathf.random(), 0.6f) * burstLength);
                burstTimer = 0f;

                burstTile = tile; // start from self
                burstDir = 0;     // dummy active flag
            }

            burstTimer += delta();
            if(burstTimer < burstStepDelay) return;
            burstTimer = 0f;

            burstStep++;

            if(burstStep > currentBurstLength || burstTile == null){
                burstDir = -1;
                burstCooldown = burstDelay;
                return;
            }

            // --- Evaluate neighbors of CURRENT burstTile ---
            int bestDir = -1;
            float bestScore = -999f;

            for(int i = 0; i < 4; i++){

                int dx = Geometry.d4[i].x;
                int dy = Geometry.d4[i].y;

                Tile check = world.tile(burstTile.x + dx, burstTile.y + dy);

                if(check == null || check.solid() || check.floor().isDeep()) continue;

                float score = 0f;

                // Prefer empty
                if(check.build == null){
                    score += 5f;

                    // Strong ore priority
                    if(check.overlay() != null){
                        score += 15f;
                    }
                }

                // Prefer connecting to same type
                if(check.build instanceof NeoplasiaBuild){
                    score += 3f;
                }

                score += Mathf.random(0.5f);

                if(score > bestScore){
                    bestScore = score;
                    bestDir = i;
                }
            }

            if(bestDir == -1){
                burstDir = -1;
                burstCooldown = burstDelay;
                return;
            }

            int dx = Geometry.d4[bestDir].x;
            int dy = Geometry.d4[bestDir].y;

            Tile t = world.tile(burstTile.x + dx, burstTile.y + dy);

            float transfer = Math.min(burstTransfer, amount * 0.15f);

            if(transfer <= 0f){
                burstDir = -1;
                burstCooldown = burstDelay;
                return;
            }

            if(t.build instanceof NeoplasiaBuild n){

                float flow = Math.min(transfer, amount);
                amount -= flow;
                n.amount = Math.min(maxAmount, n.amount + flow);

            }else if(t.build == null){

                t.setBlock(base, team);
                AquaFx.neoplasiaPlace.at(t.worldx(), t.worldy());

                if(t.build instanceof NeoplasiaBuild nb){
                    float flow = Math.min(transfer, amount);
                    amount -= flow;
                    nb.amount = flow;
                }
            }

            burstTile = t;

            if(amount <= 1f){
                burstDir = -1;
                burstCooldown = burstDelay;
            }
        }

        boolean isOre(Tile t){
            return t != null &&
                    t.overlay() != null &&
                    t.overlay().itemDrop != null;
        }

        void damageNearby(){

            float radius = tilesize * 1.25f;

            for(int i = 0; i < 4; i++){
                Tile t = tile.nearby(i);
                if(t == null) continue;

                Building other = t.build;

                if(other != null && other.team != this.team){
                    other.damage(damage * delta());
                    Fx.neoplasmHeal.at(other.x, other.y);
                }
            }

            Units.closestEnemy(team, x - radius, y - radius, radius * 2f, unit -> {
                if(!unit.dead()){
                    unit.damage(damage * delta());
                    Fx.neoplasmHeal.at(unit.x, unit.y);
                }
                return true;
            });
        }

        @Override
        public void draw() {
            float fullness = amount / (maxAmount * 0.75f);
            Draw.z(Renderer.Layer.neoplasiaBase);
            float radius = (tilesize * 1.5f) / 2f;
            Draw.color(Color.valueOf("701e1e"), Color.valueOf("cf5a3b"), fullness);
            Fill.circle(x, y, radius);
            Draw.z(Renderer.Layer.neoplasiaUnder);
            Fill.circle(x, y, radius);
        }
    }
}