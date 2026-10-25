package aquarion.world.blocks.neoplasia;

import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import aquarion.world.graphics.Renderer;
import mindustry.content.Liquids;
import mindustry.world.Tile;

/** A shore gland that converts cool surface liquid into a large, exportable mass supply. */
public class NeoplasmLiquidSlurper extends GenericNeoplasiaBlock {
    public float maxLiquidTemperature = 0.7f;

    public NeoplasmLiquidSlurper(String name){
        super(name);
        maxAmount = 20000f;
        selfGrowRate = 60f;
        massExportRate = 50f;
        cost = 100f;
        baseSize = 24f;
        bursts = 0;
        shouldEmptyUpgrade = false;
        shouldEmpty2Upgrade = false;
    }

    public boolean hasLiquid(Tile site){
        if(site == null) return false;
        for(int direction = 0; direction < 4; direction++){
            Tile nearby = site.nearby(direction);
            if(nearby == null || !nearby.floor().isLiquid) continue;
            var liquid = nearby.floor().liquidDrop;
            if(liquid != null && liquid != Liquids.slag && liquid.temperature < maxLiquidTemperature) return true;
        }
        return false;
    }

    public class LiquidSlurperBuild extends NeoplasiaBuild {
        @Override
        void grow(){
            if(hasLiquid(tile)) super.grow();
        }

        @Override
        public void draw(){
            float previousZ = Draw.z();
            super.draw();
            float progress = Mathf.clamp(spawnTime / spawnDuration);
            Draw.z(Renderer.Layer.blockOver + 2f);
            Draw.rect(region, x, y, region.width * region.scl() * progress, region.height * region.scl() * progress);
            Draw.reset();
            Draw.z(previousZ);
        }
    }
}
