package aquarion.world.units;

import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
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
