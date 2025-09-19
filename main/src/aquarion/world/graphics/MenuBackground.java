package aquarion.world.graphics;

import arc.func.Cons;

public abstract class MenuBackground {
    public static MenuBackground[] menuBackgrounds = new MenuBackground[]{};

    public Cons<MenuBackground> init = menu -> {};

    public abstract void render();
}