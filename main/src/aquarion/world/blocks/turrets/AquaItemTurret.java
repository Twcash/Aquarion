package aquarion.world.blocks.turrets;

import aquarion.ui.AquaBarHelpers;
import aquarion.world.Uti.AquaStatValues;
import mindustry.type.LiquidStack;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.consumers.Consume;
import mindustry.world.consumers.ConsumeLiquid;
import mindustry.world.consumers.ConsumeLiquidFilter;
import mindustry.world.consumers.ConsumeLiquids;
import mindustry.world.meta.Stat;

public class AquaItemTurret extends ItemTurret implements AquaBarHelpers.CustomBarHolder {
    public AquaItemTurret(String name) {
        super(name);
    }
    @Override
    public void setStats(){
        super.setStats();
        stats.remove(Stat.ammo);
        stats.add(Stat.ammo, AquaStatValues.ammo(ammoTypes));
    }

    @Override
    public void setBars(){
        super.setBars();

        for(Consume consl : consumers){
            if(consl instanceof ConsumeLiquid liq){
                removeBar("liquid-" + liq.liquid.name);
                if(consl.booster){
                    addLiquidBoostBar(liq.liquid);
                } else {
                    addLiquidBar(liq.liquid);
                }
            }else if(consl instanceof ConsumeLiquids multi){
                for(LiquidStack stack : multi.liquids){
                    removeBar("liquid-" + stack.liquid.name);
                    if(consl.booster){
                        addLiquidBoostBar(stack.liquid);
                    }else {
                        addLiquidBar(stack.liquid);
                    }
                }
            }else if(consl instanceof ConsumeLiquidFilter filt){
                if(consl.booster){
                    addLiquidBoostBar(filt::getConsumed);
                }else {
                    addLiquidBar(filt::getConsumed);
                }
            }
        }
    }
}
