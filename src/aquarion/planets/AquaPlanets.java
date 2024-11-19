package aquarion.planets;

import aquarion.AquaItems;
import aquarion.blocks.AquaCore;
import aquarion.world.AquaTeams;
import arc.func.Boolf;
import arc.graphics.Color;
import arc.struct.Seq;
import mindustry.content.Items;
import mindustry.content.Planets;
import mindustry.game.Team;
import mindustry.graphics.g3d.HexMesh;
import mindustry.graphics.g3d.HexSkyMesh;
import mindustry.graphics.g3d.MultiMesh;
import mindustry.graphics.g3d.SunMesh;
import mindustry.type.Item;
import mindustry.type.Planet;
import mindustry.world.meta.Env;

import static aquarion.AquaItems.*;
import static mindustry.content.Items.lead;

public class AquaPlanets {
    public static Planet
    tantros2,
    qeraltar,
    citun;

    public static void loadContent() {
        citun = new Planet("citun", null, 5.5f){{
            bloom = true;
            accessible = false;

            meshLoader = () -> new SunMesh(
                    this, 5,
                    5, 0.3, 1.7, 1.2, 1,
                    1.1f,
                    Color.valueOf("dcf7ff"),
                    Color.valueOf("bee7f3"),
                    Color.valueOf("dcf7ff"),
                    Color.valueOf("a3e1f3"),
                    Color.valueOf("9ed4e4"),
                    Color.valueOf("d2e5ea")
            );
        }};
        qeraltar = new Planet("qeraltar", citun, 0.9f, 2){{
            generator = new QeraltarPlanetGenerator();
            meshLoader = () -> new HexMesh(this, 5);
            accessible = true;
            bloom = false;
            alwaysUnlocked = true;
            startSector = 2;
            atmosphereRadOut = 0.19f;
            atmosphereColor = Color.valueOf("798d87");
            cloudMeshLoader = () -> new MultiMesh(
                    new HexSkyMesh(this, 2, 0.16f, 0.17f, 5, Color.valueOf("98a4d2").a(0.75f), 2, 0.42f, 1f, 0.43f),
                    new HexSkyMesh(this, 3, 0.7f, 0.18f, 5, Color.valueOf("dbe2e8").a(0.7f), 2, 0.42f, 1.2f, 0.45f)
            );
        }};
        tantros2 = new Planet("tantros", citun, 1.3f, 3){{
            generator = new AquaPlanetGenarator();
            meshLoader = () -> new HexMesh(this, 5);
            alwaysUnlocked = true;
            accessible = true;
            orbitRadius = 55;
            orbitOffset = 90;
            visible = true;
            atmosphereColor = Color.valueOf("3db899");
            iconColor = Color.valueOf("597be3");
            cloudMeshLoader = () -> new MultiMesh(
                    new HexSkyMesh(this, 3, 0.13f, 0.11f, 5, Color.valueOf("c4ebed").a(0.75f), 2, 0.18f, 1.2f, 0.3f),
                    new HexSkyMesh(this, 5, 0.7f, 0.09f, 5, Color.valueOf("edfeff").a(0.65f), 3, 0.12f, 1.5f, 0.32f),
                    new HexSkyMesh(this, 8, 0.3f, 0.08f, 5, Color.valueOf("d3cad7").a(0.55f), 2, 0.08f, 1.6f, 0.35f)
            );
            startSector = 10;
            atmosphereRadIn = -0.03f;
            orbitSpacing = 6f;
            defaultCore = AquaCore.corePike;
            allowLaunchLoadout = false;
            atmosphereRadOut = 0.6f;
            clearSectorOnLose = true;
            allowLaunchToNumbered = false;
            itemWhitelist.addAll(AquaItems.bauxite, AquaItems.sodium, AquaItems.manganese, lead, Items.metaglass, AquaItems.lithium, AquaItems.nitride, duralumin, AquaItems.lithoniteAlloy);
            defaultEnv|= Env.terrestrial | Env.underwater;
            ruleSetter = r -> {
                r.waveTeam = AquaTeams.tendere;
                r.placeRangeCheck = false;
                r.showSpawns = true;
                r.coreDestroyClear = true;
                r.fog = false;
                r.staticFog = false;
                r.onlyDepositCore = true;
            };
            hiddenItems.addAll(Items.erekirItems).addAll(
                    Items.copper, Items.sand, Items.metaglass, Items.surgeAlloy, Items.coal
                    , Items.phaseFabric, Items.graphite, Items.plastanium, Items.silicon, Items.scrap
                    , Items.pyratite, Items.blastCompound, Items.sporePod, Items.thorium);

            itemWhitelist.addAll(ceramic, bauxite, salt, lead, sodium, manganese, lithium, gallium,
                                 chirenium, nickel, duralumin, nitride, duralumin);
        }};
    }
}
