package aquarion.type;

import mindustry.game.Team;
import mindustry.gen.Bullet;
import mindustry.gen.Unit;
import mindustry.type.UnitType;

public class DerelictUnitType extends UnitType {

    public DerelictUnitType(String name) {
        super(name);
        speed = 0f;       // No movement
        rotateSpeed = 0f; // No rotation
        health = 100;     // Set some default health
    }

    @Override
    public void update(Unit unit) {
        super.update(unit);
    }
}