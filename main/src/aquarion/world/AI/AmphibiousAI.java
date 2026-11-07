package aquarion.world.AI;

import aquarion.world.units.AmphibiousPathfinder;
import mindustry.entities.units.AIController;
import mindustry.gen.Building;
import mindustry.gen.Teamc;

import static mindustry.Vars.tilesize;

/** Autonomous enemies use the same hop/swim graph as commanded units. */
public class AmphibiousAI extends AIController {
    private final AmphibiousPathfinder path = new AmphibiousPathfinder();

    @Override
    public void updateMovement(){
        Teamc destination = target != null ? target : unit.closestEnemyCore();
        if(destination == null) return;
        float stop = AmphibiousPathfinder.clearShot(unit, destination)
            ? Math.max(unit.range() * 0.8f, unit.hitSize) : unit.hitSize;
        if(destination instanceof Building build) stop += build.block.size * tilesize / 2f;
        path.move(unit, destination.x(), destination.y(), stop);
        faceTarget();
    }
}
