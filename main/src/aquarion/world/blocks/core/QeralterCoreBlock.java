package aquarion.world.blocks.core;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Interp;
import arc.math.Mathf;
import arc.scene.actions.Actions;
import arc.scene.event.Touchable;
import arc.scene.ui.Image;
import arc.struct.EnumSet;
import arc.struct.IntSeq;
import arc.struct.Queue;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.core.UI;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.Effect;
import mindustry.entities.TargetPriority;
import mindustry.entities.Units;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Team;
import mindustry.game.Teams;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.ui.Bar;
import mindustry.world.Build;
import mindustry.world.blocks.ConstructBlock;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.Env;
import mindustry.world.modules.ItemModule;

import static aquarion.content.AquaUnitTypes.iris;
import static mindustry.Vars.*;

//This core makes mining drones and has a build tower for initial construction
//FYI this is a hell of a hackjob I pulled. I reccomend not using this code whatsoever
//broken. Sucks. Why did I even... Kill yourself
public class QeralterCoreBlock extends CoreBlock {
    public final int timerTarget = timers++, timerTarget2 = timers++;
    public int targetInterval = 15;

    //Core build turret stuffs
    public float buildSpeed = 1f;
    public float buildBeamOffset = 5f;
    public float rotateSpeed = 5;
    public float range = 350;
    public float elevation = -1f;
    public Color heatColor = Pal.accent.cpy().a(0.9f);
    public TextureRegion glowRegion;
    public TextureRegion baseRegion;

    //amount of mining drones this core makes
    public int DroneCount = 4;
    public float droneConstructTime = 60f * 3f;
    public @Nullable UnitType droneType;
    public @Nullable UnitType turretType;
    protected static final float cloudScaling = 1700f, cfinScl = -2f, cfinOffset = 0.3f, calphaFinOffset = 0.25f, cloudAlpha = 0.81f;
    protected static final float[] cloudAlphas = {0, 0.5f, 1f, 0.1f, 0, 0f};

    //hacky way to pass item modules between methods
    private static ItemModule nextItems;
    protected static final float[] thrusterSizes = {0f, 0f, 0f, 0f, 0.3f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 0f};

    public float thrusterLength = 14f/4f;

    public float landDuration = 160f;
    public Effect launchEffect = Fx.launch;

    public QeralterCoreBlock(String name) {
        super(name);
        solid = true;
        update = true;
        hasItems = true;
        incinerateNonBuildable = true;
        priority = TargetPriority.core;
        flags = EnumSet.of(BlockFlag.core);
        //qeralter will have a custom way of dealing with unit caps
        unitCapModifier = 0;
        drawDisabled = false;
        unitType = iris;
        canOverdrive = false;
        envEnabled |= Env.space;
        replaceable = false;
        sync = false;
        rotateSpeed = 10f;
        suppressable = true;
    }
    @Override
    public void setBars(){
        super.setBars();

        addBar("capacity", (CoreBuild e) -> new Bar(
                () -> Core.bundle.format("bar.capacity", UI.formatAmount(e.storageCapacity)),
                () -> Pal.items,
                () -> e.items.total() / ((float)e.storageCapacity * content.items().count(UnlockableContent::unlockedNow))
        ));
    }
    @Override
    public void load() {
        super.load();
        baseRegion = Core.atlas.find(name + "-turret");
        glowRegion = Core.atlas.find(name + "-glow");
    }
    @Override
    public void init(){
        super.init();

        if(elevation < 0) elevation = size / 2f;

        //this is super hacky, but since blocks are initialized before units it does not run into init/concurrent modification issues
        turretType = new UnitType("turret-unit-" + name){{
            hidden = true;
            internal = true;
            speed = 0f;
            hitSize = 0f;
            health = 1;
            itemCapacity = 0;
            rotateSpeed = QeralterCoreBlock.this.rotateSpeed;
            buildBeamOffset = QeralterCoreBlock.this.buildBeamOffset;
            buildRange = QeralterCoreBlock.this.range;
            buildSpeed = QeralterCoreBlock.this.buildSpeed;
            constructor = BlockUnitUnit::create;
        }};
    }

    public class QeralterCoreBuild extends CoreBuild {
        public BlockUnitc unit = (BlockUnitc)turretType.create(team);
        public @Nullable Unit following;
        public @Nullable Teams.BlockPlan lastPlan;
        public float warmup;
        public int storageCapacity;
        public boolean noEffect = false;
        public Team lastDamage = Team.derelict;
        public float iframes = -1f;
        public float thrusterTime = 0f;
        protected IntSeq readUnits = new IntSeq();
        protected int readTarget = -1;
        float rot = 0;

        public Seq<Unit> units = new Seq<>();
        public @Nullable Unit target;
        public float droneProgress, droneWarmup, totalDroneProgress;

        protected float cloudSeed;

        {
            unit.rotation(90f);
        }
        public boolean canControl(){
            return true;
        }

        public float buildRotation(){
            return unit.rotation();
        }
        @Override
        public void updateTile(){
            unit.tile(this);
            unit.team(team);

            //only cares about where the unit itself is looking
            float rot = unit.rotation();

            if(unit.activelyBuilding()){
                unit.lookAt(angleTo(unit.buildPlan()));
            }

            if(checkSuppression()){
                efficiency = potentialEfficiency = 0f;
            }

            unit.buildSpeedMultiplier(potentialEfficiency * timeScale);
            unit.speedMultiplier(potentialEfficiency * timeScale);

            warmup = Mathf.lerpDelta(warmup, unit.activelyBuilding() ? efficiency : 0f, 0.1f);
            int e = 1; //oh what the fuck is this hackjob
            if(e > 0){
                unit.updateBuilding(true);

                if(following != null){
                    //validate follower
                    if(!following.isValid() || !following.activelyBuilding()){
                        following = null;
                        unit.plans().clear();
                    }else{
                        //set to follower's first build plan, whatever that is
                        unit.plans().clear();
                        unit.plans().addFirst(following.buildPlan());
                        lastPlan = null;
                    }

                }else if(unit.buildPlan() == null && timer(timerTarget, targetInterval)){ //search for new stuff
                    Queue<Teams.BlockPlan> blocks = team.data().plans;
                    for(int i = 0; i < blocks.size; i++){
                        var block = blocks.get(i);
                        if(within(block.x * tilesize, block.y * tilesize, range)){
                            var btype = block.block;

                            if(Build.validPlace(btype, unit.team(), block.x, block.y, block.rotation) && (state.rules.infiniteResources || team.rules().infiniteResources || team.items().has(btype.requirements, state.rules.buildCostMultiplier))){
                                unit.addBuild(new BuildPlan(block.x, block.y, block.rotation, block.block, block.config));
                                //shift build plan to tail so next unit builds something else
                                blocks.addLast(blocks.removeIndex(i));
                                lastPlan = block;
                                break;
                            }
                        }
                    }

                    //still not building, find someone to mimic
                    if(unit.buildPlan() == null){
                        following = null;
                        Units.nearby(team, x, y, range, u -> {
                            if(following  != null) return;

                            if(u.canBuild() && u.activelyBuilding()){
                                BuildPlan plan = u.buildPlan();

                                Building build = world.build(plan.x, plan.y);
                                if(build instanceof ConstructBlock.ConstructBuild && within(build, range)){
                                    following = u;
                                }
                            }
                        });
                    }
                }else if(unit.buildPlan() != null){ //validate building
                    BuildPlan req = unit.buildPlan();

                    //clear break plan if another player is breaking something
                    if(!req.breaking && timer.get(timerTarget2, 30f)){
                        for(Player player : team.data().players){
                            if(player.isBuilder() && player.unit().activelyBuilding() && player.unit().buildPlan().samePos(req) && player.unit().buildPlan().breaking){
                                unit.plans().removeFirst();
                                //remove from list of plans
                                team.data().plans.remove(p -> p.x == req.x && p.y == req.y);
                                return;
                            }
                        }
                    }

                    boolean valid =
                            !(lastPlan != null && lastPlan.removed) &&
                                    ((req.tile() != null && req.tile().build instanceof ConstructBlock.ConstructBuild cons && cons.current == req.block) ||
                                            (req.breaking ?
                                                    Build.validBreak(unit.team(), req.x, req.y) :
                                                    Build.validPlace(req.block, unit.team(), req.x, req.y, req.rotation)));

                    if(!valid){
                        //discard invalid request
                        unit.plans().removeFirst();
                        lastPlan = null;
                    }
                }
            }else{ //is being controlled, forget everything
                following = null;
                lastPlan = null;
            }

            //please do not commit suicide
            unit.plans().remove(b -> b.build() == this);

            unit.updateBuildLogic();
            if(!readUnits.isEmpty()){
                if(!readUnits.isEmpty()){
                    units.clear();
                    readUnits.each(i -> {
                        var unit = Groups.unit.getByID(i);
                        if(unit != null){
                            units.add(unit);
                        }
                    });
                    readUnits.clear();
                }

                units.removeAll(u -> !u.isAdded() || u.dead);

                droneWarmup = Mathf.lerpDelta(droneWarmup, units.size < DroneCount ? efficiency : 0f, 0.1f);
                totalDroneProgress += droneWarmup * Time.delta;

                if(readTarget != 0){
                    target = Groups.unit.getByID(readTarget);
                    readTarget = 0;
                }

                //TODO better effects?
                if(units.size < DroneCount && (droneProgress += edelta() / droneConstructTime) >= 1f){
                    var unit = droneType.create(team);
                    if(unit instanceof BuildingTetherc bt){
                        bt.building(this);
                    }
                    unit.set(x, y);
                    unit.rotation = 90f;
                    unit.add();

                    Fx.spawn.at(unit);
                    units.add(unit);
                    droneProgress = 0f;
                }

                if(target != null && !target.isValid()){
                    target = null;
                }

                //TODO no autotarget, bad
                if(target == null){
                    target = Units.closest(team, x, y, u -> !u.spawnedByCore && u.type != droneType);
                }
                readUnits.clear();
                iframes -= Time.delta;
                thrusterTime -= Time.delta/90f;
            }

            units.removeAll(u -> !u.isAdded() || u.dead);

            droneWarmup = Mathf.lerpDelta(droneWarmup, units.size < DroneCount ? efficiency : 0f, 0.1f);
            totalDroneProgress += droneWarmup * Time.delta;

            if(readTarget != 0){
                target = Groups.unit.getByID(readTarget);
                readTarget = 0;
            }

            //TODO better effects?
            if(units.size < DroneCount && (droneProgress += edelta() / droneConstructTime) >= 1f){
                var unit = droneType.create(team);
                if(unit instanceof BuildingTetherc bt){
                    bt.building(this);
                }
                unit.set(x, y);
                unit.rotation = 90f;
                unit.add();

                Fx.spawn.at(unit);
                units.add(unit);
                droneProgress = 0f;
            }

            if(following != null){
                //validate follower
                if(!following.isValid() || !following.activelyBuilding()){
                    following = null;
                    unit.plans().clear();
                }else{
                    //set to follower's first build plan, whatever that is
                    unit.plans().clear();
                    unit.plans().addFirst(following.buildPlan());
                    lastPlan = null;
                }

            }else if(unit.buildPlan() == null && timer(timerTarget, targetInterval)){ //search for new stuff
                Queue<Teams.BlockPlan> blocks = team.data().plans;
                for(int i = 0; i < blocks.size; i++){
                    var block = blocks.get(i);
                    if(within(block.x * tilesize, block.y * tilesize, range)){
                        var btype = block.block;

                        if(Build.validPlace(btype, unit.team(), block.x, block.y, block.rotation) && (state.rules.infiniteResources || team.rules().infiniteResources || team.items().has(btype.requirements, state.rules.buildCostMultiplier))){
                            unit.addBuild(new BuildPlan(block.x, block.y, block.rotation, block.block, block.config));
                            //shift build plan to tail so next unit builds something else
                            blocks.addLast(blocks.removeIndex(i));
                            lastPlan = block;
                            break;
                        }
                    }
                }

                //still not building, find someone to mimic
                if(unit.buildPlan() == null){
                    following = null;
                    Units.nearby(team, x, y, range, u -> {
                        if(following  != null) return;

                        if(u.canBuild() && u.activelyBuilding()){
                            BuildPlan plan = u.buildPlan();

                            Building build = world.build(plan.x, plan.y);
                            if(build instanceof ConstructBlock.ConstructBuild && within(build, range)){
                                following = u;
                            }
                        }
                    });
                }
            }else if(unit.buildPlan() != null){ //validate building
                BuildPlan req = unit.buildPlan();

                //clear break plan if another player is breaking something
                if(!req.breaking && timer.get(timerTarget2, 30f)){
                    for(Player player : team.data().players){
                        if(player.isBuilder() && player.unit().activelyBuilding() && player.unit().buildPlan().samePos(req) && player.unit().buildPlan().breaking){
                            unit.plans().removeFirst();
                            //remove from list of plans
                            team.data().plans.remove(p -> p.x == req.x && p.y == req.y);
                            return;
                        }
                    }
                }

                boolean valid =
                        !(lastPlan != null && lastPlan.removed) &&
                                ((req.tile() != null && req.tile().build instanceof ConstructBlock.ConstructBuild cons && cons.current == req.block) ||
                                        (req.breaking ?
                                                Build.validBreak(unit.team(), req.x, req.y) :
                                                Build.validPlace(req.block, unit.team(), req.x, req.y, req.rotation)));

                if(!valid){
                    //discard invalid request
                    unit.plans().removeFirst();
                    lastPlan = null;
                }
        }else{ //is being controlled, forget everything
            following = null;
            lastPlan = null;
        }

        //please do not commit suicide
            unit.plans().remove(b -> b.build() == this);

            unit.updateBuildLogic();
        }

        public Unit unit(){
            //make sure stats are correct
            unit.tile(this);
            unit.team(team);
            return (Unit)unit;
        }
        @Override
        public void draw(){
            super.draw();

            Draw.color();

            Draw.z(Layer.turret);
            Drawf.shadow(baseRegion, x - elevation, y - elevation, rot - 90);
            Draw.rect(baseRegion, x, y, rotation - 90);

            if(glowRegion.found()){
                Drawf.additive(glowRegion, heatColor, warmup, x, y, rot - 90f, Layer.turretHeat);
            }

            if(efficiency > 0){
                unit.drawBuilding();
            }
            Draw.z(Layer.block);
            //draw thrusters when just landed
            if(thrusterTime > 0){
                float frame = thrusterTime;

                Draw.alpha(1f);
                drawThrusters(frame);
                Draw.rect(block.region, x, y);
                Draw.alpha(Interp.pow4In.apply(frame));
                drawThrusters(frame);
                Draw.reset();

                drawTeamTop();
            }else{
                super.draw();
            }
        }

        // `launchType` is null if it's landing instead of launching.
        public void beginLaunch(@Nullable CoreBlock launchType){
            cloudSeed = Mathf.random(1f);
            if(launchType != null){
                Fx.coreLaunchConstruct.at(x, y, launchType.size);
            }

            if(!headless){
                // Add fade-in and fade-out foreground when landing or launching.
                if(renderer.isLaunching()){
                    float margin = 30f;

                    Image image = new Image();
                    image.color.a = 0f;
                    image.touchable = Touchable.disabled;
                    image.setFillParent(true);
                    image.actions(Actions.delay((landDuration() - margin) / 60f), Actions.fadeIn(margin / 60f, Interp.pow2In), Actions.delay(6f / 60f), Actions.remove());
                    image.update(() -> {
                        image.toFront();
                        ui.loadfrag.toFront();
                        if(state.isMenu()){
                            image.remove();
                        }
                    });
                    Core.scene.add(image);
                }else{
                    Image image = new Image();
                    image.color.a = 1f;
                    image.touchable = Touchable.disabled;
                    image.setFillParent(true);
                    image.actions(Actions.fadeOut(35f / 60f), Actions.remove());
                    image.update(() -> {
                        image.toFront();
                        ui.loadfrag.toFront();
                        if(state.isMenu()){
                            image.remove();
                        }
                    });
                    Core.scene.add(image);

                    Time.run(landDuration(), () -> {
                        launchEffect.at(this);
                        Effect.shake(5f, 5f, this);
                        thrusterTime = 1f;

                        if(state.isCampaign() && Vars.showSectorLandInfo && (state.rules.sector.preset == null || state.rules.sector.preset.showSectorLandInfo)){
                            ui.announce("[accent]" + state.rules.sector.name() + "\n" +
                                    (state.rules.sector.info.resources.any() ? "[lightgray]" + Core.bundle.get("sectors.resources") + "[white] " +
                                            state.rules.sector.info.resources.toString(" ", UnlockableContent::emoji) : ""), 5);
                        }
                    });
                }
            }
        }
        public float landDuration(){
            return landDuration;
        }
        @Override
        public float warmup(){
            return warmup;
        }
        @Override
        public void write(Writes write){
            super.write(write);

            write.i(target == null ? -1 : target.id);

            write.s(units.size);
            for(var unit : units){
                write.i(unit.id);
            }
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            readTarget = read.i();

            int count = read.s();
            readUnits.clear();
            for(int i = 0; i < count; i++){
                readUnits.add(read.i());
            }
        }
    }
}
