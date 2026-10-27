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
import arc.math.Angles;
import arc.math.geom.Vec2;
import mindustry.graphics.Drawf;
import arc.scene.style.TextureRegionDrawable;
import arc.struct.Seq;
import arc.struct.ObjectSet;
import arc.struct.ObjectIntMap;
import arc.util.Time;
import arc.scene.ui.layout.Table;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.entities.Units;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.type.ItemStack;
import mindustry.type.UnitType;
import mindustry.ai.UnitCommand;
import mindustry.ai.UnitStance;
import mindustry.ctype.ContentType;
import mindustry.gen.Icon;
import mindustry.ui.Styles;
import mindustry.io.TypeIO;

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
        rotateDraw = false;
       hasItems = !coupling;
        itemCapacity = 120;
        consumePower(2f);
        saveConfig = true;
        commandable = !coupling;
        if(!coupling) RailWorkshopCommands.register();
        config(UnitType.class, (RailWorkshopBuild build, UnitType unit) -> {
            if(!coupling && unitChoices.contains(unit, true)) build.selectedUnit = unit;
        });
        config(int[].class, (RailWorkshopBuild build, int[] queue) -> { if(!coupling) build.setQueue(queue); });
        config(Vec2.class, (RailWorkshopBuild build, Vec2 target) -> { if(!coupling) build.queueCommand(target); });
        config(UnitCommand.class, (RailWorkshopBuild build, UnitCommand command) -> {
            if(!coupling) build.rallyCommand = command;
        });
        config(UnitStance.class, (RailWorkshopBuild build, UnitStance stance) -> {
            if(!coupling){
                if(build.rallyStances.contains(stance, true)) build.rallyStances.remove(stance, true);
                else build.rallyStances.add(stance);
            }
        });
        config(Integer.class, (RailWorkshopBuild build, Integer action) -> {
            if(coupling) return;
            if(action == -1) build.rallyRoute.clear();
        });
        config(Long.class, (RailWorkshopBuild build, Long count) -> build.trainLength = (int)Math.max(2L, Math.min(Integer.MAX_VALUE, count)));
    }

    @Override
    public void init(){
        if(!coupling && recipe.length > 0) consumeItems(recipe);
        super.init();
    }

    public static class ProductionOrder{
        public UnitType unit;
        public int targetCount;
        public ProductionOrder(UnitType unit, int targetCount){ this.unit = unit; this.targetCount = targetCount; }
    }

    public class RailWorkshopBuild extends RailPointBuild{
        public float progress;
        public UnitType selectedUnit;
        private float constructWarmup, constructTime;
        public final Seq<ProductionOrder> productionQueue = new Seq<>();
        public final Seq<Vec2> rallyRoute = new Seq<>();
        public final Seq<UnitStance> rallyStances = new Seq<>();
        public UnitCommand rallyCommand;
        private final ObjectSet<RailPointBuild> populationNetwork = new ObjectSet<>();
        private final Seq<RailPointBuild> populationSearch = new Seq<>();
        private final ObjectIntMap<UnitType> populationCounts = new ObjectIntMap<>();
        private float populationCheck = Float.NEGATIVE_INFINITY;
        private int populationChanges = -1;
        private long populationRevision = -1L;
        private Object populationWorld;
        private UnitType activeProduction;

        public void invalidatePopulation(){ populationCheck = Float.NEGATIVE_INFINITY; }

        private void refreshPopulation(){
            if(world == null) return;
            if(populationWorld == world.tiles && populationChanges == world.tileChanges
                && populationRevision == RailPoint.graphRevision && Time.time < populationCheck) return;
            populationWorld = world.tiles;
            populationChanges = world.tileChanges;
            populationRevision = RailPoint.graphRevision;
            populationCheck = Time.time + 30f;
            populationCounts.clear();
            populationNetwork.clear();
            populationSearch.clear();
            populationNetwork.add(this);
            populationSearch.add(this);
            for(int i = 0; i < populationSearch.size; i++){
                RailPointBuild point = populationSearch.get(i);
                for(int j = 0; j < point.links.size; j++){
                    Building other = world.build(point.links.get(j));
                    if(other instanceof RailPointBuild rail && rail.team == team && !populationNetwork.contains(rail)
                        && RailPathfinder.edge(point, rail) != null){
                        populationNetwork.add(rail);
                        populationSearch.add(rail);
                    }
                }
            }
            if(Groups.unit == null) return;
            for(Unit unit : Groups.unit){
                if(unit.dead() || unit.team != team || !unitChoices.contains(unit.type, true)) continue;
                RailPointBuild support = unit.controller() instanceof RailCommandAI ai ? ai.trafficNode() : null;
                // A newly created car is counted before its controller attaches on the next tick.
                if(support == null && world.buildWorld(unit.x, unit.y) instanceof RailPointBuild rail) support = rail;
                if(support != null && populationNetwork.contains(support)) populationCounts.increment(unit.type, 0, 1);
            }
        }

        public int activeAmount(UnitType unit){ refreshPopulation(); return populationCounts.get(unit, 0); }

        public UnitType producedUnit(){
            if(coupling) return outputUnit;
            refreshPopulation();
            for(ProductionOrder order : productionQueue){
                if(populationCounts.get(order.unit, 0) < queuedAmount(order.unit)) return order.unit;
            }
            return null;
        }
        public UnitType selection(){ return selectedUnit == null ? outputUnit : selectedUnit; }

        public int[] queueConfig(){
            int[] data = new int[productionQueue.size * 2];
            for(int i = 0; i < productionQueue.size; i++){
                data[i * 2] = productionQueue.get(i).unit.id;
                data[i * 2 + 1] = productionQueue.get(i).targetCount;
            }
            return data;
        }

        public void setQueue(int[] data){
            if(data == null || data.length % 2 != 0 || data.length > 512) return;
            Seq<ProductionOrder> next = new Seq<>();
            for(int i = 0; i < data.length; i += 2){
                if(data[i] < 0 || data[i] >= content.units().size) return;
                UnitType unit = content.unit(data[i]);
                if(unit == null || !unitChoices.contains(unit, true) || data[i + 1] <= 0) return;
                next.add(new ProductionOrder(unit, data[i + 1]));
            }
            UnitType previous = producedUnit();
            productionQueue.clear();
            productionQueue.addAll(next);
            invalidatePopulation();
            if(previous != producedUnit()) progress = 0f;
        }

        public void finishQueuedUnit(){
            // Counts are persistent population targets, not quantities consumed by production.
            invalidatePopulation();
        }

        public long queuedAmount(UnitType unit){
            long total = 0;
            for(ProductionOrder order : productionQueue) if(order.unit == unit) total += order.targetCount;
            return total;
        }

        public int[] adjustedQueue(UnitType unit, int change){
            int[] data = queueConfig();
            if(!unitChoices.contains(unit, true) || change != 1 && change != -1) return data;
            if(change > 0){
                for(int i = 0; i < data.length; i += 2){
                    if(data[i] != unit.id) continue;
                    if(data[i + 1] < Integer.MAX_VALUE) data[i + 1]++;
                    return data;
                }
                if(productionQueue.size >= 256) return data;
                int[] next = java.util.Arrays.copyOf(data, data.length + 2);
                next[data.length] = unit.id;
                next[data.length + 1] = 1;
                return next;
            }
            // Remove from the last matching order so existing saved queues retain their order.
            for(int i = data.length - 2; i >= 0; i -= 2){
                if(data[i] != unit.id) continue;
                if(data[i + 1] > 1){ data[i + 1]--; return data; }
                int[] next = new int[data.length - 2];
                System.arraycopy(data, 0, next, 0, i);
                System.arraycopy(data, i + 2, next, i, data.length - i - 2);
                return next;
            }
            return data;
        }

        @Override public void drawSelect(){
            super.drawSelect();
            drawCommandRoute();
        }

        public void drawCommandRoute(){
            float px = x, py = y;
            for(Vec2 point : rallyRoute){
                Drawf.dashLine(mindustry.graphics.Pal.command, px, py, point.x, point.y);
                Drawf.square(point.x, point.y, 3f, 45f, mindustry.graphics.Pal.command);
                px = point.x;
                py = point.y;
            }
            Draw.reset();
        }

        @Override public Vec2 getCommandPosition(){ return rallyRoute.isEmpty() ? null : rallyRoute.first(); }
        @Override public void onCommand(Vec2 target){
            if(coupling || target == null || !Float.isFinite(target.x) || !Float.isFinite(target.y)) return;
            rallyRoute.clear();
            queueCommand(target);
        }

        public void queueCommand(Vec2 target){
            if(coupling || target == null || !Float.isFinite(target.x) || !Float.isFinite(target.y)) return;
            // CommandAI accepts one active destination and up to 50 queued stops.
            if(rallyRoute.size < 51) rallyRoute.add(target.cpy());
        }

        public void applyPrecommands(Unit result){
            if(!result.isCommandable()) return;
            var ai = result.command();
            if(rallyCommand != null && result.type.commands.contains(rallyCommand, true)) ai.command(rallyCommand);
            for(UnitStance stance : rallyStances){
                if(result.type.stances.contains(stance, true) && stance.isCompatible(ai.currentCommand())) ai.setStance(stance, true);
            }
            if(rallyRoute.any()){
                ai.commandPosition(rallyRoute.first().cpy());
                for(int i = 1; i < rallyRoute.size; i++) ai.commandQueue(rallyRoute.get(i).cpy());
            }
        }
        public int trainLength = 2;

        @Override
        public void buildConfiguration(Table table){
            table.background(Styles.black6);
            if(!coupling){
                table.table(previews -> {
                    for(UnitType unit : unitChoices){
                        if(!unit.unlockedNow() || unit.isBanned()) continue;
                        previews.table(column -> {
                            column.button(Icon.upOpen, Styles.emptyi, () -> configure(adjustedQueue(unit, 1)))
                                .size(64f, 28f).tooltip(Core.bundle.get("rail.queue-increase", "Queue one more"));
                            column.row();
                            column.button(new TextureRegionDrawable(unit.uiIcon), Styles.clearNoneTogglei, 48f, () -> configure(unit))
                                .size(64f).tooltip(unit.localizedName).update(button -> button.setChecked(selection() == unit));
                            column.row();
                            column.label(() -> Long.toString(queuedAmount(unit))).row();
                            column.button(Icon.downOpen, Styles.emptyi, () -> configure(adjustedQueue(unit, -1)))
                                .size(64f, 28f).tooltip(Core.bundle.get("rail.queue-decrease", "Queue one fewer"))
                                .disabled(button -> queuedAmount(unit) == 0);
                        }).pad(4f);
                    }
                }).row();
                table.table(commands -> {
                    Runnable rebuild = () -> {
                        commands.clear();
                        UnitType unit = selection();
                        if(unit == null) return;
                        for(UnitCommand command : unit.commands){
                            commands.button(command.getIcon(), Styles.clearNoneTogglei, () -> configure(command)).size(40f).tooltip(command.localized())
                                .update(button -> button.setChecked(rallyCommand == command || rallyCommand == null && unit.defaultCommand == command));
                        }
                        commands.row();
                        for(UnitStance stance : unit.stances){
                            if(!stance.toggle) continue;
                            commands.button(stance.getIcon(), Styles.clearNoneTogglei, () -> configure(stance)).size(40f)
                                .tooltip(stance.localized()).update(button -> button.setChecked(rallyStances.contains(stance, true)));
                        }
                    };
                    UnitType[] previous = {selection()};
                    rebuild.run();
                    commands.update(() -> {
                        if(previous[0] != selection()){
                            previous[0] = selection();
                            rebuild.run();
                            table.pack();
                        }
                    });
                }).row();
                return;
            }
            table.add(Core.bundle.get("rail.train-length", "Train length") + ": ");
            table.field(Integer.toString(trainLength), text -> {
                try{ long count = Long.parseLong(text); if(count >= 2 && count <= Integer.MAX_VALUE) configure(count); }
                catch(NumberFormatException ignored){}
            }).width(100f);
        }

        @Override
        public Object config(){ return coupling ? Long.valueOf(trainLength) : queueConfig(); }
        private Unit first, second, heldTrain, releasedTrain;
        private float entryHeading = Float.NaN;

        private int goal(Unit head){
            return trainLength;
        }

        private void capture(Unit head){
            if(heldTrain == head) return;
            if(RailConsist.members(head).size >= trainLength) return;
            entryHeading = head.controller() instanceof RailCommandAI ai ? ai.railHeading() : head.rotation;
            heldTrain = head;
            float length = stagingLength(head);
            if(head.controller() instanceof RailCommandAI ai) ai.stageCoupling(this, length, stagingExit(length));
        }

        private boolean eligible(Unit candidate){
            return !candidate.dead && candidate.team == team && candidate.type instanceof RailUnitType
                && RailConsist.leader(candidate) == candidate
                && candidate.controller() instanceof RailCommandAI ai && ai.supportingRail() != null;
        }

        public boolean holding(Unit candidate){
            return coupling && enabled && heldTrain == candidate && RailConsist.members(candidate).size < trainLength
                && (releasedTrain == null || !RailConsist.same(candidate, releasedTrain));
        }

        private float stagingLength(Unit head){
            Unit tail = RailConsist.partner(head);
            if(tail == null) tail = head;
            return RailConsist.offset(tail) + RailTraffic.bodyLength(tail) / 2f + 1.5f;
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
                    if(current instanceof RailWorkshopBuild workshop && workshop != this && ((RailWorkshop)workshop.block).coupling && workshop.enabled
                        && (heldTrain == null || RailConsist.members(heldTrain).size < workshop.trainLength)) return current;
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
            if(RailConsist.members(candidate).size >= trainLength){
                RailCommandAI controller = (RailCommandAI)candidate.controller();
                boolean commandedHere = candidate.within(this, 0.05f) && controller.targetPos != null && controller.targetPos.within(this, 1f);
                if(heldTrain == candidate || commandedHere){
                    if(heldTrain != candidate) entryHeading = controller.railHeading();
                    RailPointBuild destination = stagingExit(stagingLength(candidate) + RailTraffic.bodyLength(candidate) + 1f);
                    controller.releaseCoupling();
                    heldTrain = null;
                    releasedTrain = candidate;
                    if(destination != null){
                        candidate.command().commandPosition(new Vec2(destination.x, destination.y));
                        return true;
                    }
                }
                return false;
            }
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
                    if(candidate.within(tail, RailTraffic.carSpacing(candidate, tail) + 2f)) return true;
                }
            }
            return false;
        }

        public boolean compatible(Unit a, Unit b){
            Unit tail = RailConsist.partner(a);
            if(tail == null) tail = a;
            return !RailConsist.same(a, b) && eligible(a) && eligible(b)
                && RailConsist.members(b).size < trainLength
                && within(b, size * tilesize / 2f + RailTraffic.bodyLength(b) / 2f)
                && b.within(tail, RailTraffic.carSpacing(tail, b) + 3f)
                && a.stack.amount + b.stack.amount <= a.type.itemCapacity + b.type.itemCapacity;
        }

        public boolean canReceive(Unit incoming, Unit held){
            Unit head = RailConsist.leader(held);
            return coupling && enabled && !RailConsist.same(incoming, head) && incoming.team == team && head.team == team
                && incoming.type instanceof RailUnitType && RailConsist.members(incoming).size < trainLength && holding(head);
        }
        private boolean findPair(){
            first = second = null;
            if(heldTrain != null && eligible(heldTrain)){
                if(RailConsist.members(heldTrain).size >= trainLength){ shouldHold(heldTrain); return false; }
                first = heldTrain;
            }
            if(first == null) for(Unit candidate : Groups.unit){
                if(eligible(candidate) && RailConsist.members(candidate).size < trainLength && (releasedTrain == null || !RailConsist.same(candidate, releasedTrain)) && candidate.within(this, 1f) && (first == null || candidate.id < first.id)) first = candidate;
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
                if(other.within(this, ((a == null ? RailTraffic.bodyLength(producedUnit()) : RailTraffic.bodyLength(a)) + RailTraffic.bodyLength(other)) / 2f + 1f)) return false;
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
                if(coupling && Float.isFinite(entryHeading)){
                    facing = Mathf.cosDeg(Angles.angleDist(entryHeading, RailPathfinder.edge(this, rail).sample(0f, new Vec2())));
                    if(facing < -0.001f) continue;
                }
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
            if(!coupling){
                UnitType desired = producedUnit();
                if(activeProduction != desired){ progress = 0f; activeProduction = desired; }
            }
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
            if(!coupling){
                // Another yard may have filled the target since this construction cycle started.
                invalidatePopulation();
                if(producedUnit() != activeProduction){ progress = 0f; activeProduction = producedUnit(); return; }
            }
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
                finishQueuedUnit();
            }
            if(result.controller() instanceof RailCommandAI ai){
                RailPathfinder.Edge departure = RailPathfinder.edge(this, outlet);
                if((coupling && RailConsist.members(result).size >= goal(result)) || (!coupling && departure != null && RailTraffic.blocker(result, departure) == null)){
                    RailPointBuild nextCoupler = coupling ? forwardExit(0f, true) : null;
                    float staged = ai.couplingProgress();
                    ai.releaseCoupling();
                    RailPointBuild destination = nextCoupler != null ? nextCoupler
                        : coupling ? stagingExit(stagingLength(result) + RailTraffic.bodyLength(result) + 1f) : outlet;
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
            if(!coupling) applyPrecommands(result);
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
        public byte version(){ return 6; }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(progress);
            write.i(trainLength);
            write.s(selectedUnit == null ? -1 : selectedUnit.id);
            write.i(productionQueue.size);
            for(ProductionOrder order : productionQueue){ write.s(order.unit.id); write.i(order.targetCount); }
            write.i(rallyRoute.size);
            for(Vec2 point : rallyRoute){ write.f(point.x); write.f(point.y); }
            TypeIO.writeCommand(write, rallyCommand);
            write.i(rallyStances.size);
            for(UnitStance stance : rallyStances) write.s(stance.id);
            write.bool(false); // Retained for compatibility with version 5 saves.
            write.s(activeProduction == null ? -1 : activeProduction.id);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            progress = revision >= 2 ? read.f() : 0f;
            trainLength = revision >= 3 ? Math.max(2, read.i()) : 2;
            selectedUnit = revision >= 4 ? content.unit(read.s()) : null;
            if(!unitChoices.contains(selectedUnit, true)) selectedUnit = null;
            productionQueue.clear();
            rallyRoute.clear();
            rallyStances.clear();
            rallyCommand = null;
            if(revision >= 5){
                int count = read.i();
                if(count < 0 || count > 256) throw new IllegalArgumentException("Invalid trainyard queue length");
                for(int i = 0; i < count; i++){
                    UnitType unit = content.unit(read.s()); int remaining = read.i();
                    if(unitChoices.contains(unit, true) && remaining > 0) productionQueue.add(new ProductionOrder(unit, remaining));
                }
                count = read.i();
                if(count < 0 || count > 256) throw new IllegalArgumentException("Invalid trainyard route length");
                for(int i = 0; i < count; i++) rallyRoute.add(new Vec2(read.f(), read.f()));
                rallyCommand = TypeIO.readCommand(read);
                count = read.i();
                if(count < 0 || count > content.unitStances().size) throw new IllegalArgumentException("Invalid trainyard stances");
                for(int i = 0; i < count; i++){
                    UnitStance stance = content.getByID(ContentType.unitStance, read.s());
                    if(stance != null) rallyStances.addUnique(stance);
                }
                read.bool(); // Legacy append-mode setting is no longer used.
            }else if(!coupling && selection() != null){
                productionQueue.add(new ProductionOrder(selection(), 1));
            }
            activeProduction = revision >= 6 ? content.unit(read.s()) : productionQueue.isEmpty() ? null : productionQueue.first().unit;
            invalidatePopulation();
            first = second = null;
            heldTrain = releasedTrain = null;
            entryHeading = Float.NaN;
        }
    }
}
