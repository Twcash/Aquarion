
extend(GenericCrafter, "blast-kiln", {});
extend(GenericCrafter, "cultivation-chamber", {});
extend(HeatProducer, "water-boiler", {});
extend(GenericCrafter, "purifier", {});
extend(GenericCrafter, "clarifier", {});
extend(GenericCrafter, "electrolysis-cell", {});
extend(GenericCrafter, "cyanogen-mixer", {});
extend(HeatCrafter, "nitrogen-distillery", {});
extend(HeatCrafter, "pyrosilicon-crucible", {});
extend(HeatCrafter, "duralumin-crucible", {});
extend(Wall, "bauxite-wall", {});
extend(Wall, "gallium-wall", {});
extend(Wall, "maganese-wall", {});
extend(Wall, "duralumin-wall", {});
extend(DroneCenter, "mend-hive", {});
extend(RegenProjector, "regen-pylon", {})
extend(ForceProjector, "force-barrier", {})
extend(OverdriveProjector, "overdrive-substation", {})
extend(BuildTurret, "build-sentry", {})
extend(Duct, "sealedconveyor", {});
extend(Duct, "armored-sealed-conveyor", {});
extend(MassDriver, "small-mass-driver", {});
extend(Router, "sealedrouter", {});
extend(OverflowGate, "sealed-overflow", {});
extend(Sorter, "sealedsorter", {});
extend(DirectionalUnloader, "sealed-unloader", {})
extend(UnitCargoLoader, "cargo-dock", {});
extend(UnitCargoUnloadPoint, "cargo-depot", {});
extend(UnitCargoLoader, "cargo-terminal", {});
extend(Incinerator, "flare-tower", {});
extend(PayloadConveyor, "small-payload-conveyor", {});
extend(PayloadRouter, "small-payload-router", {});
extend(PayloadLoader, "small-payload-loader", {});
extend(PayloadUnloader, "small-payload-unloader", {});
extend(PayloadMassDriver, "small-payload-mass-driver", {});
extend(ArmoredConduit, "siphon", {})
extend(LiquidBridge, "siphon-bridge", {})
extend(LiquidRouter, "siphon-distributor", {})
extend(LiquidJunction, "siphon-junction", {})
extend(LiquidRouter, "siphon-router", {})
extend(Battery, "director", {})
extend(PowerNode, "director-beam", {})
extend(Battery, "capacitor", {})
extend(ThermalGenerator, "geothermal-generator", {})
extend(ConsumeGenerator, "pyridine-reactor", {})
extend(HeaterGenerator, "hydroxide-reactor", {})
extend(HeaterGenerator, "small-fusion-reactor", {})
extend(VariableReactor, "central-heat-exchanger", {})
extend(BurstDrill, "compression-drill", {})
extend(Drill, "ram-drill", {})
extend(Drill, "rotary-drill", {})
extend(Drill, "injection-well", {})
extend(StorageBlock, "cache", {})
extend(CoreBlock, "core-pike", {})
extend(CoreBlock, "core-cuesta", {})
extend(CoreBlock, "core-escarpment", {})
extend(ItemTurret, "forment", {})
extend(ItemTurret, "redact", {})
extend(ItemTurret, "torrefy", {})
extend(ItemTurret, "pivot", {})
extend(ItemTurret, "fragment", {})
extend(ContinuousTurret, "refraction", {})
extend(ItemTurret, "banshee", {})
extend(ItemTurret, "deviate", {})
extend(ItemTurret, "purify", {})
extend(ItemTurret, "impudence", {})

extend(UnitFactory, "submarine-factory", {})
extend(UnitFactory, "utility-factory", {})
extend(Reconstructor, "conversive-reconstructor", {})
extend(Reconstructor, "correlative-reconstructor", {})
extend(Constructor, "basic-constructor", {})
extend(UnitAssembler, "basic-assember", {})
extend(UnitAssemblerModule, "mech-catalyst", {})
extend(UnitAssemblerModule, "support-catalyst", {})
extend(UnitAssemblerModule, "assault-catalyst", {})

Events.on(ClientLoadEvent, (event) => {
    Log.info("Aquarion - Sending version check");
    var req = new Http.get(
        "https://raw.githubusercontent.com/Twcash/Tantros-Test/main/mod.hjson",
        (res) => {
            var resp = res.getResultAsString();
            Log.info("Aquaron - response: \n" + resp);

            var json = Jval.read(resp);
            Log.info("Aquarion - remote ver: " + json.get("version"));

            var vers = Vars.mods.getMod("aquarion").meta.version;
            Log.info("Aquarion - local version: " + vers);

            if (!vers.equals(json.get("version"))) {
                Log.warn("Aquarion - not up to date");
                try {
                    Vars.ui.showOkText(
                        "[#22CCFF]Aquarion[white]",
                        Core.bundle.get("scripts.update-aquaria"),
                        "[green]Update available[white], please reinstall Aquarion for latest content!",
                        () => {}
                    );
                } catch (err) {
                    Log.info("Error: " + err.toString());
                }
            } else {
                Log.info("Aquarion - up to date");
            }
        },
        (err) => {
            Vars.ui.showOkText(
                "[#22CCFF]Aquarion[white]",
                "[red]ERROR:[white] Cannot check for updates!",
                () => {}
            );
            Log.err("Aquarion - update check failed :(");
        }
    );
});
