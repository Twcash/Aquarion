package aquarion.world.blocks.production;

import aquarion.world.blocks.graphs.HeatGraph;
import aquarion.world.consumers.ConsumeHeat;
import aquarion.world.interfaces.HasHeat;
import aquarion.world.meta.HeatConfig;
import aquarion.world.modules.HeatModule;
import arc.util.io.Reads;
import arc.audio.*;
import arc.util.io.Writes;
import mindustry.gen.Sounds;
import mindustry.world.blocks.production.GenericCrafter;

public class AquaGenCrafter extends GenericCrafter {
    public HeatConfig heatConfig = new HeatConfig();
    public float heatOutput = 0;
    public AquaGenCrafter(String name) {
        super(name);
    }
    public Sound craftSound = Sounds.none;
    public float craftSoundVolume = 1f;

    @Override
    public void setBars() {
        super.setBars();
        heatConfig.addBars(this);
    }
    public void consumeHeat(float heat, float minH, float maxH, float minE, float maxE) {
        consumeGas(new ConsumeHeat() {{
            amount = heat;
            minHeat = minH;
            maxHeat = maxH;
            minEfficiency = minE;
            maxEfficiency = maxE;
        }});
    }
    public void consumeGas(ConsumeHeat consumer) {
        consume(consumer);
    }
    public class AquaGenCrafterBuild extends GenericCrafterBuild implements HasHeat {
        public HeatModule heat = new HeatModule();

        @Override public HeatModule heat() {
            return heat;
        }
        @Override public HeatConfig heatConfig() {
            return heatConfig;
        }
        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            heat.read(read);
        }
        @Override
        public void craft() {
            super.craft();
            craftSound.at(x, y, 1f, craftSoundVolume);

            if (heatOutput > 0) {
                heat().addAmount(heatOutput);  // This is where heat is added during crafting
            }
        }

        @Override
        public void updateTile() {
            super.updateTile();

            if (efficiency > 0) {
                heat.addAmount(heatOutput * efficiency());  // Ensure heat is added based on efficiency
            }
        }
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

            // Trigger a heat update for this object.
            onHeatUpdate();
        }

        @Override
        public void onProximityRemoved() {
            super.onProximityRemoved();

            // Ensure this object is removed from its current graph.
            if (heatGraph() != null) {
                heatGraph().remove(this, true);
            }
        }
        @Override
        public void write(Writes write) {
            super.write(write);
            heat.write(write);
        }
    }
}
