package aquarion.ui;

import arc.*;
import arc.scene.style.*;
import arc.scene.ui.Button.*;
import arc.scene.ui.ImageButton;
import mindustry.gen.*;

public class AquaStyles {

    public static ImageButton.ImageButtonStyle technodeFull;

    public static void load() {
        technodeFull = new ImageButton.ImageButtonStyle() {{
                this.up = Core.atlas.drawable("aquarion-circle-button");
                this.over = Core.atlas.drawable("aquarion-circle-button-down");
            }};
    }
}