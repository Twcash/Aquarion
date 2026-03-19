package aquarion.world.blocks.turrets;

import aquarion.world.Uti.AquaStatValues;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.meta.Stat;

public class AquaItemTurret extends ItemTurret {
    public AquaItemTurret(String name) {
        super(name);
    }
    @Override
    public void setStats(){
        super.setStats();
        stats.remove(Stat.ammo);
        stats.add(Stat.ammo, AquaStatValues.ammo(ammoTypes));
    }
}
