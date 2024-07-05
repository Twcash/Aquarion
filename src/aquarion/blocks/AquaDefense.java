package aquarion.blocks;

import arc.graphics.Color;
import mindustry.content.Liquids;
import mindustry.graphics.Layer;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.blocks.defense.ForceProjector;
import mindustry.world.blocks.defense.RegenProjector;
import mindustry.world.blocks.defense.Wall;

import static aquarion.AquaItems.*;
import static mindustry.content.Items.lead;
import static mindustry.content.Items.metaglass;
import static mindustry.content.Liquids.hydrogen;
import static mindustry.type.ItemStack.with;
import static mindustry.world.blocks.logic.LogicDisplay.GraphicsType.col;

import mindustry.type.Category;
import mindustry.world.blocks.units.DroneCenter;
import mindustry.world.draw.*;
import mindustry.world.meta.Env;

import javax.print.attribute.standard.Sides;

public class AquaDefense {
    public static Block bauxiteWall, duraluminWall, forceBarrier, galliumWall, lithoniteWall, manganeseWall, mendHive, regenPylon, smallBauxiteWall, smallDuraluminWall, smallGalliumWall, smallLithoniteWall, smallManganeseWall;


    public static void loadContent() {
        //Walls
        //TODO balance walls
        smallBauxiteWall = new Wall("small-bauxite-wall") {{
            requirements(Category.defense, with(bauxite, 6));
            health = 170;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled|= Env.spores | Env.scorching;
        }};

        bauxiteWall = new Wall("bauxite-wall") {{
            requirements(Category.defense, with(bauxite, 24));
            health = 680;
            size = 2;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled|= Env.spores | Env.scorching;
        }};

        smallGalliumWall = new Wall("small-gallium-wall") {{
            requirements(Category.defense, with(gallium, 6));
            health = 240;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled|= Env.spores | Env.scorching;
        }};

        galliumWall = new Wall("gallium-wall") {{
            requirements(Category.defense, with(gallium, 24));
            health = 960;
            size = 2;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled|= Env.spores | Env.scorching;
        }};

        smallManganeseWall = new Wall("small-manganese-wall") {{
            requirements(Category.defense, with(manganese, 6));
            health = 375;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled|= Env.spores | Env.scorching;
        }};

        manganeseWall = new Wall("manganese-wall") {{
            requirements(Category.defense, with(manganese, 24));
            health = 1500;
            size = 2;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled|= Env.spores | Env.scorching;
        }};

        smallDuraluminWall = new Wall("small-duralumin-wall") {{
            requirements(Category.defense, with(duralumin, 6));
            health = 875;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled|= Env.spores | Env.scorching;
        }};

        duraluminWall = new Wall("duralumin-wall") {{
            requirements(Category.defense, with(duralumin, 24));
            health = 3500;
            size = 2;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled|= Env.spores | Env.scorching;
        }};

        smallLithoniteWall = new Wall("small-lithonite-wall") {{
            requirements(Category.defense, with(lithoniteAlloy, 6));
            health = 1125;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled|= Env.spores | Env.scorching;
        }};

        lithoniteWall = new Wall("lithonite-wall") {{
            requirements(Category.defense, with(lithoniteAlloy, 24));
            health = 4500;
            size = 2;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled|= Env.spores | Env.scorching;
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


        forceBarrier = new ForceProjector("force-barrier"){{
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
