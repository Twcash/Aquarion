package aquarion.world.meta;


import aquarion.world.graphs.RTGraph;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.world.modules.BlockModule;

/**
 * only holds a graph so that it's easier to change it
 */
public class RTModule {
    public RTGraph graph = new aquarion.world.graphs.RTGraph();
}