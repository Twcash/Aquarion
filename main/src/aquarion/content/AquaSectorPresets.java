package aquarion.content;

import aquarion.world.map.AquaSectorPreset;

import static aquarion.content.AquaPlanets.fakeSerpulo;
import static aquarion.content.AquaPlanets.tantros2;

public class AquaSectorPresets {
    public static AquaSectorPreset  //tantros
    Ingress, diseasedCleft, Torrent, CrystalCaverns, Grove, Ecotone,
    //serpulo (fake)
    resurgence, twinPass, dryRiver, floodPlains, bay, lowlandStrait, mountainsideComplex, ripHold, erodedCanyon, searedWastes, fungalTropics;
    public static void load(){
        resurgence = new AquaSectorPreset("resurgence", fakeSerpulo, 361){{
            allDatabaseTabs = true;
            alwaysUnlocked = true;
            addStartingItems = true;
            captureWave = 16;
            difficulty = 1;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 2f;
        }};
        twinPass = new AquaSectorPreset("twinPass", fakeSerpulo, 16){{
            allDatabaseTabs = true;
            addStartingItems = true;
            captureWave = 25;
            alwaysUnlocked = false;
            difficulty = 2;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 3f;
        }};
        floodPlains = new AquaSectorPreset("floodPlains", fakeSerpulo, 358){{
            allDatabaseTabs = true;
            addStartingItems = true;
            captureWave = 17;
            difficulty = 4;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            alwaysUnlocked = false;
            startWaveTimeMultiplier = 4f;
        }};
        bay = new AquaSectorPreset("Lagoon", fakeSerpulo, 357){{
            allDatabaseTabs = true;
            addStartingItems = true;
            captureWave = 21;
            difficulty = 5;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 2f;
        }};
        erodedCanyon = new AquaSectorPreset("erodedCanyon", fakeSerpulo, 178){{
            allDatabaseTabs = true;
            addStartingItems = true;
            captureWave = 37;
            difficulty = 7;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 2f;
        }};
        lowlandStrait = new AquaSectorPreset("lowlandStrait", fakeSerpulo, 176){{
            allDatabaseTabs = true;
            addStartingItems = true;
            captureWave = 16;
            difficulty = 5;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 2f;
        }};
        mountainsideComplex = new AquaSectorPreset("mountainsideComplex", fakeSerpulo, 177){{
            allDatabaseTabs = true;
            addStartingItems = true;
            difficulty = 5;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 2f;
        }};
        Ingress = new AquaSectorPreset("Ingress", tantros2, 10){{
            allDatabaseTabs = true;
            addStartingItems = true;
            captureWave = 6;
            difficulty = 1;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 2f;
        }};
        Torrent = new AquaSectorPreset("EnsuingTorrent", tantros2, 142){{
            allDatabaseTabs = true;
            addStartingItems = true;
            captureWave = 11;
            difficulty = 2;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 2f;
        }};
        CrystalCaverns = new AquaSectorPreset("CrystalCavern", tantros2, 144){{
            allDatabaseTabs = true;
            addStartingItems = true;
            captureWave = 7;
            difficulty = 4;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 3;
        }};
        Grove = new AquaSectorPreset("Grove", tantros2, 143){{
            allDatabaseTabs = true;
            addStartingItems = true;
            captureWave = 17;
            difficulty = 6;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 3;

        }};
        ripHold = new AquaSectorPreset("riparian-hold", tantros2, 1444){{
            allDatabaseTabs = true;
            addStartingItems = true;
            captureWave = 18;
            difficulty = 6;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 3;
        }};
        Ecotone = new AquaSectorPreset("Ecotone", tantros2, 146){{
            allDatabaseTabs = true;
            addStartingItems = true;
            captureWave = 26;
            difficulty = 8;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 3;
        }};
        diseasedCleft = new AquaSectorPreset("diseasedCleft", fakeSerpulo, 456){{
            allDatabaseTabs = true;
            addStartingItems = true;
            difficulty = 8;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
        }};
        searedWastes = new AquaSectorPreset("seared-wastes", fakeSerpulo, 457){{
            allDatabaseTabs = true;
            addStartingItems = true;
            difficulty = 8;
            captureWave = 23;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
        }};
        fungalTropics = new AquaSectorPreset("fungal-tropics", fakeSerpulo, 359){{
            allDatabaseTabs = true;
            addStartingItems = true;
            difficulty = 10;
            captureWave = 10;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
        }};
        dryRiver = new AquaSectorPreset("dried-riverbed", fakeSerpulo, 360){{
            allDatabaseTabs = true;
            addStartingItems = true;
            difficulty = 12;
            captureWave = 85;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
        }};
    }
}
