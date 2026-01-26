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
            if (!(build instanceof OutletBuild o))return 0f;
                    if(!o.enabled) return 0;
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
        public Seq<Building> fronts = new Seq<>();
        public @Nullable Building lastFront;
        @Override
        public void updateTile() {
            lastRotation = this.rotation;
            Tile tile = this.tile;
            Tile[] neighbors = new Tile[4];

            neighbors[0] = tile.nearby(0, 1);
            neighbors[1] = tile.nearby(1, 0);
            neighbors[2] = tile.nearby(0, -1);
            neighbors[3] = tile.nearby(-1, 0);
            fronts = getFrontBuildings();
            for(Tile neighbor : neighbors){
                if(neighbor.build != null && neighbor.build.power != null) {
                    if (neighbor.build.power.graph.producers.contains(this) && !(fronts.contains(neighbor.build))) {
                        neighbor.build.power.graph.producers.remove(this);
                    } else if((fronts.contains(neighbor.build))){
                        fronts.addUnique(neighbor.build);
                    }
                }
            }
            //Remove production from current graph.
            if (this.power.graph.producers.contains(this)) {
                this.power.graph.producers.remove(this);
            }
            //Add consumption to current graph.
            if (!this.power.graph.consumers.contains(this)) {
                this.power.graph.consumers.add(this);
            }
            for (Building frontBuild : fronts) {
            //Stop if front doesn't exist or has no power
                if (frontBuild instanceof OutletBuild || frontBuild instanceof PowerPylon.PowerPylonBuild) continue;
                if (frontBuild == null || !(frontBuild.block.findConsumer(f -> f instanceof ConsumePower) instanceof ConsumePower)) {
                    need = 0;
                    productionEfficiency = 0;
                    return;
                }
            ConsumePower frontConsume = frontBuild.block.findConsumer(f -> f instanceof ConsumePower);
            //Remove consumption from target graph.
                if(lastFront != null && lastFront == frontBuild) {
                    PowerGraph front = frontBuild.power.graph;
                    if (front.consumers.contains(this)) {
                        front.consumers.remove(this);
                    }
                    //Add production to current graph
                    if (front.producers.contains(this)) {
                            BlockStatus status = front().status();
                            //TBH I could have just used an if/else but this was more fun
                            switch (status) {
                                case active:
                                    need = Math.min(frontConsume.usage, powerProduction);
                                    break;
                                case logicDisable:
                                    need = 0;
                                    break;
                                case noOutput:
                                    need = 0;
                                    break;
                                case noInput:
                                    need = Math.min(frontConsume.usage, powerProduction);
                                    break;

                                    
                            }
                            if(( front().shouldConsumePower || front().shouldConsume())){ need = Math.min(frontConsume.usage, powerProduction);} else need = 0f;
                    } else {
                        front.producers.add(this);
                    }
                } else if(lastFront != null) {
                    //Hope this works (It doesn't)
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
        public float getPowerProduction(){
            if(!enabled || need <= 0) return 0f;

            return Math.min(need, powerProduction) * power.status;
        }
        @Override
        public void onProximityAdded() {
            super.onProximityAdded();
            fronts.clear();
            fronts.set(getFrontBuildings());
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
        public boolean configTapped(){
            configure(!enabled);
            Sounds.click.at(this);
            return false;
        }
        @Override
        public void draw() {
            drawer.draw(this);
            if(enabled)             Draw.rect(onRegion, x, y);

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
        @Override
        public Boolean config(){
            return enabled;
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
