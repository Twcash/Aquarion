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
import static aquarion.blocks.AquaEffect.lantern;
import static aquarion.blocks.AquaLiquid.*;
import static aquarion.blocks.AquaPower.*;
import static aquarion.blocks.AquaTurrets.*;
import static aquarion.blocks.AquaUnitFactories.*;
import static aquarion.planets.AquaSectorPresets.*;
import static mindustry.content.Items.*;
import static mindustry.content.Liquids.*;
import static mindustry.content.TechTree.*;
public class TantrosTechTree {
    public static void load() {
        AquaPlanets.tantros2.techTree = AquaPlanets.fakeSerpulo.techTree = nodeRoot("RECOMPILE", corePike, () -> {
            node(nickelWall, () -> {
                        node(mendPyre, () -> {
                            node(lantern);
                                    node(mendPylon, Seq.with(
                                            new Objectives.OnSector(CrystalCaverns)
                                    ), () -> {
                                    });
                                });
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
            node(pylon, () -> {
                node(capacitorBank, () -> {
                    node(ionBattery, Seq.with(
                            new Objectives.OnSector(CrystalCaverns)
                    ), () -> {});
                });
                node(outlet);
                node(solarGenerator, () -> {
                    node(turbineDynamo);
                    node(heatEngine, () ->{
                        node(fumeEngine);
                        node(hydroxideReactor);
                    });
                });
            });
            node(point, () -> {
                node(buildCairn);
                node(Foment, Seq.with(
                        new Objectives.OnSector(Ingress)
                ), () -> {
                    node(buzzSaw);
                    node(redact, () ->
                            node(maelstrom));
                });
                node(sentry);
                node(pelt, () -> {
                    node(douse);
                    node(grace);
                    node(focus, ()->{
                        node(confront);
                    });
                    node(vector);
                });
            });
            node(sealedConveyor, () -> {
                node(manganeseRail, Seq.with(
                        new Objectives.SectorComplete(Grove)
                ), ()->{});
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
                                new Objectives.Research(beamBore);
                                new Objectives.SectorComplete(Torrent);
                                new Objectives.Research(pugnate);
                                new Objectives.Research(grace);
                                new Objectives.Research(vacuumFreezer);
                                new Objectives.Research(aluminum);
                                new Objectives.Research(bauxiteCentrifuge);
                            });
                            node(CrystalCaverns, Seq.with(
                                    new Objectives.Research(fumeEngine),
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
                            node(mountainsideComplex, Seq.with(
                                    new Objectives.SectorComplete(bay)
                            ), ()->{});
                            node(lowlandStrait, Seq.with(
                                    new Objectives.SectorComplete(bay),
                                    new Objectives.Research(vector),
                                    new Objectives.Research(SolidBoiler)
                            ), ()->{});
                        });
                    }));
            nodeProduce(silicon, () -> {
                nodeProduce(oil, () -> {
                });
                nodeProduce(brine, () ->{
                    nodeProduce(bioPulp, () ->{});
                    nodeProduce(coke, () -> {
                    });
                });

                nodeProduce(nickel, () -> {
                    nodeProduce(serpentine, () ->{
                        nodeProduce(magnesiumPowder, ()->{});
                    });
                    nodeProduce(Items.lead, () -> {
                        nodeProduce(bauxite, () -> {
                            nodeProduce(aluminum, () -> {
                            });
                            nodeProduce(ferricMatter, () -> nodeProduce(ferrosilicon, () -> {
                            }));
                            nodeProduce(oxygen, () -> {
                            });
                            nodeProduce(fluorine, () -> {
                            });
                            nodeProduce(nitrogen, () -> {
                            });
                            nodeProduce(argon, () -> {
                            });
                        });
                        nodeProduce(sand, () -> {
                            nodeProduce(metaglass, () -> {
                            });

                        });
                    });
                    nodeProduce(Items.copper, () -> {
                        nodeProduce(galena, () ->{});
                        nodeProduce(manganese, () ->{});
                        nodeProduce(acuminite, () ->{
                            nodeProduce(strontium, () ->{});
                        });

                        nodeProduce(water, () -> {
                            nodeProduce(air, () -> nodeProduce(halideWater, () -> {
                            }));
                            nodeProduce(magma, () -> {
                                nodeProduce(slag, () -> nodeProduce(hydroxide, () -> {
                                }));
                                nodeProduce(fumes, () -> {
                                    nodeProduce(brimstone, () ->{});
                                });
                                nodeProduce(dioxide, () -> {
                                });
                            });
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
                    node(siphonReservoir);
                }));
            });
            node(harvester, () -> {
                node(pinDrill, Seq.with(
                        new Objectives.SectorComplete(Grove)
                ), () ->{});
                node(CentrifugalPump);
                node(magmaTap, Seq.with(
                        new Objectives.SectorComplete(twinPass)
                ), () -> node(plasmaExtractor, () -> {
                    node(beamBore, Seq.with(
                    ), () -> {
                    });
                    node(DrillDerrick, Seq.with(
                            new Objectives.OnSector(CrystalCaverns)
                    ), () -> node(pinDrill));
                    node(fumeFilter, Seq.with(
                            new Objectives.OnSector(CrystalCaverns)
                    ), () -> {
                        node(algalTerrace, Seq.with(
                                new Objectives.SectorComplete(Grove)
                        ), () -> {

                        });
                    });
                }));
            });
            node(atmosphericIntake, () -> {
                node(atmosphericCentrifuge);
                node(inlet, () -> node(vacuumFreezer, () ->{
                }));
                node(AnnealingOven, () -> {
                    node(SolidBoiler, Seq.with(
                            new Objectives.SectorComplete(twinPass)
                    ), () ->{});
                });
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
                    ), () -> node(acuminiteDegredationArray, Seq.with(
                            new Objectives.SectorComplete(Grove)
                    ), () ->{}));
                    node(bauxiteCentrifuge);
                }));
            });
        });
    }
}
