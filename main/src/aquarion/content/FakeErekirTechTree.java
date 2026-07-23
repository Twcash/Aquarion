package aqua.content;

import arc.struct.ObjectSet;
import arc.struct.Seq;
import mindustry.content.*;
import mindustry.game.Objectives.*;
import mindustry.type.ItemStack;

import static mindustry.content.TechTree.*;

public class FakeErekirTechTree {

    public static void load() {
        // Указываем корень древа технологий для кастомной планеты Fake Erekir
        AquaPlanets.fakeErekir.techTree = nodeRoot("Erekir", Blocks.coreBastion, () -> {
            
            // --- РЕСУРСЫ ---
            node(Items.beryllium, () -> {
                node(Items.graphite, () -> {
                    node(Items.silicon, () -> {
                        node(Items.thorium, () -> {});
                    });
                });
                node(Items.sand, () -> {
                    node(Items.oxide, () -> {});
                });
            });

            // --- БАЗОВЫЕ БЛОКИ И ДОБЫЧА ---
            node(Blocks.plasmaBore, () -> {
                node(Blocks.impactDrill, () -> {
                    node(Blocks.eruptionDrill, () -> {});
                });
                node(Blocks.biconvexdrive, () -> {});
            });

            // --- ЭНЕРГЕТИКА ---
            node(Blocks.turbineCondenser, () -> {
                node(Blocks.beamNode, () -> {
                    node(Blocks.beamTower, () -> {});
                });
                node(Blocks.chemicalCombustionChamber, () -> {
                    node(Blocks.pyrolysisGenerator, () -> {});
                });
            });

            // --- ЗАВОДЫ И ПЕРЕРАБОТКА ---
            node(Blocks.siliconArcFurnace, () -> {
                node(Blocks.electrolyzer, () -> {
                    node(Blocks.atmosphericConcentrator, () -> {});
                    node(Blocks.oxidationChamber, () -> {});
                });
            });

            // --- ТУРРЕЛИ / ЗАЩИТА ---
            node(Blocks.breach, () -> {
                node(Blocks.diffuse, () -> {
                    node(Blocks.sublime, () -> {});
                });
                node(Blocks.titan, () -> {
                    node(Blocks.disperse, () -> {});
                });
            });

            // --- СЕКТОРА / КАРТЫ ---
            // Пример добавления сектора с условиями (Objective)
            nodeSector(SectorPresets.theOnset, () -> {
                nodeSector(SectorPresets.aegis, Seq.with(
                    new SectorComplete(SectorPresets.theOnset),
                    new Research(Blocks.diffuse)
                ), () -> {
                    nodeSector(SectorPresets.lake, () -> {});
                });
            });

        });
    }
}
