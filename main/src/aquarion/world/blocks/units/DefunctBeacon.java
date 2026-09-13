package aquarion.world.blocks.units;

import aquarion.annotations.Annotations;
import aquarion.world.graphics.AquaFx;
import arc.Events;
import arc.graphics.Blending;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import jdk.jfr.Event;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.effect.WrapEffect;
import mindustry.game.EventType;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.world.Block;

import static mindustry.Vars.net;

public class DefunctBeacon extends Block {

    public Seq<UnitType> units = new Seq<>();
    public float interval = 600;
    public float spawnRange = 24;
    public Effect spawnEffect = Fx.unitSpawn;
    public Effect burstEffect = new WrapEffect(Fx.mineImpactWave, Pal.accent);
    public @Annotations.Load("@-glow") TextureRegion glowRegion;
    public DefunctBeacon(String name){
        super(name);
        update = true;
    }

    public class DefunctBeaconBuild extends Building{
        public float progress = 0;
        @Override
        public void draw(){
            super.draw();
            Draw.color(Pal.accentBack.lerp(Pal.accent,Mathf.absin(4, 1f)+edelta()));
            Draw.alpha((Mathf.absin(4, 1f)+edelta())*edelta());
            Lines.dashCircle(x,y, spawnRange*2f);

            Draw.color(Pal.accentBack.lerp(Pal.accent,progress/interval));
            Draw.alpha((progress/interval)*edelta());
            Draw.blend(Blending.additive);
            Draw.rect(glowRegion,x,y,rotation);
            Draw.blend();
            Draw.reset();
        }
        @Override
        public void updateTile(){
            super.updateTile();
            progress += edelta();
            if(progress >= 600){
                burstEffect.at(x,y);
                progress = 0;
                //units are spawned server-side only; positions are deterministic so
                //the client-side spawn effects mirror the server
                for(int i = 0; i < units.size; i++){
                    UnitType type = units.get(i);
                    float sx = x + Mathf.randomSeed(tile.pos() * 3L + i * 3L, -spawnRange, spawnRange);
                    float sy = y + Mathf.randomSeed(tile.pos() * 3L + i * 3L + 1, -spawnRange, spawnRange);
                    if(!net.client()){
                        Unit unit = type.create(this.team);
                        unit.set(sx, sy);
                        unit.rotation = rotation;
                        unit.add();
                    }
                    spawnEffect.at(sx, sy, rotation, type);
                }
            }
        }
        @Override
        public byte version(){
            return 1;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(progress);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            if(revision >= 1) {
                progress = read.f();
            }
        }
    }
}
