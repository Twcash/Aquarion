package aquarion.world.graphics;

import arc.Events;
import arc.util.Reflect;
import mindustry.game.EventType.WorldLoadBeginEvent;
import mindustry.graphics.MenuRenderer;
import mindustry.ui.fragments.MenuFragment;

import static mindustry.Vars.headless;
import static mindustry.Vars.ui;

public class MenuReplacer {
    private static boolean hooked = false;

    /** Swaps the vanilla menu renderer for the Aquarion one, which renders the preset battle arena. */
    public static void replaceMenu(MenuFragment fragment) {
        if (headless || fragment == null) return;

        MenuRenderer old = Reflect.get(MenuFragment.class, fragment, "renderer");
        AquaMenuRenderer menu = new AquaMenuRenderer();
        Reflect.set(MenuFragment.class, fragment, "renderer", menu);
        if (old != null && old != menu) {
            old.dispose();
        }

        if (!hooked) {
            hooked = true;
            //a real map is about to load; scrub the menu sim so it can't leak into a real game
            Events.on(WorldLoadBeginEvent.class, e -> MenuBackgroundSheet.clearAll());
            //the arena itself is (re)built lazily in MenuBackgroundSheet.updateAndRender, which
            //also skips the map editor so it can never clobber a map being edited.
        }
    }
}
