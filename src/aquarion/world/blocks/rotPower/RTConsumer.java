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
    public float consumption = 10f; // Field to modify how much rotation power it consumes

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
        private float RTconsumption = RTConsumer.this.consumption; // Field to modify how much rotation power it consumes

        @Override
        public void onProximityUpdate() {
            super.onProximityUpdate();
        }

        @Override
        public HasRT getRTDest(HasRT from) {
            return this;
        }

        @Override
        public Seq<HasRT> nextBuilds() {
            return proximity().select(b -> b instanceof HasRT other && connects(other))
                    .map(b -> ((HasRT) b).getRTDest(this))
                    .removeAll(b -> !connects(b) && !b.connects(this) && b.rTConfig().graphs);
        }
        @Override
        public boolean connects(HasRT to) {
            return to.rTConfig().connects;
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
        public void onProximityRemoved() {
            super.onProximityRemoved();
            rTGraph().addBuild(this);
        }

        @Override
        public RTGraph rTGraph() {
            return rotationPower().graph;
        }

        @Override
        public float RotationPower() {
            return rTConfig.rotationPower;
        }

        public void consumeRotationPower() {
            float availablePower = rTGraph().getRT();
            if (availablePower > 0) {
                float consumption = Math.min(RTconsumption, availablePower);
                float newPower = rTConfig.rotationPower - consumption;
                rTConfig.rotationPower = Math.max(newPower, 0); // Ensure it does not go below 0
            }
        }

        @Override
        public void draw() {
            super.draw();
            Draw.rect(region, x, y);
        }
    }
}