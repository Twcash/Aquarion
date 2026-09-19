package aquarion.world.blocks.units;

import aquarion.world.graphics.AquaFx;
import arc.audio.Sound;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;
import mindustry.type.UnitType;

import static mindustry.Vars.net;

public class DefunctUnitBlock extends UnitBlock{
    public float beepInterval = 240;
    public Sound beepSound = Sounds.none;
    public DefunctUnitBlock(String name){
        super(name);
    }
    public class DefunctUnitBlockBuild extends UnitBlockBuild{
        @Override
        public boolean shouldAmbientSound(){
            return true;
        }
        float beepProg = 0;
        @Override
        public void updateTile(){
            if(efficiency > 0 && !(this.team.data().countType(unit) >= Units.getCap(this.team))) {
                progress += edelta() * Vars.state.rules.unitBuildSpeed(team) * efficiency;
                totProgress = progress/time;
            }
            beepProg += Time.delta;
            if(beepProg >= beepInterval){
                beepProg = 0;
                beepSound.at(this, 1, 1f);
                Drawf.light(this, 10, Pal.heal, 0.5f);
            }
            if(progress >= time ){
                progress %= time;
                if(!net.client()){
                    mindustry.gen.Unit b = unit.create(team);
                    if(b.isCommandable()){
                        if(commandPos != null){
                            b.command().commandPosition(commandPos);
                        }
                        //this already checks if it is a valid command for the unit type
                        b.command().command(command == null && b.type.defaultCommand != null ? b.type.defaultCommand : command);
                    }
                    b.set(x, y);
                    b.rotation = rotdeg()+90;
                    for(StatusEffect effect : effects) {
                        b.apply(effect);
                    }
                    b.add();
                    remove();
                    kill();
                }

                Effect.shake(2f, 3f, this);
                Fx.producesmoke.at(this);
            }
        }
        @Override
        public void draw() {
            Draw.z(Layer.groundUnit);
            if (unit.fullIcon != null) Draw.rect(unit.fullIcon, x, y, rotdeg()-90 + id);
            if (unit.flying) {
                float e =  Mathf.clamp(totProgress, unit.shadowElevation, 1f);

                Draw.z(Layer.groundUnit-2);
                Draw.color(Pal.shadow, Pal.shadow.a * totProgress);
                Draw.rect(unit.shadowRegion, x + -12 * e, y + -12 * e);
                Draw.color();
                Draw.alpha(1);
                Draw.z(Layer.groundUnit-1.1f);
                for(UnitType.UnitEngine engine : unit.engines){
                    float rot = 0;

                    Tmp.v1.set(x, y).rotate(rot);
                    float ex = Tmp.v1.x, ey = Tmp.v1.y;
                    float rad = (engine.radius + Mathf.absin(Time.time, 2f, engine.radius / 4f)) * totProgress;
                    Draw.color(team.color);
                    Fill.circle(
                            engine.x + ex,
                            engine.y + ey,
                            rad * totProgress
                    );
                    Draw.z(Layer.groundUnit-1f);
                    Draw.color(Color.white);
                    Fill.circle(
                            engine.x + ex - Angles.trnsx(rot + engine.rotation, rad / 4f),
                            engine.y + ey - Angles.trnsy(rot + engine.rotation, rad / 4f),
                            rad / 2f * totProgress
                    );
                }
            }
        }

    }
}
