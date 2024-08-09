package aquarion.world.blocks.rotPower;

import aquarion.world.blocks.ConsumeRT;
import aquarion.world.interfaces.HasRT;
import aquarion.world.meta.RTConfig;
import aquarion.world.meta.RTModule;
import aquarion.world.graphs.RTGraph;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.world.Block;

import mindustry.gen.*;

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
    public ConsumeRT consumeRT(float amount) {
        return consume(new ConsumeRT(amount));
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
            noSleep();
        }
        @Override
        public RTConfig rTConfig() {
            return new RTConfig(); // Ensure this returns an instance of RTConfig
        }
        @Override
        public void onProximityUpdate() {
            super.onProximityAdded();
            new RTGraph().addBuild(this);
            nextBuilds().each(build -> rTGraph().merge(build.rTGraph(), false));

            rTGraph().removeBuild(this, true);
            noSleep();
        }
        @Override
        public void read(Reads read) {
            super.read(read);
            rotationPower.read(read);
        }
        @Override
        public void write(Writes write) {
            super.write(write);
            rotationPower.write(write);
        }
    }
}