package aquarion.planets;

import aquarion.world.AquaSectorPreset;
import mindustry.type.SectorPreset;

import static aquarion.planets.AquaPlanets.fakeSerpulo;
import static aquarion.planets.AquaPlanets.tantros2;

public class AquaSectorPresets {
    public static AquaSectorPreset  //tantros
    Ingress, Torrent, CrystalCaverns, Grove, Ecotone,
    //serpulo (fake)
    resurgence, twinPass, floodPlains, bay, lowlandStrait, mountainsideComplex, erodedCanyon;
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
        resurgence = new AquaSectorPreset("resurgence", fakeSerpulo, 361){{
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
            alwaysUnlocked = false;
            difficulty = 2;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 2f;
        }};
        floodPlains = new AquaSectorPreset("floodPlains", fakeSerpulo, 358){{
            addStartingItems = true;
            captureWave = 17;
            difficulty = 4;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            alwaysUnlocked = false;
            startWaveTimeMultiplier = 4f;
        }};
        bay = new AquaSectorPreset("Lagoon", fakeSerpulo, 357){{
            addStartingItems = true;
            captureWave = 21;
            difficulty = 5;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 2f;
        }};
        erodedCanyon = new AquaSectorPreset("erodedCanyon", fakeSerpulo, 178){{
            addStartingItems = true;
            captureWave = 37;
            difficulty = 7;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 2f;
        }};
        lowlandStrait = new AquaSectorPreset("lowlandStrait", fakeSerpulo, 176){{
            addStartingItems = true;
            captureWave = 15;
            difficulty = 5;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 2f;
        }};
        mountainsideComplex = new AquaSectorPreset("mountainsideComplex", fakeSerpulo, 177){{
            addStartingItems = true;
            difficulty = 5;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 2f;
        }};
        Ingress = new AquaSectorPreset("Ingress", tantros2, 10){{
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
            captureWave = 17;
            difficulty = 6;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 3;
        }};
        Ecotone = new AquaSectorPreset("Ecotone", tantros2, 146){{
            addStartingItems = true;
            captureWave = 26;
            difficulty = 8;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 3;
        }};
    }
}
