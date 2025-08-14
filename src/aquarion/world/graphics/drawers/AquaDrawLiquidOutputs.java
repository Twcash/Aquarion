package aquarion.world.graphics.drawers;

import aquarion.world.type.AquaGenericCrafter;
import arc.*;
import arc.graphics.g2d.*;
import arc.util.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.production.*;
import mindustry.world.draw.DrawBlock;

/** This must be used in conjunction with another DrawBlock; it only draws outputs. */
public class AquaDrawLiquidOutputs extends DrawBlock {
    public TextureRegion[][] liquidOutputRegions;

    @Override
    public void draw(Building build){
        GenericCrafter crafter = (GenericCrafter)build.block;
        if(crafter.outputLiquids == null) return;

        for(int i = 0; i < crafter.outputLiquids.length; i++){
            int side = i < crafter.liquidOutputDirections.length ? crafter.liquidOutputDirections[i] : -1;
            if(side != -1){
                int realRot = (side + build.rotation) % 4;
                Draw.rect(liquidOutputRegions[realRot > 1 ? 1 : 0][i], build.x, build.y, realRot * 90);
            }
        }
    }

    @Override
    public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list){
        GenericCrafter crafter = (GenericCrafter)block;
        if(crafter.outputLiquids == null) return;

        for(int i = 0; i < crafter.outputLiquids.length; i++){
            int side = i < crafter.liquidOutputDirections.length ? crafter.liquidOutputDirections[i] : -1;
            if(side != -1){
                int realRot = (side + plan.rotation) % 4;
                Draw.rect(liquidOutputRegions[realRot > 1 ? 1 : 0][i], plan.drawx(), plan.drawy(), realRot * 90);
            }
        }
    }
    public AquaGenericCrafter expectCrafterA(Block block){
        if(!(block instanceof AquaGenericCrafter crafter)) throw new ClassCastException("This drawer requires the block to be a GenericCrafter. Use a different drawer.");
        return crafter;
    }
    @Override
    public void load(Block block){
        var crafter = expectCrafterA(block);

        if(crafter.outputLiquids == null) return;

        liquidOutputRegions = new TextureRegion[2][crafter.outputLiquids.length];
        for(int i = 0; i < crafter.outputLiquids.length; i++){
            for(int j = 1; j <= 2; j++){
                liquidOutputRegions[j - 1][i] = Core.atlas.find(block.name + "-" + crafter.outputLiquids[i].liquid.name + "-output" + j);
            }
        }
    }
}