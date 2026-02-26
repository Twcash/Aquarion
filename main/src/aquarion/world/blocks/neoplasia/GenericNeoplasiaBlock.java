package aquarion.world.blocks.neoplasia;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.Tile;

import static mindustry.Vars.tilesize;

public class GenericNeoplasiaBlock extends Block {

    public float maxAmount = 1000f;

    public float selfGrowRate = 0.08f;
    public float oreGrowBonus = 0.5f;

    public float damage = 2f;

    public float burstThresholdFraction = 0.3f;
    public float burstFraction = 0.1f;
    public float burstDelay = 40f;
    public float oreAttractBias = 3f;

    public GenericNeoplasiaBlock(String name){
        super(name);
        update = true;
        solid = false;
        destructible = true;
        rebuildable = false;
    }

    public class NeoplasiaBuild extends Building {

        public float amount = 0f;

        float burstCooldown = 0f;

        @Override
        public void created(){
            amount = 0.0001f;
            super.created();
        }

        @Override
        public void updateTile(){
            health = amount;
            maxHealth = amount;
            if(amount <= 0f){
                kill();
                return;
            }

            grow();
            burstSpread();
            damageNearby();
        }
        @Override
        public float handleDamage(float amount){
            this.amount -= amount;
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
            if(amount < threshold) return;

            if(burstCooldown > 0f){
                burstCooldown -= delta();
                return;
            }

            int bursts = Mathf.random(1, 4);

            for(int b = 0; b < bursts; b++){
                Tile target = pickTarget();
                if(target == null) continue;

                float burstAmount = amount * burstFraction;
                amount -= burstAmount;

                if(target.build instanceof NeoplasiaBuild n){
                    n.amount = Math.min(maxAmount, n.amount + burstAmount);
                } else if(target.build == null && canSpreadTo(target)){
                    target.setBlock(GenericNeoplasiaBlock.this, team);

                    if(target.build instanceof NeoplasiaBuild nb){
                        nb.amount = burstAmount;
                    }
                }
            }

            burstCooldown = burstDelay;
        }

        Tile pickTarget(){

            Tile best = null;
            float bestScore = -1f;

            for(int i = 0; i < 4; i++){

                Tile t = tile.nearby(i);
                if(t == null) continue;

                if(!canSpreadTo(t) && !(t.build instanceof NeoplasiaBuild))
                    continue;

                float score = Mathf.random();

                if(isOre(t)) score += oreAttractBias;

                if(t.build instanceof NeoplasiaBuild n){
                    score += (maxAmount - n.amount) / maxAmount;
                }

                if(score > bestScore){
                    bestScore = score;
                    best = t;
                }
            }

            return best;
        }

        boolean isOre(Tile t){
            return t != null &&
                    t.overlay() != null &&
                    t.overlay().itemDrop != null;
        }

        boolean canSpreadTo(Tile t){
            return t.build == null && !t.solid();
        }

        void damageNearby(){

            for(int i = 0; i < 4; i++){

                Tile t = tile.nearby(i);
                if(t == null) continue;

                Building other = t.build;

                if(other != null && !(other instanceof NeoplasiaBuild)){
                    other.damage(damage * delta());
                }
            }
        }

        @Override
        public void draw(){

            float fullness = amount / (maxAmount*.75f);

            Draw.alpha(fullness);

            Fill.circle(
                    x,
                    y,
                    (tilesize * fullness) / 2f
            );

            Draw.alpha(1f);
        }
    }
}