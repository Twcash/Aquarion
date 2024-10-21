package aquarion.units;

import aquarion.gen.Derelictc;
import aquarion.type.DerelictUnit;
import ent.anno.Annotations;
import mindustry.gen.Unitc;
import mindustry.type.UnitType;

public class AquaWrecks {
    public static @Annotations.EntityDef({Unitc.class, Derelictc.class}) UnitType zoarcidWreck, messengerWreck, gossWreck, stewardWreck,
    reapWreck, InfantryGerbCorpse, maimeWreck;
    public static void loadContent() {
        zoarcidWreck = new DerelictUnit("zoarcid-wreck");
        gossWreck = new DerelictUnit("goss-wreck");
        messengerWreck = new DerelictUnit("messenger-wreck");
        stewardWreck = new DerelictUnit("steward-wreck");
        reapWreck = new DerelictUnit("reap-wreck"){{ outlines = false;}};
        maimeWreck = new DerelictUnit("maime-wreck");
        InfantryGerbCorpse = new DerelictUnit("infantry-gerb-corpse"){{ outlines = false;}};
    }
}
