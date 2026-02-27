package aquarion.world.blocks.neoplasia;

import aquarion.world.graphics.AquaFx;
import aquarion.world.graphics.Renderer;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.util.Time;

import static mindustry.Vars.tilesize;

public class DefensiveNeoplasiaBlock extends GenericNeoplasiaBlock{
    public DefensiveNeoplasiaBlock(String name) {
        super(name);
        startMass = 1000;
        maxAmount = 1500;
        burstLength = 0;
        burstTransfer = 0;
        oreGrowBonus = 0;
        selfGrowRate = 0.9f;
    }
    public float reverseUpgradeThreshold = 10f;
    public float reverseUpgradeChance = 0.0003f;
    public class defensiveNeoplasiaBlockBuild extends NeoplasiaBuild {

        @Override
        public void created(){
            amount = startMass;
            recentDamage = 10000;
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
            if(damageUpgrade != null && recentDamage <= reverseUpgradeThreshold){

                if(Mathf.chanceDelta(reverseUpgradeChance)){
                    AquaFx.neoplasiaPlace.at(x, y);
                    this.tile.setBlock(damageUpgrade, this.team);
                    return;
                }
            }
        }

        @Override
        public void draw() {
            float fullness = amount / (maxAmount * 0.75f);
            Draw.z(Renderer.Layer.neoplasiaBase);
            float radius = (tilesize * 2f) / 2f;
            Draw.color(Color.valueOf("701e1e"), Color.valueOf("cf5a3b"), fullness);
            Fill.circle(x, y, radius);
            Draw.z(Renderer.Layer.neoplasiaUnder);
            Fill.circle(x, y, radius);
            Draw.z(Renderer.Layer.neoplasiaBase + .1f);
            Draw.color();
            Draw.rectv(region, tile.worldx(), tile.worldy(), region.width * region.scl(), region.height * region.scl(), 0, vec -> vec.add(
                    Mathf.sin(vec.y*3 + Time.time, wscl, wmag) + Mathf.sin(vec.x*3 - Time.time, 70 * wtscl, 0.8f * wmag2),
                    Mathf.cos(vec.x*3 + Time.time + 8, wscl + 6f, wmag * 1.1f) + Mathf.sin(vec.y*3 - Time.time, 50 * wtscl, 0.2f * wmag2)));
        }
    }
}
