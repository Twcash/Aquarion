package aquarion.world.blocks.power;

import arc.func.Floatf;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.struct.Seq;
import mindustry.gen.Building;
import mindustry.world.Tile;
import mindustry.world.blocks.power.PowerGenerator;
import mindustry.world.blocks.power.PowerGraph;
import mindustry.world.draw.DrawDefault;
import mindustry.world.draw.DrawMulti;
import mindustry.world.draw.DrawSideRegion;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;
import static mindustry.entities.part.DrawPart.PartProgress.warmup;

public class PowerOutlet extends PowerGenerator {
    public PowerOutlet(String name) {
        super(name);
        outputsPower = true;
        hasPower = true;
        insulated = true;
        consumesPower = true;
        powerProduction = 100/60f;
        rotate = true;
        rotateDraw = false;
        consumePower(100/60f);
        drawer = new DrawMulti( new DrawDefault(), new DrawSideRegion());
    }
    @Override
    public TextureRegion[] icons(){
        return drawer.finalIcons(this);
    }
    @Override
    public void load(){
        super.load();
        drawer.load(this);
    }
    @Override
    public void setStats(){
        super.setStats();
        stats.remove(generationType);
        stats.remove(Stat.powerUse);
        stats.add(new Stat("power-throughput", StatCat.power), powerProduction*60f);
    }
    @Override
    public void setBars(){
    }
    public class OutletBuild extends GeneratorBuild {
        @Override
        public void updateTile() {
            //Remove production from current graph.
            if(this.power.graph.producers.contains(this)){
                this.power.graph.producers.remove(this);
            }
            //Add consumption to current graph.
            if(!this.power.graph.consumers.contains(this)){
                this.power.graph.consumers.add(this);
            }
            //Stop if front doesn't exist or has power
            if(front() == null || front().power == null || this.team != front().team){
                efficiency = 0;
                return;
            }
            //Remove consumption from target graph.
            PowerGraph front = this.front().power.graph;
            if(front.consumers.contains(this)){
                front.consumers.remove(this);
            }
            //Add production to current graph
            if(front.producers.contains(this)){
                //Force distribution of the new power
                front.distributePower(front.getPowerNeeded(), front.getPowerProduced(), false);
            } else {
                front.producers.add(this);
            }
        }
        @Override
        public float warmup(){
            return enabled ? efficiency : 0f;
        }

        @Override
        public float getPowerProduction(){
            return enabled ? powerProduction * efficiency: 0f;
        }
        @Override
        public void onProximityRemoved(){
            if(power != null){
                powerGraphRemoved();
            }
            if(front() == null || front().power.graph == null || this.team != front().team) return;
            PowerGraph front = this.front().power.graph;
            if(front.producers.contains(this)){
                front.producers.remove(this);
            }
        }
        @Override
        public void draw(){
            drawer.draw(this);
        }
    }
}
