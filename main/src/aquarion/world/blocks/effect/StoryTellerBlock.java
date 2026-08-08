package aquarion.world.blocks.effect;

import aquarion.ModEventHandler;
import aquarion.world.dialogue.DialogueTree;
import arc.Graphics.Cursor;
import arc.Graphics.Cursor.SystemCursor;
import mindustry.gen.Building;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.meta.BuildVisibility;
import mindustry.world.meta.Env;

import static mindustry.content.Items.silicon;
import static mindustry.content.Items.titanium;
import static mindustry.Vars.*;

/** A block that opens a dialogue tree when tapped. */
public class StoryTellerBlock extends Block {
    /** The dialogue tree this block shows. */
    public DialogueTree story;

    public StoryTellerBlock(String name){
        super(name);
        solid = true;
        destructible = true;
        update = false;
        consumesTap = true;
        hasShadow = true;
        category = Category.effect;
        buildVisibility = BuildVisibility.campaignOnly;
        envEnabled = Env.terrestrial | Env.space;
        requirements(Category.effect, ItemStack.with(silicon, 40, titanium, 40));
    }

    public class StoryTellerBuild extends Building{
        @Override
        public void tapped(){
            if(story != null && ModEventHandler.storyDialog != null){
                ModEventHandler.storyDialog.open(story);
            }
        }

        @Override
        public Cursor getCursor(){
            return interactable(player.team()) ? SystemCursor.hand : SystemCursor.arrow;
        }
    }
}
