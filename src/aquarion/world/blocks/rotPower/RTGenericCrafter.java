package aquarion.world.blocks.rotPower;

import aquarion.world.blocks.RTConsumer;
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

public class RTGenericCrafter extends Block {
    public RTConfig rtConfig = new RTConfig();
    public float output = 10f; // Fixed amount of rotation power produced

    public RTGenericCrafter(String name) {
        super(name);
        update = true; // This block will update every frame
        solid = true;
        hasPower = true;
    }
    @Override
    public void setBars() {
        super.setBars();
        rtConfig.addBars(this);
    }
    public RTConsumer consumeRT(float min, float max) {
        return consume(new RTConsumer(min, max));
    }

    public class RTProducerBuild extends Building implements HasRT {
        public RTConfig rtConfig;
        public RTModule rotationPower = new RTModule();
        private boolean addedToGraph = false;

        @Override
        public void onProximityRemoved() {
            super.onProximityRemoved();
            rTGraph().removeBuild(this, false);
        }
        @Override public float rotationProduction() {
            return efficiency * output;
        }

        @Override public RTModule rotationPower() {
            return rotationPower;
        }
        @Override
        public void updateTile() {
            super.updateTile();
        }

        @Override
        public RTConfig rTConfig() {
            return new RTConfig(); // Ensure this returns an instance of RTConfig
        }
        @Override
        public void onProximityUpdate() {
            super.onProximityAdded();
            rTGraph().removeBuild(this, true);
        }
    }
}