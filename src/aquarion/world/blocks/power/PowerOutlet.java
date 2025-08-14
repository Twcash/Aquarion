package aquarion.world.blocks.power;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Geometry;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.Nullable;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
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
            float totalNeeded = 0f;
            Seq<Building> fronts = o.getFrontBuildings();
            for (Building frontBuild : fronts) {
                if (frontBuild.power == null || frontBuild.team != o.team) continue;
                ConsumePower cp = frontBuild.block.findConsumer(f -> f instanceof ConsumePower);
                if (cp == null || !frontBuild.shouldConsume()) continue;
                totalNeeded += Math.min(o.need, powerProduction);
            }
            return Math.min(totalNeeded, powerProduction);
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
            Seq<Building> fronts = getFrontBuildings();
            //Remove production from current graph.
            if (this.power.graph.producers.contains(this)) {
                this.power.graph.producers.remove(this);
            }
            //Add consumption to current graph.
            if (!this.power.graph.consumers.contains(this)) {
                this.power.graph.consumers.add(this);
            }
            for (Building frontBuild : fronts) {
            //Stop if front doesn't exist or has power
            if (frontBuild == null || !(frontBuild.block.findConsumer(f -> f instanceof ConsumePower) instanceof ConsumePower)) {
                need = 0;
                productionEfficiency = 0;
                return;
            }
            ConsumePower frontConsume = frontBuild.block.findConsumer(f -> f instanceof ConsumePower);
            //Remove consumption from target graph.
            PowerGraph front = frontBuild.power.graph;
            if (front.consumers.contains(this)) {
                front.consumers.remove(this);
            }
            //Add production to current graph
            if (front.producers.contains(this)) {
                if (frontBuild.power.status <= 0) {
                    need = Math.min(frontConsume.usage, powerProduction);
                } else {
                    need = Math.min(frontConsume.usage / frontBuild.power.status, powerProduction);
                }
            } else {
                front.producers.add(this);
            }
            }
        }

        @Override
        public float getPowerProduction() {
            if (!enabled) return 0f;
            Seq<Building> fronts = getFrontBuildings();
            boolean NoBitches = false;
            if (fronts.size > 0) {
                for (Building frontBuild : fronts) {
                    for (Building b : this.power.graph.producers) {
                        if (b.block != this.block) {
                            NoBitches = true;
                            break;
                        }
                    }
                    if (!NoBitches) {
                        return 0f;
                    }
                    if (frontBuild == null || !(frontBuild.block.findConsumer(f -> f instanceof ConsumePower) instanceof ConsumePower)) {
                        need = 0;
                        productionEfficiency = 0;
                        return 0;
                    }

                }
                return need * this.power.status / fronts.size;
            } else {
                return 0;
            }
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
        public void draw() {
            drawer.draw(this);
        }
        public Seq<Building> frontEdges() {
            Seq<Building> buildings = new Seq<>();
            int size = block.size;

            // Get direction vector (front direction)
            int dirX = Geometry.d4(rotation).x;
            int dirY = Geometry.d4(rotation).y;

            // Calculate perpendicular vector to front direction
            int perpX = -dirY;
            int perpY = dirX;

            int centerOffset = size / 2;

            for (int i = 0; i < size; i++) {
                int offsetAlongPerp = i - centerOffset + (size % 2 == 0 ? 1 : 0);

                int offsetX = dirX * (size / 2 + 1) + perpX * offsetAlongPerp;
                int offsetY = dirY * (size / 2 + 1) + perpY * offsetAlongPerp;

                Building b = nearby(offsetX, offsetY);
                if (b != null && !buildings.contains(b)) {
                    buildings.add(b);
                }
            }

            return buildings;
        }
        @Override
        public @Nullable Building front() {
            int size = block.size;
            int dirX = Geometry.d4(rotation).x;
            int dirY = Geometry.d4(rotation).y;

            int frontOffset = size / 2 + 1;

            return nearby(dirX * frontOffset, dirY * frontOffset);
        }
        public Seq<Building> getFrontBuildings() {
            Seq<Building> buildings = new Seq<>();
            int size = block.size;

            // front direction vector
            int dirX = Geometry.d4(rotation).x;
            int dirY = Geometry.d4(rotation).y;

            // perpendicular vector to front
            int perpX = -dirY;
            int perpY = dirX;

            int frontOffset = size / 2 + 1; // distance to front edge tiles

            int centerOffset = size / 2;

            for (int i = 0; i < size; i++) {
                int offsetAlongPerp = i - centerOffset + (size % 2 == 0 ? 1 : 0);

                int offsetX = dirX * frontOffset + perpX * offsetAlongPerp;
                int offsetY = dirY * frontOffset + perpY * offsetAlongPerp;

                Building b = nearby(offsetX, offsetY);
                if (b != null && !buildings.contains(b)) {
                    buildings.add(b);
                }
            }

            return buildings;
        }

    }
}