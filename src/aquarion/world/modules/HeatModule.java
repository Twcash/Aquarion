package aquarion.world.modules;

import aquarion.world.blocks.graphs.HeatGraph;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.world.modules.BlockModule;
//uujuju1 mainly made this code I just modified it so I could learn graphs less painfully. Thank you!
public class HeatModule extends BlockModule {
    public float heat = 0;

    public HeatGraph graph = new HeatGraph();

    public void addAmount(float value) {
        heat += value;
    }

    @Override
    public void read(Reads read) {
        heat = read.f();
    }

    public void setAmount(float value) {
        heat = value;
    }

    public void subAmount(float value) {
        heat -= value;
    }

    @Override
    public void write(Writes write) {
        write.f(heat);
    }
}