package aquarion.world.units;

import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.entities.Damage;
import mindustry.entities.Puddles;
import mindustry.gen.EntityMapping;
import mindustry.gen.UnitEntity;
import mindustry.type.Liquid;

/** One saved, synchronized tank per car. */
public class LiquidRailUnit extends UnitEntity{
    private static final int entityId = EntityMapping.register("aquarion-liquid-rail-unit", LiquidRailUnit::new);
    public Liquid cargoLiquid;
    public float liquidAmount;
    /** Local presentation state; the actual tank remains authoritative. */
    public transient Liquid displayLiquid;
    public transient float displayFill;

    @Override public int classId(){ return entityId; }

    @Override public void destroy(){
        if(!isAdded() || !killable()) return;
        Liquid liquid = cargoLiquid;
        float amount = liquidAmount;
        // Empty first: explosion damage can destroy other cars and re-enter death handling.
        cargoLiquid = null;
        liquidAmount = 0f;
        if(!Vars.net.client() && liquid != null && amount > 0f){
            int radius = Math.max(1, (int)Math.ceil(Math.sqrt(amount / Puddles.maxLiquid / Math.PI)));
            int cells = 0;
            for(int dx = -radius; dx <= radius; dx++){
                for(int dy = -radius; dy <= radius; dy++){
                    if(dx * dx + dy * dy <= radius * radius && Vars.world.tile(tileX() + dx, tileY() + dy) != null) cells++;
                }
            }
            if(cells > 0){
                for(int dx = -radius; dx <= radius; dx++){
                    for(int dy = -radius; dy <= radius; dy++){
                        if(dx * dx + dy * dy <= radius * radius){
                            Puddles.deposit(Vars.world.tile(tileX() + dx, tileY() + dy), liquid, amount / cells);
                        }
                    }
                }
            }
            if(liquid.flammability > 0f || liquid.explosiveness > 0f){
                Damage.dynamicExplosion(x, y, liquid.flammability * amount / 2f,
                    liquid.explosiveness * amount / 2f, 0f, hitSize / 2f,
                    Vars.state.rules.damageExplosions, liquid.flammability > 0.3f, team,
                    type.deathExplosionEffect, 0f);
            }
        }
        super.destroy();
    }
    private void writeTank(Writes write){
        write.s(cargoLiquid == null ? -1 : cargoLiquid.id);
        write.f(liquidAmount);
    }
    private void readTank(Reads read){
        cargoLiquid = Vars.content.liquid(read.s());
        liquidAmount = Math.max(0f, read.f());
        if(cargoLiquid == null) liquidAmount = 0f;
    }
    @Override public void write(Writes write){ super.write(write); writeTank(write); }
    @Override public void read(Reads read){ super.read(read); readTank(read); }
    @Override public void writeSync(Writes write){ super.writeSync(write); writeTank(write); }
    @Override public void readSync(Reads read){ super.readSync(read); readTank(read); }
}
