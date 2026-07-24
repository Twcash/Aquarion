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
import static aquarion.content.blocks.CoreBlocks.channel;
import static aquarion.content.blocks.RefineryBlocks.*;
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
            node(researchServer, () -> {
                node(researchVoider, () -> {});
            });
            node(coreCuesta, () -> {});
            node(infomatic, () -> {
                node(toggler, () -> {
                    node(channel, () -> {
                        node(splitter, () -> {});
                    });
                });
            });
            node(mendPyre, () -> {
                node(mendPylon, () -> {
                    node(mendSubstation, () -> {});
                    node(overClockProjector, () -> {});
                });
                node(lantern, () -> {});
                node(buildCairn, () -> {
                    node(bomb, () -> {});
                    node(constructionTower, () -> {});
                    node(forceGenerator, Seq.with(
                            new Objectives.OnSector(stormyCoast)
                    ), () -> {
                        node(deflectorWell, () -> {});
                    });
                });
            });
            node(nickelWall, () -> {
                node(zincWall, Seq.with(
                        new Objectives.OnSector(Ingress)
                ), () -> {
                    node(bauxiteWall, () -> {
                        node(hugeBauxiteWall, () -> {
                            node(bauxiteBarricade, () -> {});
                        });
                    });
                    node(hugeZincWall, () -> {});
                    node(aluminumWall, () -> {
                        node(hugeAluminumWall, () -> {});
                    });
                });
                node(hugeNickelWall, () -> {
                    node(nickelBarricade, () -> {});
                });
                node(cupronickelWall, () -> {
                    node(polymerWall, () -> {
                        node(hugePolymerWall, () -> {});
                    });
                    node(hugeCupronickelWall, () -> {});
                    node(ferrosilconWall, () -> {
                        node(hugeFerrosiliconWall, () -> {});
                        node(steelWall, () -> {
                            node(hugeSteelWall, () -> {});
                        });
                    });
                });
            });
            node(bulwark, Seq.with(
                    new Objectives.OnSector(floodPlains),
                    new Objectives.Research(pelt)
            ), () -> {
                node(crest, () -> {
                    node(unitByte, () -> {});
                    node(soar, () -> {});
                });
                node(weld, Seq.with(
                        new Objectives.OnSector(diseasedCleft)
                ), () -> {
                    node(solder, () -> {});
                });
                node(pugnate, () -> {
                    node(rampart, () -> {
                        node(raze, () -> {});
                        node(reave, () -> {});
                        node(shatter, () -> {});
                    });
                    node(pillage, Seq.with(
                            new Objectives.SectorComplete(mountainsideComplex)
                    ), () -> {
                        node(meteor, () -> {
                            node(vesta, () -> {});
                        });
                    });
                    node(payloadPad, () -> {
                        node(payloadDisplacer, () -> {});
                        node(initializationBay, () -> {
                            node(statusApplier, () -> {});
                        });
                        node(payloadDistributor, () -> {});
                    });
                });
            });
            node(pylon, () -> {
                node(leadBurner, () -> {
                    node(miniumReactor, Seq.with(
                            new Objectives.OnSector(icyRiver)
                    ), () -> {
                        node(turbineDynamo, Seq.with(
                                new Objectives.Produce(haze),
                                new Objectives.SectorComplete(icyRiver)
                        ), () -> {
                            node(fumeEngine, Seq.with(
                                    new Objectives.Produce(fumes),
                                    new Objectives.SectorComplete(FeldsparRavine)
                            ), () -> {});
                            node(petroleumEngine, Seq.with(
                                    new Objectives.Produce(petroleum),
                                    new Objectives.OnSector(GalenaFringe)
                            ), () -> {});
                            node(hydroxideReactor, Seq.with(
                                    new Objectives.Produce(hydroxide),
                                    new Objectives.SectorComplete(Grove)
                            ), () -> {});
                        });
                    });
                    node(heatEngine, Seq.with(
                            new Objectives.OnSector(twinPass)
                    ), () -> {
                        node(heatExchanger, Seq.with(
                                new Objectives.Research(turbineDynamo),
                                new Objectives.Research(convectionHeater),
                                new Objectives.SectorComplete(bay)
                        ), () -> {});
                    });
                    node(solarGenerator, () -> {
                        node(advSolarGen, Seq.with(
                                new Objectives.OnSector(stormyCoast)
                        ), () -> {});
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
                node(Foment, Seq.with(
                        new Objectives.OnSector(Ingress)
                ), () -> {
                    node(buzzSaw, Seq.with(
                            new Objectives.OnSector(Torrent)
                    ), () -> {});
                    node(redact, () -> {
                        node(maelstrom, () -> {});
                        node(dislocate, () -> {
                            node(perforate, () -> {
                                node(torrefy, () -> {});
                                node(flagellate, Seq.with(
                                        new Objectives.OnSector(dryRiver)
                                ), () -> {});
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
                    node(vector, Seq.with(
                            new Objectives.SectorComplete(twinPass)
                    ), () -> {
                        node(grace, () -> {
                            node(thrash, Seq.with(
                                    new Objectives.SectorComplete(bay)
                            ), () -> {
                                node(truncate, Seq.with(
                                        new Objectives.SectorComplete(erodedCanyon)
                                ), () -> {});
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
                        node(itemYeeter, () -> {
                            node(itemHopper, () -> {});
                        });
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
            node(lib, Seq.with(
                    new Objectives.OnSector(resurgence)
            ), () -> {
                node(ruinedRepository, Seq.with(
                        new Objectives.OnSector(icyRiver)
                ), () -> {});
            });
            node(resurgence, () -> {
                node(twinPass, Seq.with(
                        new Objectives.Research(cupronickelAlloyer),

                        new Objectives.Research(point)
                ), () -> {
                            node(Ingress, Seq.with(
                                    new Objectives.Research(magmaDiffuser)
                            ), () -> {
                                node(Torrent, Seq.with(
                                        new Objectives.Research(inlet),
                                        new Objectives.Research(plasmaExtractor)
                                ), () -> {
                                    node(FeldsparRavine, Seq.with(
                                            new Objectives.SectorComplete(Torrent),
                                            new Objectives.Research(biotiteLeachingVessel)
                                    ), () -> {
                                        node(CrystalCaverns, Seq.with(
                                                new Objectives.Research(fumeEngine),
                                                new Objectives.Research(armoredSealedConveyor),
                                                new Objectives.Research(DrillDerrick),
                                                new Objectives.Research(beamBore)
                                        ), () -> {
                                            node(Ecotone, Seq.with(
                                                    new Objectives.SectorComplete(CrystalCaverns),
                                                    new Objectives.Research(hydroxideReactor)
                                            ), () -> {
                                                node(brinePlateau, Seq.with(
                                                        new Objectives.SectorComplete(Ecotone),
                                                        new Objectives.Research(truncate),
                                                        new Objectives.Research(hazeCrackingUnit)
                                                ), () -> {
                                                });
                                            });
                                            node(ripHold, Seq.with(
                                                    new Objectives.SectorComplete(Ecotone)
                                            ), () -> {
                                            });
                                        });
                                        node(Grove, Seq.with(
                                                new Objectives.Research(pugnate),
                                                new Objectives.Research(vacuumFreezer),
                                                new Objectives.Produce(aluminum)
                                        ), () -> {
                                            node(SubmergedCanyon, Seq.with(
                                            ), () -> {
                                                node(GalenaFringe, Seq.with(
                                                        new Objectives.Research(galena),
                                                        new Objectives.Research(galenaCrucible),
                                                        new Objectives.Research(petroleumEngine)
                                                ), () -> {
                                                });
                                                node(verdantShallows, Seq.with(
                                                        new Objectives.Research(ultrafamicRefinery),
                                                        new Objectives.Research(DrillDerrick)
                                                ), () -> {
                                                });
                                            });
                                        });
                                    });
                                });
                            });
                            node(icyRiver, Seq.with(
                                    new Objectives.Research(SolidBoiler)
                            ), () -> {
                                node(floodPlains, Seq.with(
                                        new Objectives.Research(vector)
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
                                    ), () -> {
                                    });
                                    node(bay, Seq.with(
                                            new Objectives.Research(vector),
                                            new Objectives.Research(bulwark),
                                            new Objectives.Research(CentrifugalPump)
                                    ), () -> {

                                        node(frigidShores, () -> {
                                            node(blastedDockyards, () -> {
                                            });
                                        });
                                        node(erodedCanyon, Seq.with(
                                                new Objectives.Research(ferricGrinder),
                                                new Objectives.Research(thermalCrackingUnit),
                                                new Objectives.Research(combustionHeater)
                                        ), () -> {

                                            node(searedWastes, Seq.with(
                                                    new Objectives.SectorComplete(erodedCanyon),
                                                    new Objectives.Research(thrash)), () -> {
                                                node(dryRiver, Seq.with(new Objectives.SectorComplete(searedWastes)), () -> {
                                                });

                                            });
                                        });
                                        node(diseasedCleft, Seq.with(new Objectives.SectorComplete(bay)), () -> {
                                            node(fungalTropics, Seq.with(new Objectives.SectorComplete(diseasedCleft)), () -> {
                                                node(violetValley, Seq.with(
                                                        new Objectives.SectorComplete(fungalTropics),
                                                        new Objectives.Research(sporeProcessor),
                                                        new Objectives.Research(ferrosilicon)
                                                ), () -> {
                                                });
                                            });
                                        });
                                    });
                                });
                            });
                        });
                node(frozenLake, Seq.with(
                ), () -> {
                    node(stormyCoast, Seq.with(
                            new Objectives.Research(pelt),
                            new Objectives.Research(refraction)
                    ), () -> {});
                });
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
                            nodeProduce(clearwater, () -> {});
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
                        nodeProduce(cryogen, () -> {});
                        nodeProduce(nitrogen, () -> {
                            nodeProduce(argon, () -> {});
                        });
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
                    node(siphonUnderflow, () -> {});
                    node(siphonVessel, () -> {
                        node(pipeTank, () -> {
                            node(siphonGullet, () -> {});
                            node(siphonReservoir, () -> {});
                        });
                    });
                    node(pipe, () -> {});
                });
                node(siphonBridge, () -> {
                    node(pulseSiphonBridge, () -> {});
                });
                node(siphonJunction, () -> {});
            });
            node(CentrifugalPump, ()->{
                node(pumpAssembly);
            });
            node(harvester, () -> {
                node(plasmaExtractor, () -> {
                    node(beamBore, () -> {});
                });
                node(DrillDerrick, () -> {
                    node(drillRig, () -> {});
                });
                node(ferricGrinder, Seq.with(
                        new Objectives.SectorComplete(bay)
                ), () -> {});
            });
            node(atmosphericIntake, () -> {
                node(magmaTap, Seq.with(
                        new Objectives.SectorComplete(twinPass)
                ), () -> {
                    node(inlet, Seq.with(
                            new Objectives.SectorComplete(Ingress)
                    ), () -> {
                        node(inletArray, () -> {});
                        node(vacuumFreezer, Seq.with(
                                new Objectives.SectorComplete(FeldsparRavine)
                        ), () -> {});
                    });
                    node(magmaDiffuser, Seq.with(
                            new Objectives.SectorComplete(twinPass)
                    ), () -> {
                        node(fumeFilter, () -> {
                            node(fumeSeparator, () -> {
                                node(fumeMixer, () -> {});
                            });
                            node(slagRefinementArray, Seq.with(
                                    new Objectives.Produce(slag)
                            ), () -> {
                                node(glassPulverizer, () -> {});
                            });
                        });
                        node(azuriteKiln, Seq.with(
                                new Objectives.SectorComplete(Ingress)
                        ), () -> {
                            node(galenaCrucible, () -> {});
                            node(biotiteLeachingVessel, Seq.with(
                                    new Objectives.SectorComplete(Torrent)
                            ), () -> {
                                node(ultrafamicRefinery, () -> {});
                                node(towaniteReductionVat, () -> {
                                    node(algalTerrace, Seq.with(
                                            new Objectives.SectorComplete(ripHold)
                                    ), () -> {
                                        node(gasifier, Seq.with(
                                                new Objectives.Produce(bioPulp)
                                        ), () -> {
                                            node(coalLiquefactor, () -> {});
                                        });
                                        node(sporeProcessor, Seq.with(
                                                new Objectives.SectorComplete(diseasedCleft)
                                        ), () -> {});
                                    });
                                });
                                node(bauxiteCentrifuge, () -> {});
                            });
                            node(brassMixingPot, Seq.with(

                            ), () -> {});
                        });
                    });
                    node(thermalEvaporator, Seq.with(
                            new Objectives.SectorComplete(Ingress)
                    ), () -> {
                        node(nuetralizationChamber, Seq.with(
                                new Objectives.Produce(muriaticAcid),
                                new Objectives.Produce(hydroxide)
                        ), () -> {});
                    });
                });
                node(AnnealingOven, () -> {
                    node(scrapCentrifuge, Seq.with(
                            new Objectives.OnSector(frozenLake)
                    ), () -> {
                        /*node(filter, Seq.with(
                                new Objectives.SectorComplete(bay)
                        ), () -> {});*/
                    });
                    node(cupronickelAlloyer, Seq.with(
                            new Objectives.SectorComplete(resurgence)
                    ), () -> {
                        node(arcFurnace, () -> {
                            node(SilicaOxidator, () -> {});
                            node(ferroSiliconFoundry, Seq.with(
                                    new Objectives.OnSector(Ecotone)
                            ), () -> {
                                node(steelFoundry, () -> {});
                            });
                        });
                        node(convectionHeater, () -> {
                            node(coalHeater, Seq.with(
                                    new Objectives.Produce(oxygen)
                            ), () -> {
                                node(combustionHeater, Seq.with(
                                        new Objectives.Produce(oxygen)
                                ), () -> {});
                            });
                            node(heatChannel, () -> {});
                        });
                    });
                    node(graphiteConcentrator, () -> {
                        node(SolidBoiler, Seq.with(
                                new Objectives.SectorComplete(twinPass)
                        ), () -> {
                            node(solarBoiler, () -> {
                                node(coolingTower, Seq.with(
                                        new Objectives.SectorComplete(searedWastes)
                                ), () -> {});
                            });
                        });
                        node(electrolysisVat, Seq.with(
                                new Objectives.Produce(water)
                        ), () -> {
                            node(brineElectrolyzer, Seq.with(
                                    new Objectives.Produce(brine)
                            ), () -> {
                                node(brineMixer, () -> {});
                            });
                        });
                        node(thermalCrackingUnit, Seq.with(
                                new Objectives.Produce(oil),
                                new Objectives.SectorComplete(icyRiver)
                        ), () -> {
                            node(ammoniaCompressor, Seq.with(
                                    new Objectives.Produce(methane),
                                    new Objectives.Produce(nitrogen)
                            ), () -> {});
                            node(polymerPress, Seq.with(
                                    new Objectives.Produce(petroleum),
                                    new Objectives.OnSector(bay)
                            ), () -> {
                                node(hazeCrackingUnit, () -> {});
                            });
                            node(desulferizationAssembly, () -> {});
                        });
                    });
                });
                node(atmosphericCentrifuge, () -> {});
            });
        });
    }
}
