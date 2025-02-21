package aquarion.units;

import aquarion.gen.DerelictUnit;
import aquarion.gen.Derelictc;
import aquarion.type.DerelictUnitType;
import ent.anno.Annotations;
import mindustry.gen.Unitc;

//Worthless. Another memory to how much you fail. FUCK YOU
public class AquaWrecks {
    public static @Annotations.EntityDef({Unitc.class, Derelictc.class}) DerelictUnitType zoarcidWreck, messengerWreck, gossWreck, stewardWreck,
    reapWreck, InfantryGerbCorpse, maimeWreck;
    public static void loadContent() {
        zoarcidWreck = new DerelictUnitType("zoarcid-wreck"){{ constructor = DerelictUnit:: create;}};
        gossWreck = new DerelictUnitType("goss-wreck"){{ constructor = DerelictUnit:: create;}};
        messengerWreck = new DerelictUnitType("messenger-wreck"){{ constructor = DerelictUnit:: create;}};
        stewardWreck = new DerelictUnitType("steward-wreck"){{ constructor = DerelictUnit:: create;}};
        reapWreck = new DerelictUnitType("reap-wreck"){{outlines = false;constructor = DerelictUnit:: create;}};
        maimeWreck = new DerelictUnitType("maime-wreck");
        InfantryGerbCorpse = new DerelictUnitType("infantry-gerb-corpse"){{ outlines = false; constructor =  DerelictUnit:: create;}};
    }
}
