package aquarion.world.blocks.power;

import arc.Core;
import arc.util.Time;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.ui.Bar;
import mindustry.world.Block;

public class FuelInputModule extends Block {
    public float itemDuration = 120f; // Time in ticks to consume one item

    public FuelInputModule(String name) {
        super(name);
        update = true;
        solid = true;
        hasItems = true; // Indicates this block interacts with items
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
        public float fuelTime = 0f; // Remaining time for the current fuel item
        public boolean active = false; // Whether this module is contributing to the reactor

        @Override
        public void updateTile() {
            // Consume item when there's no fuel time left
            if (fuelTime <= 0 && items.total() > 0) {
                Item item = items.first();
                items.remove(item, 1);
                fuelTime = itemDuration; // Reset fuel time
                active = true; // Mark the module as active
            }

            // Decrease fuel time and deactivate if no fuel remains
            if (fuelTime > 0) {
                fuelTime -= Time.delta;
                if (fuelTime <= 0) {
                    active = false;
                    // Optional: Log or debug when it deactivates
                }
            }
        }

        public void supplyFuel(ModularReactor.ReactorBuild reactor) {
            if (active) {
                // Add radiation to the reactor if this module is active
                reactor.radiation = Math.min(reactor.radiation + 0.1f * Time.delta, reactor.maxRadiation); // Add radiation gradually
                // Optional: Log to confirm fuel is being supplied
            }
        }

        public boolean isActive() {
            return active;
        }
    }
}