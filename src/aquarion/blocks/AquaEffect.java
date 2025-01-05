package aquarion.blocks;

import arc.graphics.Color;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.blocks.defense.OverdriveProjector;
import mindustry.world.meta.Env;

import static aquarion.AquaItems.*;
import static mindustry.content.Items.lead;
import static mindustry.type.ItemStack.with;

public class AquaEffect {
    public static Block overdriveSubstation, overdriveTerminus;
    public static void loadContent() {

            //TODO overdrive projectors are bad. need to find a way to make it only able to boost defense/crafting related structures
            overdriveSubstation = new OverdriveProjector("overdrive-substation") {{
                requirements(Category.effect, with(manganese, 120, nitride, 250, lead, 450, bauxite, 120));
                reload = 90;
                size = 4;
                range = 110;
                useTime = 420;
                category = Category.effect;
                baseColor = Color.valueOf("f6675b");
                phaseColor = Color.valueOf("f6675b");
                // TODO rebalance power consumption for literally everything
                envEnabled |= Env.terrestrial | Env.underwater;
                envDisabled = Env.none;

                consumePower(1f);
            }};
            overdriveTerminus = new

                    OverdriveProjector("overdrive-terminus") {
                        {
                            //TODO actually make the thing
                            requirements(Category.effect, with(manganese, 120, nitride, 250, lead, 450, bauxite, 120));
                            reload = 90;
                            size = 5;
                            range = 110;
                            useTime = 420;
                            category = Category.effect;
                            baseColor = Color.valueOf("f6675b");
                            phaseColor = Color.valueOf("f6675b");
                            // TODO rebalance power consumption for literally everything
                            envEnabled |= Env.terrestrial | Env.underwater;
                            envDisabled = Env.none;

                            consumePower(1f);
                        }
                    };
        }
    }
