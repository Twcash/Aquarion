package aquarion.planets;

import aquarion.AquaItems;
import aquarion.blocks.AquaCore;
import aquarion.world.AquaTeams;
import arc.graphics.Color;
import arc.math.geom.Vec3;
import mindustry.content.Items;
import mindustry.game.Team;
import mindustry.graphics.Pal;
import mindustry.graphics.g3d.HexMesh;
import mindustry.graphics.g3d.HexSkyMesh;
import mindustry.graphics.g3d.MultiMesh;
import mindustry.graphics.g3d.SunMesh;
import mindustry.maps.planet.SerpuloPlanetGenerator;
import mindustry.type.Planet;
import mindustry.world.meta.Env;

import static aquarion.AquaItems.*;
import static mindustry.content.Items.*;

public class AquaPlanets {
    public static Planet
    tantros2,
    qeraltar,
    citun,
    fakeSerpulo,
    coradum;

    public static void loadContent() {
        citun = new Planet("citun", null, 14f){{
            bloom = true;
            accessible = false;
            cloudMeshLoader = () -> new MultiMesh(
                    new HexSkyMesh(this, 3, 0.13f, 0.11f, 5, Color.valueOf("3b64ff").a(0.99f), 2, 0.18f, 1.2f, 0.3f),
                    new HexSkyMesh(this, 3, 0.14f, 0.12f, 5, Color.valueOf("3b64ff").a(0.99f), 2, 0.17f, 1.3f, .28f),
                    new HexSkyMesh(this, 3, 0.15f, 0.15f, 5, Color.valueOf("477ef2").a(0.95f), 2, 0.16f, 1.4f, 0.26f),
                    new HexSkyMesh(this, 3, 0.16f, 0.17f, 5, Color.valueOf("55a3ef").a(0.90f), 2, 0.15f, 1.5f, 0.24f),
                    new HexSkyMesh(this, 3, 0.18f, 0.22f, 5, Color.valueOf("86d1e9").a(0.85f), 2, 0.14f, 1.7f, 0.22f),
                    new HexSkyMesh(this, 3, 0.22f, 0.28f, 5, Color.valueOf("aee6f8").a(0.65f), 2, 0.12f, 1.9f, 0.20f),
                    new HexSkyMesh(this, 3, 0.26f, 0.36f, 5, Color.valueOf("c8f2ff").a(0.50f), 2, 0.10f, 2.3f, 0.17f),
                    new HexSkyMesh(this, 3, 0.32f, 0.43f, 5, Color.valueOf("ffffff").a(0.22f), 2, 0.08f, 2.6f, 0.12f)

                    );
            meshLoader = () -> new SunMesh(
                    this, 4,
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
        fakeSerpulo = new Planet("fakeSerp", citun, 1f, 4){{
            generator = new FakeSerpuloPlanetGenerator();
            meshLoader = () -> new HexMesh(this, 6);
            alwaysUnlocked = true;
            accessible = true;
            orbitRadius = 70;
            orbitOffset = 56;
            visible = true;
            iconColor = Color.valueOf("7d4dff");
            atmosphereColor = Color.valueOf("3c1b8f");
            cloudMeshLoader = () -> new MultiMesh(
                    new HexSkyMesh(this, 3, 0.13f, 0.11f, 5, Color.valueOf("c4ebed").a(0.75f), 2, 0.18f, 1.2f, 0.3f),
                    new HexSkyMesh(this, 5, 0.7f, 0.09f, 5, Color.valueOf("edfeff").a(0.65f), 3, 0.12f, 1.5f, 0.32f),
                    new HexSkyMesh(this, 8, 0.3f, 0.08f, 5, Color.valueOf("d3cad7").a(0.55f), 2, 0.08f, 1.6f, 0.35f)
            );
            atmosphereRadIn = 0.02f;
            atmosphereRadOut = 0.2f;
            startSector = 361;
            orbitSpacing = 6f;
            defaultCore = AquaCore.corePike;
            allowLaunchLoadout = false;
            clearSectorOnLose = true;
            allowLaunchToNumbered = false;
            //for future me
            //Vars.content.planet("aquarion-fakeSerp").allowLaunchToNumbered = true
            ruleSetter = r -> {
                r.fire = true;
                r.waveTeam = Team.crux;
                r.placeRangeCheck = false;
                r.showSpawns = true;
                r.coreDestroyClear = true;
                r.fog = false;
                r.staticFog = false;
                r.onlyDepositCore = true;
                r.deconstructRefundMultiplier = 1.01f;
            };
        }};
        qeraltar = new Planet("qeraltar", citun, 0.9f, 2){{
            generator = new QeralterPlanetGen();
            meshLoader = () -> new HexMesh(this, 2);
            accessible = false;
            bloom = false;
            alwaysUnlocked = false;
            startSector = 2;
            atmosphereRadOut = 0.19f;
            atmosphereColor = Color.valueOf("798d87");
            cloudMeshLoader = () -> new MultiMesh(
                    new HexSkyMesh(this, 2, 0.16f, 0.17f, 5, Color.valueOf("98a4d2").a(0.75f), 2, 0.42f, 1f, 0.43f),
                    new HexSkyMesh(this, 3, 0.7f, 0.18f, 5, Color.valueOf("dbe2e8").a(0.7f), 2, 0.42f, 1.2f, 0.45f)

            );
        }};
        tantros2 = new Planet("tantros", citun, 1.5f, 3){{
            generator = new AquaPlanetGenarator();
            meshLoader = () -> new HexMesh(this, 5);
            alwaysUnlocked = true;
            accessible = true;
            orbitRadius = 112;
            orbitOffset = 78;
            visible = true;
            atmosphereColor = Color.valueOf("3db899");
            iconColor = Color.valueOf("597be3");
            cloudMeshLoader = () -> new MultiMesh(
                    new HexSkyMesh(this, 3, 0.13f, 0.11f, 5, Color.valueOf("c4ebed").a(0.75f), 2, 0.18f, 1.2f, 0.3f),
                    new HexSkyMesh(this, 5, 0.7f, 0.09f, 5, Color.valueOf("edfeff").a(0.65f), 3, 0.12f, 1.5f, 0.32f),
                    new HexSkyMesh(this, 8, 0.3f, 0.08f, 5, Color.valueOf("d3cad7").a(0.55f), 2, 0.08f, 1.6f, 0.35f)
            );
            startSector = 10;
            atmosphereRadIn = 0.09f;
            atmosphereRadOut = 1.6f;
            orbitSpacing = 6f;
            defaultCore = AquaCore.corePike;
            allowLaunchLoadout = false;
            atmosphereRadOut = 1.5f;
            clearSectorOnLose = true;
            allowLaunchToNumbered = false;

            defaultEnv = Env.terrestrial | Env.underwater & ~(Env.groundOil | Env.scorching | Env.spores);
            ruleSetter = r -> {
                r.fire = false;
                r.waveTeam = AquaTeams.tendere;
                r.placeRangeCheck = false;
                r.showSpawns = true;
                r.coreDestroyClear = true;
                r.fog = false;
                r.staticFog = false;
                r.onlyDepositCore = true;
                r.deconstructRefundMultiplier = 1.01f;
            };
        }};
        coradum = new Planet("coradum", citun, 0.85f, 2){{
            generator = new CoradumPlanetGenerator();
            meshLoader = () -> new HexMesh(this, 6);
            accessible = false;
            tidalLock = true;
            bloom = true;
            alwaysUnlocked = false;
            startSector = 2;
            orbitRadius = 20;
            orbitOffset = 34;
            atmosphereRadOut = 0.19f;
            atmosphereColor = Color.valueOf("ff4545");
            cloudMeshLoader = () -> new MultiMesh(
                    new HexSkyMesh(this, 2, 0.7f, 0.17f, 5, Pal.metalGrayDark, 2, 0.42f, 1f, 0.43f),
                    new HexSkyMesh(this, 3, 1.5f, 0.18f, 5,Pal.stoneGray, 2, 0.42f, 1.2f, 0.45f)

            );
        }};
    }
}
