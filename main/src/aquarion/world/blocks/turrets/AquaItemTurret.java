package aquarion.world.blocks.turrets;

import aquarion.ui.AquaBarHelpers;
import aquarion.world.Uti.AquaStatValues;
import aquarion.world.entities.bullet.AquaBulletType;
import mindustry.core.World;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.Fires;
import mindustry.gen.Fire;
import mindustry.type.LiquidStack;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.consumers.Consume;
import mindustry.world.consumers.ConsumeLiquid;
import mindustry.world.consumers.ConsumeLiquidFilter;
import mindustry.world.consumers.ConsumeLiquids;
import mindustry.world.meta.Stat;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;
public class AquaItemTurret extends ItemTurret implements AquaBarHelpers.CustomBarHolder {
    public boolean extinguish = false;
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
    public class AquaItemTurretBuild extends ItemTurretBuild{
        //Will crash when the new release of Mindustry is made!!!!
        public UnlockableContent getAmmoContent(){
            return ammo.size > 0 ? ((ItemEntry)ammo.peek()).item : null;
        }
        public int maxAmmo(){
            return maxAmmo;
        }
        @Override
        protected void findTarget(){
            if(extinguish && peekAmmo() instanceof AquaBulletType bul && bul.extinguishFires){
                int tx = World.toTile(x), ty = World.toTile(y);
                Fire result = null;
                float mindst = 0f;
                int tr = (int)(range / tilesize);
                for(int x = -tr; x <= tr; x++){
                    for(int y = -tr; y <= tr; y++){
                        Tile other = world.tile(x + tx, y + ty);
                        var fire = Fires.get(x + tx, y + ty);
                        float dst = fire == null ? 0 : dst2(fire);
                        //do not extinguish fires on other team blocks
                        if(other != null && fire != null && other.build != this && Fires.has(other.x, other.y) && dst <= range * range && (result == null || dst < mindst) && (other.build == null || other.team() == team)){
                            result = fire;
                            mindst = dst;
                        }
                    }
                }

                if(result != null){
                    target = result;
                    return;
                }
            }

            super.findTarget();
        }
    }
}
