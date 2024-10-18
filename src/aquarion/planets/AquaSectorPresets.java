package aquarion.planets;

import mindustry.type.SectorPreset;

import static aquarion.planets.AquaPlanets.tantros2;

public class AquaSectorPresets {
    public static SectorPreset
    Chasm;
    public static void load(){
        Chasm = new SectorPreset("chasm", tantros2, 10){{
            alwaysUnlocked = true;
            addStartingItems = true;
            captureWave = 5;
            difficulty = 1;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 3f;
        }};
    }
}
