package aquarion.planets;

import aquarion.blocks.*;
import aquarion.world.blocks.distribution.SealedConveyor;
import arc.struct.Seq;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.game.Objectives;

import static aquarion.AquaItems.*;
import static aquarion.AquaLiquids.brine;
import static aquarion.AquaLiquids.hydroxide;
import static aquarion.blocks.AquaCrafters.CompressionDrill;
import static aquarion.blocks.AquaCrafters.ramDrill;
import static aquarion.blocks.AquaLiquid.*;
import static aquarion.planets.AquaSectorPresets.Chasm;
import static aquarion.planets.AquaSectorPresets.Valley;
import static mindustry.content.Liquids.arkycite;
import static mindustry.content.TechTree.*;
//at least it's gonna be shredded
public class TantrosTechTree {
    public static void load() {
        AquaPlanets.tantros2.techTree = nodeRoot("Tantros", AquaCore.corePike, () -> {
            node(AquaDistribution.cargoDock, () -> {
                node(AquaDistribution.cargoDepot, () -> {});
            });
            node(AquaDistribution.sealedConveyor, () -> {
                node(AquaDistribution.sealedRouter, () -> {
                    node(AquaDistribution.sealedJunction, () -> {});
                    node(AquaDistribution.sealedSorter, () -> {
                    });
                    node(AquaDistribution.sealedOverflow, () -> {
                        node(AquaDistribution.sealedUnderflow, () -> {
                        });
                    });
                });
            });
            node(AquaPower.GeothermalGenerator, () -> {
                node(AquaPower.hydroxideGenerator, () -> {});
                node(AquaPower.Relay, () -> {});
                    });
            node(AquaCrafters.bauxiteHarvester, Seq.with(new Objectives.Research(AquaPower.GeothermalGenerator)), () -> {

                        node(AquaCrafters.cultivationChamber, () -> {
                            node(AquaCrafters.clarifier, () -> {});
                            node(AquaCrafters.ceramicKiln, () -> {
                                node(AquaCrafters.chireniumElectroplater, Seq.with(new Objectives.Research(nickel), new Objectives.SectorComplete(Chasm)), () -> {
                                });
                            });
                        });
                    });
            node(CompressionDrill, () -> {
                node(ramDrill, () -> {});
            });
            node(siphon, () -> {
                node(ThermalPump, () -> {});
                node(siphonBridge, () -> {});
                            node(siphonRouter, () -> {
                                node(siphonJunction, () -> {
                                });
                            });
                        });

            nodeProduce(Items.lead, () -> {
                nodeProduce(salt, Seq.with(new Objectives.Produce(brine)), () -> {});
                nodeProduce(ceramic, () -> {});
            });
            nodeProduce(bauxite, () -> {
                nodeProduce(gallium, () -> {});
                nodeProduce(nickel, () -> {
                    nodeProduce(chirenium, () -> {});
                });
            });
            nodeProduce(arkycite, () -> {});
            nodeProduce(brine, () -> {
                nodeProduce(hydroxide, () -> {

                });
                    });
            node(Chasm, () -> {
                node(Valley, Seq.with(new Objectives.SectorComplete(Chasm), new Objectives.Produce(ceramic)), () -> {});
            });
            node(AquaTurrets.Forment, () -> {
                node(AquaTurrets.Fragment, () -> {});
                node(AquaTurrets.gyre, Seq.with(new Objectives.Research(chirenium), new Objectives.SectorComplete(Chasm)), () -> {});
            });
            node(AquaDefense.smallBauxiteWall, () -> {
                node(AquaDefense.bauxiteWall, () -> {
                    node(AquaDefense.smallGalliumWall, () -> {
                        node(AquaDefense.galliumWall, () -> {});
                    });
                });
            });
        });

    }
}