package aquarion.content;

import arc.struct.Seq;
import mindustry.content.*;
import mindustry.game.Objectives.*;

import static mindustry.content.TechTree.*;

public class FakeErekirTechTree {

    public static void load() {
        // Привязываем древо к вашей планете из AquaPlanets
        AquaPlanets.fakeErekir.techTree = nodeRoot("erekir", Blocks.coreBastion, () -> {
            
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
                node(Blocks.duct, () -> {
                    node(Blocks.ductRouter, () -> {});
                });
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
                    node(Blocks.sublimate, () -> {});
                });
                node(Blocks.titan, () -> {
                    node(Blocks.disperse, () -> {});
                });
            });
        });
    }
}
