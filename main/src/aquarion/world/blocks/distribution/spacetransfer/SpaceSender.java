package aquarion.world.blocks.distribution.spacetransfer;

import arc.scene.ui.layout.Table;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.type.Item;
import mindustry.world.Block;

public class SpaceSender extends Block {
    // Время до запуска ракеты в кадрах (60 кадров = 1 секунда). Сделаем 5 секунд.
    public float launchTime = 60f * 5f;

    public SpaceSender(String name) {
        super(name);
        update = true;
        solid = true;
        hasItems = true;
        configurable = true; // Позволяет кликать на блок для выбора настроек

        // Синхронизация ID сектора назначения
        config(Integer.class, (SpaceSenderBuild tile, Integer sectorId) -> tile.destinationSectorId = sectorId);
    }

    public class SpaceSenderBuild extends Building {
        public int destinationSectorId = -1; // -1 значит сектор не выбран
        public float progress = 0;

        @Override
        public void buildConfiguration(Table table) {
            // Создаем кнопку с иконкой планетки, как у ванильного LaunchPad
            table.button(Icon.world, () -> {
                // Открывает оригинальную карту планет Mindustry для выбора сектора
                Vars.ui.planet.showSelect(Vars.state.getSector(), otherSector -> {
                    configure(otherSector.id); // Сохраняем ID выбранного сектора
                });
            }).size(50f);
        }

        @Override
        public void updateTile() {
            if (destinationSectorId == -1) return;

            // Ищем любой предмет, который лежит на складе отправителя
            Item toSend = items.first();

            // Если нашли и его накопилось хотя бы 10 штук — начинаем подготовку к запуску
            if (toSend != null && items.get(toSend) >= 10) {
                progress += edelta();

                if (progress >= launchTime) {
                    progress = 0;
                    int amountToSend = Math.min(items.get(toSend), 10); // Отправляем пачкой по 10 штук

                    // 1. Списываем предметы со склада отправителя
                    items.remove(toSend, amountToSend);

                    // 2. Создаем ванильный визуальный эффект улетающей в космос ракеты!
                    Fx.launchPod.at(x, y);

                    // 3. Отправляем ресурсы в нашу космическую сеть для нужного сектора
                    SpaceNet.addCargo(destinationSectorId, toSend, amountToSend);
                }
            } else {
                progress = 0; // Сбрасываем таймер, если ресурсов не хватает
            }
        }

        // Сохранение данных блока при выходе из игры
        @Override
        public void write(Writes write) {
            super.write(write);
            write.i(destinationSectorId);
            write.f(progress);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            destinationSectorId = read.i();
            progress = read.f();
        }
    }
}