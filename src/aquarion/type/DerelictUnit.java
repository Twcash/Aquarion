package aquarion.type;

import aquarion.world.AquaTeams;
import arc.graphics.Color;
import arc.util.Time;
import mindustry.game.Team;
import mindustry.gen.Bullet;
import mindustry.gen.Unit;
import mindustry.gen.UnitEntity;
import mindustry.type.UnitType;
import mindustry.world.meta.Env;

public class DerelictUnit extends UnitType {

    public float timeUntilDerelict = 100; //wee woo wee woo cringe code

    public DerelictUnit(String name) {
        super(name);
        speed = 0;
        rotateSpeed = 0;
        hidden = true;
        health = 210;
        drag = 0.99f;
        envEnabled|= Env.terrestrial | Env.underwater;
        envDisabled|= Env.spores | Env.scorching;
        accel = 0;
        isEnemy = false;
        drawCell = false;
        outlineColor = Color.valueOf("141615");
        flying = false;
        logicControllable = false;
        playerControllable = false;
        targetable = false;
        hittable = true;
        constructor = UnitEntity::create;
    }

    @Override
    public void update(Unit unit) {
        super.update(unit);

        // Reduce the time until derelict
        timeUntilDerelict -= Time.delta;

        // Check if time has run out
        if (timeUntilDerelict <= 0) {
            unit.team(AquaTeams.wrecks);  // Switch to the wreck team as switching to derelict causes "issues"
        }
    }
}