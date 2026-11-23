package aquarion.world.blocks.power;

import mindustry.Vars;
import mindustry.gen.Groups;
import mindustry.world.blocks.power.PowerGenerator;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

/** Intercepts weather strikes and immediately charges batteries in the connected power graph. */
public class LightningRod extends PowerGenerator {
    public float captureRange = 120f;
    public float energyPerStrike = 6000f;

    public LightningRod(String name){
        super(name);
        powerProduction = 0;
        canOverdrive = false;
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.range, captureRange / Vars.tilesize, StatUnit.blocks);
    }

    public static LightningRodBuild find(float x, float y){
        LightningRodBuild closest = null;
        float distance = Float.MAX_VALUE;
        for(var build : Groups.build){
            if(!(build instanceof LightningRodBuild rod) || !rod.enabled || !rod.isValid()) continue;
            LightningRod block = (LightningRod)rod.block;
            float next = rod.dst2(x, y);
            if(next <= block.captureRange * block.captureRange
                && (next < distance || next == distance && (closest == null || rod.pos() < closest.pos()))){
                closest = rod;
                distance = next;
            }
        }
        return closest;
    }

    public class LightningRodBuild extends GeneratorBuild {
        public void capture(){
            if(!Vars.net.client() && enabled && power != null){
                power.graph.chargeBatteries(energyPerStrike);
            }
        }
    }
}
