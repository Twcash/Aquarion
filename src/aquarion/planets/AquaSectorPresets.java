package aquarion.planets;

import mindustry.type.SectorPreset;

import static aquarion.planets.AquaPlanets.tantros2;

public class AquaSectorPresets {
    public static SectorPreset
    Chasm, Valley;
    /*
    Sector plans
    chasm:10 {
    Valley: 142{
    Shallows: 82
    Brine pools: 86
    }
    vast Shallows: 214 {
    estuary:
    }
    Subduction zone: {
    }
    basalt crags: {
    }
    }
    }
     */
    public static void load(){
        Chasm = new SectorPreset("chasm", tantros2, 10){{
            alwaysUnlocked = true;
            addStartingItems = true;
            captureWave = 5;
            difficulty = 1;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 3f;
        }};
        Valley = new SectorPreset("valley", tantros2, 142){{
            addStartingItems = true;
            captureWave = 8;
            difficulty = 2;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 3f;
        }};
    }
}
