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
import static aquarion.blocks.AquaUnitFactories.*;
import static aquarion.planets.AquaSectorPresets.*;
import static mindustry.content.Items.*;
import static mindustry.content.Liquids.slag;
import static mindustry.content.Liquids.water;
import static mindustry.content.TechTree.*;
public class TantrosTechTree {
    public static void load() {
        AquaPlanets.tantros2.techTree = AquaPlanets.fakeSerpulo.techTree = nodeRoot("RECOMPILE", corePike, () -> {
            node(nickelWall, () -> {
                        node(mendPyre, () ->
                                node(mendPylon, Seq.with(
                                        new Objectives.OnSector(CrystalCaverns)
                                ), () -> {
                                }));
                        node(hugeNickelWall, () -> node(nickelBarricade));
                        node(bauxiteWall, () -> {
                            node(hugeBauxiteWall, () -> {
                                node(bauxiteBarricade);
                                node(aluminumWall, () ->
                                        node(hugeAluminumWall, () ->
                                                node(ferrosilconWall, () ->
                                                        node(hugeFerrosiliconWall, () -> {
                                                        }))));
                            });
                        });
                    });
            node(bulwark, Seq.with(
                    new Objectives.OnSector(twinPass),
                    new Objectives.Produce(metaglass)
            ), () -> {
                node(crest, () -> {
                    node(soar, () -> {
                    });
                });
                node(rampart, () -> {
                    node(reave, () -> {
                        node(castellan, () -> {
                        });
                    });
                    node(pugnate, () -> {
                        node(shatter, () -> {
                        });
                        node(raze, () -> {
                        });
                    });
                });
            });
            node(point, () -> {
                node(buildCairn);
                node(Foment, () -> {
                    node(buzzSaw);
                    node(redact, () ->
                            node(maelstrom));
                });
                node(sentry);
                node(pelt, () -> {
                    node(focus);
                    node(vector);
                });
                node(douse);
            });
            node(sealedConveyor, () -> {
                node(manganeseRail);
                node(cache, () -> {
                });
                node(sealedRouter, () -> {
                    node(sealedDistributor, () -> {
                    });
                    node(sealedUnloader, () -> {
                    });
                });
                node(armoredSealedConveyor, () -> {
                });
                node(sealedSorter, () -> {
                });
                node(sealedJunction, () -> {
                });
                node(sealedOverflow, () -> node(sealedUnderflow, () -> {
                }));
            });
            node(resurgence, () ->
                    node(twinPass, Seq.with(
                            new Objectives.SectorComplete(resurgence),
                            new Objectives.Research(nickelWall),
                            new Objectives.Research(point)
                    ), () -> {
                        node(Ingress, Seq.with(
                                new Objectives.SectorComplete(twinPass),
                                new Objectives.Research(magmaTap)
                        ), () -> node(Torrent, Seq.with(
                                new Objectives.SectorComplete(Ingress)
                        ), () -> {
                            node(Grove, () -> {
                                new Objectives.SectorComplete(Torrent);
                                new Objectives.Research(bauxiteCentrifuge);
                            });
                            node(CrystalCaverns, Seq.with(
                                    new Objectives.SectorComplete(Torrent),
                                    new Objectives.Research(armoredSealedConveyor),
                                    new Objectives.Research(redact)
                            ), () -> {
                            });
                        }));
                        node(bay, Seq.with(
                                new Objectives.SectorComplete(resurgence),
                                new Objectives.SectorComplete(twinPass)
                        ), () -> {
                        });
                    }));
            nodeProduce(silicon, () -> {
                nodeProduce(nickel, () -> {
                    nodeProduce(Items.lead, () -> {
                        nodeProduce(bauxite, () -> {
                            nodeProduce(aluminum, () -> {
                            });
                            nodeProduce(ferricMatter, () -> nodeProduce(ferrosilicon, () -> {
                            }));
                            nodeProduce(oxygen, () -> {
                            });
                        });
                        nodeProduce(sand, () -> {
                            nodeProduce(metaglass, () -> {
                            });
                            nodeProduce(coke, () -> {
                            });
                        });
                    });
                    nodeProduce(Items.copper, () -> {
                        nodeProduce(water, () -> {
                            nodeProduce(air, () -> nodeProduce(halideWater, () -> {
                            }));
                            nodeProduce(magma, () -> {
                                nodeProduce(slag, () -> nodeProduce(hydroxide, () -> {
                                }));
                                nodeProduce(fumes, () -> {
                                });
                                nodeProduce(dioxide, () -> {
                                });
                            });
                        });
                        nodeProduce(manganese, () -> {
                        });
                        nodeProduce(cupronickel, () -> {
                        });
                        nodeProduce(azurite, () -> nodeProduce(towanite, () -> nodeProduce(acuminite, () -> {
                            nodeProduce(strontium, () -> {
                            });
                            nodeProduce(fluorine, () -> {
                            });
                        })));
                    });

                });
            });
            node(siphon, () -> {
                node(siphonRouter, () -> node(pipeTank));
                node(siphonBridge, () -> {
                });
                node(siphonJunction, () -> {
                });
                node(pulseSiphon, () -> node(pulseSiphonBridge, () -> {
                }));
            });
            node(harvester, () -> {
                node(CentrifugalPump);
                node(magmaTap, Seq.with(
                        new Objectives.SectorComplete(twinPass)
                ), () -> node(plasmaExtractor, () -> {
                    node(beamBore, Seq.with(
                            new Objectives.OnSector(CrystalCaverns)
                    ), () -> {
                    });
                    node(DrillDerrick, Seq.with(
                            new Objectives.OnSector(CrystalCaverns)
                    ), () -> node(pinDrill));
                    node(fumeFilter, Seq.with(
                            new Objectives.OnSector(CrystalCaverns)
                    ), () -> {
                    });
                }));
            });
            node(atmosphericIntake, () -> {
                node(inlet, () -> node(vacuumFreezer));
                node(AnnealingOven);
                node(magmaDiffser, Seq.with(
                        new Objectives.Research(magmaTap),
                        new Objectives.OnSector(Ingress)
                ), () -> node(azuriteKiln, () -> {
                    node(ferroSiliconFoundry, Seq.with(
                            new Objectives.SectorComplete(Torrent),
                            new Objectives.Research(bauxiteCentrifuge)
                    ), () -> {
                    });
                    node(towaniteReductionVat, Seq.with(
                            new Objectives.OnSector(CrystalCaverns)
                    ), () -> node(acuminiteDegredationArray));
                    node(bauxiteCentrifuge);
                }));
            });
        });
    }
}
