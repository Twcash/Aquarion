package aquarion.ui;

import arc.Core;
import arc.scene.ui.ImageButton;

public class AquaStyles {

    public static ImageButton.ImageButtonStyle technodeFull;

    public static void load() {
        technodeFull = new ImageButton.ImageButtonStyle() {{
                this.up = Core.atlas.drawable("aquarion-circle-button");
                this.over = Core.atlas.drawable("aquarion-circle-button-down");
            }};
    }
}