package aquarion.world.units;


import aquarion.gen.*;
import aquarion.world.blocks.units.AssembleUnitBlock;
import aquarion.world.blocks.units.InfantryLandingPad;
import aquarion.world.blocks.units.RailWorkshop;
import aquarion.world.blocks.units.UnitBlock;
import arc.struct.ObjectSet;
import arc.struct.ObjectMap;
import mindustry.Vars;
import mindustry.entities.abilities.Ability;
import mindustry.gen.*;
import mindustry.type.UnitType;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.blocks.units.Reconstructor;
import mindustry.world.blocks.units.UnitAssembler;
import mindustry.world.blocks.units.UnitFactory;

/** Selects player units by their core/factory recipes, independent of their current controller or team. */
public class AquaPlayerUnits{
    private static final ObjectSet<UnitType> types = new ObjectSet<>();
    private static final ObjectMap<UnitType, UnitDamageState> damageStates = new ObjectMap<>();
    /** Optional default for every core/buildable unit. Null disables damage particles by default. */
    public static UnitDamageState defaultDamageState;

    public static void setDamageState(UnitType type, UnitDamageState state){ damageStates.put(type, state); }

    public static UnitDamageState damageState(UnitType type){
        if(damageStates.containsKey(type)) return damageStates.get(type);
        if(type instanceof aquarion.units.type.DamageStateUnitType aqua && aqua.damageState != null) return aqua.damageState;
        return defaultDamageState;
    }

    public static boolean contains(UnitType type){ return types.contains(type); }

    public static void init(){
        types.clear();
        for(var block : Vars.content.blocks()){
            if(block instanceof CoreBlock core) register(core.unitType);
            if(block instanceof UnitFactory factory) for(var plan : factory.plans) register(plan.unit);
            if(block instanceof Reconstructor factory) for(var upgrade : factory.upgrades){
                register(upgrade[0]);
                register(upgrade[1]);
            }
            if(block instanceof UnitAssembler factory) for(var plan : factory.plans) register(plan.unit);
            if(block instanceof UnitBlock factory) register(factory.unit);
            if(block instanceof AssembleUnitBlock factory) register(factory.unit);
            if(block instanceof InfantryLandingPad factory) register(factory.unitType);
            if(block instanceof RailWorkshop factory){
                register(factory.inputUnit);
                register(factory.outputUnit);
                for(var unit : factory.unitChoices) register(unit);
            }
        }
    }

    /** Also usable by future custom factories that do not expose standard production plans. */
    public static void register(UnitType type){
        if(type == null || !type.name.startsWith("aquarion-") || !types.add(type)) return;
        Unit sample = type.constructor.get();
        Class<?> entity = sample.getClass();
        // Change only exact native families. Specialized entities keep their own movement and save format.
        if(entity == UnitEntity.class) aquarionEntityMapping.register(type, AquaPlayerGround.class, AquaPlayerGround::create);
        else if(entity == MechUnit.class) aquarionEntityMapping.register(type, AquaPlayerMech.class, AquaPlayerMech::create);
        else if(entity == LegsUnit.class) aquarionEntityMapping.register(type, AquaPlayerLegs.class, AquaPlayerLegs::create);
        else if(entity == TankUnit.class) aquarionEntityMapping.register(type, AquaPlayerTank.class, AquaPlayerTank::create);
        else if(entity == UnitWaterMove.class) aquarionEntityMapping.register(type, AquaPlayerWater.class, AquaPlayerWater::create);
        else if(entity == ElevationMoveUnit.class) aquarionEntityMapping.register(type, AquaPlayerElevation.class, AquaPlayerElevation::create);
        else if(entity == PayloadUnit.class) aquarionEntityMapping.register(type, AquaPlayerPayload.class, AquaPlayerPayload::create);
        else if(entity == CrawlUnit.class) aquarionEntityMapping.register(type, AquaPlayerCrawl.class, AquaPlayerCrawl::create);
        if(!type.abilities.contains(ability -> ability instanceof PlayerUnitBridge)) type.abilities.add(new PlayerUnitBridge());
    }

    /** Covers old saves and custom train entities without replacing their serialization or controllers. */
    public static class PlayerUnitBridge extends Ability{
        public PlayerUnitBridge(){ display = false; }
        @Override public void update(Unit unit){
            if(!(unit instanceof AquaUnitc)) AquaPlayerUnitHooks.updatePlayerUnit(unit);
        }
        @Override public void draw(Unit unit){ AquaPlayerUnitHooks.drawPlayerUnit(unit); }
        @Override public void created(Unit unit){ AquaPlayerUnitHooks.createdPlayerUnit(unit); }
        @Override public void death(Unit unit){ AquaPlayerUnitHooks.diedPlayerUnit(unit); }
    }
}

