package aquarion.world.blocks.neoplasia;

import arc.struct.*;
import arc.util.Time;

import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.world.Tile;
import mindustry.type.Item;

import aquarion.world.blocks.neoplasia.GenericNeoplasiaBlock.NeoplasiaBuild;
import static aquarion.world.blocks.neoplasia.GenericNeoplasiaBlock.activeNeoplasia;

public class NeoplasiaGraph {
    public static int chunkSize = 32;

    public static Seq<NeoplasmHeart.NeoplasmHeartBuild> hearts = new Seq<>();
    public static NeoplasmHeart.NeoplasmHeartBuild primeHeart = null;
    public static ObjectIntMap<NeoplasiaBuild> compMap = new ObjectIntMap<>();
    public static final IntIntMap heartCounts = new IntIntMap();
    public static final IntSeq comps = new IntSeq();
    public static final IntMap<NeoplasiaBuild> biggestBlobs = new IntMap<>();
    public static final IntFloatMap biggestAmounts = new IntFloatMap();
    public static LongMap<NeoplasiaChunk> chunks = new LongMap<>();
    public static final Queue<NeoplasiaBuild> pulseQueue = new Queue<>();
    public static float pulseTimer = 0f;
    public static float pulseInterval = 30f;
    public static float spawnCooldown = 0f;
    public static float spawnInterval = 600f;
    public static float heartCost = 150f;
    public static NeoplasmHeart heartBlock;
    private static boolean trackedNeoplasia;
    public static boolean dirty;
    public static final IntMap<Seq<NeoplasiaBuild>> members = new IntMap<>();
    private static final ObjectMap<NeoplasiaBuild, NeoplasiaBuild> flowParents = new ObjectMap<>();
    private static final ObjectSet<NeoplasiaBuild> flowAncestors = new ObjectSet<>();
    private static double[] desiredMass = new double[0], capacities = new double[0];
    private static final IntSet deliveryComponents = new IntSet();
    private static final IntIntMap veinCounts = new IntIntMap(), veinLimits = new IntIntMap(), veinGrowth = new IntIntMap();
    private static final ObjectMap<NeoplasiaBuild, NeoplasiaBuild> veinRoots = new ObjectMap<>();
    private static final Seq<NeoplasmVein.NeoplasmVeinBuild> rankedVeins = new Seq<>();
    private static final ObjectMap<Item, Seq<NeoplasiaBuild>> supplies = new ObjectMap<>();
    private static NeoplasiaBuild indexingSupply;
    private static final mindustry.world.modules.ItemModule.ItemConsumer indexSupply = (item, count) -> {
        if(count > indexingSupply.neededAmount(item)) supplies.get(item, Seq::new).add(indexingSupply);
    };
    private static final ObjectFloatMap<NeoplasiaBuild> routeCosts = new ObjectFloatMap<>();
    private record RouteStep(NeoplasiaBuild build, float cost){}
    private static final java.util.PriorityQueue<RouteStep> routeQueue = new java.util.PriorityQueue<>(
        java.util.Comparator.comparingDouble(RouteStep::cost));

    static boolean hasDeliveryNetwork(NeoplasiaBuild build){
        return deliveryComponents.contains(compMap.get(build, 0));
    }

    /** Keep routes one tile wide, including when several paths meet. */
    static boolean canGrowVein(NeoplasiaBuild build){
        int component = compMap.get(build, 0);
        if(veinCounts.get(component, 0) >= veinLimits.get(component, 4) || veinGrowth.get(component, 0) >= 2) return false;
        int adjacent = 0;
        int nearby = 0;
        NeoplasiaBuild firstRoot = null;
        for(int dx = -2; dx <= 2; dx++) for(int dy = -2; dy <= 2; dy++){
            if(dx == 0 && dy == 0) continue;
            Tile tile = Vars.world.tile(build.tile.x + dx, build.tile.y + dy);
            if(tile == null || !(tile.build instanceof NeoplasmVein.NeoplasmVeinBuild vein) || vein.team != build.team) continue;
            if(++nearby > 6) return false;
            if(Math.abs(dx) > 1 || Math.abs(dy) > 1) continue;
            if(++adjacent > 2) return false;
            NeoplasiaBuild root = veinRoot(vein);
            if(firstRoot == root) return false; // Joining the same strand twice closes a loop.
            firstRoot = root;
        }
        return true;
    }

    static void recordVeinGrowth(NeoplasiaBuild build){
        int component = compMap.get(build, 0);
        veinCounts.increment(component);
        veinGrowth.increment(component);
    }

    private static NeoplasiaBuild veinRoot(NeoplasiaBuild build){
        NeoplasiaBuild root = build;
        while(veinRoots.get(root, root) != root) root = veinRoots.get(root);
        while(build != root){
            NeoplasiaBuild parent = veinRoots.get(build, build);
            veinRoots.put(build, root);
            build = parent;
        }
        return root;
    }

    private static void joinVein(NeoplasiaBuild vein){
        for(NeoplasiaBuild neighbor : vein.neighbors()){
            if(!(neighbor instanceof NeoplasmVein.NeoplasmVeinBuild)) continue;
            NeoplasiaBuild from = veinRoot(vein), to = veinRoot(neighbor);
            if(from != to) veinRoots.put(from, to);
        }
    }

    static void updateVeinBudgets(){
        veinCounts.clear();
        veinLimits.clear();
        veinGrowth.clear();
        veinRoots.clear();
        for(var entry : members.entries()){
            rankedVeins.clear();
            for(NeoplasiaBuild build : entry.value){
                if(build instanceof NeoplasmVein.NeoplasmVeinBuild vein){
                    rankedVeins.add(vein);
                    joinVein(vein);
                }
            }
            int limit = Math.max(4, (int)(entry.value.size * 0.04f));
            veinLimits.put(entry.key, limit);
            veinCounts.put(entry.key, rankedVeins.size);
            rankedVeins.sort((a, b) -> {
                int retiring = Boolean.compare(a.crowded, b.crowded);
                if(retiring != 0) return retiring;
                int established = Float.compare(b.age, a.age);
                return established != 0 ? established : Integer.compare(a.pos(), b.pos());
            });
            for(int i = limit; i < rankedVeins.size; i++) rankedVeins.get(i).crowded = true;
        }
        rankedVeins.clear();
    }

    public static long chunkKey(int cx, int cy) {
        return ((long) cx << 32) | (cy & 0xffffffffL);
    }

    public static int chunkX(int x) {
        return Math.floorDiv(x, chunkSize);
    }

    public static int chunkY(int y) {
        return Math.floorDiv(y, chunkSize);
    }

    public static void register(Building b) {
        if (!(b instanceof NeoplasiaBuild nb)) return;
        Tile tile = b.tile;
        int cx = chunkX(tile.x);
        int cy = chunkY(tile.y);
        long key = chunkKey(cx, cy);
        NeoplasiaChunk chunk = chunks.get(key);
        if (chunk == null) {
            chunk = new NeoplasiaChunk(cx, cy);
            chunks.put(key, chunk);
        }
        chunk.builds.addUnique(nb);
        if (activeNeoplasia != null) {
            activeNeoplasia.addUnique(nb);
            trackedNeoplasia = true;
            dirty = true;
        }
        if(nb instanceof NeoplasmVein.NeoplasmVeinBuild) joinVein(nb);
        if(nb instanceof NeoplasmTreeBase.NeoplasmTreeBaseBuild tree) NeoplasmTreeBase.trees.addUnique(tree);
    }

    public static void addHeart(NeoplasmHeart.NeoplasmHeartBuild heart) {
        dirty = true;
        hearts.addUnique(heart);
        if (primeHeart == null || primeHeart.dead() || primeHeart.dissolving) {
            primeHeart = heart;
            heart.isPrime = true;
        }
    }

    public static void removeHeart(NeoplasmHeart.NeoplasmHeartBuild heart) {
        dirty = true;
        hearts.remove(heart);
        if (primeHeart == heart) {
            primeHeart = null;
            heart.isPrime = false;
        }
    }

    public static int techLevel() {
        if (primeHeart == null || primeHeart.dead() || primeHeart.dissolving) return 0;
        return primeHeart.techLevel();
    }

    public static boolean isConnected(NeoplasiaBuild build) {
        int comp = compMap.get(build, 0);
        return comp != 0 && heartCounts.get(comp, 0) > 0;
    }

    public static boolean isConnected(NeoplasmHeart.NeoplasmHeartBuild heart){ return !heart.dissolving && !heart.dead(); }

    static int heartComponent(NeoplasmHeart.NeoplasmHeartBuild heart){
        for(NeoplasiaBuild neighbor : heart.neighbors()){
            int component = compMap.get(neighbor, 0);
            if(component != 0) return component;
        }
        return 0;
    }

    public static void update() {
        if (activeNeoplasia.isEmpty() && hearts.isEmpty()) {
            veinCounts.clear();
            veinLimits.clear();
            veinGrowth.clear();
            veinRoots.clear();
            members.clear();
            deliveryComponents.clear();
            dirty = false;
            if (trackedNeoplasia) {
                hearts.clear();
                primeHeart = null;
                compMap.clear();
                heartCounts.clear();
                biggestBlobs.clear();
                biggestAmounts.clear();
                comps.clear();
                chunks.clear();
                pulseTimer = 0f;
                spawnCooldown = 0f;
                trackedNeoplasia = false;
            }
            return;
        }

        pulseTimer += Time.delta;
        // Batch topology changes and deliveries; growing tissue must not rebuild the graph every frame.
        if (pulseTimer < pulseInterval && (!members.isEmpty() || !dirty)) return;
        float elapsed = pulseTimer;
        pulseTimer = 0f;
        boolean rebuild = dirty || members.isEmpty();
        dirty = false;
        veinGrowth.clear();

        if (primeHeart == null || primeHeart.dead() || primeHeart.dissolving) {
            primeHeart = null;
            NeoplasmHeart.NeoplasmHeartBuild best = null;
            for (int i = 0; i < hearts.size; i++) {
                NeoplasmHeart.NeoplasmHeartBuild heart = hearts.get(i);
                if (heart.dead() || heart.dissolving) continue;
                if (heart.isPrime) {
                    best = heart;
                    break;
                }
                if (best == null || heart.amount > best.amount) {
                    best = heart;
                }
            }
            if (best != null) {
                primeHeart = best;
                best.isPrime = true;
            }
        }

        if(rebuild){
        compMap.clear();
        heartCounts.clear();
        biggestBlobs.clear();
        biggestAmounts.clear();
        comps.clear();
        members.clear();
        flowParents.clear();

        int cid = 0;
        Queue<NeoplasiaBuild> queue = pulseQueue;
        for (int i = 0; i < activeNeoplasia.size; i++) {
            NeoplasiaBuild seed = activeNeoplasia.get(i);
            if (seed.dead() || compMap.get(seed, 0) != 0) continue;
            cid++;
            comps.add(cid);
            Seq<NeoplasiaBuild> component = new Seq<>();
            members.put(cid, component);
            queue.clear();
            compMap.put(seed, cid);
            queue.addLast(seed);
            while (queue.size > 0) {
                NeoplasiaBuild current = queue.removeFirst();
                component.add(current);
                float amt = current.amount;
                if (biggestAmounts.get(cid, 0f) < amt) {
                    biggestAmounts.put(cid, amt);
                    biggestBlobs.put(cid, current);
                }
                for (NeoplasiaBuild nb : current.neighbors()) {
                    if (nb.dead() || compMap.get(nb, 0) != 0) continue;
                    compMap.put(nb, cid);
                    flowParents.put(nb, current);
                    queue.addLast(nb);
                }
            }
        }

        for(NeoplasmHeart.NeoplasmHeartBuild heart : hearts){
            if(!heart.dead() && !heart.dissolving){
                int component = heartComponent(heart);
                if(component != 0) heartCounts.increment(component);
            }
        }
        updateVeinBudgets();
        for(Seq<NeoplasiaBuild> component : members.values()) routeVeins(component);
        updateVeinDistances();
        }
        if(!rebuild){
            biggestAmounts.clear();
            for(var entry : members.entries()){
                for(NeoplasiaBuild build : entry.value){
                    if(build.amount > biggestAmounts.get(entry.key, 0f)){
                        biggestAmounts.put(entry.key, build.amount);
                        biggestBlobs.put(entry.key, build);
                    }
                }
            }
        }
        for(Seq<NeoplasiaBuild> component : members.values()) shareMass(component, elapsed);
        transport();

        if (spawnCooldown > 0f) spawnCooldown -= elapsed;
        if (heartBlock != null) {
            for (int i = 0; i < comps.size; i++) {
                if (spawnCooldown > 0f) break;
                int comp = comps.get(i);
                if (heartCounts.get(comp, 0) > 0) continue;
                NeoplasiaBuild big = biggestBlobs.get(comp);
                if (big != null && big.amount >= heartCost) {
                    spawnCooldown = spawnInterval;
                    big.upgradeTo(heartBlock);
                    heartCounts.increment(comp);
                }
            }
        }

        for (int i = 0; i < comps.size; i++) {
            int comp = comps.get(i);
            if (heartCounts.get(comp, 0) < 2) continue;
            NeoplasmHeart.NeoplasmHeartBuild keep = null;
            for (int h = 0; h < hearts.size; h++) {
                NeoplasmHeart.NeoplasmHeartBuild heart = hearts.get(h);
                if (heart.dead() || heart.dissolving || heartComponent(heart) != comp) continue;
                if (keep == null) keep = heart;
                if (primeHeart != null && !primeHeart.dead() && !primeHeart.dissolving && heartComponent(primeHeart) == comp) {
                    keep = primeHeart;
                    break;
                }
            }
            if (keep == null) continue;
            for (int h = 0; h < hearts.size; h++) {
                NeoplasmHeart.NeoplasmHeartBuild heart = hearts.get(h);
                if (heart.dead() || heart.dissolving || heart == keep || heartComponent(heart) != comp) continue;
                heart.dissolving = true;
                dirty = true;
                hearts.remove(h);
                h--;
            }
        }
    }

    /** Veins put their whole connected colony on one instant mass and item transport network. */
    static void transport(){
        deliveryComponents.clear();
        for(Seq<NeoplasiaBuild> component : members.values()){
            for(Seq<NeoplasiaBuild> stores : supplies.values()) stores.clear();
            NeoplasiaBuild artery = null;
            for(NeoplasiaBuild build : component){
                if(build instanceof NeoplasmVein.NeoplasmVeinBuild && build.tile.build == build && !build.dead()){
                    artery = build;
                    break;
                }
            }
            deliveryComponents.add(compMap.get(component.first(), 0));
            if(artery != null) equalizeMass(component, artery);
            for(NeoplasiaBuild build : component){
                if(build.tile.build != build || build.dead()) continue;
                indexingSupply = build;
                build.items.each(indexSupply);
            }
            for(NeoplasiaBuild build : component){
                if(build.tile.build != build || build.dead() || build.neededItems == null) continue;
                for(Item item : build.neededItems){
                    Seq<NeoplasiaBuild> sources = supplies.get(item);
                    if(sources == null) continue;
                    int room = build.block().perItemCapacity ? build.block().itemCapacity - build.items.get(item) : build.block().itemCapacity - build.items.total();
                    int need = Math.min(room, build.neededAmount(item) - build.items.get(item));
                    while(need > 0 && !sources.isEmpty()){
                        NeoplasiaBuild source = sources.peek();
                        int available = Math.max(0, source.items.get(item) - source.neededAmount(item));
                        int take = Math.min(need, available);
                        if(take > 0){
                            source.items.remove(item, take);
                            build.items.add(item, take);
                            recordFlow(source, build, take);
                            animateDelivery(source, build, item);
                            need -= take;
                        }
                        if(available <= take) sources.pop();
                    }
                }
            }
        }
        indexingSupply = null;
        for(Seq<NeoplasiaBuild> stores : supplies.values()) stores.clear();
    }

    /** Multi-source distance field, rebuilt once per graph pulse rather than during drawing. */
    static void updateVeinDistances(){
        pulseQueue.clear();
        for(Seq<NeoplasiaBuild> component : members.values()) for(NeoplasiaBuild build : component){
            if(build instanceof NeoplasmVein.NeoplasmVeinBuild vein) vein.tissueDistance = Integer.MAX_VALUE;
        }
        for(Seq<NeoplasiaBuild> component : members.values()) for(NeoplasiaBuild build : component){
            if(build instanceof NeoplasmVein.NeoplasmVeinBuild) continue;
            for(NeoplasiaBuild neighbor : build.neighbors()){
                if(neighbor instanceof NeoplasmVein.NeoplasmVeinBuild vein && vein.tissueDistance > 1){
                    vein.tissueDistance = 1;
                    pulseQueue.addLast(vein);
                }
            }
        }
        while(!pulseQueue.isEmpty()){
            NeoplasmVein.NeoplasmVeinBuild current = (NeoplasmVein.NeoplasmVeinBuild)pulseQueue.removeFirst();
            for(NeoplasiaBuild neighbor : current.neighbors()){
                if(neighbor instanceof NeoplasmVein.NeoplasmVeinBuild vein && vein.tissueDistance > current.tissueDistance + 1){
                    vein.tissueDistance = current.tissueDistance + 1;
                    pulseQueue.addLast(vein);
                }
            }
        }
    }

    /** Stable terrain variation and turn costs keep supply paths from becoming ruler-straight spokes. */
    static void routeVeins(Seq<NeoplasiaBuild> component){
        if(component.isEmpty()) return;
        routeCosts.clear();
        routeQueue.clear();
        NeoplasiaBuild root = component.first();
        for(NeoplasiaBuild build : component){
            flowParents.remove(build);
            if(build.pos() < root.pos()) root = build;
        }
        routeCosts.put(root, 0f);
        routeQueue.add(new RouteStep(root, 0f));
        while(!routeQueue.isEmpty()){
            RouteStep step = routeQueue.poll();
            NeoplasiaBuild current = step.build;
            if(step.cost > routeCosts.get(current, Float.POSITIVE_INFINITY)) continue;
            NeoplasiaBuild parent = flowParents.get(current);
            NeoplasiaBuild grandparent = parent == null ? null : flowParents.get(parent);
            for(NeoplasiaBuild neighbor : current.neighbors()){
                int dx = neighbor.tile.x - current.tile.x, dy = neighbor.tile.y - current.tile.y;
                int hash = current.pos() * 73428767 ^ neighbor.pos() * 912931;
                hash ^= hash >>> 16;
                float cost = (dx != 0 && dy != 0 ? 1.35f : 1f) + (hash & 1023) / 1024f * 0.7f;
                if(parent != null && grandparent != null &&
                    current.tile.x - parent.tile.x == dx && current.tile.y - parent.tile.y == dy &&
                    parent.tile.x - grandparent.tile.x == dx && parent.tile.y - grandparent.tile.y == dy) cost += 1.2f;
                float total = step.cost + cost;
                if(total >= routeCosts.get(neighbor, Float.POSITIVE_INFINITY)) continue;
                routeCosts.put(neighbor, total);
                flowParents.put(neighbor, current);
                routeQueue.add(new RouteStep(neighbor, total));
            }
        }
        routeQueue.clear();
        routeCosts.clear();
    }

    static float reserveFor(NeoplasiaBuild build){
        if(build instanceof NeoplasiaBlister.BlisterBuild blister && (blister.charged || blister.burstDir >= 0)) return build.amount;
        float reserve = build.lifeReserve();
        if(build.block().massExportRate > 0f) reserve = Math.max(reserve, build.block().maxAmount * 0.25f);
        if(build instanceof NeoplasmTreeBase.NeoplasmTreeBaseBuild tree){
            NeoplasmTreeBase type = (NeoplasmTreeBase)tree.block;
            reserve += type.podCost * Math.min(10, Math.max(1, tree.branches.size * type.maxPodsPerBranch));
        }else if(build.block() instanceof NeoplasiaproductionBlock factory && factory.shouldCraft){
            reserve += factory.craftCost * 2f;
        }
        return Math.min(reserve, build.massCapacity());
    }

    static void equalizeMass(Seq<NeoplasiaBuild> component, NeoplasiaBuild artery){
        if(desiredMass.length < component.size){
            desiredMass = new double[component.size];
            capacities = new double[component.size];
        }
        double pool = 0d, deficit = 0d;
        for(int i = 0; i < component.size; i++){
            NeoplasiaBuild build = component.get(i);
            if(build.tile.build != build || build.dead()){
                desiredMass[i] = capacities[i] = build.amount;
                continue;
            }
            capacities[i] = Math.max(build.amount, build.massCapacity());
            double minimum = reserveFor(build);
            desiredMass[i] = Math.min(build.amount, minimum);
            pool += build.amount - desiredMass[i];
            deficit += Math.max(0d, minimum - build.amount);
        }
        double fraction = deficit > 0d ? Math.min(1d, pool / deficit) : 0d;
        double room = 0d;
        for(int i = 0; i < component.size; i++){
            NeoplasiaBuild build = component.get(i);
            double need = build.tile.build == build && !build.dead() ? Math.max(0d, reserveFor(build) - desiredMass[i]) : 0d;
            double flow = need * fraction;
            desiredMass[i] += flow;
            pool -= flow;
            room += capacities[i] - desiredMass[i];
        }
        fraction = room > 0d ? Math.min(1d, Math.max(0d, pool) / room) : 0d;
        for(int i = 0; i < component.size; i++) desiredMass[i] += (capacities[i] - desiredMass[i]) * fraction;
        for(int i = 0; i < component.size; i++){
            NeoplasiaBuild build = component.get(i);
            // Keep local stores and rich patches instead of flattening every colony each pulse.
            float change = ((float)desiredMass[i] - build.amount) * 0.08f;
            if(change < 0f) build.amount += change;
            else if(change > 0f) build.receiveMass(change);
            // Passive balancing is not a transport route and must not grow veins.
        }
    }

    static void recordFlow(NeoplasiaBuild source, NeoplasiaBuild destination, float traffic){
        flowAncestors.clear();
        for(NeoplasiaBuild node = source; node != null; node = flowParents.get(node)) flowAncestors.add(node);
        NeoplasiaBuild common = destination;
        while(common != null && !flowAncestors.contains(common)) common = flowParents.get(common);
        for(NeoplasiaBuild node = source; node != null && node != common; node = flowParents.get(node)) node.routeTraffic = Math.min(100f, node.routeTraffic + traffic);
        for(NeoplasiaBuild node = destination; node != null && node != common; node = flowParents.get(node)) node.routeTraffic = Math.min(100f, node.routeTraffic + traffic);
        if(common != null) common.routeTraffic = Math.min(100f, common.routeTraffic + traffic);
    }

    static void animateDelivery(NeoplasiaBuild source, NeoplasiaBuild destination, Item item){
        if(!NeoplasiaItemTransit.canAnimate()) return;
        flowAncestors.clear();
        for(NeoplasiaBuild node = source; node != null; node = flowParents.get(node)) flowAncestors.add(node);
        NeoplasiaBuild common = destination;
        while(common != null && !flowAncestors.contains(common)) common = flowParents.get(common);
        if(common == null) return;
        Seq<NeoplasiaBuild> route = new Seq<>(), tail = new Seq<>();
        for(NeoplasiaBuild node = source; node != common && route.size <= 256; node = flowParents.get(node)) route.add(node);
        route.add(common);
        for(NeoplasiaBuild node = destination; node != common && tail.size <= 256; node = flowParents.get(node)) tail.add(node);
        for(int i = tail.size - 1; i >= 0; i--) route.add(tail.get(i));
        NeoplasiaItemTransit.play(route, item);
    }

    /** Feed item producers first, then growing trees, using only mass exported by miners. */
    static void shareMass(Seq<NeoplasiaBuild> component, float elapsed){
        Seq<NeoplasiaBuild> recipients = new Seq<>();
        for(NeoplasiaBuild build : component){
            if(build instanceof NeoplasmTreeBase.NeoplasmTreeBaseBuild ||
                build.block() instanceof NeoplasiaproductionBlock factory && factory.shouldCraft) recipients.add(build);
        }
        if(recipients.isEmpty()) return;
        for(NeoplasiaBuild donor : component){
            float budget = Math.min(donor.block().massExportRate * elapsed,
                Math.max(0f, donor.amount - donor.block().maxAmount * 0.25f));
            if(budget <= 0f) continue;
            for(int pass = 0; pass < 2 && budget > 0f; pass++){
                for(NeoplasiaBuild recipient : recipients){
                    if(recipient == donor) continue;
                    float target = 0f;
                    if(pass == 0 && recipient.block() instanceof NeoplasiaproductionBlock factory && factory.shouldCraft){
                        target = factory.craftCost * 3f + recipient.lifeReserve();
                    }else if(pass == 1 && recipient instanceof NeoplasmTreeBase.NeoplasmTreeBaseBuild tree){
                        NeoplasmTreeBase block = (NeoplasmTreeBase)tree.block;
                        target = block.branchCost * 4f + block.podCost * Math.max(1, tree.branches.size) + tree.lifeReserve();
                    }
                    float flow = Math.min(budget, Math.max(0f, Math.min(target, recipient.massCapacity()) - recipient.amount));
                    if(flow <= 0f) continue;
                    donor.amount -= flow;
                    recipient.receiveMass(flow);
                    recordFlow(donor, recipient, flow * 0.1f);
                    budget -= flow;
                    if(budget <= 0f) break;
                }
            }
        }
    }

    public static class NeoplasiaChunk {
        public int cx, cy;
        public Seq<NeoplasiaBuild> builds = new Seq<>();

        public NeoplasiaChunk(int cx, int cy) {
            this.cx = cx;
            this.cy = cy;
        }
    }

    public static void reset() {
        NeoplasmTreeBase.trees.clear();
        GenericNeoplasiaBlock.nextTreeSeed.clear();
        GenericNeoplasiaBlock.clearSearch();
        supplies.clear();
        indexingSupply = null;
        pulseQueue.clear();
        flowAncestors.clear();
        routeQueue.clear();
        routeCosts.clear();
        veinCounts.clear();
        veinLimits.clear();
        veinGrowth.clear();
        veinRoots.clear();
        rankedVeins.clear();
        NeoplasiaItemTransit.reset();
        hearts.clear();
        primeHeart = null;
        compMap.clear();
        heartCounts.clear();
        biggestBlobs.clear();
        biggestAmounts.clear();
        comps.clear();
        chunks.clear();
        activeNeoplasia.clear();
        members.clear();
        deliveryComponents.clear();
        flowParents.clear();
        dirty = false;
        trackedNeoplasia = false;
        pulseTimer = 0f;
        spawnCooldown = 0f;
        if (Vars.world.tiles != null) {
            for (Tile tile : Vars.world.tiles) {
                if (tile.build instanceof NeoplasmHeart.NeoplasmHeartBuild hb) {
                    Vars.state.teams.registerCore(hb);
                    hearts.add(hb);
                    if (hb.isPrime) primeHeart = hb;
                } else if (tile.build instanceof NeoplasiaBuild nb) {
                    register(nb);
                }
            }
            if (primeHeart == null && hearts.size > 0) {
                primeHeart = hearts.first();
                primeHeart.isPrime = true;
            }
        }
    }
}
