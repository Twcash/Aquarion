package aquarion.ui;

import arc.Core;
import arc.Events;
import arc.func.Cons;
import arc.scene.ui.Label;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Scaling;
import mindustry.Vars;
import mindustry.editor.WaveInfoDialog;
import mindustry.game.EventType.Trigger;
import mindustry.game.SpawnGroup;
import mindustry.gen.Icon;
import mindustry.gen.Payloadc;
import mindustry.type.UnitType;
import mindustry.ui.dialogs.BaseDialog;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class WavePayloadEditor{
    private static final String buttonName = "aquarion-wave-payloads";

    private WavePayloadEditor(){}

    public static void install(){
        try{
            // The wave editor keeps a private draft until the dialog closes.
            Field groups = WaveInfoDialog.class.getDeclaredField("groups");
            Field expanded = WaveInfoDialog.class.getDeclaredField("expandedGroup");
            Method picker = WaveInfoDialog.class.getDeclaredMethod("showUnits", Cons.class, boolean.class);
            groups.setAccessible(true);
            expanded.setAccessible(true);
            picker.setAccessible(true);
            Events.run(Trigger.update, () -> {
                if(Core.scene.getElements().find(element -> element instanceof WaveInfoDialog) instanceof WaveInfoDialog waves){
                    SpawnGroup group = selected(waves, groups, expanded);
                    if(group == null) return;

                    // Only the expanded card has a spawn row. It is rebuilt when the list changes.
                    String spawnText = Core.bundle.get("waves.spawn");
                    Label spawnLabel = waves.cont.find(element ->
                        element instanceof Label label && spawnText.contentEquals(label.getText()));
                    if(spawnLabel != null && spawnLabel.parent instanceof Table spawnRow &&
                        spawnRow.parent instanceof Table card && card.find(buttonName) == null){
                        card.button("@aquarion.waves.payloads", Icon.units, () -> edit(waves, group, picker))
                            .height(42f).padBottom(8f).get().name = buttonName;
                    }
                }
            });
        }catch(ReflectiveOperationException error){
            Log.err("Unable to attach the wave payload editor", error);
        }
    }

    private static SpawnGroup selected(WaveInfoDialog waves, Field groups, Field expanded){
        try{
            SpawnGroup group = (SpawnGroup)expanded.get(waves);
            return group != null && ((Seq<?>)groups.get(waves)).contains(value -> value == group) ? group : null;
        }catch(IllegalAccessException error){
            throw new IllegalStateException(error);
        }
    }

    private static void edit(WaveInfoDialog waves, SpawnGroup group, Method picker){
        if(group == null) return;
        if(!(group.type.constructor.get() instanceof Payloadc)){
            Vars.ui.showInfo("@aquarion.waves.payloads.unsupported");
            return;
        }

        BaseDialog dialog = new BaseDialog(Core.bundle.format("aquarion.waves.payloads.title", group.type.localizedName));
        Seq<UnitType> payloads = group.payloads == null ? new Seq<>() : group.payloads.copy();
        Table rows = new Table();
        rebuildRows(rows, payloads);
        dialog.cont.pane(pane -> pane.add(rows).growX().top()).width(330f).maxHeight(360f).row();
        dialog.cont.button("@aquarion.waves.payloads.add", Icon.add, () -> {
            try{
                picker.invoke(waves, (Cons<UnitType>)type -> {
                    payloads.add(type);
                    rebuildRows(rows, payloads);
                }, false);
            }catch(ReflectiveOperationException error){
                Log.err("Unable to open the wave unit picker", error);
            }
        }).size(200f, 56f);
        dialog.addCloseButton();
        dialog.buttons.button("@ok", () -> {
            group.payloads = payloads.isEmpty() ? null : payloads;
            dialog.hide();
        }).size(150f, 64f);
        dialog.show();
    }

    private static void rebuildRows(Table rows, Seq<UnitType> payloads){
        rows.clearChildren();
        if(payloads.isEmpty()) rows.add("@aquarion.waves.payloads.empty").pad(12f);
        for(int i = 0; i < payloads.size; i++){
            int index = i;
            UnitType type = payloads.get(i);
            rows.image(type.uiIcon).size(32f).scaling(Scaling.fit).pad(6f);
            rows.add(type.localizedName).growX().left().padRight(12f);
            rows.button(Icon.trash, () -> {
                payloads.remove(index);
                rebuildRows(rows, payloads);
            }).size(48f);
            rows.row();
        }
    }
}
