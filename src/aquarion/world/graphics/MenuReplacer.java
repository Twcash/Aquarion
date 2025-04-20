package aquarion.world.graphics;

import arc.scene.Group;
import arc.scene.ui.layout.Table;
import mindustry.ui.fragments.MenuFragment;

import java.lang.reflect.Field;
//GOGO GADGET MENU REPLACINATOR
public class MenuReplacer {
    //Ima be honest I have no clue what to do past this
    public static void replaceMenu(MenuFragment menu){
        try {
            Field containerField = MenuFragment.class.getDeclaredField("container");
            containerField.setAccessible(true);
            Table container = (Table) containerField.get(menu);

            if(container != null){
                container = null;
            }

            container.add("Why are you even here?").row();
            container.button("Why are you even here?", () -> {
            });
            container.add("I'm sorry, I can't help you").row();
            container.button("I'm sorry, I can't help you", () -> {
            });

        } catch(Exception e){
            e.printStackTrace();
        }
    }
}