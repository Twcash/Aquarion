package aquarion.world.interfaces;

import aquarion.world.blocks.graphs.HeatGraph;
import aquarion.world.meta.HeatConfig;
import aquarion.world.modules.HeatModule;
import arc.math.geom.Point2;
import arc.struct.Seq;
import mindustry.gen.Buildingc;

//uujuju1 mainly made this code I just modified it so I could learn graphs less painfully. Thank you!


public interface HasHeat extends Buildingc {
    /**
     * Determines if a heat building can connect to another building. Mutually inclusive.
     */
    static boolean connects(HasHeat from, HasHeat to) {
        return from.connectTo(to) && to.connectTo(from);
    }

    /**
     * Indicates if this building connects to another building.
     */
    default boolean connectTo(HasHeat other) {
        return heatConfig().hasHeat &&
                other.team() == team() &&
                (heatConfig().connections.isEmpty() ||
                        heatConfig().connections.contains(new Point2(other.tileX(), other.tileY()).sub(tileX(), tileY())));
    }

    /**
     * Returns the associated HeatModule for this building.
     */
    HeatModule heat();

    /**
     * Returns the configuration settings for heat.
     */
    HeatConfig heatConfig();

    /**
     * Returns the associated HeatGraph for this building.
     */
    default HeatGraph heatGraph() {
        return heat().graph;
    }

    /**
     * Gets the heat value for this building.
     * If the building is connected to a graph, it returns the shared heat from the graph.
     */
    default float getHeat() {
        // If this building belongs to a graph, return the shared heat from the graph
        HeatGraph graph = heatGraph();
        if (graph != null && graph.sharedHeat() != null) {
            return graph.sharedHeat().heat;
        }
        return heat().heat;  // Default to the building's own heat if not part of a graph
    }

    /**
     * Sets the heat value for this building.
     * If the building is connected to a graph, it updates the shared heat in the graph.
     */
    default void setHeat(float value) {
        HeatGraph graph = heatGraph();
        if (graph != null && graph.sharedHeat() != null) {
            // Set the shared heat in the graph, so all connected blocks will share it
            graph.sharedHeat().heat = value;
        } else {
            // If no graph, set the local heat of the building
            heat().heat = value;
        }
    }

    /**
     * Gets a list of buildings that this building can connect to.
     */
    default Seq<HasHeat> nextBuilds() {
        return proximity()
                .select(b -> b instanceof HasHeat a && connects(this, a.getHeatDest(this)))
                .map(a -> ((HasHeat) a).getHeatDest(this));
    }

    /**
     * Returns the destination building to connect with the heat system.
     */
    default HasHeat getHeatDest(HasHeat from) {
        return this;
    }

    /**
     * Triggered when the heat state or graph is updated.
     */
    default void onHeatUpdate() {
        // Implement specific behavior if needed when the heat state changes.
    }
}