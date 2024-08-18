package aquarion.world.blocks.rotPower;

import aquarion.world.blocks.ConsumeRT;
import aquarion.world.interfaces.HasRT;
import aquarion.world.meta.RTConfig;
import aquarion.world.meta.RTModule;
import arc.Core;
import arc.util.Strings;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.blocks.production.WallCrafter;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;

public class RTWallCrafter extends WallCrafter {
    public RTConfig rtConfig = new RTConfig();

    public RTWallCrafter(String name) {
        super(name);
    }

    @Override
    public void setStats() {
        super.setStats();

        stats.add(Stat.output, output);
        stats.add(Stat.tiles, StatValues.blocks(attribute, floating, 1f, true, false));
        stats.add(Stat.drillSpeed, 60f / drillTime * size, StatUnit.itemsSecond);
    }
    public ConsumeRT consumeRT(float amount) {
        return consume(new ConsumeRT(amount));
    }



    @Override
    public void setBars() {
        super.setBars();

        addBar("drillspeed", (WallCrafterBuild e) ->
                new Bar(() -> Core.bundle.format("bar.drillspeed", Strings.fixed(e.lastEfficiency * 60 / drillTime, 2)), () -> Pal.ammo, () -> e.warmup));
    }

    public class RTWallCrafterBuild extends WallCrafterBuild implements HasRT {
        public RTModule rotationPower = new RTModule();
        public float time, warmup, totalTime, lastEfficiency;


        @Override
        public RTModule rotationPower() {
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
        public void read(Reads read) {
            super.read(read);
            rotationPower.read(read);
        }
        @Override
        public void write(Writes write) {
            super.write(write);
            rotationPower.write(write);
        }

        @Override
        public void onProximityUpdate() {
            super.onProximityAdded();
            rTGraph().removeBuild(this, true);
            noSleep();
        }

        @Override
        public void onProximityRemoved() {
            super.onProximityRemoved();
            rTGraph().removeBuild(this, false);
        }
    }
}
