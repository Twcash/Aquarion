package aquarion.content;

import arc.struct.ObjectFloatMap;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.content.Items;
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
import static aquarion.content.blocks.TurretBlocks.*;
import static aquarion.content.blocks.UnitBlocks.*;
import static aquarion.content.AquaSectorPresets.*;
import static mindustry.content.Items.*;
import static mindustry.content.Liquids.*;
import static mindustry.content.TechTree.*;
public class TantrosTechTree {
    public static void load() {
        ObjectFloatMap<Item> costMultipliers = new ObjectFloatMap<Item>();

        for(Item item : Vars.content.items()) costMultipliers.put(item, 0.08f);

        AquaPlanets.tantros2.techTree = AquaPlanets.fakeSerpulo.techTree = nodeRoot("RECOMPILE", corePike, () -> {
            context().researchCostMultipliers = costMultipliers;
            node(nickelWall, () -> {
                node(mendPyre, () -> {
                    node(forceGenerator);
                    node(deflectorWell);
                    node(lantern);
                    node(mendPylon, Seq.with(
                            new Objectives.OnSector(CrystalCaverns)
                    ), () -> {
                    });
                });
                node(hugeNickelWall, () -> {
                    node(cupronickelWall, () -> {
                                node(hugeCupronickelWall);
                        node(polymerWall, () -> {
                                    node(hugePolymerWall);
                                }
                        );
                            }
                    );
                            node(nickelBarricade);
                            node(steelWall, () -> node(hugeSteelWall));
                        }
                );
                node(zincWall, ()->{
                    node(hugeZincWall);
                });
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
                node(payloadPad, ()-> {
                    node(payloadDistributor);
                    node(payloadDisplacer);
                    node(initializationBay);
                });
                node(weld);
                node(unitByte);
                node(crest, () -> {
                    node(soar, () -> {
                    });
                });
                node(rampart, () -> {
                    node(reave, () -> {
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
                node(outlet, () ->{
                    //node(voltageSupplyUnit);
                });
                node(energyBank, ()->{
                    node(capacitorBank, () -> {
                        node(ionBattery, Seq.with(
                                new Objectives.OnSector(CrystalCaverns)
                        ), () -> {
                        });
                    });
                });
                node(solarGenerator, () -> {
                    node(advSolarGen);
                    node(convectionHeater, () -> {
                        node(heatChannel);
                        node(coalHeater, ()->{
                            node(combustionHeater);
                        });
                    });
                    node(turbineDynamo, ()->{
                        node(petroleumEngine);
                    });
                    node(heatExchanger);
                    node(leadBurner, ()-> {
                            node(heatEngine, () -> {
                                node(fumeEngine);
                                node(hydroxideReactor);
                            });
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
                    node(refraction, () -> {
                        node(torrefy);
                    });
                    node(douse);
                    node(grace);
                    node(focus, () -> {
                        node(confront);
                    });
                    node(vector, ()->{
                        node(aftershock, ()->{
                            node(dislocate);
                        });
                        node(truncate);
                        node(thrash, ()->{
                            node(flagellate);
                        });
                    });
                });
            });
            node(sealedConveyor, () -> {
                node(manganeseRail, Seq.with(
                        new Objectives.SectorComplete(Grove)
                ), () -> {
                });
                node(crate, () -> {
                    node(cache, () -> {
                    });
                });

                node(sealedRouter, () -> {
                    node(sealedDistributor, () -> {
                        node(massDistributor);
                    });
                    node(sealedUnloader, () -> {
                    });
                });
                node(armoredSealedConveyor, () -> {
                });
                node(sealedSorter, () -> {
                    node(sealedInvertedSorter);
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
                            node(Ecotone, Seq.with(
                                    new Objectives.SectorComplete(CrystalCaverns),
                                    new Objectives.SectorComplete(Torrent)
                            ), ()->{
                                node(ripHold, Seq.with(new Objectives.SectorComplete(Ecotone)), ()->{});
                            });
                            node(Grove, Seq.with(
                                    new Objectives.Research(beamBore),
                            new Objectives.SectorComplete(Torrent),
                            new Objectives.Research(pugnate),
                            new Objectives.Research(grace),
                            new Objectives.Research(vacuumFreezer),
                            new Objectives.Research(aluminum)
                                    ), ()->{});
                            node(CrystalCaverns, Seq.with(
                                    new Objectives.Research(fumeEngine),
                                    new Objectives.SectorComplete(Torrent),
                                    new Objectives.Research(armoredSealedConveyor),
                                    new Objectives.Research(redact)
                            ), () -> {
                            });
                        }));
                        node(floodPlains, Seq.with(
                                new Objectives.Research(metaglass),
                                new Objectives.Research(AnnealingOven),

                                new Objectives.Research(pelt),
                                new Objectives.SectorComplete(twinPass)
                        ), () -> {
                            node(bay, Seq.with(
                                    new Objectives.Research(vector),
                                    new Objectives.Research(bulwark),
                                    new Objectives.Research(heatEngine),
                                    new Objectives.Research(convectionHeater),
                                    new Objectives.Research(CentrifugalPump),
                                    new Objectives.SectorComplete(resurgence),
                                    new Objectives.SectorComplete(twinPass),
                                    new Objectives.SectorComplete(floodPlains)
                            ), () -> {
                                node(erodedCanyon, Seq.with(
                                        new Objectives.Research(polymer),
                                        new Objectives.Research(ferricGrinder),
                                        new Objectives.Research(arcFurnace),
                                        new Objectives.Research(grace),
                                        new Objectives.Research(refraction),
                                        new Objectives.Research(thermalCrackingUnit),
                                        new Objectives.Research(combustionHeater),
                                        new Objectives.Research(arcFurnace)
                                        ), ()->{
                                    node(dryRiver, Seq.with(new Objectives.SectorComplete(erodedCanyon)), ()->{});
                                    node(searedWastes, Seq.with(
                                            new Objectives.SectorComplete(erodedCanyon),
                                            new Objectives.Research(thrash)),()->{

                                    });
                                });
                                node(diseasedCleft, Seq.with(new Objectives.SectorComplete(bay)), ()->{
                                    node(fungalTropics, Seq.with(new Objectives.SectorComplete(diseasedCleft)), ()->{});
                                });
                                node(mountainsideComplex, Seq.with(
                                        new Objectives.SectorComplete(bay)
                                ), () -> {
                                });
                                node(lowlandStrait, Seq.with(
                                        new Objectives.SectorComplete(bay),
                                        new Objectives.Research(vector),
                                        new Objectives.Research(SolidBoiler)
                                ), () -> {
                                });
                            });
                        });
                    }));
            nodeProduce(silicon, () -> {
                nodeProduce(oil, () -> {
                    nodeProduce(petroleum, () -> {
                        nodeProduce(polymer, ()->{});
                        nodeProduce(hydrogen, () -> {

                        });
                    });
                    nodeProduce(methane, () -> {

                    });
                });
                nodeProduce(bioPulp, () -> {
                });
                nodeProduce(brine, () -> {
                    nodeProduce(chlorine, () -> {
                    });
                });

                nodeProduce(nickel, () -> {
                    nodeProduce(serpentine, () -> {
                        nodeProduce(magnesiumPowder, () -> {
                        });
                    });
                    nodeProduce(salt, ()->{});
                    nodeProduce(Items.lead, () -> {
                        nodeProduce(minium,()->{});
                        nodeProduce(zinc, ()->{});
                        nodeProduce(biotite, ()->{
                            nodeProduce(rareSludge, ()->{});
                        });
                        nodeProduce(bauxite, () -> {
                            nodeProduce(aluminum, () -> {
                            });
                            nodeProduce(ferricMatter, () -> {
                                nodeProduce(steel, () -> {
                                });
                                nodeProduce(ferrosilicon, () -> {
                                });
                            });
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
                        nodeProduce(coal, ()->{});
                        nodeProduce(graphite, () -> {});
                        nodeProduce(galena, () -> {
                        });
                        nodeProduce(manganese, () -> {
                        });
                        nodeProduce(acuminite, () -> {
                            nodeProduce(strontium, () -> {
                            });
                        });

                        nodeProduce(water, () -> {
                            nodeProduce(haze, ()->{});
                            nodeProduce(air, () -> nodeProduce(halideWater, () -> {
                            }));
                            nodeProduce(magma, () -> {
                                nodeProduce(muriaticAcid, ()->{});
                                nodeProduce(slag, () -> nodeProduce(hydroxide, () -> {
                                }));
                                nodeProduce(fumes, () -> {
                                    nodeProduce(brimstone, () -> {
                                    });
                                });
                                nodeProduce(dioxide, () -> {
                                });
                            });
                        });
                        nodeProduce(cupronickel, () -> {
                        });
                        nodeProduce(azurite, () -> {
                            nodeProduce(towanite, () ->{
                                nodeProduce(acuminite, () ->{});
                            });
                            nodeProduce(arsenic, () ->{});
                        });
                    });

                });
            });
            node(siphon, () -> {
                node(siphonRouter, () -> {
                    node(pipeTank);
                    node(siphonUnderflow);
                });
                node(siphonBridge, () -> {
                });
                node(siphonJunction, () -> {
                });
                node(pulseSiphon, () -> node(pulseSiphonBridge, () -> {
                    node(siphonReservoir);
                }));
            });
            node(harvester, () -> {
                node(scrapCentrifuge);
                node(pinDrill, Seq.with(
                        new Objectives.SectorComplete(Grove)
                ), () -> {
                });
                node(CentrifugalPump, ()->{
                    node(pumpAssembly);
                });
                node(thermalEvaporator);
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

                    });
                }));
            });
            node(atmosphericIntake, () -> {

                node(graphiteConcentrator);
                node(atmosphericCentrifuge);
                node(inlet, () -> {
                            node(vacuumFreezer, () -> {
                            });
                            node(inletArray);
                        });
                node(SilicaOxidator, () ->{
                    node(arcFurnace);
                });
                node(scrapCentrifuge);
                node(AnnealingOven, () -> {
                    node(slagRefinementAssemblage);
                    node(sporeProcessor);
                            node(cupronickelAlloyer, () -> {
                                node(ferricGrinder, () -> {
                                    node(ultrafamicRefinery);
                                    node(ferroSiliconFoundry, Seq.with(
                                            new Objectives.SectorComplete(Torrent)
                                    ), () -> {
                                        node(steelFoundry);
                                    });
                                });
                                node(SolidBoiler, Seq.with(
                                        new Objectives.SectorComplete(twinPass)
                                ), () -> {
                                });
                            });
                        });
                node(thermalCrackingUnit, () -> {
                    node(coalLiquefactor);
                    node(desulferizationAssembly);
                    node(polymerPress);
                });
                node(algalTerrace, Seq.with(
                ), () -> {
                    node(gasifier);
                });
                node(magmaDiffser, Seq.with(
                        new Objectives.Research(magmaTap),
                        new Objectives.OnSector(Ingress)
                ), () -> node(azuriteKiln, () -> {
                    node(towaniteReductionVat, Seq.with(
                            new Objectives.OnSector(CrystalCaverns)
                    ), () -> {
                        node(brineMixer);
                        node(brineElectrolyzer);
                        node(acuminiteDegredationArray, Seq.with(
                                new Objectives.SectorComplete(Grove)
                        ), () -> {
                        });
                        node(fumeMixer);
                    });
                    node(bauxiteCentrifuge);
                    node(leachingVessel);
                }));
            });
        });
    }
}
