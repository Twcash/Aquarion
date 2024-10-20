package aquarion.units;

import aquarion.type.DerelictUnit;

public class AquaWrecks {
    public static DerelictUnit zoarcidWreck, messengerWreck, gossWreck, stewardWreck,
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
