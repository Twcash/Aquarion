package aquarion.world.blocks.distribution;

import arc.math.Mathf;
import arc.struct.Seq;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.world.blocks.liquid.LiquidRouter;

import static mindustry.Vars.state;

public class ModifiedLiquidRouter extends LiquidRouter {
    public boolean willMelt = false;

    public ModifiedLiquidRouter(String name) {
        super(name);
    }

    public class ughBuild extends LiquidRouterBuild {
        public void moveLiquide(Liquid liquid){
            if(liquids.get(liquid) <= 0.0001f) return;
            if(state.isCampaign() && team == state.rules.defaultTeam) liquid.unlock();
            Seq<Building> valid = new Seq<>();
            for(Building other : proximity){
                if(other == null) continue;
                Building dest = other.getLiquidDestination(self(), liquid);
                if(dest != null && dest.block.hasLiquids && canDumpLiquid(dest, liquid) && dest.liquids != null){
                    valid.add(dest);
                }
            }
            if(valid.isEmpty()) return;
            float totalAvailable = liquids.get(liquid);
            float perTarget = totalAvailable / valid.size;
            for(Building dest : valid){
                float space = dest.block.liquidCapacity - dest.liquids.get(liquid);
                float flow = Math.min(perTarget, space);
                if(flow > 0.001f){
                    transferLiquid(dest, flow, liquid);
                }
            }
        }


        @Override
        public void updateTile() {
            if (liquids.currentAmount() < 0.01f) return;
            Liquid liquid = liquids.current();
            moveLiquide(liquid);
            if (liquids.currentAmount() > 0.1f && liquid.temperature > 0.5f && !willMelt) {
                damageContinuous(liquid.temperature / 100f);
                if (Mathf.chanceDelta(0.01f)) {
                    Fx.steam.at(x, y);
                }
            }
        }
    }
}