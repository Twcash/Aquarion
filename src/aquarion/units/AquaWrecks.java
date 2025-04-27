package aquarion.units;

import aquarion.type.DerelictUnitType;
import mindustry.gen.UnitEntity;

//Worthless. Another memory to how much you fail. FUCK YOU
public class AquaWrecks {
    public static DerelictUnitType zoarcidWreck, anguilliWreck, messengerWreck, ambassadorWreck, consulWreck, gossWreck, stewardWreck,
    reapWreck, InfantryGerbCorpse, maimeWreck;
    public static void loadContent() {
        zoarcidWreck = new DerelictUnitType("zoarcid-wreck"){{ constructor = UnitEntity:: create;targetable = false;}};
        anguilliWreck = new DerelictUnitType("anguilli-wreck"){{ constructor = UnitEntity:: create; outlines = false; targetable = false;}};
        ambassadorWreck = new DerelictUnitType("ambassador-wreck"){{ constructor = UnitEntity::create; outlines = false; targetable = false;}};
        consulWreck = new DerelictUnitType("consul-wreck"){{ constructor = UnitEntity:: create; outlines = false; targetable = false;}};
        gossWreck = new DerelictUnitType("goss-wreck"){{ constructor = UnitEntity:: create;targetable = false;}};
        messengerWreck = new DerelictUnitType("messenger-wreck"){{ constructor = UnitEntity::create;targetable = false;}};
        stewardWreck = new DerelictUnitType("steward-wreck"){{ constructor = UnitEntity:: create;targetable = false;}};
        reapWreck = new DerelictUnitType("reap-wreck"){{outlines = false;constructor = UnitEntity::create;targetable = false;}};
        maimeWreck = new DerelictUnitType("maime-wreck");
        InfantryGerbCorpse = new DerelictUnitType("infantry-gerb-corpse"){{ outlines = false; constructor =  UnitEntity:: create;targetable = false;}};
    }
}
