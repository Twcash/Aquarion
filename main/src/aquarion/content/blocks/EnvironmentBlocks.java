package aquarion.content.blocks;

import aquarion.content.AquaAttributes;
import aquarion.content.AquaItems;
import aquarion.content.AquaLiquids;
import aquarion.content.AquaStatuses;
import aquarion.world.blocks.environment.*;
import aquarion.world.graphics.AquaFx;
import aquarion.world.graphics.AquaShaders;
import arc.func.Cons;
import arc.graphics.Color;
import mindustry.content.*;
import mindustry.ctype.UnlockableContent;
import mindustry.game.Team;
import mindustry.gen.Sounds;
import mindustry.graphics.CacheLayer;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.blocks.environment.*;
import mindustry.world.meta.Attribute;

import static aquarion.content.AquaAttributes.*;
import static aquarion.content.AquaItems.salt;
import static aquarion.content.AquaItems.*;
import static mindustry.content.Blocks.*;
import static mindustry.content.Items.silicon;
import static mindustry.type.ItemStack.with;
import static mindustry.world.meta.BuildVisibility.sandboxOnly;


public class EnvironmentBlocks {
    public static <T extends UnlockableContent> void overwrite(UnlockableContent target, Cons<T> setter) {
        setter.get((T) target);
    }
    public static Block okelBush, crasindtree, crasindWall, denseStone, stonePores, clay, metalWall1, metalWalltwo, sparseSnow, packedSnow, floorLight, brokenFloorLight, metalPlates1,metalVent, metalBankFloor, damagedPlates1, damagedPlates2, damagedPlates3, damagedPlated4, plates1, metalPlates, plates2,plates3,plates4, metalGrating, azurite, blueSandBoulder, brecciaBoulder, chertBoulder,
            arsenideBoulder, algalBoulder, feldsparBoulder, gabbroBoulder,
            arsenicBoulder, floor1, boricBoulder, ultrafamicBoulder;
    public static Block parzilSprig, kelp, rockweed, urchin,
            CrasseCoral,stoneRock,largeStoneRock,hugeStoneRock,massiveStoneRock, basaltRock, largeBasaltRock, hugeBasaltRock, massiveBasaltRock;
    public static Block leafLitter, leafLitterDense, iceWater, blueSandFLoor, blueSandWater, brecciaFloor, soil, fertileSoil,
            smoothBrecciaFloor, arsenideFloor, arsenideLayers, chertFloor,
            chertPlates, greenCoralFloor, BlueCoralFloor, redCoralFloor,
            andesiteLayers, basaltSpikes, algal_carpet, brine_liquid,
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
            bauxiticWall, algalBloom, parzilPine, algalWall,
            bloom, blueCoralWall, redCoralWall, greenCoralWall,
            feldsparWall, gabbroWall, andesiteExtrusions, CrystalGalena,
            elderParzil, boraxCluster, ksaRoot, yulrCoral, bewCoral, herylBush, tranticaBush, regoubloom, tyrqPod, bigTyrqPod, basaltBluff, basaltOutcrop;

    public static void loadContent() {
        //TODO fix the blend group
        algal_carpet = new Floor("algal-carpet", 5) {{

        }};
        brine_liquid = new Floor("brine-liquid") {{
            speedMultiplier = 0.1f;
            variants = 0;
            liquidDrop = AquaLiquids.brine;
            liquidMultiplier = 1.1f;
            isLiquid = true;
            drownTime = 6000;
            cacheLayer = AquaShaders.brineLayer;
            albedo = 1f;
            supportsOverlay = true;
        }};
        ultrafamicFloor = new Floor("ultrafamic-floor", 3) {{
            wall = ultrafamicWall;
        }};
        ultrafamicPlates = new Floor("ultrafamic-plates", 6) {{
            wall = ultrafamicWall;
            blendGroup = ultrafamicFloor;
        }};
        boricFloor = new Floor("boric-floor", 4) {{
            wall = boricWall;
        }};
        boricFloorDense = new Floor("boric-floor-dense", 4) {{
            wall = boricWall;
        }};
        coral_floor = new Floor("coral-floor", 4) {{
        }};
        denseStone = new Floor("dense-stone", 8);
        stonePores = new Floor("stone-pores", 6);
        clay = new Floor("clay", 4);


        ferricStone.attributes.set(iron, 0.8f);
        ferric_extrusions = new Floor("ferric-extrusions", 2) {{
            attributes.set(iron, 1.5f);
        }};
        feldsparRubble = new TiledFloor("feldspar-rubble") {{
            tilingVariants = 2;
            tilingSize = 4;
            attributes.set(iron, 2f);
        }};
        feldsparPebbles = new OverlayFloor("feldspar-pebbles") {{
            variants = 4;
        }};
        roughFeldspar = new Floor("rough-feldspar", 4) {{
            wall = feldsparWall;
            attributes.set(iron, 0.75f);
        }};
        feldspar = new Floor("feldspar-floor", 5) {{
            wall = feldsparWall;
            attributes.set(iron, 1f);

        }};
        smoothFeldspar = new Floor("smooth-feldspar", 6) {{
            wall = feldsparWall;
            attributes.set(iron, 1.3f);

        }};
        Blocks.salt.itemDrop = salt;

        andesiteLayers = new Floor("andesite-layers", 4) {{
            wall = daciteWall;
        }};
        overwrite(arkyicVent, (SteamVent s) -> s.effect = AquaFx.vent1);
        overwrite(basaltVent, (SteamVent s) -> s.effect = AquaFx.vent1);
        overwrite(carbonVent, (SteamVent s) -> s.effect = AquaFx.vent1);
        overwrite(stoneVent, (SteamVent s) -> s.effect = AquaFx.vent1);
        overwrite(rhyoliteVent, (SteamVent s) -> s.effect = AquaFx.vent1);
        overwrite(crystallineVent, (SteamVent s) -> s.effect = AquaFx.vent1);
        overwrite(yellowStoneVent, (SteamVent s) -> s.effect = AquaFx.vent1);
        overwrite(redStoneVent, (SteamVent s) -> s.effect = AquaFx.vent1);

        feldspar_vent = new SteamVent("feldspar-vent") {{
            attributes.set(Attribute.steam, 1f);
            variants = 3;
            parent = blendGroup = EnvironmentBlocks.feldspar;
            effectSpacing = 15f;
            effect = AquaFx.vent1;
        }};
        andesiteRubble = new Floor("andesite-rubble-", 4) {{
            wall = daciteWall;
        }};
        andesite = new Floor("andesite-", 4) {{
            wall = daciteWall;
        }};


        redCoralFloor = new Floor("redCoral-floor") {{
            variants = 6;
        }};
        BlueCoralFloor = new Floor("blue-coral-floor") {{
            variants = 4;
        }};
        greenCoralFloor = new Floor("green-coral-floor") {{
            variants = 6;
        }};
        andesiteVent = new SteamVent("andesite-vent-") {{
            attributes.set(Attribute.steam, 1f);
            variants = 2;
            parent = blendGroup = EnvironmentBlocks.andesiteRubble;
            effectSpacing = 15f;
            effect = AquaFx.vent1;
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
            effect = AquaFx.vent1;
        }};
        brimstoneVent = new SteamVent("brimstone-vent") {{
            attributes.set(Attribute.steam, 1f);
            parent = blendGroup = brimstoneFloor;
            effectSpacing = 15f;
            variants = 2;
            effect = AquaFx.vent1;
        }};
        exposedSerpentine = new StaticWall("exposed-serpentine") {{
            itemDrop = serpentine;
            variants = 3;
            playerUnmineable = true;
        }};

        gabbro_vent = new SteamVent("gabbro-vent") {{
            attributes.set(Attribute.steam, 1f);
            parent = blendGroup = EnvironmentBlocks.gabbro_extrusions;
            effectSpacing = 15f;
            variants = 3;
            effect = AquaFx.vent1;
        }};
        sporeMoss.itemDrop = Items.sporePod;
        sporeWall.itemDrop = Items.sporePod;
        sporePine.itemDrop = Items.sporePod;
        moss.itemDrop = Items.sporePod;


        geothermal_vent = new SteamVent("geothermal-vent") {
            {
                attributes.set(Attribute.steam, 1f);
                parent = blendGroup = Blocks.basalt;
                effectSpacing = 15f;
                variants = 2;
                effect = AquaFx.vent1;
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
            isLiquid = true;
            shallow = true;
        }};
        packedSnow = new Floor("packed-snow", 4){{
            speedMultiplier = 0.85f;
            status = AquaStatuses.cold;
            statusDuration = 20;
        }};
        sparseSnow = new OverlayFloor("sparse-snow"){{
            variants = 3;
        }};
        kelp_floor = new Floor("kelp-floor", 2) {{
            walkSound = mindustry.gen.Sounds.mud;
        }};


        phylite_floor = new Floor("phylite-floor", 2) {{
            wall = shaleWall;
            attributes.set(metamorphic, 1f);

        }};
        slate = new Floor("slate", 3) {{
            wall = shaleWall;
            attributes.set(metamorphic, 1f);
        }};
        basaltSpikes = new Floor("basalt-spikes", 4) {{
            wall = duneWall;
            attributes.set(metamorphic, 0.7f);
        }};
        basaltPlates = new TiledFloor("basalt-plates") {{
            tilingVariants = 2;
            tilingSize = 4;
            attributes.set(metamorphic, 0.7f);
        }};
        andesiteExtrusions = new TallBlock("andesite-extrusions") {{
            variants = 2;
            clipSize = 128f;
            shadowAlpha = 0.5f;
            shadowOffset = -2.5f;
            attributes.set(metamorphic, 0.2f);
        }};
        arsenideFloor = new Floor("arsenide-floor", 4) {{
        }};
        arsenideLayers = new Floor("arsenide-layers", 4) {{
        }};
        chertFloor = new Floor("chert-floor", 4) {{
            attributes.set(metamorphic, 0.2f);
        }};
        chertPlates = new Floor("chert-plates", 4) {{
            attributes.set(metamorphic, 0.2f);
        }};
        brecciaFloor = new Floor("breccia-floor", 4) {{
            attributes.set(iron, 0.25f);
            attributes.set(metamorphic, 0.1f);
        }};
        sporeMoss.attributes.set(fertility, 0.75f);
        basalt.attributes.set(metamorphic, 0.5f);
        darksand.attributes.set(metamorphic, 0.5f);
        smoothBrecciaFloor = new Floor("smooth-breccia-floor", 4) {{
            attributes.set(iron, 0.5f);
            attributes.set(metamorphic, 0.2f);
        }};
        leafLitter = new Floor("leaf-litter", 3) {{
        }};
        leafLitterDense = new Floor("leaf-litter-dense", 3) {{
        }};
        oreBauxite = new OreBlock("ore-bauxite", AquaItems.bauxite);
        oreGallium = new OreBlock("ore-gallium", AquaItems.gallium);
        oreNickel = new OreBlock("ore-nickel", nickel);
        oreLithium = new OreBlock("ore-lithium", AquaItems.lithium);
        oreManganese = new OreBlock("ore-manganese", AquaItems.manganese);
        acuminiteOre = new OreBlock("acuminite-ore", acuminite) {{
            variants = 5;
        }};
        ferricOre = new OreBlock("ferric-ore", ferricMatter) {{
            variants = 8;
        }};
        oreArsenic = new OreBlock("arsenic-ore", arsenic) {{
            wallOre = true;
            variants = 3;
        }};
        serpentineOre = new OreBlock("ore-serpentine", serpentine) {{
            variants = 3;
        }};
        oreNickelWall = new OreBlock("ore-nickel-wall", nickel) {{
            wallOre = true;
            variants = 2;
        }};
        oreTitaniumWall = new OreBlock("ore-titanium-wall", Items.titanium) {{
            wallOre = true;
            variants = 3;
        }};
        oreElectrum = new OreBlock("electrum-ore", electrum) {{
            variants = 3;
        }};
        oreAluminum = new OreBlock("ore-aluminum", aluminum) {{
            variants = 3;
        }};
        oreSilicon = new OreBlock("ore-silicon", silicon) {{
            variants = 3;
        }};

        ultrafamicWall = new StaticWall("ultrafamic-wall") {{
            variants = 3;
        }};

        algalBloom = new StaticWall("algal-bloom") {{
            variants = 1;
        }};

        feldsparWall = new StaticWall("feldspar-wall") {{
            variants = 3;
            attributes.set(iron, 1);
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
        boricWall = new StaticWall("boric-wall") {{
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
        azuriteLarge = new TallBlock("azurite-large") {{
            variants = 3;
            itemDrop = AquaItems.azurite;
            clipSize = 128f;
            shadowAlpha = 0.5f;
            shadowOffset = -2.5f;
            layer = Layer.blockOver + 1;
            shadowLayer = Layer.blockOver;
        }};
        towaniteCluster = new TallBlock("towanite-cluster") {{
            variants = 3;
            itemDrop = towanite;
            clipSize = 128f;
            shadowAlpha = 0.5f;
            shadowOffset = -2.5f;
            layer = Layer.blockOver + 1;
            shadowLayer = Layer.blockOver;
        }};
        basaltBluff = new TallBlock("basalt-bluff") {{
            variants = 0;
            clipSize = 200f;
            shadowAlpha = 0.7f;
            layer = Layer.blockOver + 2;
            shadowLayer = Layer.blockOver;
        }};
        basaltOutcrop = new StaticTree("basalt-outcrop"){{
           variants = 2;
        }};
        okelBush = new WobbleProp("okel-bush"){{
            variants = 2;
            destroySound = Sounds.plantBreak;
        }};
        crasindWall = new StaticTree("crasind-wall"){{
            variants = 0;
        }};
        crasindtree = new FloraBlock("crasind-tree"){{
            shadowAlpha = 0.6f;
            buildVisibility = sandboxOnly;
            variants = 2;
            drawTeamOverlay = false;
            rotationRand = 30;
            size = 2;
            breakable = false;
            health = 2500;
            updateEffectChance = 0.01f;
            clipSize = 120;
            underBullets = true;
            targetable = false;
            createRubble = false;
            layer = Layer.power - 3;
            shadowLayer = Layer.blockOver;
            buildTime = 15 * 60f;
        }};
        tyrqPod = new FloraBlock("tyrq-pod") {{
            shadowAlpha = 0.7f;
            buildVisibility = sandboxOnly;
            variants = 2;
            rotationRand = 50;
            size = 2;
            breakable = false;
            health = 6000;
            buildTime = 30*60f;
            baseExplosiveness = 5;
            emitLight = true;
            lightColor = Color.valueOf("dcf270");
            lightRadius = 75;
            explosionShake = 2;
            explodeEffect =AquaFx.tyrqExplode;
            clipSize = 120;
            underBullets = true;
            targetable = false;
            explosionRadius = 6*8/2;
            destroyEffect = AquaFx.parzilDebrisSmall;
            createRubble = false;
            layer = Layer.power - 5;
            shadowLayer = Layer.blockOver;
        }};
        bigTyrqPod = new FloraBlock("big-tyrq-pod") {{
            shadowAlpha = 0.7f;
            buildVisibility = sandboxOnly;
            variants = 2;
            buildTime = 30*60f;
            rotationRand = 50;
            size = 3;
            breakable = false;
            health = 9000;
            baseExplosiveness = 12;
            explosionDamage = 2500;
            explosionRadius = 10*8/2;
            emitLight = true;
            lightColor = Color.valueOf("dcf270");
            lightRadius = 110;
            explosionShake = 5;
            explodeEffect =AquaFx.tyrqExplode;
            clipSize = 120;
            underBullets = true;
            targetable = false;
            destroyEffect = AquaFx.parzilDebrisSmall;
            createRubble = false;
            layer = Layer.power - 3;
            shadowLayer = Layer.power - 4;
        }};
        parzilPine = new FloraBlock("parzil-pine") {{
            shadowAlpha = 0.6f;
            buildVisibility = sandboxOnly;
            variants = 2;
            rotationRand = 30;
            effect = AquaFx.parzilLeaf;
            size = 3;
            breakable = false;
            health = 2500;
            updateEffectChance = 0.01f;
            clipSize = 120;
            underBullets = true;
            targetable = false;
            destroyEffect = AquaFx.parzilDebrisSmall;
            createRubble = false;
            layer = Layer.power - 3;
            shadowLayer = Layer.blockOver;
            buildTime = 15 * 60f;
        }};
        elderParzil = new FloraBlock("elder-parzil") {{
            buildTime = 30 * 60f;
            buildVisibility = sandboxOnly;
            underBullets = false;
            effect = AquaFx.parzilLeaf;
            destroyEffect = AquaFx.parzilDebrisLarge;
            createRubble = false;
            targetable = false;
            size = 5;
            updateEffectChance = 0.02f;
            variants = 1;
            health = 7500;
            breakable = false;
            rotationRand = 30;
            clipSize = 300;
            shadowLayer = Layer.power - 2;
            layer = Layer.power - 1;
            shadowAlpha = 0.8f;
            shadowOffset = -4;
        }};
        basaltRock = new rokBlock("basalt-rock") {{
            requirements(Category.effect, with(zinc, 100, silicon, 150));
            buildVisibility = sandboxOnly;
            size = 1;
            health = 1200;
            clipSize = 120;
            underBullets = true;
            variants = 2;
            targetable = false;
            layer = Layer.groundUnit + 1;
            destroyEffect = Fx.breakProp;
            createRubble = false;
            buildTime = 20 * 60f;
            forceDark = true;
        }};
        largeBasaltRock = new rokBlock("large-basalt-rock") {{
            requirements(Category.effect, with(zinc, 900, silicon, 1200));
            buildVisibility = sandboxOnly;
            size = 2;
            health = 3500;
            layer = Layer.groundUnit + 2;
            clipSize = 120;
            underBullets = true;
            variants = 2;
            targetable = false;
            destroyEffect = Fx.breakProp;
            createRubble = false;
            buildTime = 45 * 60f;
            forceDark = true;
        }};
        hugeBasaltRock = new rokBlock("huge-basalt-rock") {{
            requirements(Category.effect, with(zinc, 2100, silicon, 3000));
            buildVisibility = sandboxOnly;
            size = 3;
            health = 8000;
            layer = Layer.groundUnit + 3;
            clipSize = 120;
            underBullets = true;
            variants = 2;
            targetable = false;
            destroyEffect = Fx.breakProp;
            createRubble = false;
            buildTime = 90 * 60f;
            forceDark = true;
        }};
        massiveBasaltRock = new rokBlock("massive-basalt-rock") {{
            requirements(Category.effect, with(zinc, 4000, silicon, 6000));
            buildVisibility = sandboxOnly;
            size = 4;
            health = 12000;
            layer = Layer.groundUnit + 4;
            clipSize = 120;
            underBullets = true;
            targetable = false;
            destroyEffect = Fx.breakProp;
            createRubble = false;
            buildTime = 280 * 60f;
            forceDark = true;
        }};
        stoneRock = new rokBlock("stone-rock") {{
            requirements(Category.effect, with( silicon, 150, nickel, 50));
            buildVisibility = sandboxOnly;
            size = 1;
            health = 1200;
            clipSize = 120;
            underBullets = true;
            targetable = false;
            layer = Layer.groundUnit + 1;
            destroyEffect = Fx.breakProp;
            createRubble = false;
            buildTime = 20 * 60f;
            forceDark = true;
        }};
        largeStoneRock = new rokBlock("large-stone-rock") {{
            requirements(Category.effect, with( silicon, 1200, nickel, 500));
            buildVisibility = sandboxOnly;
            size = 2;
            health = 3500;
            layer = Layer.groundUnit + 2;
            clipSize = 120;
            underBullets = true;
            targetable = false;
            destroyEffect = Fx.breakProp;
            createRubble = false;
            buildTime = 45 * 60f;
            forceDark = true;
        }};
        hugeStoneRock = new rokBlock("huge-stone-rock") {{
            requirements(Category.effect, with( silicon, 3000, nickel, 1100));
            buildVisibility = sandboxOnly;
            size = 3;
            health = 8000;
            layer = Layer.groundUnit + 3;
            clipSize = 120;
            underBullets = true;
            targetable = false;
            destroyEffect = Fx.breakProp;
            createRubble = false;
            buildTime = 90 * 60f;
            forceDark = true;
        }};
        massiveStoneRock = new rokBlock("massive-stone-rock") {{
            requirements(Category.effect, with( silicon, 6000, nickel, 2500));
            buildVisibility = sandboxOnly;
            size = 4;
            health = 12000;
            layer = Layer.groundUnit + 4;
            clipSize = 120;
            underBullets = true;
            targetable = false;
            destroyEffect = Fx.breakProp;
            createRubble = false;
            buildTime = 280 * 60f;
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
        azurite = new Prop("azurite") {{
            variants = 3;
        }};
        //boulders
        algalBoulder = new Prop("algal-boulder") {{
            variants = 2;
        }};
        ultrafamicBoulder = new Prop("ultrafamic-boulder") {{
            variants = 2;
            ultrafamicFloor.asFloor().decoration = this;
            ultrafamicPlates.asFloor().decoration = this;
        }};
        gabbroBoulder = new Prop("gabbro-boulder") {{
            variants = 3;
            gabbro.asFloor().decoration = this;
            gabbro_extrusions.asFloor().decoration = this;
        }};
        feldsparBoulder = new Prop("feldspar-boulder") {{
            variants = 3;
            feldspar.asFloor().decoration = this;
        }};
        chertBoulder = new Prop("chert-boulder") {{
            variants = 3;
            chertFloor.asFloor().decoration = this;
        }};
        brecciaBoulder = new Prop("breccia-boulder") {{
            variants = 2;
            brecciaFloor.asFloor().decoration = this;
            smoothBrecciaFloor.asFloor().decoration = this;
        }};
        parzilSprig = new Prop("parzil-sprig") {{
            variants = 3;

        }};
        arsenideBoulder = new Prop("arsenide-boulder") {{
            variants = 3;
            arsenideFloor.asFloor().decoration = this;
        }};
        blueSandBoulder = new Prop("blue-sand-boulder") {{
            variants = 2;
        }};
        arsenicBoulder = new Prop("arsenic-boulder") {{
            variants = 3;
            arsenideFloor.asFloor().decoration = this;
        }};
        yulrCoral = new TallBlock("yulr-coral"){{
            variants = 2;
        }};
        bewCoral = new TallBlock("bew-coral"){{
            variants = 2;
        }};
        tranticaBush = new TallBlock("trantica-bush"){{
            variants = 2;
            lightColor = Color.valueOf("f1563c45");
            lightRadius = 40;
            emitLight = true;
        }};
        herylBush = new WobbleProp("heryl-bush"){{
            buildTime = 120;
            variants = 2;
            instantDeconstruct = false;
        }};
        ksaRoot = new TallBlock("ksa-root"){{
            variants = 2;
        }};
        regoubloom = new TallBlock("regou-bloom"){{
            variants = 3;
            lightColor = Color.valueOf("f2f2c1");
            lightRadius = 35;
            emitLight = true;
        }};
        CrasseCoral = new SeaBush("crasse-coral") {{
            solid = true;
            lobesMax = 10;
            lobesMin = 7;
            breakable = alwaysReplace = false;
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
        kelp = new SeaBush("kelp") {{
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
        tile = new Floor("tile", 1) {{
        }};
        engravedTile = new Floor("engraved-tile", 1) {{
            blendGroup = tile;
        }};
        gildedTile = new Floor("gilded-tile", 1) {{

        }};
        pottedGrass = new Floor("potted-grass", 2) {{

        }};
        soil = new Floor("soil", 3) {{
            attributes.set(AquaAttributes.fertility, 1f);
        }};
        fertileSoil = new Floor("fertile-soil", 3) {{
            attributes.set(AquaAttributes.fertility, 1.5f);
        }};
        plates1 = new Floor("plates"){{
            autotile = true;
            drawEdgeIn = drawEdgeOut = false;
            emitLight = true;
            lightRadius = 30f;
            lightColor = Team.crux.color.cpy().a(0.1f);
        }};
        plates2 = new Floor("metal-plates2"){{
            autotile = true;
            drawEdgeIn = drawEdgeOut = false;
        }};
        plates3 = new Floor("metal-plates3"){{
            autotile = true;
            variants = 0;
            drawEdgeIn = drawEdgeOut = false;
        }};
        plates4 = new Floor("plates4"){{
            autotile = true;
            drawEdgeIn = drawEdgeOut = false;
        }};
        damagedPlates1 = new Floor("damaged-metal1"){{
            autotile = true;
            drawEdgeIn = drawEdgeOut = false;
            autotileVariants = 3;
            autotileMidVariants = 6;
        }};
//        metalGrating = new Floor("metal-pit") {{
//            autotile = true;
//            drawEdgeIn = drawEdgeOut = false;
//            cacheLayer = AquaShaders.beamPitLayer;
//        }};
        metalPlates = new GreedyFloor("metal-plates", 3, 4){{
            drawEdgeOut = false;
            drawEdgeIn = false;
            clipSize = 600;

        }};
        metalPlates1 = new GreedyFloor("metal-plates1", 2, 8){{
            drawEdgeOut = false;
            drawEdgeIn = false;
            clipSize = 3000;

        }};
        floorLight = new Floor("floor-light", 0){{
            lightRadius = 70;
            emitLight = true;
            drawEdgeIn = drawEdgeOut = false;
            lightColor = Pal.techBlue.cpy().a(0.7f);
        }};
        brokenFloorLight = new Floor("broken-floor-light", 4){{
            lightRadius = 5;
            emitLight = true;
            drawEdgeIn = drawEdgeOut = false;
            lightColor = Color.gray.cpy().a(0.05f);
        }};

        metalBankFloor = new Floor("metal-bank-floor"){{
            autotile = true;
            drawEdgeIn = drawEdgeOut = false;
            emitLight = true;
            lightRadius = 20f;
            lightColor = Pal.reactorPurple.cpy().a(0.1f);
        }};
        metalVent = new GreedyFloor("metal-vent", 0, 3){{
            drawEdgeOut = false;
            drawEdgeIn = false;
            effect = AquaFx.vent1;
            attributes.set(Attribute.steam, 1f);
        }};
        metalWall1 = new StaticWall("metal-wall"){{
            variants = 6;
        }};
        metalWalltwo = new StaticWall("metal-wall1"){{
            variants = 4;
        }};
    }
}
