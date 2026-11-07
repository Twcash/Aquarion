package aquarion.world.units;

import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.gen.EntityMapping;
import mindustry.gen.PayloadUnit;
import mindustry.world.Block;

/** Remembers the consumed missile load across saves and network updates. */
public class PayloadRailUnit extends PayloadUnit{
    private static final int entityId = EntityMapping.register("aquarion-payload-rail-unit", PayloadRailUnit::new);
    public Block missileLoad;

    @Override public int classId(){ return entityId; }
    @Override public void write(Writes write){ super.write(write); write.s(missileLoad == null ? -1 : missileLoad.id); }
    @Override public void read(Reads read){ super.read(read); missileLoad = Vars.content.block(read.s()); }
    @Override public void writeSync(Writes write){ super.writeSync(write); write.s(missileLoad == null ? -1 : missileLoad.id); }
    @Override public void readSync(Reads read){ super.readSync(read); missileLoad = Vars.content.block(read.s()); }
}
