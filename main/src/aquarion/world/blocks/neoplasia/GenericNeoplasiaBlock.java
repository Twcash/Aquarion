package aquarion.world.blocks.neoplasia;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.Tile;

import static mindustry.Vars.tilesize;

public class GenericNeoplasiaBlock extends Block {

    public float maxAmount = 1000f;
    public float spreadRate = 0.15f;
    public float minSpread = 1f;

    public float selfGrowRate = 0.02f;
    public float oreGrowBonus = 0.15f;
    public float oreAttractMultiplier = 3f;

    public float damage = 2f;

    public GenericNeoplasiaBlock(String name){
        super(name);
        update = true;
        solid = false;
        destructible = true;
        rebuildable = false;
    }

    public class NeoplasiaBuild extends Building {

        public float amount = 0f;

        @Override
        public void updateTile(){

            if(amount <= 0f){
                kill();
                return;
            }

            grow();
            spread();
            damageNearby();
        }

        void grow(){
            float growth = selfGrowRate;

            if(tile.overlay() != null && tile.overlay().itemDrop != null){
                growth += oreGrowBonus;
            }

            amount = Math.min(maxAmount, amount + growth * edelta());
        }

        void spread(){

            for(int i = 0; i < 4; i++){
                Tile otherTile = nearby(i).tile;
                Building other = nearby(i);

                if(other instanceof NeoplasiaBuild n){

                    float diff = amount - n.amount;

                    if(diff > minSpread){
                        float flow = diff * spreadRate;

                        if(isOre(otherTile)){
                            flow *= oreAttractMultiplier;
                        }

                        flow *= edelta();
                        flow = Math.min(flow, amount);

                        amount -= flow;
                        n.amount = Math.min(maxAmount, n.amount + flow);
                    }

                }else if(other == null && otherTile != null && canSpreadTo(otherTile)){

                    if(amount > minSpread){

                        float spreadAmount = amount * 0.25f;

                        if(isOre(otherTile)){
                            spreadAmount *= oreAttractMultiplier;
                        }

                        spreadAmount = Math.min(spreadAmount, amount);

                        amount -= spreadAmount;

                        otherTile.setBlock(GenericNeoplasiaBlock.this, team);

                        if(otherTile.build instanceof NeoplasiaBuild nb){
                            nb.amount = spreadAmount;
                        }
                    }
                }
            }
        }

        boolean isOre(Tile tile){
            return tile != null &&
                    tile.overlay() != null &&
                    tile.overlay().itemDrop != null;
        }

        boolean canSpreadTo(Tile tile){
            return tile.block().isAir();
        }

        void damageNearby(){
            for(int i = 0; i < 4; i++){
                Building other = nearby(i);
                if(other != null && !(other instanceof NeoplasiaBuild)){
                    other.damage(damage * delta());
                }
            }
        }

        @Override
        public void draw(){
            Draw.alpha(amount / maxAmount);
            Fill.circle(x, y, (amount/maxAmount * tilesize) / 2);
            Draw.alpha(1f);
        }
    }
}