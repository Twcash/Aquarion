package aquarion.content;

import aquarion.world.map.AquaSectorPreset;
import mindustry.type.SectorPreset;

import static aquarion.content.AquaPlanets.fakeSerpulo;
import static aquarion.content.AquaPlanets.tantros2;

public class AquaSectorPresets {
    public static SectorPreset  //tantros
    Ingress, diseasedCleft, brinePlateau, FeldsparRavine, Torrent, CrystalCaverns, Grove, Ecotone,
    //serpulo (fake)
    resurgence, SubmergedCanyon, GalenaFringe, twinPass, lib, dryRiver, blastedDockyards, coupledBasin, frigidShores, floodPlains, bay, lowlandStrait, mountainsideComplex, ripHold, erodedCanyon, searedWastes, fungalTropics, violetValley;
    public static void load(){
        resurgence = new SectorPreset("resurgence", fakeSerpulo, 399){{
            allDatabaseTabs = true;
            alwaysUnlocked = true;
            addStartingItems = true;
            captureWave = 12;
            difficulty = 1;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 2f;
        }};

        lib = new SectorPreset("lakesideLibrary", AquaPlanets.delubrum,1){{
        }};
        frigidShores = new SectorPreset("frigidShores", fakeSerpulo, 450){{
            allDatabaseTabs = true;
            addStartingItems = true;
            captureWave = 38;
            difficulty = 6;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 2f;
        }};
        blastedDockyards = new SectorPreset("blastedDockyard", fakeSerpulo, 355){{
            allDatabaseTabs = true;
            addStartingItems = true;
            captureWave = 33;
            difficulty = 12;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 2f;
        }};
        twinPass = new SectorPreset("twinPass", fakeSerpulo, 400){{
            allDatabaseTabs = true;
            addStartingItems = true;
            captureWave = 25;
            alwaysUnlocked = false;
            difficulty = 4;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 3f;
        }};
        floodPlains = new SectorPreset("floodPlains", fakeSerpulo, 510){{
            allDatabaseTabs = true;
            addStartingItems = true;
            captureWave = 17;
            difficulty = 4;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            alwaysUnlocked = false;
            startWaveTimeMultiplier = 4f;
        }};
        bay = new SectorPreset("Lagoon", fakeSerpulo, 220){{
            allDatabaseTabs = true;
            addStartingItems = true;
            captureWave = 21;
            difficulty = 5;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 2f;
        }};
        erodedCanyon = new SectorPreset("erodedCanyon", fakeSerpulo, 100){{
            allDatabaseTabs = true;
            addStartingItems = true;
            captureWave = 37;
            difficulty = 7;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 2f;
        }};
        lowlandStrait = new SectorPreset("lowlandStrait", fakeSerpulo, 221){{
            allDatabaseTabs = true;
            addStartingItems = true;
            captureWave = 16;
            difficulty = 5;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 2f;
        }};
        mountainsideComplex = new SectorPreset("mountainsideComplex", fakeSerpulo, 401){{
            allDatabaseTabs = true;
            addStartingItems = true;
            difficulty = 5;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 2f;
        }};
        diseasedCleft = new SectorPreset("diseased-cleft", fakeSerpulo, 778){{
            allDatabaseTabs = true;
            addStartingItems = true;
            difficulty = 8;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
        }};
        coupledBasin = new SectorPreset("coupled-basin", fakeSerpulo, 213){{
            allDatabaseTabs = true;
            addStartingItems = true;
            difficulty = 8;
            startWaveTimeMultiplier = 3.0f;
            captureWave = 11;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
        }};
        searedWastes = new SectorPreset("seared-wastes", fakeSerpulo, 502){{
            allDatabaseTabs = true;
            addStartingItems = true;
            difficulty = 8;
            captureWave = 23;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
        }};
        fungalTropics = new SectorPreset("fungal-tropics", fakeSerpulo, 86){{
            allDatabaseTabs = true;
            addStartingItems = true;
            difficulty = 10;
            captureWave = 10;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
        }};
        dryRiver = new SectorPreset("dried-riverbed", fakeSerpulo, 503){{
            allDatabaseTabs = true;
            addStartingItems = true;
            difficulty = 12;
            captureWave = 85;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
        }};
        violetValley = new SectorPreset("violet-valley", fakeSerpulo, 772){{
            allDatabaseTabs = true;
            addStartingItems = true;
            difficulty = 12;
            captureWave = 50;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
        }};
        Ingress = new SectorPreset("Ingress", tantros2, 10){{
            allDatabaseTabs = true;
            addStartingItems = true;
            captureWave = 6;
            difficulty = 1;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 2f;
        }};
        Torrent = new SectorPreset("EnsuingTorrent", tantros2, 142){{
            allDatabaseTabs = true;
            addStartingItems = true;
            captureWave = 11;
            difficulty = 2;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 2f;
        }};
        CrystalCaverns = new SectorPreset("CrystalCavern", tantros2, 144){{
            allDatabaseTabs = true;
            addStartingItems = true;
            captureWave = 7;
            difficulty = 4;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 3;
        }};
        brinePlateau = new SectorPreset("brine-plateau", tantros2, 194){{
            allDatabaseTabs = true;
            addStartingItems = true;
            captureWave = 27;
            difficulty = 5;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 3;
        }};
        Grove = new SectorPreset("Grove", tantros2, 143){{
            allDatabaseTabs = true;
            addStartingItems = true;
            captureWave = 17;
            difficulty = 6;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 3;

        }};
        ripHold = new SectorPreset("riparian-holt", tantros2, 1444){{
            allDatabaseTabs = true;
            addStartingItems = true;
            captureWave = 17;
            difficulty = 6;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 3;
        }};

        FeldsparRavine = new SectorPreset("FeldsparRavine", tantros2, 145){{
            allDatabaseTabs = true;
            addStartingItems = true;
            captureWave = 11;
            difficulty = 6;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 3;
        }};

        SubmergedCanyon = new SectorPreset("submerged-canyon", tantros2, 214){{
            allDatabaseTabs = true;
            addStartingItems = true;
            captureWave = 31;
            difficulty = 8;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 3;
        }};

        GalenaFringe = new SectorPreset( "galenafringe", tantros2, 82){{
            allDatabaseTabs = true;
            addStartingItems = true;
            captureWave = 14;
            difficulty = 8;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 5;
        }};

        Ecotone = new SectorPreset("Ecotone", tantros2, 146){{
            allDatabaseTabs = true;
            addStartingItems = true;
            captureWave = 26;
            difficulty = 8;
            showSectorLandInfo = false;
            overrideLaunchDefaults = true;
            startWaveTimeMultiplier = 3;
        }};
    }
}
