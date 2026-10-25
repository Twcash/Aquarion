package aquarion.world.blocks.neoplasia;

import aquarion.world.graphics.Renderer;
import arc.Core;
import arc.func.Cons;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.struct.IntSet;
import arc.struct.IntFloatMap;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.UnitTypes;
import mindustry.gen.Building;
import mindustry.entities.Units;
import mindustry.game.Team;
import mindustry.type.ItemStack;
import mindustry.type.UnitType;
import mindustry.Vars;
import mindustry.world.Tile;
import mindustry.world.meta.Env;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class NeoplasmTreeBase extends GenericNeoplasiaBlock {
    public static final Seq<NeoplasmTreeBaseBuild> trees = new Seq<>();
    public UnitType unitType = UnitTypes.flare;
    public UnitType spewerType;
    public float spewerChance = 0.3f;
    public ItemStack[] spewerItemCost;
    /** Growth times and intervals are in ticks (60 ticks per second). */
    public float unitGrowTime = 600f;
    /** Time between starting new pod cycles; finished pods hatch independently. */
    public float unitSpawnInterval = 30f * 60f;
    public float podCreationInterval = 5f * 60f;
    public float podGrowthVariation = 0.15f;
    public float branchGrowTime = 180f;
    public float branchInterval = 60f;
    /** After the initial crown, tips keep extending while mass is available. */
    public boolean continuousGrowth = true;
    public int maxChildrenPerBranch = 3;
    public int branchExpansionStages = 3;
    /** Added cross-sectional support for each mature descendant limb. */
    public float branchThickening = 0.12f;
    public float canopyDamage = 12f;
    public float canopyInterval = 30f;
    public boolean damageFriendly = false;
    /** Above structures, turrets, power lines, and environment walls; below flying units and effects. */
    public float canopyLayer = mindustry.graphics.Layer.flyingUnitLow - 1f;
    public float limbShadowOffset = 6f;
    public float limbShadowAlpha = 0.25f;
    /** This share of incoming mass is reserved for structural growth rather than pod crafting. */
    public float branchMassFraction = 0.25f;
    public float podCost = 100f;
    public float branchCost = 200f;
    public int maxPodsPerBranch = 2;
    /** Radius in world units around the tree within which an empty floor tile must exist to keep producing poppers. */
    public float spawnScanRadius = 200f;
    public ItemStack[] unitItemCost;
    public float minVisualSize = 14f;
    public float maxVisualSize = 70f;
    public int[] maxBranchesByLevel = {8, 12, 16};
    public float[] branchLengthByLevel = {44f, 28f, 16f};
    public float[] branchThicknessByLevel = {3f, 1.8f, 0.9f};
    public float spacing = 16f;
    public TextureRegion branchRegion;
    public TextureRegion podRegion;
    public TextureRegion leafRegion;

    public NeoplasmTreeBase(String name) {
        super(name);
        shouldEmptyUpgrade = false;
        shouldEmpty2Upgrade = false;
        envDisabled = Env.none;
        hasItems = true;
    }

    @Override
    public void load() {
        super.load();
        branchRegion = Core.atlas.find(name + "-branch");
        podRegion = Core.atlas.find(name + "-pod");
        leafRegion = Core.atlas.find(name + "-leaf");
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        if (tile.floor().isDeep()) return false;
        if (!tile.floor().isFloor()) return false;
        for(NeoplasmTreeBaseBuild tree : trees){
            if(tree.dead() || tree.tile == null || tree.tile.build != tree) continue;
            float clearance = Math.max(spacing * tilesize, tree.radius + tilesize * 2f);
            if(Mathf.dst2(tile.worldx(), tile.worldy(), tree.x, tree.y) < clearance * clearance) return false;
        }
        return true;
    }

    public class LeafPod {
        float position;
        float progress;
        int side;
        int plannedUnit = -1;
        int cycle;
        boolean growing;
        float growDuration;
        float idleTime;

        void write(Writes write) {
            write.f(position);
            write.f(progress);
            write.b(side);
            write.b(plannedUnit);
            write.i(cycle);
            write.bool(growing);
            write.f(growDuration);
            write.f(idleTime);
        }

        void read(Reads read, byte revision) {
            position = read.f();
            progress = read.f();
            side = read.b();
            plannedUnit = revision >= 4 ? read.b() : -1;
            cycle = revision >= 4 ? read.i() : 0;
            growing = revision >= 5 && read.bool();
            growDuration = revision >= 5 ? read.f() : 0f;
            idleTime = revision >= 5 ? read.f() : 0f;
        }
    }

    public class BranchNode {
        int level;
        float angle, length, thickness, phase, attachPos;
        int parentIndex;
        float growth;
        int index;
        int expansionStage = 1;
        Seq<LeafPod> pods = new Seq<>();

        BranchNode(int level, float angle, float length, float thickness, float phase, float attachPos, int parentIndex) {
            this.level = level;
            this.angle = angle;
            this.length = length;
            this.thickness = thickness;
            this.phase = phase;
            this.attachPos = attachPos;
            this.parentIndex = parentIndex;
        }

        void write(Writes write) {
            write.i(level);
            write.f(angle);
            write.f(length);
            write.f(thickness);
            write.f(phase);
            write.f(attachPos);
            write.i(parentIndex);
            write.i(expansionStage);
            write.b(pods.size);
            for (LeafPod pod : pods) pod.write(write);
        }

        void read(Reads read, byte revision) {
            level = revision >= 2 ? read.i() : read.ub();
            angle = read.f();
            length = read.f();
            thickness = read.f();
            phase = read.f();
            attachPos = read.f();
            parentIndex = revision >= 2 ? read.i() : read.s();
            expansionStage = revision >= 6 ? read.i() : 1;
            // Revision 6 stored an extra length multiplier for each expansion stage.
            if(revision == 6) length /= Math.max(1, expansionStage);
            int pc = read.b();
            pods.clear();
            for (int i = 0; i < pc; i++) {
                LeafPod pod = new LeafPod();
                pod.read(read, revision);
                pods.add(pod);
            }
        }
    }

    public class NeoplasmTreeBaseBuild extends NeoplasiaBuild {
        public Seq<BranchNode> branches = new Seq<>();
        public float treeAngle = Mathf.random(360f);
        public float spawnSpotTimer;
        public boolean hasSpawnSpot;
        public float branchTimer;
        public float branchBudget;
        float unitSpawnCooldown;
        float podCreationCooldown;
        int podCreationCursor;
        float canopyTimer;
        float geometryTime = Float.NaN;
        int geometryCount = -1;
        int childCountSize = -1;
        int[] childCounts = new int[0];
        float[] geometry = new float[0];
        float[] limbSupport = new float[0];
        float radius;
        long lastDrawFrame = Long.MIN_VALUE;
        final IntSet damagedBuildings = new IntSet();
        final IntFloatMap terrainDamage = new IntFloatMap();

        @Override
        public void receiveMass(float value){
            float reserved = value * branchMassFraction;
            branchBudget += reserved;
            amount += value - reserved;
        }

        @Override
        public float massCapacity(){
            return maxAmount + branches.size * branchCost * 8f;
        }

        @Override
        void grow(){
            receiveMass((selfGrowRate + (isOre(tile) ? oreGrowBonus : 0f)) * delta());
        }

        boolean canGrowBranch(float price){
            return branchBudget + Math.max(0f, amount - lifeReserve()) >= price;
        }

        void payBranch(float price){
            float reserved = Math.min(branchBudget, price);
            branchBudget -= reserved;
            amount -= price - reserved;
        }

        @Override
        public void updateTile() {
            super.updateTile();
            if(tile == null || tile.build != this || dead()) return;
            growBranches();
            managePods();
            spawnSpotTimer -= Time.delta;
            if (spawnSpotTimer <= 0f) {
                spawnSpotTimer = 90f;
                hasSpawnSpot = scanSpawnSpot();
            }
            tickPods();
            canopyTimer += delta();
            if(canopyTimer >= canopyInterval){
                float elapsed = canopyTimer;
                canopyTimer = 0f;
                if(!Vars.net.client()) affectCanopy(elapsed);
            }
        }

        void growBranches() {
            geometryTime = Float.NaN;
            for(BranchNode branch : branches){
                branch.growth = Math.min(1f, branch.growth + delta() / branchGrowTime);
            }
            branchTimer += delta();
            if(branchTimer < branchInterval) return;
            for(BranchNode limb : branches){
                if(limb.growth < 1f || limb.expansionStage >= branchExpansionStages) continue;
                float price = branchCost * (Math.min(10, limb.level) + 2f);
                if(!canGrowBranch(price)) return;
                int previous = limb.expansionStage++;
                // Preserve the visible endpoint as the next length increment begins growing.
                limb.growth = (float)previous / limb.expansionStage;
                payBranch(price);
                branchTimer = 0f;
                return;
            }
            for(int level = 0; level < maxBranchesByLevel.length; level++){
                if(countBranches(level) >= maxBranchesByLevel[level]) continue;
                float price = branchCost * (level + 2f);
                if(!canGrowBranch(price)) return;
                int parent = -1, children = Integer.MAX_VALUE;
                if(level > 0){
                    for(int i = 0; i < branches.size; i++){
                        BranchNode candidate = branches.get(i);
                        if(candidate.level == level - 1 && candidate.growth >= 1f && candidate.expansionStage >= branchExpansionStages && countChildren(i) < children){
                            parent = i;
                            children = countChildren(i);
                        }
                    }
                    if(parent < 0) return;
                }
                long seed = tile.pos() * 31L + branches.size * 7L;
                float angle = parent < 0
                    ? treeAngle + 360f * countBranches(0) / maxBranchesByLevel[0]
                    : branches.get(parent).angle + (children % 2 == 0 ? -1 : 1) * Mathf.randomSeed(seed, 20f, 45f);
                float length = branchLengthByLevel[level] * Mathf.randomSeed(seed + 1, 0.9f, 1.1f);
                float attach = Mathf.randomSeed(seed + 3, 0.3f, 0.8f);
                if(parent >= 0) angle = outwardAngle(parent, attach, length, children, seed);
                branches.add(new BranchNode(level, angle,
                    length,
                    branchThicknessByLevel[level], Mathf.randomSeed(seed + 2, 0f, 360f),
                    attach, parent));
                payBranch(price);
                branchTimer = 0f;
                return;
            }
            if(!continuousGrowth) return;
            int parent = -1;
            for(int i = 0; i < branches.size; i++){
                BranchNode candidate = branches.get(i);
                if(candidate.growth < 1f || candidate.expansionStage < branchExpansionStages || countChildren(i) >= maxChildrenPerBranch) continue;
                if(parent < 0 || candidate.level < branches.get(parent).level ||
                    candidate.level == branches.get(parent).level && countChildren(i) < countChildren(parent)) parent = i;
            }
            if(parent < 0) return;
            BranchNode tip = branches.get(parent);
            float price = branchCost * (Math.min(10, tip.level) + 3f);
            if(!canGrowBranch(price)) return;
            long seed = tile.pos() * 31L + branches.size * 7L;
            int children = countChildren(parent);
            int last = branchLengthByLevel.length - 1;
            float length = branchLengthByLevel[last] * Mathf.randomSeed(seed + 1, 0.9f, 1.1f);
            float attach = children == 0 ? 1f : Mathf.randomSeed(seed + 3, 0.45f, 0.85f);
            float angle = outwardAngle(parent, attach, length, children, seed);
            branches.add(new BranchNode(tip.level + 1, angle,
                length,
                branchThicknessByLevel[last], Mathf.randomSeed(seed + 2, 0f, 360f),
                attach, parent));
            payBranch(price);
            branchTimer = 0f;
            geometryTime = Float.NaN;
        }

        float outwardAngle(int parent, float attach, float length, int children, long seed){
            updateGeometry();
            int offset = parent * 4;
            float bx = Mathf.lerp(geometry[offset], geometry[offset + 2], attach);
            float by = Mathf.lerp(geometry[offset + 1], geometry[offset + 3], attach);
            float radial = Mathf.angle(bx - x, by - y);
            float preferred = children == 0 ? 0f : (children % 2 == 1 ? -40f : 40f);
            float bestAngle = radial, bestScore = -Float.MAX_VALUE;
            for(int attempt = 0; attempt < 7; attempt++){
                float turn = attempt == 0 ? preferred : Mathf.randomSeed(seed + attempt * 37L, -55f, 55f);
                float angle = radial + turn;
                float tx = bx + Mathf.cosDeg(angle) * length, ty = by + Mathf.sinDeg(angle) * length;
                float clearance = length * length * 4f;
                for(int i = 0; i < branches.size; i++){
                    if(i == parent) continue;
                    clearance = Math.min(clearance, Mathf.dst2(tx, ty, geometry[i * 4 + 2], geometry[i * 4 + 3]));
                }
                float score = clearance + Mathf.dst2(x, y, tx, ty) * 0.05f;
                if(score > bestScore){ bestScore = score; bestAngle = angle; }
            }
            return bestAngle;
        }
        int countBranches(int level) {
            int c = 0;
            for (BranchNode b : branches) if (b.level == level) c++;
            return c;
        }

        int countChildren(int parentIndex) {
            if(childCountSize != branches.size){
                childCountSize = branches.size;
                if(childCounts.length < branches.size) childCounts = new int[Math.max(branches.size, childCounts.length * 2)];
                java.util.Arrays.fill(childCounts, 0);
                for(BranchNode branch : branches){
                    if(branch.parentIndex >= 0 && branch.parentIndex < branches.size) childCounts[branch.parentIndex]++;
                }
            }
            return parentIndex >= 0 && parentIndex < branches.size ? childCounts[parentIndex] : 0;
        }

        void managePods() {
            podCreationCooldown = Math.max(0f, podCreationCooldown - delta());
            if(podCreationCooldown > 0f || branches.isEmpty() || amount < podCost + lifeReserve()) return;
            for (int offset = 0; offset < branches.size; offset++) {
                int bi = (podCreationCursor + offset) % branches.size;
                BranchNode branch = branches.get(bi);
                if(branch.growth < 1f) continue;
                if (branch.pods.size < maxPodsPerBranch) {
                    amount -= podCost;
                    long seed = tile.pos() * 13L + bi * 7L + branch.pods.size * 3L;
                    LeafPod pod = new LeafPod();
                    pod.position = 0.2f + Mathf.randomSeed(seed, 0f, 0.7f);
                    pod.progress = 0f;
                    pod.side = Mathf.randomSeed(seed + 1, 0, 1) == 0 ? -1 : 1;
                    branch.pods.add(pod);
                    podCreationCursor = (bi + 1) % branches.size;
                    podCreationCooldown = podCreationInterval;
                    return;
                }
            }
        }

        float[] tmp4 = new float[4];
        float[] tmp2 = new float[2];
        final Cons<Vec2> wiggle = vec -> vec.add(
            Mathf.sin(vec.y * 3 + Time.time * 0.4f, wscl, wmag) + Mathf.sin(vec.x * 3 - Time.time * 0.3f, 70 * wtscl, 0.8f * wmag2),
            Mathf.cos(vec.x * 3 + Time.time + 4f, wscl + 6f, wmag * 1.1f) + Mathf.sin(vec.y * 3 - Time.time * 0.5f, 50 * wtscl, 0.2f * wmag2));

        void leafPos(BranchNode branch, LeafPod pod, float[] out) {
            branchEndpoints(branch, tmp4);
            float bx = tmp4[0], by = tmp4[1], tx = tmp4[2], ty = tmp4[3];
            float alongX = bx + (tx - bx) * pod.position;
            float alongY = by + (ty - by) * pod.position;
            float sway = Mathf.sin(Time.time / 60f + pod.position * 10f + branch.phase) * 3f;
            out[0] = alongX + sway;
            out[1] = alongY + 3f + Math.abs(sway);
        }

        void tickPods() {
            unitSpawnCooldown = Math.max(0f, unitSpawnCooldown - Time.delta);
            BranchNode nextBranch = null;
            LeafPod nextPod = null;
            for (BranchNode branch : branches) {
                for (int pi = 0; pi < branch.pods.size; pi++) {
                    LeafPod pod = branch.pods.get(pi);
                    if(!pod.growing) pod.idleTime += Time.delta;
                    if(pod.growing || unitSpawnCooldown > 0f || branch.growth < 1f || amount < podCost + lifeReserve()) continue;
                    planPod(branch, pod);
                    if(!hasSpawnSpot && pod.plannedUnit != 1) continue;
                    if (!hasUnitCost(pod.plannedUnit == 1 ? spewerCost() : unitCost())) continue;
                    if(nextPod == null || pod.idleTime > nextPod.idleTime){
                        nextBranch = branch;
                        nextPod = pod;
                    }
                }
            }
            if(nextPod != null){
                if(!Vars.net.client()){
                    consumeUnitCost(nextPod.plannedUnit == 1 ? spewerCost() : unitCost());
                    amount -= podCost;
                }
                long seed = tile.pos() * 31L + Float.floatToIntBits(nextBranch.phase) * 17L + Float.floatToIntBits(nextPod.position) * 13L + nextPod.cycle * 997L;
                nextPod.growDuration = Math.max(1f, unitGrowTime * Mathf.randomSeed(seed + 91, 1f - podGrowthVariation, 1f + podGrowthVariation));
                nextPod.growing = true;
                nextPod.idleTime = 0f;
                unitSpawnCooldown = unitSpawnInterval;
            }
            for(BranchNode branch : branches){
                for(LeafPod pod : branch.pods){
                    if(!pod.growing) continue;
                    // Resources were reserved at the start; other pods cannot stall this one.
                    pod.progress += Time.delta;
                    if(pod.progress < pod.growDuration || !hasSpawnSpot && pod.plannedUnit != 1 || branch.growth < 1f) continue;
                    UnitType offspring = pod.plannedUnit == 1 && spewerType != null ? spewerType : unitType;
                    if(!Vars.net.client()){
                        leafPos(branch, pod, tmp2);
                        float angle = Mathf.random(360f), dist = Mathf.random(2f, 6f);
                        offspring.spawn(team, tmp2[0] + Mathf.cosDeg(angle) * dist, tmp2[1] + Mathf.sinDeg(angle) * dist);
                    }
                    pod.progress = 0f;
                    pod.plannedUnit = -1;
                    pod.cycle++;
                    pod.growing = false;
                    pod.growDuration = 0f;
                }
            }
        }

        void planPod(BranchNode branch, LeafPod pod){
            if(pod.plannedUnit >= 0) return;
            long seed = tile.pos() * 31L + Float.floatToIntBits(branch.phase) * 17L + Float.floatToIntBits(pod.position) * 13L + pod.cycle * 997L;
            pod.plannedUnit = spewerType != null && Mathf.randomSeed(seed, 0f, 1f) < spewerChance ? 1 : 0;
        }

        ItemStack[] unitCost() {
            return unitItemCost;
        }

        ItemStack[] spewerCost() {
            return spewerItemCost != null ? spewerItemCost : unitItemCost;
        }

        boolean scanSpawnSpot() {
            int r = (int) (spawnScanRadius / tilesize);
            for (int dx = -r; dx <= r; dx++) {
                for (int dy = -r; dy <= r; dy++) {
                    if (Mathf.dst(dx, dy) > r) continue;
                    Tile t = world.tile(tile.x + dx, tile.y + dy);
                    if (t == null || t.block() != mindustry.content.Blocks.air || t.floor().isDeep() || t.build != null) continue;
                    return true;
                }
            }
            return false;
        }

        @Override
        void tryUpgrades() {
            super.tryUpgrades();
            int pods = 0;
            for(BranchNode branch : branches) pods += branch.pods.size;
            if (unitItemCost != null) {
                for (ItemStack stack : unitItemCost) {
                    needItem(stack.item, Math.min(itemCapacity, stack.amount * Math.max(1, pods)));
                }
            }
            if (spewerItemCost != null) {
                for (ItemStack stack : spewerItemCost) {
                    needItem(stack.item, Math.min(itemCapacity, stack.amount * Math.max(1, pods)));
                }
            }
        }

        boolean hasUnitCost(ItemStack[] cost) {
            if (cost == null) return true;
            for (ItemStack stack : cost) {
                if (items.get(stack.item) < stack.amount) return false;
            }
            return true;
        }

        void consumeUnitCost(ItemStack[] cost) {
            if (cost == null) return;
            for (ItemStack stack : cost) {
                items.remove(stack.item, stack.amount);
            }
        }

        void updateGeometry(){
            if(geometryTime == Time.time && geometryCount == branches.size) return;
            geometryTime = Time.time;
            geometryCount = branches.size;
            if(geometry.length < branches.size * 4) geometry = new float[Math.max(branches.size * 4, geometry.length * 2)];
            if(limbSupport.length < branches.size) limbSupport = new float[Math.max(branches.size, limbSupport.length * 2)];
            for(int i = 0; i < branches.size; i++) limbSupport[i] = limbMaturity(branches.get(i));
            // Accumulate support once, iteratively; very deep trees never recurse.
            for(int i = branches.size - 1; i >= 0; i--){
                int parent = branches.get(i).parentIndex;
                if(parent >= 0 && parent < i) limbSupport[parent] += limbSupport[i];
            }
            radius = minVisualSize / 2f;
            for(int i = 0; i < branches.size; i++){
                BranchNode branch = branches.get(i);
                branch.index = i;
                int offset = i * 4;
                float bx = x, by = y;
                if(branch.parentIndex >= 0 && branch.parentIndex < i){
                    int parent = branch.parentIndex * 4;
                    bx = Mathf.lerp(geometry[parent], geometry[parent + 2], branch.attachPos);
                    by = Mathf.lerp(geometry[parent + 1], geometry[parent + 3], branch.attachPos);
                }
                float angle = branch.angle + Mathf.sin(Time.time / 80f + branch.phase) * 4f;
                geometry[offset] = bx;
                geometry[offset + 1] = by;
                float length = branch.length * branch.growth * Math.min(1f, (float)branch.expansionStage / Math.max(1, branchExpansionStages));
                geometry[offset + 2] = bx + Mathf.cosDeg(angle) * length;
                geometry[offset + 3] = by + Mathf.sinDeg(angle) * length;
                float maturity = limbMaturity(branch);
                float width = branch.thickness * maturity * Mathf.sqrt(1f + Math.max(0f, limbSupport[i] - maturity) * branchThickening) * 2.5f;
                radius = Math.max(radius, Mathf.dst(x, y, geometry[offset + 2], geometry[offset + 3]) + width);
            }
        }

        public float drawingClipSize(){
            updateGeometry();
            float trunk = Mathf.lerp(minVisualSize, maxVisualSize, Mathf.clamp(amount / maxAmount));
            return Math.max(trunk, radius * 2f) + 2f * (Math.abs(limbShadowOffset) + tilesize * 2f);
        }

        void branchEndpoints(BranchNode branch, float[] out) {
            updateGeometry();
            int offset = branch.index * 4;
            System.arraycopy(geometry, offset, out, 0, 4);
        }

        public float canopyRadius(){
            updateGeometry();
            return radius;
        }

        void affectCanopy(float elapsed){
            float reach = canopyRadius();
            float damage = canopyDamage * elapsed / 60f;
            if(!damageFriendly && Vars.indexer != null){
                for(int i = trees.size - 1; i >= 0; i--){
                    NeoplasmTreeBaseBuild smaller = trees.get(i);
                    if(smaller != this && smaller.team == team && smaller.tile != null && smaller.tile.build == smaller && within(smaller, reach)) absorbTree(smaller, reach);
                }
                for(var data : Vars.state.teams.present){
                    if(data.team != team) Vars.indexer.eachBlock(data.team, x, y, reach, b -> !b.dead(), b -> b.damage(damage));
                }
                Units.nearbyEnemies(team, x, y, reach, unit -> {if(!unit.dead()) unit.damage(damage);});
                return;
            }
            damagedBuildings.clear();
            int r = (int)Math.ceil(reach / tilesize);
            int minX = Math.max(0, tile.x - r), maxX = Math.min(world.width() - 1, tile.x + r);
            int minY = Math.max(0, tile.y - r), maxY = Math.min(world.height() - 1, tile.y + r);
            for(int tx = minX; tx <= maxX; tx++) for(int ty = minY; ty <= maxY; ty++){
                Tile covered = world.tile(tx, ty);
                if(!within(covered, reach)) continue;
                Building other = covered.build;
                if(other != null){
                    if(other == this || !damagedBuildings.add(other.pos())) continue;
                    if(other instanceof NeoplasmTreeBaseBuild smaller && other.team == team){
                        absorbTree(smaller, reach);
                        continue;
                    }
                    if(other instanceof NeoplasiaBuild && other.team == team) continue;
                    if(damageFriendly || other.team != team) other.damage(damage);
                }else if(damageFriendly && covered.block() != mindustry.content.Blocks.air && (covered.block().solid || covered.block().breakable)){
                    float taken = terrainDamage.get(covered.pos(), 0f) + damage;
                    if(taken >= Math.max(1f, covered.block().health)){
                        terrainDamage.remove(covered.pos(), 0f);
                        covered.setAir();
                    }else{
                        terrainDamage.put(covered.pos(), taken);
                    }
                }
            }
            Units.nearby(x - reach, y - reach, reach * 2f, reach * 2f, unit -> {
                if(unit.dead() || !within(unit, reach)) return;
                if(unit.team == team && (unit.type == unitType || unit.type == spewerType)) return;
                if(damageFriendly || unit.team != team) unit.damage(damage);
            });
        }

        void absorbTree(NeoplasmTreeBaseBuild smaller, float reach){
            if(reach <= smaller.canopyRadius() + 0.1f || !within(smaller, reach)) return;
            float remnant = base == null ? 0f : Math.min(smaller.amount, Math.max(1f, base.maxAmount * 0.1f));
            receiveMass(smaller.amount + smaller.branchBudget + smaller.branches.size * branchCost - remnant);
            smaller.items.each((item, count) -> items.add(item, count));
            if(base == null) smaller.tile.setAir();
            else{
                smaller.tile.setBlock(base, team);
                if(smaller.tile.build instanceof NeoplasiaBuild tissue) tissue.amount = remnant;
            }
        }
        float branchScale(BranchNode branch) {
            updateGeometry();
            float maturity = limbMaturity(branch);
            return maturity * Mathf.sqrt(1f + Math.max(0f, limbSupport[branch.index] - maturity) * branchThickening);
        }

        float limbMaturity(BranchNode branch){
            return Math.min(1f, branch.growth * branch.expansionStage);
        }


        @Override
        public void draw() {
            // The canopy renderer also draws trees whose trunk is outside the camera.
            long frame = Core.graphics.getFrameId();
            if(lastDrawFrame == frame) return;
            lastDrawFrame = frame;
            float previousZ = Draw.z();
            float s = Mathf.clamp(amount / maxAmount);
            float drawSize = minVisualSize + (maxVisualSize - minVisualSize) * s;
            float saved = baseSize;
            baseSize = drawSize;
            super.draw();
            baseSize = saved;
            Draw.z(Renderer.Layer.blockOver + 2);

            float scale = 1f;
            if (spawnTime < spawnDuration) {
                scale = Interp.smooth.apply(spawnTime / spawnDuration);
            }
                Draw.scl(scale);
                Draw.z(Renderer.Layer.blockOver + 2);
                Draw.color();
                Draw.rectv(block.region, tile.worldx(), tile.worldy(), block.region.width * block.region.scl() * scale, block.region.height * block.region.scl() * scale, treeAngle, wiggle);
                Draw.scl(1f);

            Draw.z(Renderer.Layer.shadow);
            Draw.color(Color.black, limbShadowAlpha);
            for(BranchNode branch : branches){
                branchEndpoints(branch, tmp4);
                Lines.stroke(branch.thickness * branchScale(branch) * 5f);
                Lines.line(branchRegion, tmp4[0] - limbShadowOffset, tmp4[1] - limbShadowOffset,
                    tmp4[2] - limbShadowOffset, tmp4[3] - limbShadowOffset, true);
            }
            Draw.color();
            Draw.z(canopyLayer);
            for (BranchNode branch : branches) {
                branchEndpoints(branch, tmp4);
                float bx = tmp4[0], by = tmp4[1], tx = tmp4[2], ty = tmp4[3];
                float thick = branch.thickness * branchScale(branch);
                float ang = Mathf.angle(tx - bx, ty - by);
                    Lines.stroke(thick * 5f);
                    Lines.line(branchRegion, bx, by, tx, ty, true);
                    long leafSeed = tile.pos() * 23L + Float.floatToIntBits(branch.phase) * 7L + branch.index * 53L;
                    int leafCount = Mathf.randomSeed(leafSeed, 1, 4);
                    for (int li = 0; li < leafCount; li++) {
                        long seed = leafSeed + li * 127L;
                        float pos = Mathf.randomSeed(seed, 0.12f, 0.95f);
                        float lx = bx + (tx - bx) * pos;
                        float ly = by + (ty - by) * pos;
                        float side = Mathf.randomSeed(seed + 1, 0, 1) == 0 ? -1f : 1f;
                        float sway = Mathf.sin(Time.time / Mathf.randomSeed(seed + 2, 40f, 75f) + pos * 9f + branch.phase) * Mathf.randomSeed(seed + 3, 2f, 7f);
                        float size = Mathf.randomSeed(seed + 4, 1.8f, 3.8f) * limbMaturity(branch);
                        float leafAng = ang + side * Mathf.randomSeed(seed + 5, 25f, 80f) + sway * 0.4f;
                        float offset = Mathf.randomSeed(seed + 6, 1f, 3.5f);
                        float offX = Mathf.cosDeg(ang + side * 90f) * offset;
                        float offY = Mathf.sinDeg(ang + side * 90f) * offset + 1.5f + Math.abs(sway) * 0.25f;
                        Draw.color();
                        Draw.rect(leafRegion, lx + offX, ly + offY, size * 2f, size * 2f, leafAng - 90);
                    }
                for (LeafPod pod : branch.pods) {

                    if (pod.progress <= 0f) continue;
                    leafPos(branch, pod, tmp2);
                    float px = tmp2[0], py = tmp2[1];
                    float podProgress = Mathf.clamp(pod.progress / Math.max(1f, pod.growDuration > 0f ? pod.growDuration : unitGrowTime));
                    float podSize = 2f + podProgress * 6f;
                    float podSway = Mathf.sin(Time.time / 45f + pod.position * 8f + branch.phase) * 6f;
                    float podAng = ang + pod.side * (15f + podSway * 0.3f);
                        Draw.color();
                        Draw.alpha(1f - Mathf.clamp((podProgress - 0.2f) / 0.8f));
                        Draw.rect(podRegion, px, py, podSize * 1.25f, podSize * 1.25f, podAng - 90);
                    UnitType offspring = pod.plannedUnit == 1 && spewerType != null ? spewerType : unitType;
                    float embryo = Interp.smooth.apply(Mathf.clamp((podProgress - 0.1f) / 0.9f));
                    if(offspring != null && offspring.fullIcon != null && embryo > 0f){
                        TextureRegion icon = offspring.fullIcon;
                        Draw.alpha(embryo);
                        Draw.rect(icon, px, py, icon.width * icon.scl() * embryo,
                            icon.height * icon.scl() * embryo, podAng - 90);
                    }
                    Draw.color();
                }
            }
            Draw.color();
            Lines.stroke(1f);
            Draw.z(previousZ);
        }

        @Override
        public byte version(){ return transportSaveRevision + 7; }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.i(branches.size);
            for (BranchNode branch : branches) branch.write(write);
            write.f(treeAngle);
            for(BranchNode branch : branches){
                write.f(branch.growth);
            }
            write.f(branchBudget);
            write.f(unitSpawnCooldown);
            write.f(podCreationCooldown);
            write.i(podCreationCursor);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            int bc = revision >= 2 ? read.i() : read.s();
            branches.clear();
            for (int i = 0; i < bc; i++) {
                BranchNode branch = new BranchNode(0, 0f, 0f, 0f, 0f, 0f, -1);
                branch.read(read, revision);
                branches.add(branch);
            }
            treeAngle = read.f();
            for(BranchNode branch : branches){
                branch.growth = revision >= 1 ? read.f() : 1f;
            }
            branchBudget = revision >= 2 ? read.f() : 0f;
            unitSpawnCooldown = revision >= 3 ? read.f() : 0f;
            podCreationCooldown = revision >= 5 ? read.f() : 0f;
            podCreationCursor = revision >= 5 ? read.i() : 0;
            geometryTime = Float.NaN;
            childCountSize = -1;
        }
    }
}
