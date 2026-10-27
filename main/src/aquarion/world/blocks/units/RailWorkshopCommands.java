package aquarion.world.blocks.units;

import arc.Core;
import arc.Events;
import arc.input.InputProcessor;
import arc.input.KeyCode;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import mindustry.content.Fx;
import mindustry.game.EventType;
import mindustry.gen.Building;
import mindustry.input.Binding;
import aquarion.world.blocks.units.RailWorkshop.RailWorkshopBuild;

import static mindustry.Vars.*;

/** Preserve the unit queue-movement input for trainyards, whose native command packet has no queue flag. */
public final class RailWorkshopCommands{
    private static boolean registered;

    public static void register(){
        if(registered || headless) return;
        registered = true;
        Events.on(EventType.ClientLoadEvent.class, event -> Core.input.getInputMultiplexer().addProcessor(0, new InputProcessor(){
            @Override public boolean touchDown(int x, int y, int pointer, KeyCode button){
                if(button != Binding.commandQueue.value.key || Core.scene.hasMouse() || state == null || !state.isPlaying()
                    || control == null || control.input == null || !control.input.commandMode || player == null) return false;
                Seq<Building> selected = control.input.commandBuildings.copy();
                Seq<RailWorkshopBuild> yards = new Seq<>();
                for(Building build : selected){
                    if(build instanceof RailWorkshopBuild yard && !((RailWorkshop)yard.block).coupling
                        && yard.team == player.team() && yard.isCommandable()) yards.add(yard);
                }
                if(yards.isEmpty()) return false;
                Vec2 target = Core.input.mouseWorld(x, y).cpy();
                // Let the native input send queued orders to selected units and ordinary buildings.
                // The trainyards receive an append-only config packet instead of a replacing command packet.
                control.input.commandBuildings.removeAll(build -> build instanceof RailWorkshopBuild yard && yards.contains(yard, true));
                try{
                    control.input.commandTap(x, y, true);
                }finally{
                    control.input.commandBuildings.clear();
                    control.input.commandBuildings.addAll(selected);
                }
                for(RailWorkshopBuild yard : yards) yard.configure(target.cpy());
                if(!state.isPaused()) Fx.moveCommand.at(target);
                return true;
            }
        }));
        Events.run(EventType.Trigger.draw, () -> {
            if(control == null || control.input == null || !control.input.commandMode || player == null) return;
            for(Building build : control.input.commandBuildings){
                if(build instanceof RailWorkshopBuild yard && !((RailWorkshop)yard.block).coupling && yard.team == player.team()){
                    float z = arc.graphics.g2d.Draw.z();
                    arc.graphics.g2d.Draw.z(mindustry.graphics.Layer.overlayUI);
                    yard.drawCommandRoute();
                    arc.graphics.g2d.Draw.z(z);
                }
            }
        });
    }
}
