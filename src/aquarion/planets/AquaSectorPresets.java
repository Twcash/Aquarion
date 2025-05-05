package aquarion.planets;

import aquarion.world.AquaSectorPreset;
import mindustry.type.SectorPreset;

import static aquarion.planets.AquaPlanets.fakeSerpulo;
import static aquarion.planets.AquaPlanets.tantros2;

public class AquaSectorPresets {
    public static AquaSectorPreset  //tantros
    Ingress, Torrent, CrystalCaverns, Grove,
    //serpulo (fake)
    resurgence, twinPass, bay;
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
        resurgence = new AquaSectorPreset("resurgence", fakeSerpulo, 15){{
            alwaysUnlocked = true;
            addStartingItems = true;
            captureWave = 3;
            difficulty = 1;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 2f;
        }};
        twinPass = new AquaSectorPreset("twinPass", fakeSerpulo, 16){{
            addStartingItems = true;
            captureWave = 11;
            difficulty = 1;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 2f;
        }};
        bay = new AquaSectorPreset("Lagoon", fakeSerpulo, 17){{
            addStartingItems = true;
            captureWave = 21;
            difficulty = 1;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 2f;
        }};
        Ingress = new AquaSectorPreset("Ingress", tantros2, 10){{
            alwaysUnlocked = true;
            addStartingItems = true;
            captureWave = 5;
            difficulty = 1;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 2f;
        }};
        Torrent = new AquaSectorPreset("EnsuingTorrent", tantros2, 142){{
            addStartingItems = true;
            captureWave = 11;
            difficulty = 2;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 2f;
        }};
        CrystalCaverns = new AquaSectorPreset("CrystalCavern", tantros2, 144){{
            addStartingItems = true;
            captureWave = 7;
            difficulty = 4;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 3;
        }};
        Grove = new AquaSectorPreset("Grove", tantros2, 143){{
            addStartingItems = true;
            captureWave = 16;
            difficulty = 6;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 3;
        }};
    }
}
