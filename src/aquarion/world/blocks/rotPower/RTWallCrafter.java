package aquarion.world.blocks.rotPower;

import aquarion.world.blocks.RTConsumer;
import aquarion.world.interfaces.HasRT;
import aquarion.world.meta.RTConfig;
import aquarion.world.meta.RTModule;
import arc.Core;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.util.Strings;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.blocks.production.WallCrafter;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;

import static mindustry.Vars.tilesize;

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
    public RTConsumer consumeRT(float min, float max) {
        return consume(new RTConsumer(min, max));
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

        @Override
        public void onProximityRemoved() {
            super.onProximityRemoved();
            rTGraph().removeBuild(this, false);
        }
    }
}
