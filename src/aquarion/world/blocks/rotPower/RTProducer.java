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
    public float output = 10f; // Fixed amount of rotation power produced

    public RTProducer(String name) {
        super(name);
        update = true; // This block will update every frame
        solid = true;
        hasPower = true;
    }

    @Override
    public void init() {
        super.init();
        config(RTConfig.class, (RTProducerBuild tile, RTConfig value) -> {
            tile.rtConfig = value;
        });
    }

    public class RTProducerBuild extends Building implements HasRT {
        public RTConfig rtConfig;
        private float rotationPower;
        private boolean addedToGraph = false;

        @Override
        public void updateTile() {
            if (!addedToGraph) {
                rTGraph().addBuilding(this);
                rTGraph().addRotationPower(this, (int) output);
                addedToGraph = true;
            }
        }

        @Override
        public void onProximityRemoved() {
            super.onProximityRemoved();
            if (addedToGraph) {
                rTGraph().removeRotationPower(this, (int) output);
                addedToGraph = false;
            }
        }

        @Override
        public void onRemoved() {
            super.onRemoved();
            if (addedToGraph) {
                rTGraph().removeRotationPower(this, (int) output);
                addedToGraph = false;
            }
        }

        @Override
        public float getRotationPower() {
            return rotationPower;
        }

        @Override
        public void setRotationPower(float rotationPower) {
            this.rotationPower = rotationPower;
        }

        @Override
        public RTModule rotationPower() {
            return new RTModule(); // Ensure this returns an instance of RTModule
        }

        @Override
        public RTConfig rTConfig() {
            return new RTConfig(); // Ensure this returns an instance of RTConfig
        }
    }
}