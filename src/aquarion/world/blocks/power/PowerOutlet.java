package aquarion.world.blocks.power;

import aquarion.world.graphics.DrawTop;
import arc.Core;
import arc.func.Floatf;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.WindowedMean;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.Log;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.world.Tile;
import mindustry.world.blocks.power.PowerGenerator;
import mindustry.world.blocks.power.PowerGraph;
import mindustry.world.consumers.ConsumePower;
import mindustry.world.consumers.ConsumePowerDynamic;
import mindustry.world.draw.DrawBlock;
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
    public DrawBlock drawer = new DrawMulti( new DrawDefault(), new DrawSideRegion());
    public TextureRegion sideRegion2, sideRegion1;
    public PowerOutlet(String name) {
        super(name);
        outputsPower = true;
        hasPower = true;
        insulated = true;
        consumesPower = true;
        powerProduction = 100/60f;
        rotate = true;
        rotateDraw = true;
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        Draw.rect(region, plan.drawx(), plan.drawy());
        Draw.rect(plan.rotation >= 2 ? sideRegion2 : sideRegion1, plan.drawx(), plan.drawy(), plan.rotation * 90);
    }
    @Override
    public void init() {
        super.init();


        consume(new ConsumePowerDynamic(powerProduction, build -> {
            if (!(build instanceof OutletBuild o)) return 0f;
            if (o.front() == null || o.front().power == null || o.front().team != o.team) return 0f;
            if(!o.front().shouldConsume()) return 0;
            return Math.min(o.need, powerProduction);
        }));
    }
    @Override
    public TextureRegion[] icons(){
        return drawer.finalIcons(this);
    }
    @Override
    public void load(){
        super.load();
        sideRegion1 = Core.atlas.find(name + "-top1");
        sideRegion2 = Core.atlas.find(name + "-top2");
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
        super.setBars();
        removeBar("power");
    }
    public class OutletBuild extends GeneratorBuild {
        public float need = 0;

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
            if(front() == null || !(front().block.findConsumer(f -> f instanceof ConsumePower) instanceof ConsumePower)){
                need = 0;
                productionEfficiency = 0;
                return;
            }
            ConsumePower frontConsume =  front().block.findConsumer(f -> f instanceof ConsumePower);
            //Remove consumption from target graph.
            PowerGraph front = this.front().power.graph;
            if(front.consumers.contains(this)){
                front.consumers.remove(this);
            }
            //Add production to current graph
            if(front.producers.contains(this)){
                    if (front().power.status <= 0) {
                        need = Math.min(frontConsume.usage, powerProduction);
                    } else {
                        need = Math.min(frontConsume.usage / front().power.status, powerProduction);
                    }
            } else {
                front.producers.add(this);
            }
        }

        @Override
        public float getPowerProduction(){
            if(!enabled) return 0f;
            boolean NoBitches = false;
            for(Building b : this.power.graph.producers){
                if(b.block != this.block){
                    NoBitches = true;
                    break;
                }
            }
            if(!NoBitches){
                return 0f;
            }
            if(front() == null || !(front().block.findConsumer(f -> f instanceof ConsumePower) instanceof ConsumePower)){
                need = 0;
                productionEfficiency = 0;
                return 0;
            }
            return need  * this.power.status;
        }
        @Override
        public void onProximityRemoved(){
            super.onProximityRemoved();
            if(power != null){
                powerGraphRemoved();
            }
            if(front() == null || front().power == null || this.team != front().team) return;

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