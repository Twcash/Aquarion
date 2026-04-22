package aquarion.world.blocks.turrets;

import aquarion.world.Uti.AquaStatValues;
import arc.struct.ObjectMap;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.meta.Stat;

public class AquaPowerTurret extends PowerTurret {
    public AquaPowerTurret(String name) {
        super(name);
    }
    @Override
    public void setStats(){
        super.setStats();
        stats.remove(Stat.ammo);
        stats.add(Stat.ammo, AquaStatValues.ammo(ObjectMap.of(this, shootType)));
    }
}
