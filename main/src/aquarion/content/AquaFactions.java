package aquarion.content;

import aquarion.content.blocks.*;
import aquarion.units.type.AquaUnitType;
import aquarion.world.blocks.units.UnitBlock;
import arc.Events;
import arc.struct.ObjectMap;
import arc.struct.ObjectSet;
import arc.util.Nullable;
import arc.util.Time;
import aquarion.ui.ModSettings;
import aquarion.units.DefunctUnitType;
import mindustry.content.TechTree;
import mindustry.game.EventType;
import mindustry.game.Rules;
import mindustry.gen.Unit;
import mindustry.type.UnitType;
import mindustry.world.Block;
import mindustry.world.blocks.liquid.LiquidBlock;
import mindustry.world.meta.BuildVisibility;

import static mindustry.Vars.*;

/**
 * Faction-based build menu filtering. Blocks tagged with a faction are hidden from the
 * placement menu unless the player's current unit belongs to that faction.
 * A unit's faction is its explicit tag, "defunct" for DefunctUnitTypes, or "crux" by default.
 * Untagged blocks are visible to everyone.
 * Hiding is done by swapping block buildVisibility, so nothing is written into saves or rules.
 * Works in multiplayer: content is per-process, so each client filters by its own unit's faction.
 */
public class AquaFactions{
    public static final String sharded = "sharded", crux = "crux", defunct = "defunct";

    static final ObjectMap<Block, String> blockFactions = new ObjectMap<>();
    static final ObjectMap<UnitType, String> unitFactions = new ObjectMap<>();
    static final ObjectMap<Block, BuildVisibility> originals = new ObjectMap<>();
    static final ObjectSet<Block> hiddenBlocks = new ObjectSet<>();
    static Unit lastUnit;
    static long lastApply;
    static boolean lastEnabled = true;

    /** Tags blocks with a faction. They become visible only to units of that faction. Null entries (declared but unimplemented blocks) are skipped. */
    public static void set(String faction, Block... blocks){
        for(Block block : blocks){
            if(block != null) blockFactions.put(block, faction);
        }
    }

    /** Removes faction tags, making the blocks visible to everyone again. */
    public static void clear(Block... blocks){
        for(Block block : blocks) blockFactions.remove(block);
    }

    /** Tags unit types with a faction, overriding the default DefunctUnitType/team rules. */
    public static void setUnits(String faction, UnitType... types){
        for(UnitType type : types) unitFactions.put(type, faction);
    }

    /** Tags every block from this mod that has no explicit tag yet. */
    public static void defaultTagModBlocks(String faction){
        for(Block block : content.blocks()){
            if(block.minfo.mod != null && block.minfo.mod.name.equals("aquarion") && !blockFactions.containsKey(block)){
                blockFactions.put(block, faction);
            }
        }
    }

    /** Tags every mod block present in the tech tree that has no explicit tag yet. Vanilla blocks in the tree are left untagged. */
    public static void tagTechTreeBlocks(String faction){
        for(TechTree.TechNode node : TechTree.all){
            if(node.content instanceof Block block && block.minfo.mod != null && block.minfo.mod.name.equals("aquarion") && !blockFactions.containsKey(block)){
                blockFactions.put(block, faction);
            }
        }
    }

    public static void loadContent(){
        //Sharded
        set(sharded,
                //Mining
                CrafterBlocks.harvester,CrafterBlocks.pinDrill, CrafterBlocks.DrillDerrick, CrafterBlocks.drillRig,
                CrafterBlocks.plasmaExtractor, CrafterBlocks.beamBore, CrafterBlocks.algalTerrace,CrafterBlocks.CentrifugalPump ,CrafterBlocks.pumpAssembly,
                CrafterBlocks.ferricGrinder, CrafterBlocks.graphiteConcentrator,
                //Liquid
                LiquidBlocks.siphon,LiquidBlocks.siphonBridge,LiquidBlocks.siphonJunction,LiquidBlocks.siphonSorter,
                LiquidBlocks.siphonRouter, LiquidBlocks.siphonReservoir, LiquidBlocks.siphonGullet, LiquidBlocks.siphonVessel,
                LiquidBlocks.pulseSiphon, LiquidBlocks.pulseSiphonBridge, LiquidBlocks.pipe, LiquidBlocks.pipeTank,
                //heat
                CrafterBlocks.heatChannel, CrafterBlocks.convectionHeater, CrafterBlocks.combustionHeater, CrafterBlocks.coalHeater,
                CrafterBlocks.nuetralizationChamber, CrafterBlocks.coolingTower,
                //Power
                PowerBlocks.advSolarGen, PowerBlocks.capacitorBank, PowerBlocks.energyBank, PowerBlocks.heatExchanger, PowerBlocks.turbineDynamo,
                PowerBlocks.hydroxideReactor, PowerBlocks.heatEngine, PowerBlocks.ionBattery, PowerBlocks.pylon, PowerBlocks.solarGenerator,
                PowerBlocks.outlet, PowerBlocks.petroleumEngine, PowerBlocks.miniumReactor, PowerBlocks.leadBurner, PowerBlocks.fumeEngine,
                //crafting
                CrafterBlocks.ammoniaCompressor, CrafterBlocks.AnnealingOven, CrafterBlocks.arcFurnace, CrafterBlocks.atmosphericIntake,
                CrafterBlocks.brassMixingPot, CrafterBlocks.coalLiquefactor, CrafterBlocks.inlet, CrafterBlocks.inletArray, CrafterBlocks.glassPulverizer,
                RefineryBlocks.acuminiteDegredationArray, RefineryBlocks.atmosphericCentrifuge, RefineryBlocks.azuriteKiln, RefineryBlocks.bauxiteCentrifuge,
                RefineryBlocks.biotiteLeachingVessel, RefineryBlocks.brineElectrolyzer, RefineryBlocks.desulferizationAssembly,RefineryBlocks.electrolysisVat,
                RefineryBlocks.fumeSeparator, RefineryBlocks.galenaCrucible, RefineryBlocks.gasifier, RefineryBlocks.hazeCrackingUnit, RefineryBlocks.magmaDiffuser,
                RefineryBlocks.vacuumFreezer, RefineryBlocks.ultrafamicRefinery, RefineryBlocks.towaniteReductionVat, RefineryBlocks.thermalCrackingUnit,
                RefineryBlocks.scrapCentrifuge, RefineryBlocks.slagRefinementArray, CrafterBlocks.brineMixer, CrafterBlocks.cupronickelAlloyer, CrafterBlocks.ferroSiliconFoundry,
                CrafterBlocks.sporeProcessor, CrafterBlocks.steelFoundry, CrafterBlocks.SilicaOxidator, CrafterBlocks.polymerPress, CrafterBlocks.SolidBoiler,
                CrafterBlocks.solarBoiler, CrafterBlocks.thermalEvaporator, CrafterBlocks.fumeFilter, CrafterBlocks.fumeMixer,
                //Turrets.
                TurretBlocks.aftershock, TurretBlocks.douse, TurretBlocks.dislocate, TurretBlocks.redact, TurretBlocks.pelt, TurretBlocks.point, TurretBlocks.vector,
                TurretBlocks.confront, TurretBlocks.concuss, TurretBlocks.perforate, TurretBlocks.sentry, TurretBlocks.suffocate, TurretBlocks.javelin, TurretBlocks.volt,
                TurretBlocks.thrash, TurretBlocks.flagellate, TurretBlocks.Foment, TurretBlocks.truncate, TurretBlocks.torrefy, TurretBlocks.grace, TurretBlocks.maelstrom,
                TurretBlocks.refraction,
                //Units
                UnitBlocks.armamentMounting,UnitBlocks.bulwark,UnitBlocks.castellan,UnitBlocks.rampart, UnitBlocks.crest, UnitBlocks.index,UnitBlocks.initializationBay,
                UnitBlocks.pillage,UnitBlocks.mite,UnitBlocks.shatter,UnitBlocks.soar,UnitBlocks.reave,UnitBlocks.regality,UnitBlocks.pugnate,UnitBlocks.raze,UnitBlocks.weld,
                UnitBlocks.solder, UnitBlocks.reinforcedFraming, UnitBlocks.unitByte, UnitBlocks.statusApplier,
                //effect
                EffectBlocks.lantern,CoreBlocks.bomb,CoreBlocks.buildCairn,CoreBlocks.buzzSaw,CoreBlocks.cache,CoreBlocks.coreCuesta,CoreBlocks.corePike,
                CoreBlocks.mendPylon, CoreBlocks.mendPyre, CoreBlocks.mendSubstation, CoreBlocks.crate, CoreBlocks.deflectorWell, CoreBlocks.laboratory, CoreBlocks.infomatic,
                CoreBlocks.toggler, CoreBlocks.channel, CoreBlocks.storageReader, CoreBlocks.splitter,
                //Payload
                DistributionBlocks.payloadDisplacer, DistributionBlocks.payloadPad, DistributionBlocks.payloadDistributor,
                //distribution
                DistributionBlocks.sealedConveyor, DistributionBlocks.sealedDistributor, DistributionBlocks.sealedInvertedSorter, DistributionBlocks.sealedJunction,
                DistributionBlocks.sealedOverflow, DistributionBlocks.cremator, DistributionBlocks.itemHopper, DistributionBlocks.itemYeeter, DistributionBlocks.tungstenHopper, DistributionBlocks.tungstenYeeter,
                DistributionBlocks.steelRouter, DistributionBlocks.steelConveyor, DistributionBlocks.massDistributor, DistributionBlocks.sealedUnderflow, DistributionBlocks.sealedSorter,
                DistributionBlocks.sealedUnloader, DistributionBlocks.sealedRouter, DistributionBlocks.armoredSealedConveyor

        );
        setUnits(sharded, AquaUnitTypes.cull,  AquaUnitTypes.cullButScorch);
        //Defunct
        set(defunct, EnvironmentBlocks.defunctFramingCross, EnvironmentBlocks.smallDefunctRadarTower);
        setUnits(defunct, AquaUnitTypes.maintainer);
        //everything researchable in the modded tech tree is sharded
        tagTechTreeBlocks(sharded);
    }

    public static void init(){
        Events.run(EventType.Trigger.update, () -> {
            if(!state.isGame() || player == null) return;
            Unit unit = player.unit();
            boolean enabled = ModSettings.getBlockFactionVisibility();
            //in multiplayer, re-apply periodically in case the local team changes without a unit swap
            if(unit != lastUnit || enabled != lastEnabled || (net.active() && Time.millis() - lastApply >= 2000)){
                lastUnit = unit;
                lastEnabled = enabled;
                apply();
            }
        });
        Events.on(EventType.WorldLoadEvent.class, e -> {
            lastUnit = null;
            apply();
        });
    }

    public static void apply(){
        //never touch the editor
        if(!state.isGame() || state.rules.editor) return;
        lastApply = Time.millis();

        //clean up bans left behind by the old rules-based implementation
        Rules rules = state.rules;
        for(Block block : blockFactions.keys()) rules.bannedBlocks.remove(block);

        //restore everything we hid before
        for(Block block : hiddenBlocks){
            block.buildVisibility = originals.get(block, BuildVisibility.shown);
        }
        hiddenBlocks.clear();

        String faction = enabled() ? factionOf(player == null ? null : player.unit()) : null;
        if(faction != null){
            for(var entry : blockFactions.entries()){
                if(!entry.value.equals(faction)){
                    Block block = entry.key;
                    if(!originals.containsKey(block)) originals.put(block, block.buildVisibility);
                    block.buildVisibility = BuildVisibility.hidden;
                    hiddenBlocks.add(block);
                }
            }
        }

        refreshMenu();
    }

    /** Whether faction filtering currently applies. Never applies in sandbox games, and can be toggled in the mod settings. */
    public static boolean enabled(){
        return ModSettings.getBlockFactionVisibility() && !state.rules.infiniteResources;
    }

    static void refreshMenu(){
        if(!headless && ui.hudfrag != null && ui.hudfrag.blockfrag != null){
            ui.hudfrag.blockfrag.rebuild();
        }
    }

    public static @Nullable String factionOf(@Nullable Unit unit){
        if(unit == null) return null;
        String tagged = unitFactions.get(unit.type);
        if(tagged != null) return tagged;
        if(unit.type instanceof DefunctUnitType) return defunct;
        return crux;
    }
}
