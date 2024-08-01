package aquarion.world.blocks.rotPower;

import aquarion.world.interfaces.HasRT;
import aquarion.world.meta.RTConfig;
import aquarion.world.meta.RTModule;
import aquarion.world.graphs.RTGraph;
import arc.Core;
import arc.struct.Seq;
import mindustry.world.Block;
import mindustry.entities.units.BuildPlan;
import arc.graphics.g2d.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;

public class RTProducer extends Block {
    public RTConfig rTConfig = new RTConfig();
    public float output = 10f; // Field to modify how much rotation power it produces

    public RTProducer(String name) {
        super(name);
        update = true; // This block will update every frame
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("rotation-power", (RTProducerBuild entity) -> new Bar(
                () -> Core.bundle.format("bar.rotationpower", entity.RotationPower()),
                () -> Pal.powerBar,
                () -> entity.RotationPower() / 100f // Assuming max rotation power is 100
        ));
    }

    @Override
    public void init() {
        super.init();
        config(RTConfig.class, (RTProducerBuild tile, RTConfig value) -> {
            tile.rtConfig = value;
        });
    }

    public class RTProducerBuild extends Building implements HasRT {
        private RTModule rtModule = new RTModule();
        private RTConfig rtConfig = new RTConfig();
        private float rToutput = output; // Fixed rotation power

        @Override
        public void placed() {
            super.placed();
            updateGraph();
        }

        @Override
        public void onProximityUpdate() {
            super.onProximityUpdate();
            updateGraph();
        }

        private void updateGraph() {
            RTGraph oldGraph = rTGraph();
            RTGraph newGraph = new RTGraph();
            rTConfig.graphs = true; // Ensure the build is active in the graph
            newGraph.addBuild(this);
            if (oldGraph != newGraph) {
                oldGraph.removeBuild(this, false);
                rTGraph().addBuild(this);
            }
        }

        @Override
        public RTModule rotationPower() {
            return this.rtModule;
        }

        @Override
        public RTConfig rTConfig() {
            return this.rtConfig;
        }

        @Override
        public RTGraph rTGraph() {
            return rotationPower().graph;
        }

        @Override
        public float RotationPower() {
            return rTConfig.rotationPower; // Fixed rotation power
        }

        private void updateRotationPower() {
            float currentPower = rTConfig.rotationPower;
            rTConfig.rotationPower = rToutput; // Fixed output value
            if (rTConfig.rotationPower != currentPower) {
                // Update the graph with the new power value
                rTGraph().addBuild(this);
            }
        }

        @Override
        public void updateTile() {
            super.updateTile();
            updateRotationPower();
        }

        @Override
        public void draw() {
            super.draw();
            Draw.rect(region, x, y);
        }
    }
}