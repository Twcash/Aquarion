package aquarion.planets;

import mindustry.type.SectorPreset;

import static aquarion.planets.AquaPlanets.tantros2;

public class AquaSectorPresets {
    public static SectorPreset
    Chasm, Valley;
    public static void load(){
        Chasm = new SectorPreset("chasm", tantros2, 10){{
            alwaysUnlocked = true;
            addStartingItems = true;
            captureWave = 5;
            difficulty = 1;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 3f;
        }};
        Valley = new SectorPreset("Valley", tantros2, 11){{
            addStartingItems = true;
            captureWave = 8;
            difficulty = 2;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 3f;
        }};
    }
}
