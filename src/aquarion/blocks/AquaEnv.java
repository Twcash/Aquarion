package aquarion.blocks;

import aquarion.AquaAttributes;
import aquarion.AquaItems;
import aquarion.AquaLiquids;
import aquarion.world.blocks.environment.NonRandomTreeBlock;
import aquarion.world.blocks.environment.PineTree;
import aquarion.world.graphics.AquaCacheLayers;
import arc.graphics.Color;
import arc.math.Interp;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.entities.effect.ParticleEffect;
import mindustry.graphics.CacheLayer;
import mindustry.graphics.Layer;
import mindustry.world.Block;
import mindustry.world.blocks.environment.*;
import mindustry.world.meta.Attribute;

import static aquarion.AquaItems.*;
import static mindustry.content.Blocks.*;
import static mindustry.content.Items.lead;
import static mindustry.content.Items.silicon;
import static mindustry.world.meta.BuildVisibility.sandboxOnly;


public class AquaEnv {

    // Boulders
    public static Block arsenicBoulder, parzilSprig, azurite, blueSandBoulder, brecciaBoulder, chertBoulder, arsenideBoulder, algalBoulder, feldsparBoulder, gabbroBoulder, kelp, rockweed, urchin, CrasseCoral;

    // Floors
    public static Block leafLitter, blueSandFLoor, blueSandWater, brecciaFloor, smoothBrecciaFloor, arsenideFloor, arsenideLayers, chertFloor, chertPlates, greenCoralFloor, BlueCoralFloor, redCoralFloor, andesiteLayers, basaltSpikes ,algal_carpet, brine_liquid, coral_floor, feldspar_vent, feldspar, ferric_extrusions, gabbro_extrusions, gabbro_vent, gabbro, geothermal_vent, kelp_floor, roughFeldspar, phylite_floor, slate, shaleVent, andesite, andesiteRubble, andesiteVent;

    // Ore blocks
    public static Block oreSilicon, oreAluminum, oreNickelWall, oreTitaniumWall, oreArsenic, oreElectrum, oreNickel, leadNodules, oreBauxite, oreGallium, oreLithium, oreManganese, exposedGallium;

    // Walls
    public static Block CrystalGalena, elderParzil, towaniteCluster, azuriteLarge, blueSandWall, brecciaWall, arsenicCrystals, arsenicalOutcrop, arsenideWall, chertWall, chertOutcrop, pillarCoral, loteasCoral, songCoral,  bauxiticWall ,algalBloom, parzilPine, algalWall, bloom, blueCoralWall, redCoralWall, greenCoralWall, feldsparWall, gabbroWall, andesiteExtrusions;


    public static void loadContent() {

        //TODO fix the blend group
        algal_carpet = new Floor("algal-carpet", 5) {{

        }};

        brine_liquid = new Floor("brine-liquid"){{
            speedMultiplier = 0.1f;
            variants = 0;
            liquidDrop = AquaLiquids.brine;
            liquidMultiplier = 1.1f;
            isLiquid = true;
            cacheLayer = AquaCacheLayers.brine;
            albedo = 1f;
            supportsOverlay = true;
        }};

        coral_floor = new Floor("coral-floor", 4) {{
        }};


        feldspar = new Floor("feldspar", 3) {{
            wall = feldsparWall;
        }};
        andesiteLayers = new Floor("andesite-layers", 4){{
            wall = daciteWall;
        }};

        feldspar_vent = new SteamVent("feldspar-vent") {{
            attributes.set(Attribute.steam, 1f);
            variants = 3;
            parent = blendGroup = AquaEnv.feldspar;
            effectSpacing = 15f;
            effect = new ParticleEffect(){{
                particles = 3;
                lifetime = 340;
                length = 125;
                cone = 20;
                baseRotation = 50;
                sizeFrom = 0f;
                sizeTo = 12f;
                colorFrom = Color.valueOf("18161c90");
                colorTo = Color.valueOf("2a282d10");
                sizeInterp = Interp.pow3Out;
                interp = Interp.pow3Out;
            }};
        }};
        andesiteRubble = new Floor("andesite-rubble-", 4) {{
            wall = daciteWall;
        }};
        andesite = new Floor("andesite-", 4) {{
            wall = daciteWall;
        }};

        ferric_extrusions = new Floor("ferric-extrusions", 2) {{
        }};
        redCoralFloor = new Floor("redCoral-floor"){{
            variants = 6;
        }};
        BlueCoralFloor = new Floor("blue-coral-floor"){{
            variants = 4;
        }};
        greenCoralFloor = new Floor("green-coral-floor"){{
            variants = 6;
        }};
        andesiteVent = new SteamVent("andesite-vent-") {{
            attributes.set(Attribute.steam, 1f);
            variants = 2;
            parent = blendGroup = AquaEnv.andesiteRubble;
            effectSpacing = 15f;
            effect = new ParticleEffect(){{
                particles = 3;
                lifetime = 340;
                length = 125;
                cone = 20;
                baseRotation = 50;
                sizeFrom = 0f;
                sizeTo = 12f;
                layer = 90;
                colorFrom = Color.valueOf("65666090");
                colorTo = Color.valueOf("a2a2a200");
                sizeInterp = Interp.pow3Out;
                interp = Interp.pow3Out;
            }};
        }};

        gabbro = new Floor("gabbro", 3) {{

        }};

        gabbro_extrusions = new Floor("gabbro-extrusions", 4) {{
        }};



        shaleVent = new SteamVent("shale-vent") {{
            attributes.set(Attribute.steam, 1f);
            parent = blendGroup = Blocks.shale;
            effectSpacing = 15f;
            variants = 3;
            effect = new ParticleEffect(){{
                particles = 3;
                lifetime = 340;
                length = 125;
                cone = 20;
                baseRotation = 50;
                sizeFrom = 0f;
                sizeTo = 12f;
                colorFrom = Color.valueOf("18161c90");
                colorTo = Color.valueOf("2a282d10");
                sizeInterp = Interp.pow3Out;
                interp = Interp.pow3Out;
            }};
        }};

        gabbro_vent = new SteamVent("gabbro-vent") {{
            attributes.set(Attribute.steam, 1f);
            parent = blendGroup = AquaEnv.gabbro;
            effectSpacing = 15f;
            variants = 3;
            effect = new ParticleEffect(){{
                particles = 3;
                lifetime = 340;
                length = 125;
                cone = 20;
                baseRotation = 50;
                sizeFrom = 0f;
                sizeTo = 12f;
                colorFrom = Color.valueOf("18161c90");
                colorTo = Color.valueOf("2a282d10");
                sizeInterp = Interp.pow3Out;
                interp = Interp.pow3Out;
            }};
        }};

        geothermal_vent = new SteamVent("geothermal-vent") {{
            attributes.set(Attribute.steam, 1f);
            parent = blendGroup = Blocks.basalt;
            effectSpacing = 15f;
            variants = 2;
            effect = new ParticleEffect(){{
                particles = 3;
                lifetime = 340;
                length = 125;
                cone = 20;
                baseRotation = 50;
                sizeFrom = 0f;
                sizeTo = 12f;
                colorFrom = Color.valueOf("18161c90");
                colorTo = Color.valueOf("2a282d10");
                sizeInterp = Interp.pow3Out;
                interp = Interp.pow3Out;
            }};
        }};
        blueSandFLoor = new Floor("blue-sand-floor", 3) {{
            itemDrop = Items.sand;
        }};
        blueSandWater = new Floor("blue-sand-floor-water", 3) {{
            liquidDrop = Liquids.water;
            liquidMultiplier = 0.75f;
            speedMultiplier = 0.8f;
            cacheLayer = CacheLayer.water;
            shallow = true;
        }};
        kelp_floor = new Floor("kelp-floor", 2) {{
            walkSound = mindustry.gen.Sounds.mud;
        }};

        roughFeldspar = new Floor("rough-feldspar", 4) {{
            wall = feldsparWall;
        }};

        phylite_floor = new Floor("phylite-floor", 2) {{
            wall = shaleWall;
        }};
        slate = new Floor("slate", 3) {{
            wall = shaleWall;
        }};
        basaltSpikes = new Floor("basalt-spikes", 4) {{
            wall = duneWall;
        }};
        andesiteExtrusions = new TallBlock("andesite-extrusions"){{
            variants = 2;
            clipSize = 128f;
            shadowAlpha = 0.5f;
            shadowOffset = -2.5f;
        }};
        arsenideFloor = new Floor("arsenide-floor", 4) {{
        }};
        arsenideLayers = new Floor("arsenide-layers", 4) {{
        }};
        chertFloor = new Floor("chert-floor", 4) {{
        }};
        chertPlates = new Floor("chert-plates", 4) {{
        }};
        brecciaFloor = new Floor("breccia-floor", 4) {{
        }};
        smoothBrecciaFloor = new Floor("smooth-breccia-floor", 4) {{
        }};
        leafLitter = new Floor("leaf-litter", 3) {{
        }};

        // Ore blocks
        leadNodules = new OreBlock("lead-nodules", lead);
        oreBauxite = new OreBlock("ore-bauxite", bauxite);
        oreGallium= new OreBlock("ore-gallium", AquaItems.gallium);
        oreNickel = new OreBlock("ore-nickel", nickel);
        oreLithium= new OreBlock("ore-lithium", AquaItems.lithium);
        oreManganese= new OreBlock("ore-manganese", AquaItems.manganese);
        oreArsenic= new OreBlock("arsenic-ore", arsenic){{
            wallOre = true;
            variants = 3;
        }};
        oreNickelWall= new OreBlock("ore-nickel-wall", nickel){{
            wallOre = true;
            variants = 2;
        }};
        oreTitaniumWall= new OreBlock("ore-titanium-wall", Items.titanium){{
            wallOre = true;
            variants = 3;
        }};
        oreElectrum= new OreBlock("electrum-ore", electrum){{
            variants = 3;
        }};
        oreAluminum= new OreBlock("ore-aluminum", aluminum){{
            variants = 3;
        }};
        oreSilicon= new OreBlock("ore-silicon", silicon){{
            variants = 3;
        }};

        // Walls & blocks
        bauxiticWall = new StaticWall("bauxitic-wall"){{
            variants = 3;
            itemDrop = bauxite;
            attributes.set(AquaAttributes.bauxite, 1f);
        }};
        exposedGallium = new StaticWall("exposed-gallium-wall"){{
            variants = 3;
            itemDrop = gallium;
            attributes.set(AquaAttributes.gallium, 1f);
        }};


        algalBloom = new StaticWall("algal-bloom") {{
            variants = 1;
        }};

        feldsparWall = new StaticWall("feldspar-wall") {{
            variants = 4;
        }};
        redCoralWall = new StaticWall("red-coral-wall") {{
            variants = 3;
        }};
        greenCoralWall = new StaticWall("green-coral-wall") {{
            variants = 3;
        }};
        blueCoralWall = new StaticWall("blue-coral-wall") {{
            variants = 3;
        }};

        gabbroWall = new StaticWall("gabbro-wall") {{
            variants = 4;
        }};
        arsenideWall = new StaticWall("arsenide-wall") {{
            variants = 3;
        }};
        chertWall = new StaticWall("chert-wall") {{
            variants = 2;
        }};
        chertOutcrop = new StaticTree("chert-outcrop") {{
            variants = 3;
        }};
        arsenicalOutcrop = new StaticTree("arsenical-outcrop") {{
            variants = 3;
            attributes.set(AquaAttributes.chromium, 1f);
        }};
        brecciaWall = new StaticWall("breccia-wall") {{
            variants = 2;
        }};
        blueSandWall = new StaticWall("blue-sand-wall") {{
            variants = 2;
        }};
        arsenicCrystals = new TallBlock("arsenic-crystals") {{
            variants = 3;
            itemDrop = arsenic;
            clipSize = 128f;
            shadowAlpha = 0.5f;
            shadowOffset = -2.5f;
        }};
        CrystalGalena = new TallBlock("galena-crystal") {{
            variants = 2;
            itemDrop = galena;
            clipSize = 128f;
            shadowAlpha = 0.6f;
            shadowOffset = -2.5f;
        }};
        azuriteLarge = new TallBlock("azurite-large"){{
            variants = 3;
            itemDrop = AquaItems.azurite;
            clipSize = 128f;
            shadowAlpha = 0.5f;
            shadowOffset = -2.5f;
            layer = Layer.blockOver;
        }};
        towaniteCluster = new TallBlock("towanite-cluster"){{
            variants = 3;
            itemDrop = towanite;
            clipSize = 128f;
            shadowAlpha = 0.5f;
            shadowOffset = -2.5f;
            layer = Layer.blockOver;
        }};
        parzilPine = new PineTree("parzil-pine"){{
            shadowAlpha = 0.6f;
            buildVisibility = sandboxOnly;
            variants = 2;
            rotationRand = 30;
            underBullets = true;
            size = 3;
            health = 2500;
            buildCost = 3600;
            clipSize = 120;
            layer = Layer.power - 5;
            shadowLayer = Layer.blockOver;
        }};
        elderParzil = new PineTree("elder-parzil"){{
            buildVisibility = sandboxOnly;
            underBullets = false;
            size = 5;
            variants = 1;
            health = 2500;
            buildCost = 9000;
            rotationRand = 30;
            clipSize = 300;
            shadowLayer = Layer.power - 4;
            layer = Layer.power - 1;
            shadowAlpha = 0.8f;
            shadowOffset = -4;
        }};
        bloom = new TreeBlock("bloom");

        algalWall = new TreeBlock("algal-wall") {{
            variants = 2;
            shadowOffset = -1;
        }};
        //TODO proper shadows that dont look bad
        songCoral = new TreeBlock("song-coral") {{
            variants = 3;
        }};
        loteasCoral = new TreeBlock("loteas-coral") {{
            variants = 3;
        }};
        pillarCoral = new NonRandomTreeBlock("pillar-coral") {{
            variants = 3;
        }};
        azurite = new Prop("azurite"){{
            variants = 3;
        }};
        //boulders
        algalBoulder = new Prop("algal-boulder"){{
            variants = 2;
        }};
        gabbroBoulder = new Prop("gabbro-boulder"){{
            variants = 3;
        }};
        feldsparBoulder = new Prop("feldspar-boulder"){{
            variants = 3;
        }};
        chertBoulder = new Prop("chert-boulder"){{
            variants = 3;
        }};
        brecciaBoulder = new Prop("breccia-boulder"){{
            variants = 2;
        }};
        parzilSprig = new Prop("parzil-sprig"){{
            variants = 3;
        }};
        arsenideBoulder = new Prop("arsenide-boulder"){{
            variants = 3;
        }};
        blueSandBoulder = new Prop("blue-sand-boulder"){{
            variants = 2;
        }};
        arsenicBoulder = new Prop("arsenic-boulder"){{
            variants = 3;
        }};
        CrasseCoral = new SeaBush("crasse-coral"){{
            solid = true;
            lobesMax = 10;
            lobesMin = 7;
            breakable = alwaysReplace = false;
            cacheLayer = CacheLayer.walls;
            destructible = false;
            timeRange = 150;
            botAngle = 80;
            magMax = 4;
            magMin = 3;
            sclMin = 60;
            clipSize = 100;
            sclMax = 90;
            layer = Layer.power + 0.1f;
            placeableLiquid = true;
        }};

        //plants and stuff
        kelp = new SeaBush("kelp"){{
        }};

        rockweed = new SeaBush("rockweed") {{
            lobesMin = 3;
            lobesMax = 5;
            timeRange = 160;
        }};

        urchin = new SeaBush("urchin") {{
            lobesMin = 3;
            lobesMax = 7;
            timeRange = 70;
            magMax = 30;
            sclMax = 55;
            sclMin = 10;
            magMin = 10;

        }};

    }
}
