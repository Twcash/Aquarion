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
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.UnitTypes;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.game.Team;
import mindustry.type.ItemStack;
import mindustry.type.UnitType;
import mindustry.world.Tile;
import mindustry.world.meta.Env;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class NeoplasmTreeBase extends GenericNeoplasiaBlock {
    public UnitType unitType = UnitTypes.flare;
    public UnitType spewerType;
    public float spewerChance = 0.3f;
    public int maxSpewers = 24;
    public ItemStack[] spewerItemCost;
    public float unitGrowTime = 10f;
    public float podCost = 100f;
    public float branchCost = 200f;
    public int maxPodsPerBranch = 2;
    /** Maximum number of live spawned units of this unitType on the team before the tree stops producing more. */
    public int maxPoppers = 64;
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
        int r = (int) spacing;
        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -r; dy <= r; dy++) {
                if (Mathf.dst(dx, dy) > spacing) continue;
                Tile other = world.tile(tile.x + dx, tile.y + dy);
                if (other != null && other.build instanceof NeoplasmTreeBaseBuild) return false;
            }
        }
        return true;
    }

    public class LeafPod {
        float position;
        float progress;
        int side;

        void write(Writes write) {
            write.f(position);
            write.f(progress);
            write.b(side);
        }

        void read(Reads read) {
            position = read.f();
            progress = read.f();
            side = read.b();
        }
    }

    public class BranchNode {
        int level;
        float angle, length, thickness, phase, attachPos;
        int parentIndex;
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
            write.b(level);
            write.f(angle);
            write.f(length);
            write.f(thickness);
            write.f(phase);
            write.f(attachPos);
            write.s(parentIndex);
            write.b(pods.size);
            for (LeafPod pod : pods) pod.write(write);
        }

        void read(Reads read) {
            level = read.b();
            angle = read.f();
            length = read.f();
            thickness = read.f();
            phase = read.f();
            attachPos = read.f();
            parentIndex = read.s();
            int pc = read.b();
            pods.clear();
            for (int i = 0; i < pc; i++) {
                LeafPod pod = new LeafPod();
                pod.read(read);
                pods.add(pod);
            }
        }
    }

    public class NeoplasmTreeBaseBuild extends NeoplasiaBuild {
        public Seq<BranchNode> branches = new Seq<>();
        public float treeAngle = Mathf.random(360f);
        public float spawnCountTimer;
        public int livePoppers;
        public int liveSpewers;
        public float spawnSpotTimer;
        public boolean hasSpawnSpot;

        @Override
        public void updateTile() {
            super.updateTile();
            growBranches();
            managePods();
            spawnCountTimer -= Time.delta;
            if (spawnCountTimer <= 0f) {
                spawnCountTimer = 90f;
                livePoppers = countPoppers();
                liveSpewers = countSpewers();
            }
            spawnSpotTimer -= Time.delta;
            if (spawnSpotTimer <= 0f) {
                spawnSpotTimer = 90f;
                hasSpawnSpot = scanSpawnSpot();
            }
            tickPods();
        }

        void growBranches() {
            float s = Mathf.clamp(amount / maxAmount);
            int targetL0 = Math.max(2, Math.min(maxBranchesByLevel[0], (int) (s * maxBranchesByLevel[0])));
            while (countBranches(0) < targetL0 && amount >= branchCost * 2f) {
                amount -= branchCost * 2f;
                int idx = countBranches(0);
                float base = treeAngle + 360f / maxBranchesByLevel[0] * idx;
                float spread = 20f + Mathf.random(10f);
                float len = branchLengthByLevel[0] * (0.7f + Mathf.random(0.6f));
                float thick = branchThicknessByLevel[0] * (0.8f + Mathf.random(0.4f));
                branches.add(new BranchNode(0, base - spread, len, thick, Mathf.random(360f), 0f, -1));
                branches.add(new BranchNode(0, base + spread, len, thick, Mathf.random(360f), 0f, -1));
            }
            if (s > 0.35f) {
                for (int i = 0; i < branches.size; i++) {
                    BranchNode p = branches.get(i);
                    if (p.level != 0) continue;
                    int targetL1 = Math.max(1, Math.min(maxBranchesByLevel[1] / maxBranchesByLevel[0], 1 + (int) ((s - 0.35f) * 4f)));
                    int haveL1 = countChildren(i);
                    while (haveL1 < targetL1 && amount >= branchCost * 3f) {
                        amount -= branchCost * 3f;
                        float spread = 25f + Mathf.random(15f);
                        float base = p.angle + Mathf.random(-10f, 10f);
                        float len = branchLengthByLevel[1] * (0.6f + Mathf.random(0.8f));
                        float thick = branchThicknessByLevel[1] * (0.7f + Mathf.random(0.6f));
                        float pos = 0.3f + Mathf.random(0.4f);
                        branches.add(new BranchNode(1, base - spread, len, thick, Mathf.random(360f), pos, i));
                        branches.add(new BranchNode(1, base + spread, len, thick, Mathf.random(360f), pos, i));
                        haveL1 += 2;
                    }
                }
            }
            if (s > 0.7f) {
                int l1count = countBranches(1);
                for (int i = 0; i < branches.size; i++) {
                    BranchNode p = branches.get(i);
                    if (p.level != 1) continue;
                    int targetL2 = Math.min(maxBranchesByLevel[2] / Math.max(1, l1count), 1);
                    int haveL2 = countChildren(i);
                    while (haveL2 < targetL2 && amount >= branchCost * 4f) {
                        amount -= branchCost * 4f;
                        float spread = 20f + Mathf.random(20f);
                        float base = p.angle + Mathf.random(-10f, 10f);
                        float len = branchLengthByLevel[2] * (0.5f + Mathf.random(1f));
                        float thick = branchThicknessByLevel[2] * (0.6f + Mathf.random(0.8f));
                        float pos = 0.5f + Mathf.random(0.4f);
                        branches.add(new BranchNode(2, base - spread, len, thick, Mathf.random(360f), pos, i));
                        branches.add(new BranchNode(2, base + spread, len, thick, Mathf.random(360f), pos, i));
                        haveL2 += 2;
                    }
                }
            }
        }

        int countBranches(int level) {
            int c = 0;
            for (BranchNode b : branches) if (b.level == level) c++;
            return c;
        }

        int countChildren(int parentIndex) {
            int c = 0;
            for (BranchNode b : branches) if (b.parentIndex == parentIndex) c++;
            return c;
        }

        void managePods() {
            for (BranchNode branch : branches) {
                while (branch.pods.size < maxPodsPerBranch && amount >= podCost) {
                    amount -= podCost;
                    LeafPod pod = new LeafPod();
                    pod.position = 0.2f + Mathf.random(0.7f);
                    pod.progress = 0f;
                    pod.side = Mathf.randomBoolean() ? 1 : -1;
                    branch.pods.add(pod);
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
            for (BranchNode branch : branches) {
                for (int pi = 0; pi < branch.pods.size; pi++) {
                    LeafPod pod = branch.pods.get(pi);
                    if (!hasUnitCost(unitCost())) continue;
                    if (livePoppers >= maxPoppers) continue;
                    if (!hasSpawnSpot) continue;
                    pod.progress += Time.delta;
                    if (pod.progress >= unitGrowTime) {
                        pod.progress = 0f;
                        if (spawnSpewer(branch, pod)) continue;
                        consumeUnitCost(unitCost());
                        leafPos(branch, pod, tmp2);
                        float px = tmp2[0], py = tmp2[1];
                        float angle = Mathf.random(360f);
                        float dist = Mathf.random(2f, 6f);
                        unitType.spawn(team, px + Mathf.cosDeg(angle) * dist, py + Mathf.sinDeg(angle) * dist);
                    }
                }
            }
        }

        boolean spawnSpewer(BranchNode branch, LeafPod pod) {
            if (spewerType == null || liveSpewers >= maxSpewers) return false;
            if (!Mathf.chance(spewerChance)) return false;
            ItemStack[] cost = spewerCost();
            if (!hasUnitCost(cost)) return false;
            consumeUnitCost(cost);
            leafPos(branch, pod, tmp2);
            float angle = Mathf.random(360f);
            float dist = Mathf.random(2f, 6f);
            spewerType.spawn(team, tmp2[0] + Mathf.cosDeg(angle) * dist, tmp2[1] + Mathf.sinDeg(angle) * dist);
            return true;
        }

        ItemStack[] unitCost() {
            return unitItemCost;
        }

        ItemStack[] spewerCost() {
            return spewerItemCost != null ? spewerItemCost : unitItemCost;
        }

        int countPoppers() {
            return Groups.unit.count(u -> u.team == team && u.type == unitType);
        }

        int countSpewers() {
            return Groups.unit.count(u -> u.team == team && u.type == spewerType);
        }

        boolean scanSpawnSpot() {
            int r = (int) (spawnScanRadius / tilesize);
            for (int dx = -r; dx <= r; dx++) {
                for (int dy = -r; dy <= r; dy++) {
                    if (Mathf.dst(dx, dy) > r) continue;
                    Tile t = world.tile(tile.x + dx, tile.y + dy);
                    if (t == null || t.solid() || t.floor().isDeep() || t.build != null) continue;
                    return true;
                }
            }
            return false;
        }

        @Override
        void tryUpgrades() {
            super.tryUpgrades();
            if (unitItemCost != null) {
                for (ItemStack stack : unitItemCost) {
                    needItem(stack.item, stack.amount);
                }
            }
            if (spewerItemCost != null) {
                for (ItemStack stack : spewerItemCost) {
                    needItem(stack.item, stack.amount);
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

        void branchEndpoints(BranchNode branch, float[] out) {
            float sway = Mathf.sin(Time.time / 80f + branch.phase) * 4f;
            float a = branch.angle + sway;
            float bx, by;
            if (branch.parentIndex < 0) {
                bx = x;
                by = y;
            } else {
                BranchNode parent = branches.get(branch.parentIndex);
                float pa = parent.angle + Mathf.sin(Time.time / 80f + parent.phase) * 4f;
                float pl = parent.length * branchScale(parent);
                bx = x + Mathf.cosDeg(pa) * pl * parent.attachPos;
                by = y + Mathf.sinDeg(pa) * pl * parent.attachPos;
            }
            float len = branch.length * branchScale(branch);
            out[0] = bx;
            out[1] = by;
            out[2] = bx + Mathf.cosDeg(a) * len;
            out[3] = by + Mathf.sinDeg(a) * len;
        }

        float branchScale(BranchNode branch) {
            return 0.5f + 0.5f * Mathf.clamp(amount / maxAmount);
        }


        @Override
        public void draw() {
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

            Draw.z(Renderer.Layer.blockOver);
            for (BranchNode branch : branches) {
                branchEndpoints(branch, tmp4);
                float bx = tmp4[0], by = tmp4[1], tx = tmp4[2], ty = tmp4[3];
                float thick = branch.thickness * branchScale(branch);
                float ang = Mathf.angle(tx - bx, ty - by);
                    Lines.stroke(thick * 5f);
                    Lines.line(branchRegion, bx, by, tx, ty, true);
                    for (int li = 0; li < 2; li++) {
                        float seed = Mathf.sin(branch.phase * 7.3f + li * 11.7f) * 0.5f + 0.5f;
                        float pos = 0.2f + seed * 0.55f;
                        float lx = bx + (tx - bx) * pos;
                        float ly = by + (ty - by) * pos;
                        float side = li == 0 ? -1f : 1f;
                        float sway = Mathf.sin(Time.time / 55f + pos * 9f + branch.phase + li * 2f) * 5f;
                        float size = 2.6f + Mathf.sin(branch.phase * 3.1f + li) * 0.4f;
                        float leafAng = ang + side * 45f + sway * 0.4f;
                        float offX = Mathf.cosDeg(ang + side * 90f) * 1.5f;
                        float offY = Mathf.sinDeg(ang + side * 90f) * 1.5f + 1.5f + Math.abs(sway) * 0.25f;
                        Draw.color();
                        Draw.rect(leafRegion, lx + offX, ly + offY, size * 2f, size * 2f, leafAng - 90);
                    }
                for (LeafPod pod : branch.pods) {

                    if (pod.progress <= 0f) continue;
                    leafPos(branch, pod, tmp2);
                    float px = tmp2[0], py = tmp2[1];
                    float podProgress = Mathf.clamp(pod.progress / unitGrowTime);
                    float podSize = 2f + podProgress * 6f;
                    float podSway = Mathf.sin(Time.time / 45f + pod.position * 8f + branch.phase) * 6f;
                    float podAng = ang + pod.side * (15f + podSway * 0.3f);
                        Draw.rect(podRegion, px, py, podSize * 1.25f, podSize * 1.25f, podAng - 90);
                }
            }
            Draw.color();
            Lines.stroke(1f);
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.s(branches.size);
            for (BranchNode branch : branches) branch.write(write);
            write.f(treeAngle);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            int bc = read.s();
            branches.clear();
            for (int i = 0; i < bc; i++) {
                BranchNode branch = new BranchNode(0, 0f, 0f, 0f, 0f, 0f, -1);
                branch.read(read);
                branches.add(branch);
            }
            treeAngle = read.f();
        }
    }
}
