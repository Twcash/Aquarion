package aquarion.content;

import aquarion.content.blocks.CrafterBlocks;
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

        AquaPlanets.tantros2.techTree = AquaPlanets.fakeSerpulo.techTree = AquaPlanets.fakeErekir.techTree = nodeRoot("RECOMPILE", corePike, () -> {
            context().researchCostMultipliers = costMultipliers;
            node(infomatic);
            node(nickelWall, () -> {
                node(mendPyre, () -> {
                    node(overClockProjector);
                    node(forceGenerator);
                    node(deflectorWell);
                    node(lantern);
                    node(mendPylon, () -> {
                        node(mendSubstation);
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
                node(aluminumWall, () ->
                        node(hugeAluminumWall, () ->
                                node(ferrosilconWall, () ->
                                        node(hugeFerrosiliconWall, () -> {
                                        }))));
            });
            node(bauxiteWall, () -> {
                node(hugeBauxiteWall, () -> {
                    node(bauxiteBarricade);

                });
            });
            node(bulwark, Seq.with(
                    new Objectives.OnSector(twinPass),
                    new Objectives.Research(pelt)
            ), () -> {
                node(bomb);
                node(meteor, ()-> {
                    node(vesta);
                });
                node(pillage);
                node(payloadPad, ()-> {
                    node(payloadDistributor);
                    node(payloadDisplacer);
                    node(initializationBay);
                });
                node(weld, ()->{
                    node(solder);
                });
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
                node(leadBurner, () -> {
                    node(turbineDynamo, () -> {
                        node(fumeEngine, () -> {});
                        node(petroleumEngine, () -> {});
                        node(hydroxideReactor, () -> {});
                    });
                    node(heatEngine, () -> {
                        node(heatExchanger, () -> {});
                    });
                    node(miniumReactor, () -> {});
                    node(solarGenerator, () -> {
                        node(advSolarGen, () -> {});
                    });
                });
                node(outlet, () -> {});
                node(energyBank, () -> {
                    node(capacitorBank, () -> {
                        node(ionBattery, () -> {});
                    });
                });
            });
            node(point, () -> {
                node(Foment, () -> {
                    node(buzzSaw, () -> {});
                    node(redact, () -> {
                        node(maelstrom, () -> {});
                        node(dislocate, () -> {
                            node(perforate, () -> {
                                node(torrefy, () -> {});
                                node(flagellate, () -> {});
                            });
                        });
                        node(focus, () -> {});
                    });
                    node(confront, () -> {});
                });
                node(pelt, () -> {
                    node(douse, () -> {
                        node(suffocate, () -> {});
                        node(concuss, () -> {});
                        node(sentry, () -> {});
                    });
                    node(vector, () -> {
                        node(grace, () -> {
                            node(thrash, () -> {
                                node(truncate, () -> {});
                                node(aftershock, () -> {});
                            });
                        });
                    });
                    node(refraction, () -> {});
                });
                node(volt, () -> {});
            });
            node(sealedConveyor, () -> {
                node(sealedRouter, () -> {
                    node(sealedUnloader, () -> {
                        node(cremator, () -> {});
                        node(crate, () -> {
                            node(cache, () -> {});
                        });
                    });
                    node(sealedDistributor, () -> {
                        node(massDistributor, () -> {});
                    });
                    node(sealedOverflow, () -> {
                        node(sealedUnderflow, () -> {});
                        node(itemYeeter, () -> {});
                        node(sealedSorter, () -> {
                            node(sealedInvertedSorter, () -> {});
                        });
                    });
                });
                node(armoredSealedConveyor, () -> {
                    node(steelConveyor, () -> {
                        node(steelRouter, () -> {});
                    });
                });
                node(sealedJunction, () -> {});
            });
            node(resurgence, () -> {
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
                            node(FeldsparRavine, Seq.with(
                                    new Objectives.SectorComplete(Torrent),
                                    new Objectives.Research(leachingVessel)
                            ), ()->{

                                node(CrystalCaverns, Seq.with(
                                        new Objectives.Research(fumeEngine),
                                        new Objectives.SectorComplete(FeldsparRavine),
                                        new Objectives.Research(armoredSealedConveyor),
                                        new Objectives.Research(redact)
                                ), () -> {
                                    node(Ecotone, Seq.with(
                                            new Objectives.SectorComplete(CrystalCaverns)
                                    ),()->{
                                        node(brinePlateau, Seq.with(
                                                new Objectives.SectorComplete(Ecotone)
                                        ), ()->{});
                                    });
                                    node(ripHold, Seq.with(new Objectives.SectorComplete(Ecotone)),()->{});
                                });
                                node(Grove, Seq.with(
                                        new Objectives.SectorComplete(FeldsparRavine),
                                        new Objectives.Research(pugnate),
                                        new Objectives.Research(vacuumFreezer),
                                        new Objectives.Research(aluminum)
                                ), ()->{
                                    node(SubmergedCanyon, Seq.with(
                                            new Objectives.SectorComplete(Grove)
                                    ), ()->{
                                        node(GalenaFringe, Seq.with(
                                                new Objectives.SectorComplete(SubmergedCanyon),
                                                new Objectives.Research(galena),
                                                new Objectives.Research(galenaCrucible),
                                                new Objectives.Research(petroleumEngine)
                                        ), () -> {});
                                        node(verdantShallows, Seq.with(
                                                new Objectives.SectorComplete(SubmergedCanyon),
                                                new Objectives.Research(ultrafamicRefinery),
                                                new Objectives.Research(DrillDerrick)
                                        ), () -> {});
                                    });
                                });
                            });
                        }));
                        node(floodPlains, Seq.with(
                                new Objectives.Research(metaglass),
                                new Objectives.Research(AnnealingOven),

                                new Objectives.Research(pelt),
                                new Objectives.SectorComplete(twinPass)
                        ), () -> {
                            node(mountainsideComplex, Seq.with(
                                    new Objectives.SectorComplete(floodPlains)
                            ), () -> {
                                node(lowlandStrait, Seq.with(
                                        new Objectives.SectorComplete(mountainsideComplex),
                                        new Objectives.Research(vector),
                                        new Objectives.Research(SolidBoiler)
                                ), () -> {
                                });
                            });
                            node(coupledBasin, Seq.with(
                                    new Objectives.SectorComplete(floodPlains)
                            ),()->{});
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

                                node(frigidShores, ()->{
                                    node(blastedDockyards);
                                });
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

                                    node(searedWastes, Seq.with(
                                            new Objectives.SectorComplete(erodedCanyon),
                                            new Objectives.Research(thrash)),()->{
                                        node(dryRiver, Seq.with(new Objectives.SectorComplete(searedWastes)), ()->{});

                                    });
                                });
                                node(diseasedCleft, Seq.with(new Objectives.SectorComplete(bay)), ()->{
                                    node(fungalTropics, Seq.with(new Objectives.SectorComplete(diseasedCleft)), ()->{
                                        node(violetValley, Seq.with(
                                                new Objectives.SectorComplete(fungalTropics),
                                                new Objectives.Research(sporeProcessor),
                                                new Objectives.Research(ferrosilicon)
                                        ), ()->{});
                                    });
                                });


                            });
                        });

                    });
                    node(frozenLake, Seq.with(
                            new Objectives.SectorComplete(resurgence)
                    ), ()->{});
            });
            nodeProduce(silicon, () -> {
                nodeProduce(zinc, () -> {
                    nodeProduce(biotite, () -> {
                        nodeProduce(ferricMatter, () -> {
                            nodeProduce(fumes, () -> {});
                            nodeProduce(ferrosilicon, () -> {});
                            nodeProduce(steel, () -> {});
                        });
                        nodeProduce(aluminum, () -> {});
                        nodeProduce(rareSludge, () -> {});
                    });
                    nodeProduce(azurite, () -> {
                        nodeProduce(bauxite, () -> {
                            nodeProduce(galena, () -> {});
                        });
                        nodeProduce(towanite, () -> {});
                        nodeProduce(cuprite, () -> {});
                    });
                    nodeProduce(magma, () -> {});
                });
                nodeProduce(nickel, () -> {
                    nodeProduce(air, () -> {
                        nodeProduce(water, () -> {
                            nodeProduce(halideWater, () -> {
                                nodeProduce(muriaticAcid, () -> {
                                    nodeProduce(hydroxide, () -> {});
                                });
                                nodeProduce(salt, () -> {
                                    nodeProduce(brine, () -> {
                                        nodeProduce(chlorine, () -> {});
                                    });
                                });
                            });
                            nodeProduce(haze, () -> {});
                            nodeProduce(oxygen, () -> {
                                nodeProduce(hydrogen, () -> {});
                            });
                        });
                        nodeProduce(argon, () -> {});
                        nodeProduce(nitrogen, () -> {});
                    });
                    nodeProduce(cupronickel, () -> {});
                });
                nodeProduce(copper, () -> {
                    nodeProduce(graphite, () -> {
                        nodeProduce(coal, () -> {
                            nodeProduce(sporePod, () -> {
                                nodeProduce(bioPulp, () -> {});
                            });
                            nodeProduce(brimstone, () -> {});
                        });
                    });
                    nodeProduce(brass, () -> {});
                });
                nodeProduce(lead, () -> {
                    nodeProduce(minium, () -> {
                        nodeProduce(vitriol, () -> {
                            nodeProduce(serpentine, () -> {
                                nodeProduce(magnesiumPowder, () -> {});
                            });
                        });
                    });
                    nodeProduce(metaglass, () -> {
                        nodeProduce(oil, () -> {
                            nodeProduce(petroleum, () -> {
                                nodeProduce(polymer, () -> {});
                            });
                            nodeProduce(methane, () -> {
                                nodeProduce(ammonia, () -> {});
                            });
                        });
                    });
                });
                nodeProduce(sand, () -> {
                    nodeProduce(scrap, () -> {});
                    nodeProduce(slag, () -> {});
                });
            });
            node(siphon, () -> {
                node(siphonRouter, () -> {
                    node(siphonVessel, ()-> {
                        node(pipeTank);
                        node(siphonReservoir, ()-> {
                            node(siphonGullet);
                        });
                    });
                    node(siphonUnderflow);
                });
                node(siphonBridge, () -> {
                    node(pulseSiphonBridge);
                });
                node(siphonJunction);
                node(pipe);
            });
            node(CentrifugalPump, ()->{
                node(pumpAssembly);
            });
            node(magmaTap, ()->{
                node(thermalEvaporator);
            });
            node(harvester, () -> {
                node(DrillDerrick, ()->{
                    node(drillRig);
                });
                node(pinDrill);
                node(plasmaExtractor);
                node(beamBore);
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
                node(electrolysisCell);
                node(AnnealingOven, () -> {
                    node(slagRefinementAssemblage, Seq.with(
                            new Objectives.Produce(slag)
                    ), ()->{});
                    node(sporeProcessor, Seq.with(
                            new Objectives.OnSector(diseasedCleft)
                    ), ()->{});
                    node(cupronickelAlloyer, () -> {
                        node(ferricGrinder, () -> {
                            node(ultrafamicRefinery);
                            node(ferroSiliconFoundry, Seq.with(
                            ), () -> {
                                node(steelFoundry);
                            });
                        });
                        node(SolidBoiler, Seq.with(
                                new Objectives.SectorComplete(twinPass)
                        ), () -> {
                            node(solarBoiler);
                        });
                    });
                });
                node(thermalCrackingUnit, () -> {
                    node(steamCrackingUnit);
                    node(coalLiquefactor);
                    node(desulferizationAssembly);
                    node(polymerPress);
                });
                node(fumeMixer);
                node(fumeSeparator);
                node(fumeFilter, Seq.with(
                ), () -> {

                });
                node(gasifier);
                node(algalTerrace);
                node(magmaDiffser, Seq.with(
                        new Objectives.Research(magmaTap),
                        new Objectives.OnSector(Ingress)
                ), () -> node(azuriteKiln, () -> {
                    node(galenaCrucible);
                    node(towaniteReductionVat, Seq.with(
                            new Objectives.OnSector(CrystalCaverns)
                    ), () -> {
                        node(brineMixer);
                        node(brineElectrolyzer);
                        node(acuminiteDegredationArray, Seq.with(
                                new Objectives.SectorComplete(Grove)
                        ), () -> {
                        });
                    });
                    node(bauxiteCentrifuge);
                    node(leachingVessel);
                }));
            });
        });
    }
}