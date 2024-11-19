package aquarion.world.blocks.graphs;

import aquarion.gen.HeatGraphUpdater;
import aquarion.world.interfaces.HasHeat;
import aquarion.world.meta.HeatConfig;
import aquarion.world.modules.HeatModule;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Time;
import mindustry.gen.Building;
//uujuju1 mainly made this code I just modified it so I could learn graphs less painfully. Thank you!
/**
 * Graph containing an isolated group of uildings sharing common stuff.
 */
public class HeatGraph {
    public final HeatGraphUpdater updater;
    public final Seq<HasHeat> all = new Seq<>(false, 16, HasHeat.class);
    public boolean changed;
    private final int graphID;
    private static int lastGraphID = 0;

    private HeatModule sharedHeat; // Shared heat for the entire graph

    public HeatGraph() {
        updater = HeatGraphUpdater.create().setGraph(this);
        graphID = lastGraphID++;
    }

    /**
     * Adds a building to the graph, sharing the same heat value across all connected blocks.
     */
    public void add(HasHeat obj) {
        if (obj.heatGraph() != null) {
            obj.heatGraph().remove(obj, false);
            changed = true;
        }
        if (sharedHeat == null) {
            sharedHeat = obj.heat();
        } else {
            // Sync the new object's heat with the shared heat
            obj.heat().heat = sharedHeat.heat;
        }

        all.add(obj);
        obj.heat().graph = this;

        all.each(HasHeat::onHeatUpdate);
    }

    /**
     * Removes a building from the graph. Splits the graph if necessary.
     */
    public void remove(HasHeat obj, boolean split) {
        all.remove(obj);
        obj.heat().graph = null;
        changed = true;
        if (split) {
            Seq<HasHeat> connected = new Seq<>();
            obj.nextBuilds().each(next -> gatherConnected(next, connected));

            if (!connected.isEmpty()) {
                HeatGraph newGraph = new HeatGraph();
                connected.each(newGraph::add);
            }
        }

        // Update shared heat reference if graph becomes empty
        if (all.isEmpty()) {
            sharedHeat = null;
        }

        all.each(HasHeat::onHeatUpdate);
    }

    /**
     * Gathers all connected objects into the provided sequence.
     */
    private void gatherConnected(HasHeat obj, Seq<HasHeat> connected) {
        if (connected.contains(obj)) return;

        connected.add(obj);
        obj.nextBuilds().each(next -> {
            if (!connected.contains(next)) gatherConnected(next, connected);
        });
    }

    /**
     * Gets the shared heat module.
     */
    public HeatModule sharedHeat() {
        if(sharedHeat != null) {
            return sharedHeat;
        }
        return null;
    }
    public void updateHeat() {
        // If the graph has changed, reinitialize the shared heat pool
        if (changed) {
            // Notify all connected blocks about the graph update
            all.each(HasHeat::onHeatUpdate);

            // Mark the graph as stable
            changed = false;
        }

        // Notify all blocks in the graph of the current heat state
        all.each(HasHeat::onHeatUpdate);
    }
}