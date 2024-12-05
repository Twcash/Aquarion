package aquarion.world.blocks.power;

import arc.Core;
import arc.graphics.Color;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.blocks.power.PowerGenerator;

import static mindustry.Vars.world;

public class ModularReactor extends PowerGenerator {
    public float maxHeat = 100f; // Heat limit before destruction
    public float maxRadiationCap = 100f; // Maximum radiation storage
    public float radiationDecayRate = 0.75f; // Radiation to heat conversion rate per tick

    public Effect overheatEffect = Fx.explosion;

    public ModularReactor(String name) {
        super(name);
        hasPower = false; // This block itself doesn't generate power
    }

    @Override
    public void setBars() {
        super.setBars();

        // Add heat and radiation bars
        addBar("heat", (ReactorBuild build) -> new Bar(
                () -> Core.bundle.format("bar.heat", build.heat),
                () -> Pal.lightOrange,
                () -> build.heat / maxHeat
        ));

        addBar("radiation", (ReactorBuild build) -> new Bar(
                () -> Core.bundle.format("bar.radiation", build.radiation),
                () -> Pal.accent,
                () -> build.radiation / maxRadiationCap
        ));
    }

    public class ReactorBuild extends GeneratorBuild {
        public float heat, radiation, efficiency;
        public float maxRadiation = maxRadiationCap;

        @Override
        public void updateTile() {
            // Reset efficiency
            efficiency = 0f;

            // Iterate through adjacent blocks and add radiation from active fuel modules
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx == 0 && dy == 0) continue;

                    Building nearby = world.build(tile.x + dx, tile.y + dy);
                    if (nearby instanceof FuelInputModule.FuelInputBuild module) {
                        module.supplyFuel(this); // Add radiation to the reactor
                        if (module.isActive()) {
                            efficiency += 0.25f; // Each active module adds 25% efficiency
                        }
                    }
                }
            }

            // Normalize efficiency (e.g., max 4 modules = 100%)
            efficiency = Math.min(1f, efficiency);

            // Decay radiation into heat
            if (radiation > 0) {
                float decayAmount = Math.min(radiationDecayRate * Time.delta, radiation);
                radiation -= decayAmount;
                heat += decayAmount * 0.75f;
            }

            // Check for overheat
            if (heat >= maxHeat) {
                kill();
                overheatEffect.at(x, y);
                createExplosion();
                return;
            }
        }

        @Override
        public void draw() {
            super.draw();
            Drawf.light(x, y, 40f * size, Color.orange, heat / maxHeat);
        }
    }
}