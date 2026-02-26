package aquarion.world.blocks.neoplasia;

import aquarion.world.graphics.Renderer;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.math.geom.Geometry;
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

    public float maxAmount = 1000f;

    public float selfGrowRate = 0.04f;
    public float oreGrowBonus = 0.25f;

    public float damage = 2f;

    public float burstThresholdFraction = 0.3f;
    public float burstDelay = 600f;
    public float oreAttractBias = 1.5f;
    public int burstLength = 5;
    public float burstTransfer = 35f;
    public GenericNeoplasiaBlock(String name){
        super(name);
        update = true;
        solid = false;
        destroyEffect = Fx.neoplasmHeal;
        destructible = true;
        rebuildable = false;
    }

    public class NeoplasiaBuild extends Building {
        int burstDir = -1;
        int burstStep = 0;
        float burstTimer = 0f;
        float burstStepDelay = 4f;
        public float amount = 0f;
        float burstCooldown = 0f;
        int currentBurstLength = 0;

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

            // === Start a new burst ===
            if(burstDir == -1){

                if(amount < threshold) return;

                if(burstCooldown > 0f){
                    burstCooldown -= delta();
                    return;
                }

                burstDir = Mathf.random(3);
                burstStep = 0;

                currentBurstLength = 1 + (int)(Mathf.pow(Mathf.random(), 0.6f) * burstLength);

                burstTimer = 0f;
            }

            // === Step timing ===
            burstTimer += delta();
            if(burstTimer < burstStepDelay) return;
            burstTimer = 0f;

            burstStep++;

            if(burstStep > currentBurstLength){
                burstDir = -1;
                burstCooldown = burstDelay;
                return;
            }

            int dx = Geometry.d4[burstDir].x;
            int dy = Geometry.d4[burstDir].y;

            Tile t = world.tile(tile.x + dx * burstStep, tile.y + dy * burstStep);

            if(t == null || t.solid()){
                burstDir = -1;
                burstCooldown = burstDelay;
                return;
            }

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

                t.setBlock(GenericNeoplasiaBlock.this, team);
                Fx.neoplasmHeal.at(t.worldx(), t.worldy());

                if(t.build instanceof NeoplasiaBuild nb){
                    float flow = Math.min(transfer, amount);
                    amount -= flow;
                    nb.amount = flow;
                }
            }
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
            Draw.alpha(fullness);
            Draw.z(Renderer.Layer.neoplasiaBase);
            float radius = (tilesize * 1.5f) / 2f;
            Draw.color(Pal.neoplasm2, Pal.neoplasm1, fullness);
            Fill.circle(x, y, radius);
        }
    }
}