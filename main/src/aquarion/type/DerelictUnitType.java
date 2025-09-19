package aquarion.type;

import arc.graphics.Color;
import mindustry.gen.UnitEntity;
import mindustry.type.UnitType;
import mindustry.world.meta.Env;

public class DerelictUnitType extends UnitType {
    public DerelictUnitType(String name) {
        super(name);
        speed = 0;
        rotateSpeed = 0;
        hidden = true;
        hitSize = 10;
        health = 10*hitSize;
        drag = 0.99f;
        envEnabled |= Env.terrestrial | Env.underwater;
        envDisabled = Env.none;
        accel = 0;
        isEnemy = false;
        drawCell = false;
        outlineColor = Color.valueOf("141615");
        flying = false;
        logicControllable = false;
        playerControllable = false;
        targetable = true;
        hittable = true;
        constructor = UnitEntity::create;
    }
}
