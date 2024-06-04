extend(GenericCrafter, "blast-kiln", {});
extend(GenericCrafter, "cultivation-chamber", {});
extend(GenericCrafter, "cultivation-array", {});
extend(HeatProducer, "water-boiler", {});
extend(GenericCrafter, "purifier", {});
extend(GenericCrafter, "clarifier", {});
extend(GenericCrafter, "electrolysis-cell", {});
extend(GenericCrafter, "cyanogen-mixer", {});
extend(HeatCrafter, "nitrogen-distillery", {});
extend(HeatCrafter, "pyrosilicon-crucible", {});
extend(HeatCrafter, "duralumin-crucible", {});
extend(GenericCrafter, "melting-crucible", {});
extend(Separator, "centrifuge-slag", {});
extend(GenericCrafter, "enrichment-chamber", {});
extend(GenericCrafter, "lithonite-furnace", {});
extend(Wall, "small-bauxite-wall", {});
extend(Wall, "bauxite-wall", {});
extend(Wall, "small-gallium-wall", {});
extend(Wall, "gallium-wall", {});
extend(Wall, "small-manganese-wall", {});
extend(Wall, "maganese-wall", {});
extend(Wall, "small-duralumin-wall", {});
extend(Wall, "duralumin-wall", {});
extend(Wall, "small-lithonite-wall", {});
extend(Wall, "lithonite-wall", {});
extend(DroneCenter, "mend-hive", {});
extend(RegenProjector, "regen-pylon", {});
extend(ForceProjector, "force-barrier", {});
extend(OverdriveProjector, "overdrive-substation", {});
extend(BuildTurret, "build-sentry", {});
extend(Duct, "sealedconveyor", {});
extend(Duct, "armored-sealed-conveyor", {});
extend(Conveyor, "manganese-conveyor", {});
extend(BufferedItemBridge, "manganese-bridge", {});
extend(MassDriver, "small-mass-driver", {});
extend(MassDriver, "large-mass-driver", {});
extend(Router, "sealedrouter", {});
extend(OverflowGate, "sealed-overflow", {});
extend(Sorter, "sealedsorter", {});
extend(DirectionalUnloader, "sealed-unloader", {});
extend(UnitCargoLoader, "cargo-dock", {});
extend(UnitCargoUnloadPoint, "cargo-depot", {});
extend(UnitCargoLoader, "cargo-terminal", {});
extend(Incinerator, "flare-tower", {});
extend(PayloadConveyor, "small-payload-conveyor", {});
extend(PayloadRouter, "small-payload-router", {});
extend(PayloadLoader, "small-payload-loader", {});
extend(PayloadUnloader, "small-payload-unloader", {});
extend(PayloadMassDriver, "small-payload-mass-driver", {});
extend(PayloadMassDriver, "intermediate-payload-mass-driver", {});
extend(ArmoredConduit, "siphon", {});
extend(LiquidBridge, "siphon-bridge", {});
extend(ArmoredConduit, "pulse-siphon", {});
extend(LiquidBridge, "pulse-siphon-bridge", {});
extend(LiquidRouter, "siphon-distributor", {});
extend(LiquidJunction, "siphon-junction", {});
extend(LiquidRouter, "siphon-router", {});
extend(Battery, "conductor", {});
extend(PowerNode, "director-beam", {});
extend(PowerNode, "large-director-beam", {});
extend(Battery, "capacitor", {});
extend(ThermalGenerator, "geothermal-generator", {});
extend(ConsumeGenerator, "pyridine-reactor", {});
extend(HeaterGenerator, "hydroxide-reactor", {});
extend(HeaterGenerator, "small-fusion-reactor", {});
extend(VariableReactor, "small-heat-exchanger", {});
extend(VariableReactor, "central-heat-exchanger", {});
extend(HeaterGenerator, "large-fusion-reactor", {});
extend(GenericCrafter, "reactor-coolant-synthesizer", {});
extend(BurstDrill, "compression-drill", {});
extend(Drill, "ram-drill", {});
extend(Drill, "rotary-drill", {});
extend(Drill, "injection-well", {});
extend(StorageBlock, "cache", {});
extend(CoreBlock, "core-pike", {});
extend(CoreBlock, "core-cuesta", {});
extend(CoreBlock, "core-escarpment", {});
extend(ItemTurret, "forment", {});
extend(ItemTurret, "redact", {});
extend(ItemTurret, "torrefy", {});
extend(ItemTurret, "pivot", {});
extend(ItemTurret, "fragment", {});
extend(ContinuousTurret, "refraction", {});
extend(ItemTurret, "banshee", {});
extend(ItemTurret, "deviate", {});
extend(ItemTurret, "purify", {});
extend(ItemTurret, "clarity", {});
extend(ItemTurret, "impudence", {});
extend(UnitFactory, "submarine-factory", {});
extend(UnitFactory, "utility-factory", {});
extend(Reconstructor, "conversive-reconstructor", {});
extend(Reconstructor, "correlative-reconstructor", {});
extend(Constructor, "basic-constructor", {});
extend(UnitAssembler, "basic-assember", {});
extend(UnitAssemblerModule, "mech-catalyst", {});
extend(UnitAssemblerModule, "support-catalyst", {});
extend(UnitAssemblerModule, "assault-catalyst", {});

//for mechs, replace UnitEntity with MechUnit, for legs it's LegsUnit and for naval is UnitWaterMove.tank = TankUnit, crawl = CrawlUnit, missile = TimedKillUnit,  THANKS LIZ!

//core tree

const cull = extend(UnitType, "cull", {});
cull.constructor = () => extend(UnitEntity, {}); 

//zoarcid tree

const zoarcid = extend(UnitType, "a0-zoarcid", {});
zoarcid.constructor = () => extend(UnitEntity, {}); 

const anguilli = extend(UnitType, "b1-anguilli", {});
anguilli.constructor = () => extend(UnitEntity, {}); 

const cyprin = extend(UnitType, "c2-cyprin", {});
cyprin.constructor = () => extend(UnitEntity, {}); 

const pycogen = extend(UnitType, "d3-pycogen", {});
pycogen.constructor = () => extend(UnitEntity, {}); 

const batoid = extend(UnitType, "e4-batoid", {});
batoid.constructor = () => extend(UnitEntity, {}); 

//Obligate tree

const obligate = extend(UnitType, "unknown", {});
obligate.constructor = () => extend(UnitEntity, {});

const enforce = extend(UnitType, "carid", {});
enforce.constructor = () => extend(UnitEntity, {});

const impel = extend(UnitType, "impel", {});
impel.constructor = () => extend(UnitEntity, {});

const perpetrate = extend(UnitType, "perpetrate", {});
perpetrate.constructor = () => extend(UnitEntity, {});

//steward tree

const steward = extend(UnitType, "steward", {});
steward.constructor = () => extend(MechUnit, {});

const curator = extend(UnitType, "curator", {});
curator.constructor = () => extend(LegsUnit, {});

const custodian = extend(UnitType, "custodian", {});
custodian.constructor = () => extend(TankUnit, {});

const caretaker = extend(UnitType, "caretaker", {});
caretaker.constructor = () => extend(TankUnit, {});

//messenger tree

const messenger = extend(UnitType, "messenger", {});
messenger.constructor = () => extend(MechUnit, {});

const ambassador = extend(UnitType, "ambassador", {});
ambassador.constructor = () => extend(LegsUnit, {});

const consul = extend(UnitType, "consul", {});
consul.constructor = () => extend(LegsUnit, {});

const legate = extend(UnitType, "legate", {});
legate.constructor = () => extend(LegsUnit, {});

// filament tree

const filament = extend(UnitType, "filament", {});
filament.constructor = () => extend(UnitEntity, {});

// pico Tree

const pico = extend(UnitType, "pico", {});
pico.constructor = () => extend(UnitEntity, {});

const byte = extend(UnitType, "byte", {});
byte.constructor = () => extend(UnitEntity, {});