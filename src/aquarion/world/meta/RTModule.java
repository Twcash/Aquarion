package aquarion.world.meta;


import aquarion.world.graphs.RTGraph;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.world.modules.BlockModule;

/**
 * only holds a graph so that it's easier to change it
 */
public class RTModule extends BlockModule {
    public RTGraph graph = new aquarion.world.graphs.RTGraph();
    public float rotationPower = 0;
    @Override
    public void read(Reads read) {
        rotationPower = read.f();
    }

    @Override
    public void write(Writes write) {
        write.f(rotationPower);
    }
}