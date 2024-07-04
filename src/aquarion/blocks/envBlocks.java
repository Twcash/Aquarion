package aquarion.blocks;

import arc.graphics.Color;
import mindustry.entities.effect.ParticleEffect;
import mindustry.graphics.CacheLayer;
import mindustry.type.Liquid;
import mindustry.world.Block;
import mindustry.world.blocks.environment.*;
import mindustry.world.meta.Attribute;
import aquarion.aquarionItems.*;
import aquarion.aquarionLiquids.*;

import static aquarion.aquarionItems.*;
import static aquarion.aquarionLiquids.brine;
import static mindustry.content.Blocks.*;
import static mindustry.content.Items.lead;


public class envBlocks {

    // Boulders
    public static Block algalBoulder, feldsparBoulder, gabbroBoulder, kelp, rockweed, urchin;

    // Floors
    public static Block algal_carpet, brine_liquid, coral_floor, feldspar_vent, feldspar, ferric_extrusions, gabbro_extrusions, gabbro_vent, gabbro, geothermal_vent, kelp_floor, laterite, phylite_floor, slate;

    // Ore blocks
    public static Block leadNodules, oreBauxite, oreGallium, oreLithium, oreManganese;

    // Walls
    public static Block algalBloom, algalWall, bloom, coralWall, feldsparWall, gabbroWall;

    public static void loadContent() {
        // Boulders

        algalBoulder = new Prop("algal-boulder"){{
            variants = 2;
        }};

        feldsparBoulder = new Prop("feldspar-boulder"){{
            variants = 1;
        }};

        gabbroBoulder = new Prop("gabbro-boulder."){{
            variants = 1;
        }};

        kelp = new SeaBush("kelp");

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
        // Floors


        //TODO fix the blend group
        algal_carpet = new Floor("algal-carpet", 5) {{

        }};

        brine_liquid = new Floor("brine-liquid"){{
            speedMultiplier = 0.1f;
            variants = 0;
            liquidDrop = brine;
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

        feldspar_vent = new SteamVent("feldspar-vent") {{
            attributes.set(Attribute.steam, 1f);
            parent = blendGroup = feldspar;
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

        feldspar = new Floor("feldspar", 3) {{
            decoration = feldsparBoulder;
        }};

        ferric_extrusions = new Floor("ferric-extrusions", 2) {{

        }};

        gabbro_extrusions = new Floor("gabbro-extrusions", 2) {{

        }};

        gabbro_vent = new SteamVent("gabbro-vent") {{
            attributes.set(Attribute.steam, 1f);
            parent = blendGroup = gabbro;
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

        gabbro = new Floor("gabbro", 3) {{
            decoration = gabbroBoulder;

        }};

        geothermal_vent = new SteamVent("geothermal-vent") {{
            attributes.set(Attribute.steam, 1f);
            parent = blendGroup = basalt;
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

        kelp_floor = new Floor("kelp-floor", 2) {{
            walkSound = mindustry.gen.Sounds.mud;
        }};

        laterite = new Floor("laterite", 4) {{
            decoration = rockweed;

        }};

        phylite_floor = new Floor("phylite-floor", 2) {{

        }};
        slate = new Floor("slate", 3) {{

        }};

        // Ore blocks
        leadNodules = new OreBlock("lead-nodules", lead);
        oreBauxite = new OreBlock("ore-bauxite", bauxite);
        oreGallium= new OreBlock("ore-gallium", gallium);
        oreLithium= new OreBlock("ore-lithium", lithium);
        oreManganese= new OreBlock("ore-manganese", manganese);

        // Walls & blocks

        algalBloom = new StaticWall("algal-bloom") {{
            variants = 1;
        }};

        feldsparWall = new StaticWall("feldspar-wall") {{
            variants = 2;
        }};

        gabbroWall = new StaticWall("gabbro-wall") {{
            variants = 2;
        }};


        coralWall = new StaticTree("coral-wall") {{
            variants = 3;
        }};

        bloom = new TreeBlock("bloom");

        algalWall = new TreeBlock("algal-wall") {{
            variants = 2;
            shadowOffset = -1;
        }};


    }
}
