package aquarion.world.blocks.distribution;

import aquarion.ui.LiquidBar;
import aquarion.world.content.LiquidUtil;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.world.blocks.liquid.LiquidBlock;
import mindustry.world.blocks.liquid.LiquidJunction;
import mindustry.world.meta.Stat;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

public class LiquidValve extends LiquidJunction {
    public boolean invert = false;
    public boolean willMelt = false;
    public LiquidValve(String name){
        super(name);
        floating = true;
        destructible = true;
        update = false;
        drawCached = true;
        drawDynamic = false;
        canOverdrive = false;
        solid = false;
        hasLiquids = true;
        instantTransfer = true;
    }

    public class LiquidValveBuild extends Building{
        @Override
        public void draw(){
            Draw.rect(region, x, y);
        }

        @Override
        public Building getLiquidDestination(Building source, Liquid liquid){
            if(!enabled) return this;

            int dir = (source.relativeTo(tile.x, tile.y) + 1) % 4;
            Building next = nearby(dir);
            if(next == null || (!next.acceptLiquid(this, liquid) && !(next.block instanceof LiquidJunction) && !(next.block instanceof LiquidUnderflow))){
                dir = (source.relativeTo(tile.x, tile.y) + 3) % 4;
                next = nearby(dir);
                if(next == null || (!next.acceptLiquid(this, liquid) && !(next.block instanceof LiquidJunction) && !(next.block instanceof LiquidUnderflow))){
                    dir = (source.relativeTo(tile.x, tile.y) + 4) % 4;
                    next = nearby(dir);
                    if(next == null || (!next.acceptLiquid(this, liquid) && !(next.block instanceof LiquidJunction) && !(next.block instanceof LiquidUnderflow))){
                        return this;
                    }
                }
            }
            return next.getLiquidDestination(this, liquid);
        }
    }
}