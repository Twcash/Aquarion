package aquarion.world.blocks.power;

import aquarion.annotations.Annotations;
import aquarion.world.graphics.CircleDistort;
import aquarion.world.graphics.Renderer;
import aquarion.world.graphics.AquaFx;
import aquarion.world.blocks.heatBlocks.SharedHeat;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.Angles;
import arc.math.geom.Position;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.gen.Teamc;
import mindustry.gen.Posc;
import mindustry.gen.Call;
import mindustry.entities.Effect;
import mindustry.graphics.Pal;
import mindustry.logic.LAccess;
import mindustry.type.Item;
import mindustry.ui.Bar;
import mindustry.world.blocks.heat.HeatBlock;
import mindustry.world.blocks.heat.HeatConsumer;
import mindustry.world.blocks.power.PowerGenerator;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.tilesize;

/** Item-fed, omnidirectional heat source. GeneratorBuild is retained for old saves. */
public class VoidGenerator extends PowerGenerator{
    public float heatPerItem = 30f;
    public float maxHeat = 500f;
    public Effect intakeEffect;
    public int maxIntakeParticles = 6;
    /** Weighted item energy per tick; sustained excessive intake ruptures containment. */
    public double overloadInput = 6000d;
    public aquarion.world.entities.bullet.LooseSingularityBulletType looseSingularity =
        new aquarion.world.entities.bullet.LooseSingularityBulletType();
    public @Annotations.Load("circle-shadow") TextureRegion softCircle;
    public VoidGenerator(String name){
        super(name);
        hasItems = acceptsItems = update = true;
        hasPower = outputsPower = consumesPower = false;
        powerProduction = 0f;
        rotate = false;
        canOverdrive = false;
        buildType = VoidGenBuild::new;
        SharedHeat.register();
        intakeEffect = AquaFx.itemVortex(() -> size * tilesize / 2f, 75f, 1.5f);
    }
    public float heatForInput(double input){
        if(!(input > 0d) || maxHeat <= 0f) return 0f;
        double fraction = 1d - 1d / (1d + Math.log1p(input));
        return Math.min(Math.nextDown(maxHeat), (float)(maxHeat * fraction));
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.remove(generationType);
        stats.add(Stat.output, maxHeat, StatUnit.heatUnits);
    }

    @Override
    public void setBars(){
        super.setBars();
        addBar("heat", (VoidGenBuild build) -> new Bar(
            () -> Core.bundle.format("bar.heatamount", (int)build.generatedHeat()),
            () -> Pal.lightOrange, build::heatFrac));
    }

    public class VoidGenBuild extends GeneratorBuild implements SharedHeat.Source{
        public float heat;
        public float totalProgress;
        public float smoothing = 0.1f;
        public double smoothedInput;
        private double pendingInput;

        @Override
        public float getPowerProduction(){
            return 0f;
        }

        @Override
        public void updateTile(){
            super.updateTile();
            float dt = delta();
            if(dt <= 0f) return;
            float alpha = 1f - Mathf.pow(1f - Mathf.clamp(smoothing), dt);
            double input = enabled ? pendingInput / dt : 0d;
            pendingInput = 0d;
            smoothedInput += (input - smoothedInput) * alpha;
            if(!mindustry.Vars.net.client() && smoothedInput >= overloadInput){
                looseSingularity.createNet(team, x, y, Mathf.random(360f), -1f, 1f, 1f);
                mindustry.content.Fx.impactReactorExplosion.at(x, y);
                Call.effect(mindustry.content.Fx.impactReactorExplosion, x, y, 0f, Color.white);
                Effect.shake(12f, 30f, x, y);
                kill();
                return;
            }
            float target = enabled ? heatForInput(smoothedInput) * Mathf.clamp(efficiency) : 0f;
            heat = Math.min(Math.nextDown(maxHeat), Math.max(0f, Mathf.lerp(heat, target, alpha)));
            productionEfficiency = heatFrac();
            totalProgress += dt * warmup();
        }

        @Override
        public float warmup(){
            return heatFrac();
        }

        @Override
        public float totalProgress(){
            return totalProgress;
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            return enabled;
        }

        @Override
        public int acceptStack(Item item, int amount, Teamc source){
            return enabled ? Math.max(0, amount) : 0;
        }

        @Override
        public void handleItem(Building source, Item item){
            handleStack(item, 1, source);
        }

        @Override
        public void handleStack(Item item, int amount, Teamc source){
            if(!enabled || amount <= 0) return;
            pendingInput += (double)heatPerItem * item.cost * item.cost * amount;
            if(!mindustry.Vars.net.client()){
                float angle = source != null && source != this
                    ? Angles.angle(x, y, source.x(), source.y()) : Mathf.random(360f);
                int count = Math.min(amount, maxIntakeParticles);
                for(int i = 0; i < count; i++){
                    float entryAngle = count == 1 ? angle : angle + Mathf.range(18f);
                    // Call.effect only broadcasts; it does not spawn an effect on this instance.
                    intakeEffect.at(x, y, entryAngle, Color.white, item);
                    Call.effect(intakeEffect, x, y, entryAngle, Color.white, item);
                }
            }
        }

        /** Total reactor heat, before sharing it among adjacent heat inputs. */
        public float generatedHeat(){
            return maxHeat <= 0f ? 0f : Math.min(Math.nextDown(maxHeat), Math.max(0f, heat));
        }

        @Override
        public float heat(){
            return generatedHeat();
        }

        /** Every eligible neighboring building gets one equal share. */
        @Override
        public float heatTo(Building receiver){
            if(receiver.team != team || !SharedHeat.canReceive(receiver) || !proximity.contains(receiver)) return 0f;
            int receivers = 0;
            for(Building other : proximity){
                if(other.team == team && SharedHeat.canReceive(other)) receivers++;
            }
            return receivers == 0 ? 0f : generatedHeat() / receivers;
        }

        @Override
        public float heatFrac(){
            return maxHeat <= 0f ? 0f : generatedHeat() / maxHeat;
        }

        @Override
        public double sense(LAccess sensor){
            return sensor == LAccess.heat ? generatedHeat() : super.sense(sensor);
        }

        @Override
        public void draw(){
            super.draw();
            Draw.z(Renderer.Layer.power-1);
            float output = generatedHeat();
            if(output <= 0f) return;
            Draw.color(Color.black);
            Fill.circle(x, y, output * 0.035f);
            Draw.color(Color.white);
            Fill.circle(x, y, output * 0.03f);
            Draw.rect(softCircle, x, y, output * 0.02f,output * 0.02f,0);
            Draw.rect(softCircle, x, y, output * 0.025f,output * 0.02f,45);
            Draw.reset();
            CircleDistort.drawSmoke(x, y, output * 0.05f, Time.time, heatFrac()*2);
            CircleDistort.draw(x, y, output * 0.015f, heatFrac());

        }

        @Override
        public byte version(){
            return 3;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(heat);
            write.d(smoothedInput);
            write.d(pendingInput);
            write.f(totalProgress);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            if(revision >= 2){
                heat = read.f();
                smoothedInput = read.d();
                pendingInput = read.d();
                heat = generatedHeat();
            }else{
                heat = 0f;
                smoothedInput = pendingInput = 0d;
            }
            totalProgress = revision >= 3 ? read.f() : 0f;
            productionEfficiency = heatFrac();
        }
    }
}
