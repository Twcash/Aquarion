package aquarion.world.blocks.power;

import arc.graphics.Color;
import arc.util.Strings;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.Block;
import arc.*;
import arc.math.*;
import mindustry.game.EventType.*;
import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.power.PowerGenerator;
import mindustry.world.meta.*;

public class Reactor extends PowerGenerator{
    public Reactor(String name) {
        super(name);
    }
    public float maxRadiation = 50;
    public float maxHeat = 250;
    public float maxSteam = 100;
    public float useInterval = 10;
    public float warmupSpeed = 0.07f;
    @Override
    public void setBars() {
        super.setBars();
        addBar("radiation", (ReactorBuild build) -> new Bar(
                () -> Core.bundle.format("bar.radiation", build.radiation),
                () -> Pal.heal,
                () -> (float) build.radiation /maxRadiation
        ));
        addBar("heat", (ReactorBuild build) -> new Bar(
                () -> Core.bundle.format("bar.heat", build.heat),
                () -> Pal.lightOrange,
                () -> (float) build.heat /maxHeat
        ));
        addBar("steam", (ReactorBuild build) -> new Bar(
                () -> Core.bundle.format("bar.steam", build.steam),
                () -> Pal.techBlue,
                () -> (float) build.steam /maxSteam
        ));
    }


    public class ReactorBuild extends GeneratorBuild {
        public float radiation;
        public float heat;
        public float steam;
        public float progress;
        public float warmup;
        @Override
        public void updateTile(){
            progress += getProgressIncrease(useInterval);
            warmup = Mathf.approachDelta(warmup, 1, warmupSpeed);
            if(progress >= 1f && radiation == maxRadiation){
                progress = 0;
                radDecay(5);
            }
        }
        public void radDecay(float amount){
            if(radiation != 0) {
                radiation -=1;
                heat += 2;
            };
        };
        public void addRadiation(float amount){
            if(radiation+amount < maxRadiation) {
                radiation += amount;
            } else{
                radiation -= 5;
                addHeat(5);
            }
        };
        public void addHeat(float amount){
            if(heat+amount < maxHeat) {
                heat += amount;
            } else {
                Events.fire(Trigger.thoriumReactorOverheat);
                kill();
            }
        };
        public void removeHeat(float amount){
            if(heat-amount >= 0) {
                heat = heat - amount;
            }
        };
        public void removeRadiation(float amount){
            if(radiation-amount >= 0) {
                radiation = radiation - amount;
            }
        };
        public void addSteam(float amount){
            if(steam+amount < maxSteam && heat >= amount*2) {
                heat -= amount *2;
                steam += amount;
            } else {
                Events.fire(Trigger.thoriumReactorOverheat);
                kill();
            }
        };
    }
}
