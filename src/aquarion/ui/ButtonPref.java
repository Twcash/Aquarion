package aquarion.ui;

import arc.scene.style.Drawable;
import arc.scene.ui.ImageButton;
import arc.scene.utils.Elem;
import mindustry.ui.dialogs.SettingsMenuDialog;

public class ButtonPref extends SettingsMenuDialog.SettingsTable.Setting {
    Drawable icon;
    Runnable listener;

    public ButtonPref(String name, Drawable icon, Runnable listener){
        super(name);
        this.icon = icon;
        this.listener = listener;
    }

    @Override
    public void add(SettingsMenuDialog.SettingsTable table){
        ImageButton b = Elem.newImageButton(icon, listener);
        b.resizeImage(32);
        b.label(() -> title).padLeft(6).growX();
        b.left();

        addDesc(table.add(b).left().padTop(3f).get());
        table.row();
    }
}
