package aquarion.content;

import aquarion.content.blocks.CoreBlocks;
import aquarion.planets.AquaPlanetGenerator;
import aquarion.planets.CoradumPlanetGenerator;
import aquarion.planets.FakeSerpuloPlanetGenerator;
import aquarion.planets.QeralterPlanetGen;
import arc.func.Cons;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Mat3D;
import arc.math.geom.Vec3;
import arc.struct.Seq;
import arc.util.Tmp;
import mindustry.content.Blocks;
import mindustry.content.Planets;
import mindustry.game.Team;
import mindustry.graphics.Pal;
import mindustry.graphics.g3d.*;
import mindustry.maps.planet.AsteroidGenerator;
import mindustry.maps.planet.ErekirPlanetGenerator;
import mindustry.type.Planet;
import mindustry.type.Sector;
import mindustry.type.Weather;
import mindustry.world.Block;
import mindustry.world.meta.Attribute;
import mindustry.world.meta.Env;

public class AquaPlanets {
    public static Planet
    tantros2,
    qeraltar,
    citun,
    fakeSerpulo,
    fakeErekir,
    ring1,
    delubrum,
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
//        ring1 = makeAsteroid("ring", fakeSerpulo, Blocks.ferricStoneWall, Blocks.carbonWall, -5, 0.4f, 9, 1f, gen -> {
//            gen.min = 25;
//            gen.max = 35;
//            gen.carbonChance = 0.6f;
//            gen.iceChance = 0f;
//            gen.berylChance = 0.1f;
//        });

        fakeErekir = new Planet("fakeErekir", citun, 1.9f, 4){{
            generator = new ErekirPlanetGenerator();
            meshLoader = () -> new HexMesh(this, 6);
            alwaysUnlocked = false;
            orbitOffset = 85;
            orbitRadius = 60;
            atmosphereRadIn = 0.02f;
            atmosphereRadOut = 0.3f;
            defaultEnv = Env.scorching | Env.terrestrial;
            lightSrcTo = 0.5f;
            lightDstFrom = 0.2f;
            updateLighting = false;
            defaultAttributes.set(Attribute.heat, 0.8f);//Heat engine unbalance.
            atmosphereColor = Color.valueOf("a7787d");
            visible = true;
            allowLaunchLoadout = false;
            clearSectorOnLose = true;
            allowLaunchToNumbered = false;
            cloudMeshLoader = () -> new MultiMesh(
                    new HexSkyMesh(this, 2, 0.15f, 0.14f, 5, Color.valueOf("eba768").a(0.75f), 2, 0.42f, 1f, 0.43f),
                    new HexSkyMesh(this, 3, 0.6f, 0.15f, 5, Color.valueOf("eea293").a(0.75f), 2, 0.42f, 1.2f, 0.45f)
            );
            ruleSetter = r -> {
                r.fire = false;
                r.waveTeam = Team.malis;
                r.placeRangeCheck = false;
                r.showSpawns = true;
                r.coreDestroyClear = true;
                r.fog = false;
                r.staticFog = false;
                r.onlyDepositCore = true;
                r.deconstructRefundMultiplier = 1.01f;
            };
            campaignRuleDefaults.fog = false;
            campaignRuleDefaults.showSpawns = true;
            campaignRuleDefaults.rtsAI = false;
            allowCampaignRules = true;
            orbitSpacing = 6f;
            defaultCore = CoreBlocks.corePike;
            atmosphereRadIn = 0.02f;
            atmosphereRadOut = 0.2f;
        }};
        fakeSerpulo = new Planet("fakeSerp", citun, 3f, 4){{
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
            prebuildBase = false;
            orbitSpacing = 6f;
            defaultCore = CoreBlocks.corePike;
            allowLaunchLoadout = true;
            clearSectorOnLose = true;
            allowLaunchToNumbered = false;
            //for future me
            //Vars.content.planet("aquarion-fakeSerp").allowLaunchToNumbered = true
            ruleSetter = r -> {
                r.fire = true;
                r.waveTeam = Team.crux;
                r.placeRangeCheck = false;
                r.showSpawns = true;
                r.coreDestroyClear = false;
                r.fog = false;
                r.staticFog = false;
                r.onlyDepositCore = true;
                r.deconstructRefundMultiplier = 1.01f;
            };
            campaignRuleDefaults.fog = false;
            campaignRuleDefaults.showSpawns = true;
            campaignRuleDefaults.rtsAI = false;
            allowCampaignRules = true;
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
        delubrum = new Planet("delubrum", fakeSerpulo, 1f, 1){{
            startSector = 1;
            generator = new AquaPlanetGenerator();
            meshLoader = () -> new HexMesh(this, 3);
            orbitRadius = 12;
            orbitOffset = 52;
            visible = true;
            accessible = true;
            atmosphereRadIn = 0.09f;
            alwaysUnlocked = true;
            defaultCore = CoreBlocks.reception;
            ruleSetter = r -> {
                r.infiniteResources = true;
                r.buildSpeedMultiplier = 0;
                r.deconstructRefundMultiplier = 0;
                r.possessionAllowed = false;
                r.derelictRepair = false;
            };
            atmosphereRadOut = 1.1f;
            atmosphereColor = Color.valueOf("3db899");
            defaultCore = CoreBlocks.corePike;
        }};
        tantros2 = new Planet("tantros", citun, 1.5f, 3){{
            generator = new AquaPlanetGenerator();
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
            orbitSpacing = 6f;
            defaultCore = CoreBlocks.corePike;
            allowLaunchLoadout = false;
            atmosphereRadOut = 1.5f;
            lightColor = Color.valueOf("0c0924");

            clearSectorOnLose = true;
            allowLaunchToNumbered = false;
            allowCampaignRules = true;
            landCloudColor = Pal.spore.cpy().a(0);
            defaultEnv = Env.terrestrial | Env.underwater & ~(Env.groundOil | Env.scorching | Env.spores);
            ruleSetter = r -> {
                r.weather.addAll(new Weather.WeatherEntry(AquaWeathers.currents), new Weather.WeatherEntry(AquaWeathers.sedimentDisturance), new Weather.WeatherEntry(AquaWeathers.bioluminescentBlooms));
                r.fire = false;
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
            atmosphereColor = Color.valueOf("101e2c");
            cloudMeshLoader = () -> new MultiMesh(
                    new HexSkyMesh(this, 2, 0.7f, 0.17f, 5, Pal.metalGrayDark, 2, 0.42f, 1f, 0.43f),
                    new HexSkyMesh(this, 3, 1.5f, 0.18f, 5,Pal.stoneGray, 2, 0.42f, 1.2f, 0.45f)

            );
        }};

    }
    private static Planet makeAsteroidRing(String name, Planet parent, Block base, Block tint, int seed, float tintThresh, int pieces, float scale, int amount, Cons<AsteroidGenerator> cgen){
        return new Planet(name, parent, 0.12f){{
            hasAtmosphere = false;
            updateLighting = false;
            camRadius = 0.68f * scale;
            minZoom = 0.6f;
            drawOrbit = false;
            accessible = false;
            clipRadius = 2f;
            defaultEnv = Env.space;
            icon = "commandRally";
            generator = new AsteroidGenerator();
            cgen.get((AsteroidGenerator)generator);

            meshLoader = () -> {
                iconColor = tint.mapColor;
                Color tinted = tint.mapColor.cpy().a(1f - tint.mapColor.a);
                Seq<GenericMesh> meshes = new Seq<>();
                Color color = base.mapColor;
                Rand rand = new Rand(id + 2);
                for(int i = 0; i < amount; i++) {
                    meshes.add(new NoiseMesh(
                            this, seed, 2, radius, 2, 0.55f, 0.45f, 14f,
                            color, tinted, 3, 0.6f, 0.38f, tintThresh
                    ));

                    for (int j = 0; j < pieces; j++) {
                        sectors.add(new Sector(this, new PlanetGrid.Ptile(i * j * seed, 0)));
                        rand.setSeed((long) pieces * i * amount * j + seed);
                        meshes.add(new MatMesh(
                                new NoiseMesh(this, seed + j + 1, 1, 0.022f + rand.random(0.039f) * scale, 2, 0.6f, 0.38f, 20f,
                                        color, tinted, 3, 0.6f, 0.38f, tintThresh),
                                new Mat3D().setToTranslation(Tmp.v31.setToRandomDirection(rand).setLength(rand.random(0.44f, 1.4f) * scale)))
                        );
                    }
                }
                return new MultiMesh(meshes.toArray(GenericMesh.class));
            };
        }};
    }
    private static Planet makeAsteroid(String name, Planet parent, Block base, Block tint, int seed, float tintThresh, int pieces, float scale, Cons<AsteroidGenerator> cgen){
        return new Planet(name, parent, 0.12f){{
            hasAtmosphere = false;
            updateLighting = false;
            sectors.add(new Sector(this, PlanetGrid.Ptile.empty));
            camRadius = 0.68f * scale;
            minZoom = 0.6f;
            orbitRadius = 40;
            drawOrbit = false;
            accessible = false;
            clipRadius = 2f;
            defaultEnv = Env.space;
            icon = "commandRally";
            generator = new AsteroidGenerator();
            cgen.get((AsteroidGenerator)generator);

            meshLoader = () -> {
                iconColor = tint.mapColor;
                Color tinted = tint.mapColor.cpy().a(1f - tint.mapColor.a);
                Seq<GenericMesh> meshes = new Seq<>();
                Color color = base.mapColor;
                Rand rand = new Rand(id + 2);

                meshes.add(new NoiseMesh(
                        this, seed, 2, radius, 2, 0.55f, 0.45f, 14f,
                        color, tinted, 3, 0.6f, 0.38f, tintThresh
                ));

                for(int j = 0; j < pieces; j++){
                    meshes.add(new MatMesh(
                            new NoiseMesh(this, seed + j + 1, 1, 0.022f + rand.random(0.039f) * scale, 2, 0.6f, 0.38f, 20f,
                                    color, tinted, 3, 0.6f, 0.38f, tintThresh),
                            new Mat3D().setToTranslation(Tmp.v31.setToRandomDirection(rand).setLength(rand.random(0.44f, 1.4f) * scale)))
                    );
                }

                return new MultiMesh(meshes.toArray(GenericMesh.class));
            };
        }};
    }
}
