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
        public boolean connects(HasRT to) {
            return to.rTConfig().connects;
        }
        @Override
        public void onProximityRemoved() {
            super.onProximityRemoved();
            rTGraph().removeBuild(this, false);
        }
        @Override public float RotationPower() {
            return efficiency * output;
        }
        @Override
        public void onProximityUpdate() {
            super.onProximityAdded();
            rTGraph().removeBuild(this, true);
        }

        @Override
        public void draw() {
            super.draw();
            Draw.rect(region, x, y);
        }
    }
}