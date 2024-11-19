package aquarion.world.blocks.distribution;

import aquarion.world.blocks.graphs.HeatGraph;
import aquarion.world.interfaces.HasHeat;
import aquarion.world.meta.HeatConfig;
import aquarion.world.modules.HeatModule;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.world.Block;

public class HeatPipe extends Block {
    public HeatConfig heatConfig = new HeatConfig();

    public HeatPipe(String name) {
        super(name);
        update = true;
        rotate = false;
        noUpdateDisabled = true;
        // Ensure that bars are added
    }
    @Override
    public void setBars() {
        super.setBars();
        heatConfig.addBars(this);
    }

    public class HeatPipeBuild extends Building implements HasHeat {
        public HeatModule heat = new HeatModule();

        @Override
        public void onProximityUpdate() {
            super.onProximityUpdate();

            // Create a new graph if this object is not already in one.
            if (heatGraph() == null) {
                HeatGraph newGraph = new HeatGraph();
                newGraph.add(this);
            }

            // Add all valid connected objects to the same graph.
            nextBuilds().each(build -> {
                if (heatGraph() != null) {
                    heatGraph().add(build);
                }
            });

            // Ensure the heat graph is updated
            if (heatGraph() != null) {
                heatGraph().updateHeat(); // Make sure the graph updates the heat flow
            }

            // Trigger a heat update for this object.
            onHeatUpdate();
        }

        @Override
        public void onProximityRemoved() {
            super.onProximityRemoved();

            // Ensure this object is removed from its current graph and no heat transfer continues
            if (heatGraph() != null) {
                heatGraph().remove(this, true);  // Ensure no lingering heat effects
            }
        }
        @Override
        public void update() {
            super.update();

            // Continuously update the heat graph for this building.
            if (heatGraph() != null) {
                heatGraph().updateHeat();  // Ensure the graph updates heat transfer
            }

            // Trigger a heat update for this object
            onHeatUpdate();
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            heat.read(read);
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            heat.write(write);
        }

        @Override
        public HeatModule heat() {
            return heat;
        }

        @Override
        public HeatConfig heatConfig() {
            return heatConfig;
        }
    }
}