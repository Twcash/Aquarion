package aquarion.world.graphics;

import arc.Core;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Time;
import mindustry.graphics.MenuRenderer;

import java.util.Objects;

public class AquaMenuRenderer extends MenuRenderer {
    public static final MenuProv[] menus = new MenuProv[] {
            new MenuProv("Sheet", new MenuBackgroundSheet()),
            new MenuProv("Shader", new MenuBackgroundShader())
    };

    public MenuBackground currentMenuBackground;

    public AquaMenuRenderer() {
        reload(find(Core.settings.getString("aqua-current-menu", "a"), menus[0]).menuBackground);
    }

    public void reload(MenuBackground menuBackground) {
        Time.mark();
        currentMenuBackground = menuBackground;
        currentMenuBackground.init.get(menuBackground);
        Log.info("Menu generated in @. Menu: @", Time.elapsed(), menuBackground.getClass());
    }

    @Override
    public void render() {
        currentMenuBackground.render();
    }

    public static class MenuProv {
        public String name;
        public MenuBackground menuBackground;

        public MenuProv(String name, MenuBackground menuBackground) {
            this.name = name;
            this.menuBackground = menuBackground;
        }
    }

    public static MenuProv find(MenuBackground bg, MenuProv def) {
        MenuProv prov = new Seq<>(AquaMenuRenderer.menus).find(menu -> menu.menuBackground == bg);
        if (prov == null) return def;
        return prov;
    }
    public static MenuProv find(String name, MenuProv def) {
        MenuProv prov = new Seq<>(AquaMenuRenderer.menus).find(menu -> Objects.equals(menu.name, name));
        if (prov == null) return def;
        return prov;
    }
}