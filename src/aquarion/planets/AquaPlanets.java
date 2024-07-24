package aquarion.planets;

import aquarion.AquaItems;
import arc.graphics.Color;
import mindustry.content.Items;
import mindustry.content.Planets;
import mindustry.game.Team;
import mindustry.graphics.g3d.HexMesh;
import mindustry.graphics.g3d.HexSkyMesh;
import mindustry.graphics.g3d.MultiMesh;
import mindustry.type.Planet;
import mindustry.world.meta.Env;

import static mindustry.content.Items.serpuloItems;

public class AquaPlanets {
    public static Planet
    tantros2;

    public static void loadContent() {
        tantros2 = new Planet("tantros", Planets.sun, 1.3f, 3){{
            generator = new AquaPlanetGenarator();
            meshLoader = () -> new HexMesh(this, 5);
            alwaysUnlocked = true;
            accessible = true;
            visible = true;
            atmosphereColor = Color.valueOf("3db899");
            iconColor = Color.valueOf("597be3");
            cloudMeshLoader = () -> new MultiMesh(
                    new HexSkyMesh(this, 3, 0.13f, 0.16f, 5, Color.valueOf("c4ebed").a(0.75f), 2, 0.18f, 1.2f, 0.38f),
                    new HexSkyMesh(this, 5, 0.14f, 0.17f, 5, Color.valueOf("edfeff").a(0.65f), 2, 0.12f, 1.5f, 0.32f)
            );
            startSector = 10;
            atmosphereRadIn = -0.01f;
            atmosphereRadOut = 0.3f;
            itemWhitelist.addAll(AquaItems.bauxite, AquaItems.sodium, AquaItems.manganese, Items.lead, Items.metaglass, AquaItems.lithium, AquaItems.nitride, AquaItems.duralumin, AquaItems.lithoniteAlloy);
            defaultEnv|= Env.terrestrial | Env.underwater;
            ruleSetter = r -> {
                r.waveTeam = Team.blue;
                r.placeRangeCheck = false;
                r.showSpawns = true;
                r.fog = true;
                r.staticFog = false;
                r.lighting = true;
                r.coreDestroyClear = true;
                r.onlyDepositCore = true;
            };
        }};
    }
}
