package aquarion;

import aquarion.annotations.Annotations;
import aquarion.content.ModMusic;
import aquarion.dialogs.AquaDatabaseDialog;
import aquarion.dialogs.AquaResearchDialog;
import aquarion.ui.AquaHud;
import aquarion.ui.ModSettings;
import arc.Events;
import arc.scene.Element;
import mindustry.Vars;
import mindustry.core.UI;
import mindustry.game.EventType;
import mindustry.ui.fragments.FadeInFragment;
import mindustry.ui.fragments.HudFragment;
import mindustry.ui.fragments.PlacementFragment;
import arc.*;
import arc.Graphics.*;
import arc.Input.*;
import arc.assets.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.input.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.*;
import arc.scene.actions.*;
import arc.scene.event.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.TextField.*;
import arc.scene.ui.Tooltip.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.editor.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.*;
import mindustry.mod.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;
import mindustry.ui.fragments.*;

import static arc.scene.actions.Actions.*;
import static mindustry.Vars.*;

@Annotations.LoadRegs("error")// Need this temporarily, so the class gets generated.
@Annotations.EnsureLoad
public class ModEventHandler {
    public static AquaResearchDialog techDialog;
    public static AquaDatabaseDialog databaseDialog;
    public static AquaHud hudFrag;
    public static WidgetGroup hudGroup;
    public static void load(){
        techDialog = new AquaResearchDialog();
        databaseDialog = new AquaDatabaseDialog();
    }
    public static void init() {
//        Events.on(EventType.ClientLoadEvent.class, e->{
//            ui.hudGroup.color.a = 0;
//            ui.hudGroup.touchable = Touchable.disabled;
//            hudGroup = new WidgetGroup();
//
//            hudFrag = new AquaHud();
//
//            hudGroup.setFillParent(true);
//            hudGroup.touchable = Touchable.childrenOnly;
//            hudGroup.visible(() -> state.isGame());
//            Core.scene.add(hudGroup);
//            hudFrag.build(hudGroup);
//            ui.consolefrag.build(hudGroup);
//
//        });
//        Events.on(EventType.ClientLoadEvent.class, e -> {
//            if(Vars.ui.menufrag != null) {
//                try {
//                    // Reflect.set(MenuFragment.class, Vars.ui.menufrag, "renderer", new AquaMenuRenderer());
//                } catch (Exception ex) {
//                    Log.err("Failed to replace renderer", ex);
//                }
//            } else {
//                Log.info("Vars.ui.menufrag was null. Unable to replace menu");
//            }
//        });
        Events.on(EventType.ClientLoadEvent.class, e -> ModMusic.attach());
        Events.on(EventType.ClientLoadEvent.class, e -> ModSettings.init());

        Events.on(EventType.MusicRegisterEvent.class, e -> ModMusic.load());
    }
}
