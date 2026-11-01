package aquarion.world.blocks.power;

import aquarion.world.entities.bullet.LooseSingularityBulletType;
import arc.util.Time;
import mindustry.gen.Bullet;
import mindustry.world.Block;
import mindustry.gen.Building;
import arc.util.io.Reads;
import arc.util.io.Writes;

/** Adjacent emergency extractor with one second of safe launch flight. */
public class SingularityEjector extends Block{
    public final LooseSingularityBulletType containedCore = new LooseSingularityBulletType(){
        {
            safeFlightTime = 60f;
        }

        @Override
        public void update(Bullet bullet){
            if(bullet.time < safeFlightTime){
                bullet.vel.trns(bullet.rotation(), 6f);
                return;
            }
            super.update(bullet);
        }
    };

    public SingularityEjector(String name){
        super(name);
        update = solid = rotate = true;
        canOverdrive = false;
        buildType = SingularityEjectorBuild::new;
    }

    public class SingularityEjectorBuild extends Building{
        public float cooldown;

        public boolean eject(VoidGenerator.VoidGenBuild reactor){
            if(cooldown > 0f || !enabled || !isValid() || reactor.team != team || !proximity.contains(reactor)
                || mindustry.Vars.net.client()) return false;
            containedCore.createNet(mindustry.game.Team.derelict, x, y, rotdeg(), -1f, 1f, 1f);
            cooldown = 120f;
            reactor.clearCore();
            return true;
        }

        @Override
        public void updateTile(){
            cooldown = Math.max(0f, cooldown - Time.delta);
            if(!enabled || mindustry.Vars.net.client()) return;
            for(Building other : proximity){
                if(other instanceof VoidGenerator.VoidGenBuild reactor
                    && reactor.smoothedInput >= ((VoidGenerator)reactor.block).overloadInput * 0.75d){
                    eject(reactor);
                }
            }
        }

        @Override
        public byte version(){ return 1; }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(cooldown);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            cooldown = revision >= 1 ? read.f() : 0f;
        }
    }
}
