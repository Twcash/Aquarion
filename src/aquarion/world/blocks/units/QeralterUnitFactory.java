package aquarion.world.blocks.units;

import arc.Core;
import arc.Events;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.ai.*;
import mindustry.ctype.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.io.*;
import mindustry.logic.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.consumers.*;
import mindustry.world.meta.*;
import mindustry.ui.Bar;
import mindustry.world.blocks.payloads.UnitPayload;
import mindustry.world.blocks.units.UnitFactory;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static mindustry.net.Administration.ActionType.command;
import static mindustry.world.meta.Stat.unitType;
//fuck you this sucks and I hate this
public class QeralterUnitFactory extends UnitFactory {
    public int maxUnitsPerFactory = 4;

    public QeralterUnitFactory(String name) {
        super(name);
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.maxUnits, maxUnitsPerFactory, StatUnit.none);
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("unitLimit", (UnitFactoryBuild e) -> new Bar(
                () -> Core.bundle.format("bar.unitlimit", ((QeralterUnitFactoryBuild) e).spawnedUnits, maxUnitsPerFactory),
                () -> Pal.command,
                () -> (float) ((QeralterUnitFactoryBuild) e).spawnedUnits / maxUnitsPerFactory
        ));
    }

    public class QeralterUnitFactoryBuild extends UnitFactoryBuild {
        public int spawnedUnits = 0; // Track the number of units spawned
        @Override
        public void draw(){
            Draw.rect(region, x, y);
            Draw.rect(outRegion, x, y, rotdeg());

            if(currentPlan != -1){
                UnitPlan plan = plans.get(currentPlan);
                Draw.draw(Layer.blockOver, () -> Drawf.construct(this, plan.unit, rotdeg() - 90f, progress / plan.time, speedScl, time));
            }

            Draw.z(Layer.blockOver);

            payRotation = rotdeg();
            drawPayload();

            Draw.z(Layer.blockOver + 0.1f);

            Draw.rect(topRegion, x, y);
        }

        @Override
        public void updateTile(){
            if (spawnedUnits >= maxUnitsPerFactory) {
                progress = 0; // Prevent further progress
                return;
            }

            // Proceed with standard update logic
            super.updateTile();

            if(!configurable){
                currentPlan = 0;
            }

            if(currentPlan < 0 || currentPlan >= plans.size){
                currentPlan = -1;
            }

            if(efficiency > 0 && currentPlan != -1){
                time += edelta() * speedScl * Vars.state.rules.unitBuildSpeed(team);
                progress += edelta() * Vars.state.rules.unitBuildSpeed(team);
                speedScl = Mathf.lerpDelta(speedScl, 1f, 0.05f);
            }else{
                speedScl = Mathf.lerpDelta(speedScl, 0f, 0.05f);
            }

            moveOutPayload();

            if(currentPlan != -1 && payload == null){
                UnitPlan plan = plans.get(currentPlan);

                //make sure to reset plan when the unit got banned after placement
                if(plan.unit.isBanned()){
                    currentPlan = -1;
                    return;
                }

                if(progress >= plan.time){
                    progress %= 1f;

                    Unit unit = plan.unit.create(team);
                    if(unit.isCommandable()){
                        if(commandPos != null){
                            unit.command().commandPosition(commandPos);
                        }
                    }

                    payload = new UnitPayload(unit);
                    payVector.setZero();
                    consume();
                    Events.fire(new EventType.UnitCreateEvent(payload.unit, this));
                    spawnedUnits++;
                }

                progress = Mathf.clamp(progress, 0, plan.time);
            }else{
                progress = 0f;
            }
        }
    }
}
