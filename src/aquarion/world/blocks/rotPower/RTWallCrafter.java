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
    public float rTOutput = 10f; // Field to modify how much rotation power it produces

    public RTWallCrafter(String name) {
        super(name);
        update = true; // This block will update every frame
    }

    @Override
    public void init() {
        super.init();
        // Initialize the RTConfig and RTModule for the block
        config(RTConfig.class, (RTWallCrafterBuild tile, RTConfig value) -> {
            tile.rtConfig = value;
        });
    }

    public class RTWallCrafterBuild extends WallCrafterBuild implements HasRT {
        private RTModule rtModule = new RTModule();
        private RTConfig rtConfig = new RTConfig();
        private float rTOutput = RTWallCrafter.this.rTOutput;

        @Override
        public void placed() {
            super.placed();
            rTGraph().addBuild(this);
        }

        @Override
        public void onProximityUpdate() {
            super.onProximityAdded();
            rTGraph().addBuild(this);

        }
        @Override
        public HasRT getRTDest(HasRT from) {
            return this;
        }

        @Override
        public Seq<HasRT> nextBuilds() {
            Seq<HasRT> builds = new Seq<>();
            for (Building b : proximity()) {
                if (b instanceof HasRT other && connects(other) && other != this) {
                    builds.add(other.getRTDest(this));
                }
            }
            return builds;
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
        public RTGraph rTGraph() {
            return rotationPower().graph;
        }

        @Override
        public float RotationPower() {
            return rTConfig.rotationPower;
        }

        public void produceRotationPower() {
            float totalPower = rTGraph().getRT() + rTOutput;
            rTConfig.rotationPower = totalPower;
            rtModule.graph.builds.each(b -> {
                if (b != this) {
                    b.rTConfig().rotationPower += rTOutput / rtModule.graph.builds.size;
                }
            });
        }

        @Override
        public void updateTile() {
            super.updateTile();
            produceRotationPower();
        }
    }
}