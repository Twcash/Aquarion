package aquarion.entities.comp;


import aquarion.world.AquaTeams;
import arc.util.Time;
import mindustry.game.Team;
import mindustry.gen.Healthc;
import mindustry.gen.Hitboxc;
import mindustry.gen.Teamc;
import mindustry.gen.Unitc;
import ent.anno.Annotations.*;
import mindustry.type.UnitType;

import static mindustry.gen.Nulls.unit;


@SuppressWarnings({"unused", "UnnecessaryReturnStatement"})
@EntityComponent
abstract class DerelictComp implements Unitc, Healthc, Hitboxc, Teamc {
    @Import
    Team team = (AquaTeams.wrecks);;
    public float timeUntilDerelict = 100;  // Timer before becoming derelict
    @Import UnitType type;

    @Replace
      @Override
    public void update() {
        type.update(self());
        // Your regular update logic here
    }

    // Override the kill() method to prevent death
}