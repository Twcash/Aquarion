package aquarion.content;

import arc.struct.ObjectFloatMap;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.game.Objectives.*;
import mindustry.type.Item;
import aquarion.content.blocks.CrafterBlocks;
import arc.struct.ObjectFloatMap;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.game.Objectives;
import mindustry.type.Item;

import static aquarion.content.AquaItems.*;
import static aquarion.content.AquaLiquids.*;
import static aquarion.content.blocks.CoreBlocks.*;
import static aquarion.content.blocks.CrafterBlocks.*;
import static aquarion.content.blocks.DefenseBlocks.*;
import static aquarion.content.blocks.DistributionBlocks.*;
import static aquarion.content.blocks.EffectBlocks.lantern;
import static aquarion.content.blocks.LiquidBlocks.*;
import static aquarion.content.blocks.PowerBlocks.*;
import static aquarion.content.blocks.CoreBlocks.channel;
import static aquarion.content.blocks.RefineryBlocks.*;
import static aquarion.content.blocks.TurretBlocks.*;
import static aquarion.content.blocks.UnitBlocks.*;
import static aquarion.content.AquaSectorPresets.*;
import static mindustry.content.Items.*;
import static mindustry.content.Liquids.*;
import static mindustry.content.TechTree.*;

public class FakeErekirTechTree {

    public static void load() {
        ObjectFloatMap<Item> costMultipliers = new ObjectFloatMap<Item>();
        for(Item item : Vars.content.items()) costMultipliers.put(item, 0.08f);

        AquaPlanets.fakeErekir.techTree = nodeRoot("fakeErekir", Blocks.coreBastion, () -> {
            context().researchCostMultipliers = costMultipliers;

            node(Blocks.plasmaBore, () -> {
                node(Blocks.impactDrill, () -> {
                    node(Blocks.eruptionDrill, () -> {});
                });
                node(Blocks.duct, () -> {
                    node(Blocks.ductRouter, () -> {
                        node(Blocks.overflowDuct, () -> {});
                    });
                });
            });

            node(beryllium, () -> {
                node(Items.graphite, () -> {
                    node(Items.silicon, () -> {
                        node(Items.thorium, () -> {});
                    });
                });
                node(Items.sand, () -> {
                    node(Items.oxide, () -> {});
                });
            });

            node(Blocks.turbineCondenser, () -> {
                node(Blocks.beamNode, () -> {
                    node(Blocks.beamTower, () -> {});
                });
                node(Blocks.chemicalCombustionChamber, () -> {
                    node(Blocks.pyrolysisGenerator, () -> {});
                });
            });

            node(Blocks.siliconArcFurnace, () -> {
                node(Blocks.electrolyzer, () -> {
                    node(Blocks.atmosphericConcentrator, () -> {});
                    node(Blocks.oxidationChamber, () -> {});
                });
            });

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