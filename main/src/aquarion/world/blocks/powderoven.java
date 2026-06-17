package aquarion.world.blocks;

import aquarion.world.type.AquaGenericCrafter;
import arc.Core;
import arc.math.Mathf;
import arc.struct.Seq;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.ui.Bar;
import mindustry.world.blocks.heat.HeatConsumer;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValue;
import mindustry.world.meta.StatValues;
import arc.scene.ui.layout.Table;

public class powderoven extends AquaGenericCrafter {
    public float heatRequirement = 10f;
    public float maxEfficiency = 1f;

    // Список всех рецептов печи
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

        // Заглушка для логистики
        outputItem = new ItemStack(mindustry.content.Items.copper, 1);
    }

    public void addRecipe(Item input, int inputCount, Item output, int outputCount) {
        recipes.add(new OvenRecipe(input, inputCount, output, outputCount));
    }

    @Override
    public void setBars() {
        super.setBars();

        // Полоса нагрева теперь использует простую формулу процентов и полностью локализуемый ключ
        addBar("heat", (powderovenBuild entity) -> new Bar(
                () -> Core.bundle.get("bar.aquarion-heat") + " (" + (int)(Math.min(entity.heat / heatRequirement, 1f) * 100) + "%)",
                () -> Pal.lightOrange,
                () -> heatRequirement > 0 ? entity.heat / heatRequirement : 0f
        ));
    }

    @Override
    public void setStats() {
        // Загружаем базовые параметры (размер, прочность, потребление энергии/жидкости)
        super.setStats();

        // Добавляем требование тепла во вкладку "Вход"
        stats.add(Stat.input, heatRequirement, StatUnit.heatUnits);

        // --- БУСТЕРЫ И ЭФФЕКТИВНОСТЬ ТУТ ПОЛНОСТЬЮ СТЕРТЫ ИЗ ОПИСАНИЯ ---

        // Создаем кастомное отображение ВХОДНЫХ ресурсов с иконками
        stats.add(Stat.input, new StatValue() {
            @Override
            public void display(Table table) {
                for (OvenRecipe r : recipes) {
                    table.row();
                    // Добавляем иконку предмета
                    table.image(r.inputItem.uiIcon).size(24f).padRight(4f);
                    // Добавляем переводимый текст: "Вход: ИмяРесурса xКоличество"
                    table.add(Core.bundle.get("stat.aquarion-input-prefix") + " " + r.inputItem.localizedName + " x" + r.inputAmount).left().padTop(2f).padBottom(2f);
                }
            }
        });

        // Создаем кастомное отображение ВЫХОДНЫХ ресурсов с иконками
        stats.add(Stat.output, new StatValue() {
            @Override
            public void display(Table table) {
                for (OvenRecipe r : recipes) {
                    table.row();
                    // Добавляем иконку предмета
                    table.image(r.outputItem.uiIcon).size(24f).padRight(4f);
                    // Добавляем переводимый текст: "Выход: ИмяРесурса xКоличество"
                    table.add(Core.bundle.get("stat.aquarion-output-prefix") + " " + r.outputItem.localizedName + " x" + r.outputAmount).left().padTop(2f).padBottom(2f);
                }
            }
        });
    }

    public class powderovenBuild extends AquaGenericCrafterBuild implements HeatConsumer {
        public float[] sideHeat = new float[4];
        public float heat = 0f;

        @Override
        public void updateTile() {
            heat = calculateHeat(sideHeat);

            // 1. Проверка тепла
            if (heat < heatRequirement) {
                efficiency = 0f;
                warmup = Mathf.approachDelta(warmup, 0f, 0.05f);
                customDumpOutputs();
                return;
            }

            // Поиск подходящего рецепта
            OvenRecipe currentRecipe = null;
            for (OvenRecipe r : recipes) {
                if (items.get(r.inputItem) >= r.inputAmount && items.get(r.outputItem) + r.outputAmount <= itemCapacity) {
                    currentRecipe = r;
                    break;
                }
            }

            // 2. Логика крафта
            if (currentRecipe != null && potentialEfficiency > 0) {
                efficiency = 1f;
                progress += edelta();
                warmup = Mathf.approachDelta(warmup, warmupTarget(), 0.05f);

                if (progress >= craftTime) {
                    consume();

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

        // --- Реализация интерфейса HeatConsumer ---
        @Override
        public boolean shouldConsume() {
            return (heatRequirement <= 0f || heat > 0);
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
            return heat >= heatRequirement ? 1f : 0f;
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