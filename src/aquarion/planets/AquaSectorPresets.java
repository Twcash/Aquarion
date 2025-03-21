package aquarion.planets;

import mindustry.type.SectorPreset;

import static aquarion.planets.AquaPlanets.tantros2;

public class AquaSectorPresets {
    public static SectorPreset Ingress, Torrent, CrystalCaverns, Grove;
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
        Ingress = new SectorPreset("Ingress", tantros2, 10){{
            alwaysUnlocked = true;
            addStartingItems = true;
            captureWave = 5;
            difficulty = 1;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 2f;
        }};
        Torrent = new SectorPreset("EnsuingTorrent", tantros2, 142){{
            addStartingItems = true;
            captureWave = 11;
            difficulty = 2;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 2f;
        }};
        CrystalCaverns = new SectorPreset("CrystalCavern", tantros2, 144){{
            addStartingItems = true;
            captureWave = 7;
            difficulty = 4;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 3;
        }};
        Grove = new SectorPreset("Grove", tantros2, 143){{
            addStartingItems = true;
            captureWave = 16;
            difficulty = 6;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 3;
        }};
    }
}
