package aquarion.world.blocks.distribution;

import arc.math.Mathf;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.entities.Puddles;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.world.Build;
import mindustry.world.Tile;
import mindustry.world.blocks.liquid.LiquidRouter;

import static mindustry.Vars.state;

public class ModifiedLiquidRouter extends LiquidRouter {
    public boolean willMelt = false;

    public ModifiedLiquidRouter(String name) {
        super(name);
    }

    public class ughBuild extends LiquidRouterBuild {

        public void updateTransport(Building other){
            if(liquids.currentAmount() > 0.001f){
                 moveLiquid(other, liquids.current());
            }
            Liquid liquid = liquids.current();

            if(liquids.currentAmount() > 0.1f && liquid.temperature > 0.5f && !willMelt){
                damageContinuous(liquid.temperature/100f);
                if(Mathf.chanceDelta(0.01)){
                    Fx.steam.at(x, y);
                }
            }
        }
        public void moveLiquide(Liquid liquid, int outputDir){
            int dump = this.cdump;

            if(liquids.get(liquid) <= 0.0001f) return;

            if(state.isCampaign() && team == state.rules.defaultTeam) liquid.unlock();

            for(int i = 0; i < proximity.size; i++){
                incrementDump(proximity.size);

                Building other = proximity.get((i + dump) % proximity.size);
                if(outputDir != -1 && (outputDir + rotation) % 4 != relativeTo(other)) continue;

                other = other.getLiquidDestination(self(), liquid);

                if(other != null && other.block.hasLiquids && canDumpLiquid(other, liquid) && other.liquids != null){
                    float levelHere = liquids.get(liquid) / block.liquidCapacity;
                    float levelNext = other.liquids.get(liquid) / other.block.liquidCapacity;
                    float deltaLevel = Math.max(levelHere - levelNext, 0f) * 50;

                    float rho = 1f;
                    float viscosityFactor = Mathf.clamp(1f - liquid.viscosity * 0.5f, 0.2f, 1f);
                    float Cd = 0.8f;
                    float A = 1f;

                    float flow = Cd * A * Mathf.sqrt(2f * deltaLevel / rho) * viscosityFactor;

                    flow *= 10f;

                    flow = Math.min(flow, liquids.get(liquid));
                    flow = Math.min(flow, other.block.liquidCapacity - other.liquids.get(liquid));

                    if(flow > 0.001) transferLiquid(other, flow, liquid);
                }
            }
        }

        @Override
        public void updateTile() {
            if (liquids.currentAmount() < 0.01f) return;
            Liquid liquid = liquids.current();
            moveLiquide(liquid, -1);
            if (liquids.currentAmount() > 0.1f && liquid.temperature > 0.5f && !willMelt) {
                damageContinuous(liquid.temperature / 100f);
                if (Mathf.chanceDelta(0.01f)) {
                    Fx.steam.at(x, y);
                }
            }
        }
    }
}