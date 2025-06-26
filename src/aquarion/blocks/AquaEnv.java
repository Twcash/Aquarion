package aquarion.blocks;

import aquarion.AquaAttributes;
import aquarion.AquaItems;
import aquarion.AquaLiquids;
import aquarion.world.blocks.environment.NonRandomTreeBlock;
import aquarion.world.blocks.environment.PineTree;
import aquarion.world.blocks.environment.TiledFloor;
import aquarion.world.blocks.environment.rokBlock;
import aquarion.world.graphics.AquaFx;
import aquarion.world.graphics.AquaShaders;
import arc.graphics.Color;
import arc.math.Interp;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.entities.effect.ParticleEffect;
import mindustry.graphics.CacheLayer;
import mindustry.graphics.Layer;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.blocks.environment.*;
import mindustry.world.meta.Attribute;

import static aquarion.AquaItems.*;
import static mindustry.content.Blocks.*;
import static mindustry.content.Items.*;
import static mindustry.content.Items.copper;
import static mindustry.type.ItemStack.with;
import static mindustry.world.meta.BuildVisibility.sandboxOnly;


public class AquaEnv {
    public static Block azurite, blueSandBoulder, brecciaBoulder, chertBoulder,
            arsenideBoulder, algalBoulder, feldsparBoulder, gabbroBoulder,
            arsenicBoulder, boricBoulder, ultrafamicBoulder;
    public static Block parzilSprig, kelp, rockweed, urchin,
            CrasseCoral, basaltRock, largeBasaltRock, hugeBasaltRock, massiveBasaltRock;
    public static Block leafLitter, iceWater, blueSandFLoor, blueSandWater, brecciaFloor, soil, fertileSoil,
            smoothBrecciaFloor, arsenideFloor, arsenideLayers, chertFloor,
            chertPlates, greenCoralFloor, BlueCoralFloor, redCoralFloor,
            andesiteLayers, basaltSpikes ,algal_carpet, brine_liquid,
            coral_floor, feldspar_vent, feldspar, ferric_extrusions,
            gabbro_extrusions, gabbro_vent, gabbro, geothermal_vent,
            kelp_floor, roughFeldspar, feldsparPebbles, feldsparRubble, smoothFeldspar, phylite_floor, slate, ultrafamicFloor, brimstoneFloor, brimstoneVent,
            boricFloor, boricFloorDense, tile, engravedTile, gildedTile, pottedGrass,
            shaleVent, andesite, andesiteRubble, andesiteVent, basaltPlates, ultrafamicPlates;
    public static Block oreNickelWall, oreTitaniumWall, oreArsenic, oreElectrum,
            oreNickel, leadNodules, oreBauxite, oreGallium, oreLithium,
            oreManganese, oreAluminum, oreSilicon, exposedGallium, cryoliteOre, acuminiteOre, ferricOre, serpentineOre;
    public static Block towaniteCluster, azuriteLarge, blueSandWall, brecciaWall, ultrafamicWall, exposedSerpentine,
            arsenicCrystals, arsenicalOutcrop, boricWall, arsenideWall, chertWall,
            chertOutcrop, pillarCoral, loteasCoral, songCoral,
            bauxiticWall ,algalBloom, parzilPine, algalWall,
            bloom, blueCoralWall, redCoralWall, greenCoralWall,
            feldsparWall, gabbroWall, andesiteExtrusions, CrystalGalena,
            elderParzil, boraxCluster;

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
            cacheLayer = AquaShaders.brineLayer;
            albedo = 1f;
            supportsOverlay = true;
        }};
        ultrafamicFloor = new Floor("ultrafamic-floor", 3){{
            wall = ultrafamicWall;

        }};
        ultrafamicPlates = new Floor("ultrafamic-plates", 6){{
            wall = ultrafamicWall;
            blendGroup = ultrafamicFloor;
        }};
        boricFloor = new Floor("boric-floor", 4){{
            wall = boricWall;
        }};
        boricFloorDense = new Floor("boric-floor-dense", 4){{
            wall = boricWall;
        }};
        coral_floor = new Floor("coral-floor", 4) {{
        }};
        ferric_extrusions = new Floor("ferric-extrusions", 2) {{
        }};
        feldsparRubble = new TiledFloor("feldspar-rubble"){{
            tilingVariants = 2;
            tilingSize = 4;
        }};
        feldsparPebbles = new OverlayFloor("feldspar-pebbles") {{
            variants = 4;
        }};
        roughFeldspar = new Floor("rough-feldspar", 4) {{
            wall = feldsparWall;
        }};
        feldspar = new Floor("feldspar-floor", 5) {{
            wall = feldsparWall;
        }};
        smoothFeldspar = new Floor("smooth-feldspar", 6) {{
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

        gabbro_extrusions = new Floor("gabbro-extrusions", 10) {{
        }};
        brimstoneFloor = new Floor("brimstone-floor", 3) {{
            itemDrop = brimstone;
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
        brimstoneVent = new SteamVent("brimstone-vent") {{
            attributes.set(Attribute.steam, 1f);
            parent = blendGroup = brimstoneFloor;
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
                colorFrom = Color.valueOf("f0ed85");
                colorTo = Color.valueOf("22221d10");
                sizeInterp = Interp.pow3Out;
                interp = Interp.pow3Out;
            }};
        }};
        exposedSerpentine = new StaticWall("exposed-serpentine") {{
            itemDrop = serpentine;
            variants = 3;
            playerUnmineable = true;
        }};

        gabbro_vent = new SteamVent("gabbro-vent") {{
            attributes.set(Attribute.steam, 1f);
            parent = blendGroup = AquaEnv.gabbro_extrusions;
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
            playerUnmineable = true;
        }};
        blueSandWater = new Floor("blue-sand-floor-water", 3) {{
            liquidDrop = Liquids.water;
            liquidMultiplier = 0.75f;
            speedMultiplier = 0.8f;
            cacheLayer = CacheLayer.water;
            shallow = true;
        }};
        iceWater = new Floor("ice-water", 0) {{
            liquidDrop = Liquids.water;
            liquidMultiplier = 0.75f;
            speedMultiplier = 0.7f;
            cacheLayer = CacheLayer.water;
            shallow = true;
        }};
        kelp_floor = new Floor("kelp-floor", 2) {{
            walkSound = mindustry.gen.Sounds.mud;
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
        basaltPlates = new TiledFloor("basalt-plates"){{
            tilingVariants = 2;
            tilingSize = 4;
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

        oreBauxite = new OreBlock("ore-bauxite", bauxite);
        oreGallium= new OreBlock("ore-gallium", AquaItems.gallium);
        oreNickel = new OreBlock("ore-nickel", nickel);
        oreLithium= new OreBlock("ore-lithium", AquaItems.lithium);
        oreManganese= new OreBlock("ore-manganese", AquaItems.manganese);
        acuminiteOre= new OreBlock("acuminite-ore", acuminite){{
            variants = 5;
        }};
        ferricOre= new OreBlock("ferric-ore", ferricMatter){{
            variants = 8;
        }};
        oreArsenic= new OreBlock("arsenic-ore", arsenic){{
            wallOre = true;
            variants = 3;
        }};
        serpentineOre= new OreBlock("ore-serpentine", serpentine){{
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
        ultrafamicWall = new StaticWall("ultrafamic-wall") {{
            variants = 3;
        }};

        algalBloom = new StaticWall("algal-bloom") {{
            variants = 1;
        }};

        feldsparWall = new StaticWall("feldspar-wall") {{
            variants = 3;
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
        boricWall = new StaticWall("boric-wall"){{
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
            layer = Layer.blockOver + 1;
            shadowLayer = Layer.blockOver;
        }};
        towaniteCluster = new TallBlock("towanite-cluster"){{
            variants = 3;
            itemDrop = towanite;
            clipSize = 128f;
            shadowAlpha = 0.5f;
            shadowOffset = -2.5f;
            layer = Layer.blockOver + 1;
            shadowLayer = Layer.blockOver;
        }};
        parzilPine = new PineTree("parzil-pine"){{
            shadowAlpha = 0.6f;
            buildVisibility = sandboxOnly;
            variants = 2;
            rotationRand = 30;
            size = 3;
            breakable = false;
            health = 2500;
            clipSize = 120;
            underBullets = true;
            targetable = false;
            destroyEffect = AquaFx.parzilDebrisSmall;
            createRubble = false;
            layer = Layer.power - 3;
            shadowLayer = Layer.blockOver;
            buildTime = 15*60f;
        }};
        elderParzil = new PineTree("elder-parzil"){{
            buildTime = 30*60f;
            buildVisibility = sandboxOnly;
            underBullets = false;
            destroyEffect = AquaFx.parzilDebrisLarge;
            createRubble = false;
            targetable = false;
            size = 5;
            variants = 1;
            health = 2500;
            breakable = false;
            rotationRand = 30;
            clipSize = 300;
            shadowLayer = Layer.power - 2;
            layer = Layer.power - 1;
            shadowAlpha = 0.8f;
            shadowOffset = -4;
        }};
        basaltRock = new rokBlock("basalt-rock"){{
            requirements(Category.effect, with( bauxite, 100, silicon, 150));
            buildVisibility = sandboxOnly;
            size = 1;
            health = 1200;
            clipSize = 120;
            underBullets = true;
            variants = 2;
            targetable = false;
            destroyEffect = Fx.breakProp;
            createRubble = false;
            buildTime = 20*60f;
            forceDark = true;
        }};
        largeBasaltRock = new rokBlock("large-basalt-rock"){{
            requirements(Category.effect, with( bauxite, 900, silicon, 1200));
            buildVisibility = sandboxOnly;
            size = 2;
            health = 3500;
            clipSize = 120;
            underBullets = true;
            variants = 2;
            targetable = false;
            destroyEffect = Fx.breakProp;
            createRubble = false;
            buildTime = 45*60f;
            forceDark = true;
        }};
        hugeBasaltRock = new rokBlock("huge-basalt-rock"){{
            requirements(Category.effect, with( bauxite, 2100, silicon, 3000));
            buildVisibility = sandboxOnly;
            size = 3;
            health = 8000;
            clipSize = 120;
            underBullets = true;
            variants = 2;
            targetable = false;
            destroyEffect = Fx.breakProp;
            createRubble = false;
            buildTime = 90*60f;
            forceDark = true;
        }};
        massiveBasaltRock = new rokBlock("massive-basalt-rock"){{
            requirements(Category.effect, with( bauxite, 4000, silicon, 6000));
            buildVisibility = sandboxOnly;
            size = 4;
            health = 12000;
            clipSize = 120;
            underBullets = true;
            targetable = false;
            destroyEffect = Fx.breakProp;
            createRubble = false;
            buildTime = 280*60f;
            forceDark = true;
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
        ultrafamicBoulder = new Prop("ultrafamic-boulder"){{
            variants = 2;
            ultrafamicFloor.asFloor().decoration = this;
            ultrafamicPlates.asFloor().decoration = this;
        }};
        gabbroBoulder = new Prop("gabbro-boulder"){{
            variants = 3;
            gabbro.asFloor().decoration = this;
            gabbro_extrusions.asFloor().decoration = this;
        }};
        feldsparBoulder = new Prop("feldspar-boulder"){{
            variants = 3;
            feldspar.asFloor().decoration = this;
        }};
        chertBoulder = new Prop("chert-boulder"){{
            variants = 3;
            chertFloor.asFloor().decoration = this;
        }};
        brecciaBoulder = new Prop("breccia-boulder"){{
            variants = 2;
            brecciaFloor.asFloor().decoration = this;
            smoothBrecciaFloor.asFloor().decoration = this;
        }};
        parzilSprig = new Prop("parzil-sprig"){{
            variants = 3;

        }};
        arsenideBoulder = new Prop("arsenide-boulder"){{
            variants = 3;
            arsenideFloor.asFloor().decoration = this;
        }};
        blueSandBoulder = new Prop("blue-sand-boulder"){{
            variants = 2;
        }};
        arsenicBoulder = new Prop("arsenic-boulder"){{
            variants = 3;
            arsenideFloor.asFloor().decoration = this;
        }};
        CrasseCoral = new SeaBush("crasse-coral"){{
            solid = true;
            lobesMax = 10;
            lobesMin = 7;
            breakable = alwaysReplace = false;
            //cacheLayer = CacheLayer.walls;
            destructible = false;
            timeRange = 150f;
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
        tile = new Floor("tile", 1){{
        }};
        engravedTile = new Floor("engraved-tile", 1){{
            blendGroup = tile;
        }};
        gildedTile = new Floor("gilded-tile", 1){{

        }};
        pottedGrass = new Floor("potted-grass", 2){{

        }};
        soil = new Floor("soil", 3){{
            attributes.set(AquaAttributes.fertility, 1f);
        }};
        fertileSoil = new Floor("fertile-soil", 3){{
            attributes.set(AquaAttributes.fertility, 1.5f);
        }};

    }
}
