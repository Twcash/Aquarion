package aquarion.entities;

import aquarion.annotations.Annotations.EntityDef;
import aquarion.gen.AquaUnitc;
import mindustry.gen.*;

/** Native movement families with the common player component mixed in. */
public class AquaPlayerUnitDefs{
    @EntityDef({Unitc.class, AquaUnitc.class})
    abstract static class AquaPlayerGroundComp{}
    @EntityDef({Unitc.class, Mechc.class, AquaUnitc.class})
    abstract static class AquaPlayerMechComp{}
    @EntityDef({Unitc.class, Legsc.class, AquaUnitc.class})
    abstract static class AquaPlayerLegsComp{}
    @EntityDef({Unitc.class, Tankc.class, AquaUnitc.class})
    abstract static class AquaPlayerTankComp{}
    @EntityDef({Unitc.class, WaterMovec.class, AquaUnitc.class})
    abstract static class AquaPlayerWaterComp{}
    @EntityDef({Unitc.class, ElevationMovec.class, AquaUnitc.class})
    abstract static class AquaPlayerElevationComp{}
    @EntityDef({Unitc.class, Payloadc.class, AquaUnitc.class})
    abstract static class AquaPlayerPayloadComp{}
    @EntityDef({Unitc.class, Crawlc.class, AquaUnitc.class})
    abstract static class AquaPlayerCrawlComp{}
}
