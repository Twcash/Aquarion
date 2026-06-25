package aquarion.world.blocks;

import aquarion.world.type.AquaGenericCrafter;
import arc.Core;
import arc.math.Mathf;
import arc.struct.Seq;
import mindustry.content.Liquids;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.ui.Bar;
import mindustry.world.blocks.heat.HeatConsumer;
import mindustry.world.consumers.ConsumeLiquidBase;
import mindustry.world.meta.*;
import arc.scene.ui.layout.Table;

public class powderoven extends AquaGenericCrafter {
    public float heatRequirement = 10f;
    public float maxEfficiency = 1f;
    public float liquidBoostIntensity = 1.5f;

    public Seq<OvenRecipe> recipes = new Seq<>();

    public static class OvenRecipe {
        public Item inputItem;
        public int inputAmount;
        public Item outputItem;
        public int outputAmount;

        public OvenRecipe(Item inputItem, int inputAmount, Item outputItem, int outputAmount) {
            this.inputItem = inputItem;
            this.inputAmount = inputAmount;
            this.outputItem = outputItem;
            this.outputAmount = outputAmount;
        }
    }

    public powderoven(String name) {
        super(name);
        hasItems = true;
        hasLiquids = true;
        hasPower = true;
        hasHeat = true;

        outputItem = new ItemStack(mindustry.content.Items.copper, 1);
    }

    public void addRecipe(Item input, int inputCount, Item output, int outputCount) {
        recipes.add(new OvenRecipe(input, inputCount, output, outputCount));
    }

    @Override
    public void setBars() {
        super.setBars();

        addBar("heat", (powderovenBuild entity) -> new Bar(
                () -> Core.bundle.get("bar.aquarion-heat") + " (" + (int)(Math.min(entity.heat / heatRequirement, 1f) * 100) + "%)",
                () -> Pal.lightOrange,
                () -> heatRequirement > 0 ? entity.heat / heatRequirement : 0f
        ));
    }

    @Override
    public void setStats() {
        stats.add(Stat.size, "@x@", size, size);
        stats.add(Stat.health, health, StatUnit.none);

        if (hasPower && consPower != null) {
            stats.add(Stat.powerUse, consPower.usage * 60f, StatUnit.powerSecond);
        }

        stats.add(Stat.input, heatRequirement, StatUnit.heatUnits);

        stats.add(Stat.input, new StatValue() {
            @Override
            public void display(Table table) {
                for (OvenRecipe r : recipes) {
                    table.row();
                    table.image(r.inputItem.uiIcon).size(24f).padRight(4f);
                    table.add(Core.bundle.get("stat.aquarion-input-prefix") + " " + r.inputItem.localizedName + " x" + r.inputAmount).left().padTop(2f).padBottom(2f);
                }
            }
        });

        stats.add(Stat.output, new StatValue() {
            @Override
            public void display(Table table) {
                for (OvenRecipe r : recipes) {
                    table.row();
                    table.image(r.outputItem.uiIcon).size(24f).padRight(4f);
                    table.add(Core.bundle.get("stat.aquarion-output-prefix") + " " + r.outputItem.localizedName + " x" + r.outputAmount).left().padTop(2f).padBottom(2f);
                }
            }
        });

        if (findConsumer(f -> f instanceof ConsumeLiquidBase && f.booster) instanceof ConsumeLiquidBase consBase) {
            stats.remove(Stat.booster);
            stats.add(Stat.booster, StatValues.speedBoosters(
                    "{0}" + StatUnit.timesSpeed.localized(),
                    consBase.amount,
                    liquidBoostIntensity,
                    false,
                    consBase::consumes
            ));
        }
    }

    public class powderovenBuild extends AquaGenericCrafterBuild implements HeatConsumer {
        public float[] sideHeat = new float[4];
        public float heat = 0f;

        @Override
        public void updateTile() {
            heat = calculateHeat(sideHeat);

            OvenRecipe currentRecipe = null;
            for (OvenRecipe r : recipes) {
                if (items.get(r.inputItem) >= r.inputAmount && items.get(r.outputItem) + r.outputAmount <= itemCapacity) {
                    currentRecipe = r;
                    break;
                }
            }

            boolean canWork = currentRecipe != null && this.productionValid() && heat >= heatRequirement - 0.01f;

            if (canWork) {
                efficiency = 1f;

                if (optionalEfficiency > 0f) {
                    efficiency *= Mathf.lerp(1f, liquidBoostIntensity, optionalEfficiency);
                }

                progress += edelta();
                warmup = Mathf.approachDelta(warmup, warmupTarget(), 0.05f);

                if (progress >= craftTime) {
                    this.consume();

                    items.remove(currentRecipe.inputItem, currentRecipe.inputAmount);
                    items.add(currentRecipe.outputItem, currentRecipe.outputAmount);

                    progress %= craftTime;

                    if (craftEffect != null) {
                        craftEffect.at(x, y);
                    }
                }
            } else {
                efficiency = 0f;
                warmup = Mathf.approachDelta(warmup, 0f, 0.05f);
            }

            customDumpOutputs();
        }

        public void customDumpOutputs() {
            for (OvenRecipe r : recipes) {
                if (items.get(r.outputItem) > 0) {
                    dump(r.outputItem);
                }
            }
        }

        @Override
        public void draw() {
            super.draw();
        }

        @Override
        public float heatRequirement() {
            return heatRequirement;
        }

        @Override
        public float[] sideHeat() {
            return sideHeat;
        }

        @Override
        public float warmupTarget() {
            return Mathf.clamp(heat / heatRequirement);
        }

        @Override
        public float efficiencyScale() {
            return heat >= heatRequirement - 0.01f ? 1f : 0f;
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            for (OvenRecipe r : recipes) {
                if (r.inputItem == item && items.get(item) < itemCapacity) {
                    return true;
                }
            }
            return false;
        }
    }
}