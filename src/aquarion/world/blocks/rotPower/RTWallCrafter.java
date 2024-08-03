package aquarion.world.blocks.rotPower;

import aquarion.world.interfaces.HasRT;
import aquarion.world.meta.RTConfig;
import aquarion.world.meta.RTModule;
import aquarion.world.graphs.RTGraph;
import arc.struct.Seq;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.production.WallCrafter;


public class RTWallCrafter extends WallCrafter {
    public RTConfig rTConfig = new RTConfig();
    public float requiredRotationPower = 10f; // Field for the required rotation power

    public RTWallCrafter(String name) {
        super(name);
        update = true; // This block will update every frame
    }

    @Override
    public void init() {
        super.init();
        config(RTConfig.class, (RTWallCrafterBuild tile, RTConfig value) -> {
            tile.rtConfig = value;
        });
    }

    public class RTWallCrafterBuild extends WallCrafterBuild implements HasRT {
        private RTModule rtModule = new RTModule();
        private RTConfig rtConfig = new RTConfig();
        private float receivedRotationPower;

        @Override
        public void placed() {
            super.placed();
            rTGraph().addBuilding(this);
        }

        @Override
        public void onProximityUpdate() {
            super.onProximityAdded();
            rTGraph().addBuilding(this);
        }

        @Override
        public void update() {
            super.update();
            consumeRotationPower();
        }

        public void consumeRotationPower() {
            float availablePower = rTGraph().getTotalRotationPower();
            if (availablePower > 0) {
                float consumption = Math.min(requiredRotationPower, availablePower);
                receivedRotationPower = consumption;
                rTGraph().removeRotationPower(this, (int) consumption); // Remove consumed power from the graph
            } else {
                receivedRotationPower = 0f;
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
        public float getRotationPower() {
            return this.receivedRotationPower;
        }

        @Override
        public void setRotationPower(float rotationPower) {
            this.receivedRotationPower = rotationPower;
        }

        // Calculate efficiency based on the received rotation power
        public float getEfficiency() {
            if (requiredRotationPower <= 0) return 1f; // Avoid division by zero
            return Math.min(receivedRotationPower / requiredRotationPower, 1f);
        }
    }
}