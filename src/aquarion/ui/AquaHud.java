package aquarion.ui;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.*;
import arc.scene.actions.*;
import arc.scene.event.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.ImageButton.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.core.GameState.*;
import mindustry.core.*;
import mindustry.ctype.*;
import mindustry.game.EventType.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.input.*;
import mindustry.net.Packets.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.ui.fragments.HudFragment;
import mindustry.ui.fragments.PlacementFragment;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.meta.*;

import java.lang.reflect.Field;

import static mindustry.Vars.*;
import static mindustry.gen.Tex.*;

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