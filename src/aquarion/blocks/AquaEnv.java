package aquarion.blocks;

import aquarion.AquaAttributes;
import aquarion.AquaItems;
import aquarion.AquaLiquids;
import aquarion.world.blocks.environment.PineTree;
import arc.graphics.Color;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.entities.effect.ParticleEffect;
import mindustry.graphics.CacheLayer;
import mindustry.type.Liquid;
import mindustry.world.Block;
import mindustry.world.blocks.environment.*;
import mindustry.world.meta.Attribute;
import aquarion.AquaItems.*;
import aquarion.AquaLiquids.*;

import static aquarion.AquaItems.bauxite;
import static mindustry.content.Blocks.*;
import static mindustry.content.Items.lead;
import static mindustry.world.meta.BuildVisibility.sandboxOnly;


public class AquaEnv {

    // Boulders
    public static Block algalBoulder, feldsparBoulder, gabbroBoulder, kelp, rockweed, urchin;

    // Floors
    public static Block andesiteLayers, basaltSpikes ,algal_carpet, brine_liquid, coral_floor, feldspar_vent, feldspar, ferric_extrusions, gabbro_extrusions, gabbro_vent, gabbro, geothermal_vent, kelp_floor, roughFeldspar, phylite_floor, slate, shaleVent, andesite, andesiteRubble, andesiteVent;

    // Ore blocks
    public static Block leadNodules, oreBauxite, oreGallium, oreLithium, oreManganese;

    // Walls
    public static Block bauxiticWall ,algalBloom, parzilPine, algalWall, bloom, coralWall, feldsparWall, gabbroWall, andesiteExtrusions;

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
            // status = StatusEffects.wet;
            statusDuration = 120f;
            drownTime = 200f;
            cacheLayer = CacheLayer.water;
            albedo = 1f;
            supportsOverlay = true;
        }};

        coral_floor = new Floor("coral-floor", 4) {{
            decoration = yellowCoral;

        }};


        feldspar = new Floor("feldspar", 3) {{
            wall = feldsparWall;
        }};
        andesiteLayers = new Floor("andesite-layers", 4){{
            wall = daciteWall;
        }};

        feldspar_vent = new SteamVent("feldspar-vent") {{
            attributes.set(Attribute.steam, 1f);
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
                sizeInterp = interp.pow3Out;
                interp = interp.pow3Out;
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
        andesiteVent = new SteamVent("andesite-vent-") {{
            attributes.set(Attribute.steam, 1f);
            variants = 2;
            parent = blendGroup = AquaEnv.andesiteRubble;
            effectSpacing = 15f;
            effect = new ParticleEffect(){{
                particles = 5;
                lifetime = 340;
                length = 125;
                cone = 20;
                baseRotation = 50;
                sizeFrom = 0f;
                sizeTo = 12f;
                //particles have to be darker due to andesite bleding in with the particles
                colorFrom = Color.valueOf("121214120");
                colorTo = Color.valueOf("12121410");
                sizeInterp = interp.pow3Out;
                interp = interp.pow3Out;
            }};
        }};



        gabbro_extrusions = new Floor("gabbro-extrusions", 2) {{

        }};

        gabbro = new Floor("gabbro", 3) {{
            decoration = gabbroBoulder;

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
                sizeInterp = interp.pow3Out;
                interp = interp.pow3Out;
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
                sizeInterp = interp.pow3Out;
                interp = interp.pow3Out;
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
                sizeInterp = interp.pow3Out;
                interp = interp.pow3Out;
            }};
        }};

        kelp_floor = new Floor("kelp-floor", 2) {{
            walkSound = mindustry.gen.Sounds.mud;
        }};

        roughFeldspar = new Floor(" rough-feldspar", 4) {{
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

        // Ore blocks
        leadNodules = new OreBlock("lead-nodules", lead);
        oreBauxite = new OreBlock("ore-bauxite", bauxite);
        oreGallium= new OreBlock("ore-gallium", AquaItems.gallium);
        oreLithium= new OreBlock("ore-lithium", AquaItems.lithium);
        oreManganese= new OreBlock("ore-manganese", AquaItems.manganese);

        // Walls & blocks
        bauxiticWall = new StaticWall("bauxitic-wall"){{
            variants = 3;
            itemDrop = bauxite;
        }};


        algalBloom = new StaticWall("algal-bloom") {{
            variants = 1;
        }};

        feldsparWall = new StaticWall("feldspar-wall") {{
            variants = 4;
        }};

        gabbroWall = new StaticWall("gabbro-wall") {{
            variants = 4;
        }};


        coralWall = new StaticTree("coral-wall") {{
            variants = 3;
        }};

        parzilPine = new PineTree("parzil-pine"){{
            shadowAlpha = 0.6f;
            buildVisibility = sandboxOnly;
            variants = 2;
            rotationRand = 30;
            underBullets = true;
            size = 3;
            effect = Fx.impactReactorExplosion;
            health = 2500;
            buildCost = 3600;
            clipSize = 120;
        }};

        bloom = new TreeBlock("bloom");

        algalWall = new TreeBlock("algal-wall") {{
            variants = 2;
            shadowOffset = -1;
        }};
        //boulders
        algalBoulder = new Prop("algal-boulder"){{
            variants = 2;
            algal_carpet.asFloor().decoration = kelp_floor.asFloor().decoration;
        }};

        feldsparBoulder = new Prop("feldspar-boulder"){{
            variants = 1;
            feldspar.asFloor().decoration = andesite.asFloor().decoration = andesiteRubble.asFloor().decoration = this;
        }};

        gabbroBoulder = new Prop("gabbro-boulder."){{
            variants = 1;
            gabbro.asFloor().decoration =  gabbro_extrusions.asFloor().decoration;
        }};
        //plants and stuff
        kelp = new SeaBush("kelp"){{
            algal_carpet.asFloor().decoration =  kelp_floor.asFloor().decoration;
        }};

        rockweed = new SeaBush("rockweed") {{
            feldspar.asFloor().decoration =  roughFeldspar.asFloor().decoration;
            lobesMin = 3;
            lobesMax = 5;
            timeRange = 160;
        }};

        urchin = new SeaBush("urchin") {{
            lobesMin = 3;
            gabbro.asFloor().decoration =  gabbro_extrusions.asFloor().decoration;
            lobesMax = 7;
            timeRange = 70;
            magMax = 30;
            sclMax = 55;
            sclMin = 10;
            magMin = 10;

        }};

    }
}
