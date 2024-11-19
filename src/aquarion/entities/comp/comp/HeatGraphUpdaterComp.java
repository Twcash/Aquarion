package aquarion.entities.comp.comp;

import aquarion.gen.HeatGraphUpdater;
import aquarion.gen.HeatGraphUpdaterc;
import aquarion.world.blocks.graphs.HeatGraph;
import arc.util.Log;
import ent.anno.Annotations;
import mindustry.gen.Entityc;
//uujuju1 mainly made this code I just modified it so I could learn graphs less painfully. Thank you!
@Annotations.EntityComponent(base = true)
@Annotations.EntityDef(value = {HeatGraphUpdaterc.class}, serialize = false)
abstract class HeatGraphUpdaterComp implements Entityc {
    public transient HeatGraph graph;

    public HeatGraphUpdater setGraph(HeatGraph newGraph) {
        graph = newGraph;
        return self();
    }

    @Override
    public void update() {
        if (graph != null) {
            return;
        } else {
            remove();
        }
    }
}