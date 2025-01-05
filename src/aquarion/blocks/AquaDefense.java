package aquarion.blocks;

import aquarion.world.blocks.defense.BlockingForceProjector;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.blocks.defense.RegenProjector;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.draw.DrawMulti;
import mindustry.world.draw.DrawRegion;
import mindustry.world.meta.Env;

import static aquarion.AquaItems.*;
import static mindustry.content.Items.lead;
import static mindustry.content.Items.metaglass;
import static mindustry.content.Liquids.hydrogen;
import static mindustry.type.ItemStack.with;

public class AquaDefense {
    public static Block bauxiteWall, duraluminWall, forceBarrier, galliumWall, lithoniteWall, manganeseWall, mendHive, regenPylon, smallBauxiteWall, smallDuraluminWall, smallGalliumWall, smallLithoniteWall, smallManganeseWall;


    public static void loadContent() {
        //Walls
        //TODO balance walls
        smallBauxiteWall = new Wall("small-bauxite-wall") {{
            requirements(Category.defense, with(bauxite, 6));
            health = 128;
            researchCostMultiplier = 0.1f;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};

        bauxiteWall = new Wall("bauxite-wall") {{
            requirements(Category.defense, with(bauxite, 24));
            health = 512;
            size = 2;
            researchCostMultiplier = 0.1f;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};

        smallGalliumWall = new Wall("small-gallium-wall") {{
            requirements(Category.defense, with(gallium, 6));
            health = 240;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};

        galliumWall = new Wall("gallium-wall") {{
            requirements(Category.defense, with(gallium, 24));
            health = 512;
            size = 2;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};

        smallManganeseWall = new Wall("small-manganese-wall") {{
            requirements(Category.defense, with(manganese, 6));
            health = 375;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};

        manganeseWall = new Wall("manganese-wall") {{
            requirements(Category.defense, with(manganese, 24));
            health = 1500;
            size = 2;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};

        smallDuraluminWall = new Wall("small-duralumin-wall") {{
            requirements(Category.defense, with(duralumin, 6));
            health = 875;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};

        duraluminWall = new Wall("duralumin-wall") {{
            requirements(Category.defense, with(duralumin, 24));
            health = 3500;
            size = 2;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        //endRegion

        //regeneration

        // WARNING: this requires the dronetype but there's none implemented yet...

        /* mendHive = new DroneCenter("mend-hive") {{
            unitsSpawned = 2;
            droneType = repair-drone;
            size = 2;
            droneRange = 70;
            requirements(Category.defense, with(lead, 70, sodium, 60, gallium, 40, bauxite, 80));
            consumePower(6.66666F);
            consumeLiquid(hydrogen, 0.25F);
            envEnabled = 4;

        }};*/


        regenPylon = new RegenProjector("regen-pylon"){{
            requirements(Category.effect, with(nitride, 90, lead, 90, metaglass, 60));
            size = 2;
            //norax make sure to read the files. This caused massive issues
            range = 45;
            consumePower(8.333333F);
            consumeLiquid(hydrogen, 0.2F);
            healPercent = 0.2F;
            drawer = new DrawMulti(new DrawRegion("-bottom"),new DrawRegion("-top"), new DrawRegion("-rotator"){{spinSprite = true; rotateSpeed = 2;}});
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled|= Env.spores | Env.scorching;
            }};


        forceBarrier = new BlockingForceProjector("force-barrier"){{
            requirements(Category.defense, ItemStack.with(lead,80, duralumin, 120, metaglass, 90, nitride, 150));
            consumePower(3f / 60f);
            sides = 18;
            radius = 120;
            shieldRotation = 0;
            outputsPower = false;
            hasPower = true;
            consumesPower = true;
            conductivePower = true;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled|= Env.spores | Env.scorching;
            shieldHealth = 660;
            size = 5;
        }};
    }
}
