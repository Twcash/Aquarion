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
public class RTConsumer extends Block {
    public RTConfig rTConfig = new RTConfig();
    public float consumption = 10f; // Fixed amount of rotation power consumed

    public RTConsumer(String name) {
        super(name);
        update = true; // This block will update every frame
    }

    @Override
    public void init() {
        super.init();
        config(RTConfig.class, (RTConsumerBuild tile, RTConfig value) -> {
            tile.rtConfig = value;
        });
    }

    public class RTConsumerBuild extends Building implements HasRT {
        private RTModule rtModule = new RTModule();
        private RTConfig rtConfig = new RTConfig();
        private float rTConsumption = RTConsumer.this.consumption; // Fixed amount of rotation power consumed

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
        public void onProximityUpdate() {
            super.onProximityUpdate();
            rTGraph().addBuilding(this);
            updateGraph(); // Ensuring that the graph is updated with connected buildings
            consumeRotationPower();
        }

        public void consumeRotationPower() {
            float availablePower = rTGraph().getTotalRotationPower();
            if (availablePower > 0) {
                float consumption = Math.min(rTConsumption, availablePower);
                float newPower = availablePower - consumption;
                rTGraph().removeRotationPower(this, (int) consumption); // Remove consumed power from the graph
            }
        }

        @Override
        public void draw() {
            super.draw();
            Draw.rect(region, x, y);
        }

        @Override
        public float getRotationPower() {
            return rtModule.rotationPower;
        }

        @Override
        public void setRotationPower(float rotationPower) {
            rtModule.rotationPower = rotationPower;
        }
    }
}