package aquarion.world.blocks.power;

import arc.Core;
import arc.util.Nullable;
import arc.util.Time;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.ui.Bar;
import mindustry.world.Block;

public class FuelInputModule extends Block {
    public float itemDuration = 120f;
    public int radiationAmount = 20;
    public FuelInputModule(String name) {
        super(name);
        update = true;
        rotate = true;
        drawArrow = true;
        solid = true;
        hasItems = true;
    }

    @Override
    public void setBars() {
        super.setBars();

        // Display a bar to show how much fuel time is left
        addBar("fuel", (FuelInputBuild build) -> new Bar(
                () -> Core.bundle.format("bar.fuel", build.fuelTime / itemDuration),
                () -> Pal.items,
                () -> build.fuelTime / itemDuration
        ));
    }

    public class FuelInputBuild extends Building {
        public @Nullable Building next;
        public @Nullable Reactor.ReactorBuild nextc;
        public float fuelTime = 0f;

        @Override
        public void updateTile() {
            if (fuelTime <= 0 && items.total() > 0 && nextc != null) {
                Item item = items.first();
                items.remove(item, 1);
                fuelTime = itemDuration;
                nextc.addRadiation(radiationAmount);
            }
            if (fuelTime > 0) {
                fuelTime -= Time.delta;
            }
        }
        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();
            next = front();
            nextc = next instanceof Reactor.ReactorBuild d ? d : null;
        }
    }
}