package aquarion.ui;

import arc.scene.Group;
import arc.scene.ui.Label;
import mindustry.ui.Styles;
import mindustry.ui.fragments.HudFragment;

public class AquaHud extends HudFragment {

    @Override
    public void build(Group parent) {


        parent.fill(t -> {
            t.name = "customhud";
            t.top().left();
            t.margin(10f);
            t.add(new Label("I killed the Hud, (:", Styles.outlineLabel));
        });
        blockfrag.build(parent);

    }
}