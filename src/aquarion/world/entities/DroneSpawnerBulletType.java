package aquarion.world.entities;

import aquarion.units.AquaUnitTypes;
import aquarion.world.AI.DroneAI;
import arc.util.Log;
import mindustry.content.Fx;
import mindustry.content.UnitTypes;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.units.UnitController;
import mindustry.gen.Bullet;
import mindustry.gen.Unit;
import mindustry.type.UnitType;

public class DroneSpawnerBulletType extends BasicBulletType {
public UnitType drone = UnitTypes.flare;
    public DroneSpawnerBulletType() {
        super(0f, 0f);
        collides = false;
        hittable = false;
        despawnEffect = Fx.none;
    }

    @Override
    public void update(Bullet b) {
        if (b.owner instanceof Unit unit) {
                    Unit dron = drone.spawn(unit.team, b.x, b.y);
                    dron.type.controller = u -> new DroneAI();
                    UnitController ab = dron.controller();
                    if(ab instanceof DroneAI ai) {
                        Log.info("This should work");
                        ai.initiatied = true;
                        ai.shooter = unit;
                        b.remove();
                    }
        }
    }
}