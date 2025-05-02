package aquarion.planets;

import arc.struct.Seq;
import mindustry.content.Items;
import mindustry.game.Objectives;

import static aquarion.AquaItems.*;
import static aquarion.AquaLiquids.*;
import static aquarion.blocks.AquaCore.*;
import static aquarion.blocks.AquaCrafters.*;
import static aquarion.blocks.AquaDefense.*;
import static aquarion.blocks.AquaDistribution.*;
import static aquarion.blocks.AquaLiquid.*;
import static aquarion.blocks.AquaTurrets.*;
import static aquarion.planets.AquaSectorPresets.*;
import static mindustry.content.Items.*;
import static mindustry.content.Liquids.slag;
import static mindustry.content.TechTree.*;
public class TantrosTechTree {
    public static void load() {
        AquaPlanets.tantros2.techTree = AquaPlanets.fakeSerpulo.techTree = nodeRoot("Tantros", corePike, () -> {
            nodeProduce(magma, () -> {
                nodeProduce(slag, () -> nodeProduce(hydroxide, () -> {}));
                nodeProduce(fumes, () -> {});
                nodeProduce(dioxide, () -> {});
            });

            nodeProduce(nickel, () -> {});
            nodeProduce(Items.copper, () -> {
                nodeProduce(cupronickel, () -> {});
            });
            nodeProduce(azurite, () -> {

            });

            nodeProduce(Items.lead, () -> {
                nodeProduce(sand, () -> {
                    nodeProduce(metaglass, () -> {});
                });
            });
            nodeProduce(bauxite, () -> {
                nodeProduce(aluminum, () -> {});
                nodeProduce(ferricMatter, () -> {});
                nodeProduce(oxygen, () -> {});
            });
            nodeProduce(silicon, () -> {
                nodeProduce(ferrosilicon, () -> {});
            });

            node(siphon, () ->{
                node(siphonRouter, () -> {
                    node(pipeTank);
                });
                node(siphonBridge, () -> {});
                node(siphonJunction, () -> {});
                node(pulseSiphon, () -> node(pulseSiphonBridge, () -> {}));
            });
            node(harvester, () -> {
                node(CentrifugalPump);
                node(magmaTap, () -> node(plasmaExtractor, () -> {
                    node(beamBore, Seq.with(
                            new Objectives.OnSector(CrystalCaverns)
                    ), () -> {});
                    node(DrillDerrick, Seq.with(
                            new Objectives.OnSector(CrystalCaverns)
                    ), () -> {});
                    node(fumeFilter, Seq.with(
                            new Objectives.OnSector(CrystalCaverns)
                    ), () -> {});
                }));
            });
            node(atmosphericIntake, () -> {
                node(AnnealingOven);
                node(magmaDiffser, () -> node(azuriteKiln, () -> {
                    node(ferroSiliconFoundry, Seq.with(
                            new Objectives.SectorComplete(Torrent),
                            new Objectives.Research(bauxiteCentrifuge)
                    ), () -> {});
                    node(towaniteReductionVat, Seq.with(
                            new Objectives.OnSector(CrystalCaverns)
                    ), () -> {});
                    node(bauxiteCentrifuge);
                }));
            });

            node(nickelWall, () -> {
                node(hugeNickelWall, () -> {
                    node(nickelBarricade);
                });
                node(bauxiteWall, () -> node(hugeBauxiteWall, () -> {
                    node(aluminumWall, () -> {
                        node(hugeAluminumWall, () -> {
                            node(ferrosilconWall, () -> {
                                node(hugeFerrosiliconWall, () -> {});
                            });
                        });
                    });
                }));
            });
            node(point, () -> {
                node(Foment, () ->  {
                    node(buzzSaw);
                    node(redact, () -> {
                        node(maelstrom);
                    });
                });
                node(sentry);
                node(pelt);
                node(douse);
            });
            node(resurgence, () ->{
                node(twinPass, Seq.with(
                        new Objectives.SectorComplete(resurgence),
                        new Objectives.Research(nickelWall),
                        new Objectives.Research(point)
                ), () ->{
                    node(bay);
                });
            });
            node(Ingress, () -> {
                node(Torrent, Seq.with(
                        new Objectives.SectorComplete(Ingress)
                ), () -> {
                    node(Grove, ()->{
                        new Objectives.SectorComplete(Torrent);
                    });
                    node(CrystalCaverns, Seq.with(
                            new Objectives.SectorComplete(Torrent),
                            new Objectives.Research(armoredSealedConveyor),
                            new Objectives.Research(redact)
                    ), () -> {});
                });
            });
            node(mendPyre, () -> {
                node(mendPylon, Seq.with(
                        new Objectives.OnSector(CrystalCaverns)
                ), () -> {});
            });
            node(sealedConveyor, () ->{
                node(cache, () -> {});
                node(sealedRouter, () ->{
                    node(sealedDistributor, () -> {});
                    node(sealedUnloader, () -> {});
                });
                node(armoredSealedConveyor, () -> {});
                node(sealedSorter, () -> {});
                node(sealedJunction, () -> {});
                node(sealedOverflow, () -> node(sealedUnderflow, () -> {}));
            });


        });

    }
}
