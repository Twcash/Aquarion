package aquarion.world.blocks.units;

import aquarion.world.AI.RailCommandAI;
import aquarion.world.blocks.distribution.RailPoint;
import aquarion.world.units.RailPathfinder;
import aquarion.world.units.RailTraffic;
import aquarion.world.units.RailConsist;
import aquarion.units.type.RailUnitType;
import aquarion.world.units.RailLiquidCargo;
import arc.Core;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import mindustry.graphics.Drawf;
import mindustry.world.blocks.ItemSelection;
import arc.struct.Seq;
import arc.scene.ui.layout.Table;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.entities.Units;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.type.ItemStack;
import mindustry.type.UnitType;

import static mindustry.Vars.*;

/** Produces rail units, or assembles an ordered train of the configured length. */
public class RailWorkshop extends RailPoint{
    public final boolean coupling;
    public UnitType inputUnit, outputUnit;
    public Seq<UnitType> unitChoices = new Seq<>();
    public float buildTime = 600f;
    public ItemStack[] recipe = {};

    public RailWorkshop(String name, boolean coupling){
        super(name);
        this.coupling = coupling;
        size = 3;
        range = 120f;
        rotateDraw = false;
        canOverdrive = false;
        hasItems = !coupling;
        itemCapacity = 120;
        consumePower(2f);
        buildType = RailWorkshopBuild::new;
        saveConfig = true;
        config(UnitType.class, (RailWorkshopBuild build, UnitType unit) -> {
            if(!coupling && unitChoices.contains(unit, true) && build.producedUnit() != unit){
                build.selectedUnit = unit;
                build.progress = 0f;
            }
        });
        config(Long.class, (RailWorkshopBuild build, Long count) -> build.trainLength = (int)Math.max(2L, Math.min(Integer.MAX_VALUE, count)));
    }

    @Override
    public void init(){
        if(!coupling && recipe.length > 0) consumeItems(recipe);
        super.init();
    }

    @Override
    public void load(){
        super.load();
        region = Core.atlas.find(name, Core.atlas.find(coupling ? "additive-reconstructor" : "ground-factory"));
    }

    public class RailWorkshopBuild extends RailPointBuild{
        public float progress;
        public UnitType selectedUnit;
        private float constructWarmup, constructTime;
        public UnitType producedUnit(){ return selectedUnit == null ? outputUnit : selectedUnit; }
        public int trainLength = 2;

        @Override
        public void buildConfiguration(Table table){
            if(!coupling){
                if(unitChoices.any()) ItemSelection.buildTable(RailWorkshop.this, table, unitChoices, this::producedUnit, this::configure);
                else super.buildConfiguration(table);
                return;
            }
            table.add(Core.bundle.get("rail.train-length", "Train length") + ": ");
            table.field(Integer.toString(trainLength), text -> {
                try{ long count = Long.parseLong(text); if(count >= 2 && count <= Integer.MAX_VALUE) configure(count); }
                catch(NumberFormatException ignored){}
            }).width(100f);
        }

        @Override
        public Object config(){ return coupling ? Long.valueOf(trainLength) : unitChoices.any() ? producedUnit() : super.config(); }
        private Unit first, second, heldTrain, releasedTrain;
        private int assemblyLength;

        private int goal(Unit head){
            return heldTrain == head ? assemblyLength : Math.max(trainLength, RailConsist.members(head).size + 1);
        }

        private void capture(Unit head){
            if(heldTrain == head) return;
            Unit tail = RailConsist.partner(head);
            RailPointBuild outlet = exit();
            if(tail != null && outlet != null && head.controller() instanceof RailCommandAI ai
                && ai.reverseConsistFor(RailPathfinder.edge(this, outlet))){
                head = RailConsist.leader(head);
            }
            heldTrain = head;
            float length = stagingLength(head);
            if(head.controller() instanceof RailCommandAI ai) ai.stageCoupling(this, length, stagingExit(length));
            assemblyLength = Math.max(trainLength, RailConsist.members(head).size + 1);
        }

        private boolean eligible(Unit candidate){
            return !candidate.dead && candidate.team == team && candidate.type instanceof RailUnitType
                && RailConsist.leader(candidate) == candidate
                && candidate.controller() instanceof RailCommandAI ai && ai.supportingRail() != null;
        }

        public boolean holding(Unit candidate){
            return coupling && enabled && heldTrain == candidate && (releasedTrain == null || !RailConsist.same(candidate, releasedTrain));
        }

        private float stagingLength(Unit head){
            Unit tail = RailConsist.partner(head);
            if(tail == null) tail = head;
            return RailConsist.offset(tail) + tail.hitSize / 2f + 1.5f;
        }

        /** Follow the outlet far enough to bring the tail's rear coupler onto the pad. */
        private RailPointBuild stagingExit(float length){
            return forwardExit(length, false);
        }

        private RailPointBuild forwardExit(float length, boolean seekCoupler){
            RailPointBuild previous = this, current = exit();
            if(current == null) return null;
            Seq<RailPointBuild> visited = new Seq<>();
            visited.add(this);
            float travelled = 0f;
            while(current != null){
                RailPathfinder.Edge segment = RailPathfinder.edge(previous, current);
                if(segment == null) return seekCoupler ? null : previous;
                travelled += segment.length;
                if(seekCoupler){
                    if(current instanceof RailWorkshopBuild workshop && workshop != this && ((RailWorkshop)workshop.block).coupling && workshop.enabled) return current;
                }else if(travelled >= length) return current;
                visited.add(current);
                RailPointBuild next = null;
                float best = -Float.MAX_VALUE;
                float dx = current.x - previous.x, dy = current.y - previous.y;
                for(int i = 0; i < current.links.size; i++){
                    Building other = world.build(current.links.get(i));
                    if(!(other instanceof RailPointBuild rail) || visited.contains(rail, true)
                        || RailPathfinder.edge(current, rail) == null) continue;
                    float alignment = ((rail.x - current.x) * dx + (rail.y - current.y) * dy) / current.dst(rail);
                    if(alignment > best){ best = alignment; next = rail; }
                }
                if(next == null) return seekCoupler ? null : current;
                previous = current;
                current = next;
            }
            return seekCoupler ? null : previous;
        }

        public boolean shouldHold(Unit candidate){
            if(candidate.controller() instanceof RailCommandAI ai && ai.couplingElsewhere(this)) return false;
            if(!coupling || !enabled || !eligible(candidate) || (releasedTrain != null && RailConsist.same(candidate, releasedTrain))) return false;
            if(heldTrain == candidate || candidate.within(this, 0.05f)){
                capture(candidate);
                if(heldTrain != candidate) return true;
                float length = stagingLength(candidate);
                RailPointBuild destination = stagingExit(length);
                return destination == null || ((RailCommandAI)candidate.controller()).stageCoupling(this, length, destination);
            }
            for(Unit other : Groups.unit){
                if(!RailConsist.same(other, candidate) && eligible(other) && holding(other)
                    && (releasedTrain == null || !RailConsist.same(other, releasedTrain))){
                    Unit tail = RailConsist.partner(other);
                    if(tail == null) tail = other;
                    if(candidate.within(tail, RailTraffic.clearance(candidate, tail) + 2f)) return true;
                }
            }
            return false;
        }

        public boolean compatible(Unit a, Unit b){
            return !RailConsist.same(a, b) && eligible(a) && eligible(b)
                && within(b, size * tilesize / 2f + b.hitSize / 2f)
                && b.within(RailConsist.partner(a) == null ? a : RailConsist.partner(a), RailTraffic.clearance(a, b) + 3f)
                && a.stack.amount + b.stack.amount <= a.type.itemCapacity + b.type.itemCapacity;
        }

        public boolean canReceive(Unit incoming, Unit held){
            Unit head = RailConsist.leader(held);
            return coupling && enabled && !RailConsist.same(incoming, head) && incoming.team == team && head.team == team
                && incoming.type instanceof RailUnitType && holding(head);
        }
        private boolean findPair(){
            first = second = null;
            if(heldTrain != null && eligible(heldTrain)) first = heldTrain;
            if(first == null) for(Unit candidate : Groups.unit){
                if(eligible(candidate) && (releasedTrain == null || !RailConsist.same(candidate, releasedTrain)) && candidate.within(this, 1f) && (first == null || candidate.id < first.id)) first = candidate;
            }
            if(first == null) return false;
            capture(first);
            first = heldTrain;
            if(!shouldHold(first)) return false;
            for(Unit candidate : Groups.unit){
                if(compatible(first, candidate) && (second == null || candidate.id < second.id)) second = candidate;
            }
            return second != null;
        }

        private boolean padClear(Unit a, Unit b){
            for(Unit other : Groups.unit){
                if(other == a || other == b || (a != null && RailConsist.same(a, other)) || (b != null && RailConsist.same(b, other)) || other.dead || other.type.flying) continue;
                if(other.within(this, ((a == null ? producedUnit().hitSize : a.type.hitSize) + other.hitSize) / 2f + 1f)) return false;
            }
            return true;
        }

        private RailPointBuild exit(){
            RailPointBuild best = null;
            float alignment = -Float.MAX_VALUE;
            float dx = rotation == 0 ? 1f : rotation == 2 ? -1f : 0f;
            float dy = rotation == 1 ? 1f : rotation == 3 ? -1f : 0f;
            for(int i = 0; i < links.size; i++){
                Building target = world.build(links.get(i));
                if(!(target instanceof RailPointBuild rail) || RailPathfinder.edge(this, rail) == null) continue;
                float facing = ((rail.x - x) * dx + (rail.y - y) * dy) / dst(rail);
                if(best == null || facing > alignment || (facing == alignment && rail.pos() < best.pos())){
                    best = rail;
                    alignment = facing;
                }
            }
            return best;
        }

        @Override
        public boolean shouldConsume(){
            if(!enabled || (!coupling && (producedUnit() == null || !Units.canCreate(team, producedUnit())))) return false;
            return coupling ? findPair() && padClear(first, second) : links.size > 0 && padClear(null, null);
        }

        @Override
        public void updateTile(){
            super.updateTile();
            if(releasedTrain != null && (!RailTraffic.present(releasedTrain) || (releasedTrain.controller() instanceof RailCommandAI ai && !ai.onStation(this)))) releasedTrain = null;
            boolean working = efficiency > 0f && shouldConsume();
            constructWarmup = Mathf.lerpDelta(constructWarmup, working ? 1f : 0f, 0.05f);
            constructTime += constructWarmup * delta();
            if(net.client() || !working) return;
            if(!coupling && !items.has(recipe)) return;
            progress += edelta() * state.rules.unitBuildSpeed(team);
            if(progress < buildTime) return;
            RailPointBuild outlet = exit();
            if(outlet == null) return;
            Unit result;
            if(coupling){
                result = first;
                if(!RailConsist.link(result, result == first ? second : first)) return;
            }else{
                result = producedUnit().create(team);
                result.set(x, y);
                result.rotation = rotation * 90f;
                consume();
                result.add();
            }
            if(result.controller() instanceof RailCommandAI ai){
                RailPathfinder.Edge departure = RailPathfinder.edge(this, outlet);
                if((coupling && RailConsist.members(result).size >= goal(result)) || (!coupling && departure != null && RailTraffic.blocker(result, departure) == null)){
                    RailPointBuild nextCoupler = coupling ? forwardExit(0f, true) : null;
                    float staged = ai.couplingProgress();
                    ai.releaseCoupling();
                    RailPointBuild destination = nextCoupler != null ? nextCoupler
                        : coupling ? stagingExit(stagingLength(result) + result.hitSize + 1f) : outlet;
                    if(destination == null) destination = outlet;
                    ai.commandPosition(new arc.math.geom.Vec2(destination.x, destination.y));
                    if(coupling){
                        releasedTrain = result;
                        heldTrain = null;
                        // Long trains can already span the next pad while clearing this one.
                        if(nextCoupler instanceof RailWorkshopBuild workshop){
                            RailPathfinder.Route path = RailPathfinder.find(this, workshop.x, workshop.y);
                            if(path != null && path.nodes.peek() == workshop && staged >= path.cost && workshop.heldTrain == null){
                                workshop.capture(result);
                            }
                        }
                    }
                }
            }
            progress = 0f;
            first = second = null;
        }

        @Override
        protected void drawUnderTop(){
            if(coupling || producedUnit() == null || (progress <= 0f && constructWarmup <= 0.001f)) return;
            Draw.draw(Draw.z() - 0.01f, () -> Drawf.construct(this, producedUnit(), rotdeg() - 90f,
                Mathf.clamp(progress / buildTime), constructWarmup, constructTime));
        }

        @Override
        public byte version(){ return 4; }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(progress);
            write.i(trainLength);
            write.s(selectedUnit == null ? -1 : selectedUnit.id);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            progress = revision >= 2 ? read.f() : 0f;
            trainLength = revision >= 3 ? Math.max(2, read.i()) : 2;
            selectedUnit = revision >= 4 ? content.unit(read.s()) : null;
            if(!unitChoices.contains(selectedUnit, true)) selectedUnit = null;
            first = second = null;
        }
    }
}
