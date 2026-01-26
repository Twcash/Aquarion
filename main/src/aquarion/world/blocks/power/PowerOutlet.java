package aquarion.world.blocks.power;

import aquarion.world.drawers.SwitchRegion;
import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Geometry;
import arc.struct.EnumSet;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.Nullable;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.BuildTurret;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.blocks.power.PowerGenerator;
import mindustry.world.blocks.power.PowerGraph;
import mindustry.world.consumers.ConsumePower;
import mindustry.world.consumers.ConsumePowerDynamic;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.draw.DrawMulti;
import mindustry.world.draw.DrawSideRegion;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.BlockStatus;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;

public class PowerOutlet extends PowerGenerator {
    public DrawBlock drawer = new DrawMulti( new DrawDefault(), new DrawSideRegion(), new SwitchRegion());
    public TextureRegion sideRegion2, sideRegion1, onRegion;

    public PowerOutlet(String name) {
        super(name);
        outputsPower = true;
        hasPower = true;
        flags = EnumSet.of(BlockFlag.battery);
        insulated = true;
        consumesPower = true;
        powerProduction = 100/60f;
        rotate = true;
        rotateDraw = true;
        configurable = true;
        autoResetEnabled = false;
        config(Boolean.class, (OutletBuild entity, Boolean b) -> entity.enabled = b);
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
            if (!o.enabled) return 0;
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
        onRegion = Core.atlas.find(name + "-switch");
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
        public float lastRotation = 0;
        public @Nullable Building lastFront;

        @Override
        public void updateTile() {
            lastRotation = this.rotation;
            //Remove production from current graph.
            if (this.power.graph.producers.contains(this)) {
                this.power.graph.producers.remove(this);
            }
            //Add consumption to current graph.
            if (!this.power.graph.consumers.contains(this)) {
                this.power.graph.consumers.add(this);
            }
            Building frontBuild = front();
            if (!(frontBuild instanceof OutletBuild || frontBuild instanceof PowerPylon.PowerPylonBuild)) {
                if (frontBuild == null || !(frontBuild.block.findConsumer(f -> f instanceof ConsumePower) instanceof ConsumePower)) {
                    need = 0;
                    productionEfficiency = 0;
                    return;
                }
                ConsumePower frontConsume = frontBuild.block.findConsumer(f -> f instanceof ConsumePower);
                //Remove consumption from target graph.
                if (lastFront != null && lastFront == frontBuild) {
                    PowerGraph front = frontBuild.power.graph;
                    if (front.consumers.contains(this)) {
                        front.consumers.remove(this);
                    }
                    //Add production to current graph
                    if (front.producers.contains(this)) {
                        BlockStatus status = front().status();
                        //TBH I could have just used an if/else but this was more fun
                        switch (status) {
                            case active, noInput:
                                need = Math.min(frontConsume.usage, powerProduction);
                                break;
                            case logicDisable:
                                need = 0;
                                break;
                            case noOutput:
                                need = 0;
                                break;
                        }
                        if (frontBuild.shouldConsume()) {
                            need = Math.min(frontConsume.usage, powerProduction);
                        } else {
                            //0.1f because if the turret had 0 power it would never try and target anything
                            need = 0.1f;
                        }
                        if (!frontBuild.shouldConsumePower && !(frontBuild instanceof Turret.TurretBuild)) {
                            need = 0;
                        }
                    } else {
                        front.producers.add(this);
                    }
                } else if (lastFront != null) {
                    PowerGraph last = lastFront.power.graph;
                    if (last.consumers.contains(this)) {
                        last.consumers.remove(this);
                    }
                    if (last.producers.contains(this)) {
                        last.producers.remove(this);
                    }
                }
                lastFront = frontBuild;
            }
        }

        @Override
        public float getPowerProduction() {
            if (!enabled || need <= 0) return 0f;

            return Math.min(need, powerProduction) * power.status;
        }

        @Override
        public void onProximityRemoved() {
            super.onProximityRemoved();
            if (power != null) {
                powerGraphRemoved();
            }
            if (front() == null || front().power == null || this.team != front().team) return;

            PowerGraph front = this.front().power.graph;
            if (front.producers.contains(this)) {
                front.producers.remove(this);
            }
        }

        @Override
        public boolean configTapped() {
            configure(!enabled);
            Sounds.click.at(this);
            return false;
        }

        @Override
        public void draw() {
            drawer.draw(this);
            if (enabled) Draw.rect(onRegion, x, y);

        }

        @Override
        public Boolean config() {
            return enabled;
        }
    }
}
