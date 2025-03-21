package aquarion.units;

import aquarion.gen.DerelictUnit;
import aquarion.gen.Derelictc;
import aquarion.type.DerelictUnitType;
import ent.anno.Annotations;
import mindustry.gen.Unitc;

//Worthless. Another memory to how much you fail. FUCK YOU
public class AquaWrecks {
    public static @Annotations.EntityDef({Unitc.class, Derelictc.class}) DerelictUnitType zoarcidWreck, anguilliWreck, messengerWreck, ambassadorWreck, consulWreck, gossWreck, stewardWreck,
    reapWreck, InfantryGerbCorpse, maimeWreck;
    public static void loadContent() {
        zoarcidWreck = new DerelictUnitType("zoarcid-wreck"){{ constructor = DerelictUnit:: create;targetable = false;}};
        anguilliWreck = new DerelictUnitType("anguilli-wreck"){{ constructor = DerelictUnit:: create; outlines = false; targetable = false;}};
        ambassadorWreck = new DerelictUnitType("ambassador-wreck"){{ constructor = DerelictUnit:: create; outlines = false; targetable = false;}};
        consulWreck = new DerelictUnitType("consul-wreck"){{ constructor = DerelictUnit:: create; outlines = false; targetable = false;}};
        gossWreck = new DerelictUnitType("goss-wreck"){{ constructor = DerelictUnit:: create;targetable = false;}};
        messengerWreck = new DerelictUnitType("messenger-wreck"){{ constructor = DerelictUnit:: create;targetable = false;}};
        stewardWreck = new DerelictUnitType("steward-wreck"){{ constructor = DerelictUnit:: create;targetable = false;}};
        reapWreck = new DerelictUnitType("reap-wreck"){{outlines = false;constructor = DerelictUnit:: create;targetable = false;}};
        maimeWreck = new DerelictUnitType("maime-wreck");
        InfantryGerbCorpse = new DerelictUnitType("infantry-gerb-corpse"){{ outlines = false; constructor =  DerelictUnit:: create;targetable = false;}};
    }
}
