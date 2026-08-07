package aquarion.world.blocks.neoplasia;

import aquarion.world.entities.bullet.NeoplasmItemBulletType;
import aquarion.world.graphics.AquaShaders;
import aquarion.world.graphics.Renderer;
import arc.func.Cons;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.struct.ObjectIntMap;
import arc.struct.ObjectMap;
import arc.struct.ObjectSet;
import arc.struct.Queue;
import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Blocks;
import mindustry.entities.Units;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.ConstructBlock;
import mindustry.world.blocks.environment.Prop;
import mindustry.world.meta.BuildVisibility;

import static aquarion.world.blocks.neoplasia.NeoplasiaGraph.*;
import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;
import static mindustry.Vars.*;
import static mindustry.content.Blocks.*;

public class GenericNeoplasiaBlock extends Block {
    public static Seq<NeoplasiaBuild> activeNeoplasia = new Seq<>();
    public static ObjectMap<Item, GenericNeoplasiaBlock> itemProducers = new ObjectMap<>();
    public static GenericNeoplasiaBlock veinBlock;
    public static GenericNeoplasiaBlock treeBlock;
    public static final NeoplasmItemBulletType itemBullet = new NeoplasmItemBulletType();

    public float wscl = 25f, wmag = 0.4f, wtscl = 1f, wmag2 = 1f;
    public float maxAmount = 1000f;
    public float startMass = 0.001f;
    public float selfGrowRate = 0.04f;
    public float oreGrowBonus = 0.25f;
    public float cost = 10;//Internal var for build requests
    public ItemStack[] itemCost; //Internal item cost(s) for build requests
    public GenericNeoplasiaBlock base;
    //Multiplies the recent damage.
    public float upgradeDamageScale = 0.9f;
    public ItemStack output;
    public float emptyUpgradeCost = 400;
    public boolean shouldEmptyUpgrade = true;
    public GenericNeoplasiaBlock emptyUpgrade;
    public float baseSize = 8 * 1.5f;
    public int bursts = 1;
    public float damage = 1.25f;

    public float burstThresholdFraction = 0.3f;
    public float burstDelay = 150f;
    public int burstLength = 5;
    public float burstTransfer = 35f;
    public float recentDamageDecay = 0.004f;

    public GenericNeoplasiaBlock oreUpgrade;
    public GenericNeoplasiaBlock damageUpgrade;
    public GenericNeoplasiaBlock empty2Upgrade;
    public boolean shouldEmpty2Upgrade = false;
    public float empty2UpgradeCost = 800;

    public Color colFrom = Color.valueOf("701e1e");
    public Color colTo = Color.valueOf("cf5a3b");

    public float oreUpgradeCost = 300;

    public int requiredTech = 0;

    public float damageUpgradeCost = 10;

    public boolean perItemCapacity = false;

    public GenericNeoplasiaBlock(String name) {
        super(name);
        update = true;
        solid = false;
        destructible = true;
        rebuildable = false;
        drawTeamOverlay = false;
        hasItems = true;
        itemCapacity = 2;
        buildVisibility = BuildVisibility.sandboxOnly;
    }

    public ItemStack getOutput() {
        return output;
    }

    public boolean canUpgradeToThis(NeoplasiaBuild build) {
        return true;
    }

    public boolean isProducing(Item item) {
        ItemStack o = getOutput();
        return o != null && o.item == item;
    }

    public class NeoplasiaBuild extends Building {
        //TODO This is a LOT of variables for a single block, especially when there will be thousands of these.
        Item current;
        Tile burstTile;
        int burstStep = 0;
        float burstTimer = 0f;
        float burstCooldown = 0f;
        int currentBurstLength = 0;
        int burstDir = -1;
        public float amount = 0f;
        public float recentDamage = 0f;
        float spawnTime = 0f;
        float spawnDuration = 90f;
        float burstStepDelay = 4f;
        float clogTimer = 0f;
        float clogThreshold = 120f;
        float disconnectionTime = 0f;
        ObjectSet<Item> neededItems;
        ObjectIntMap<Item> neededAmounts;
        float requestTimer = 0f;
        float itemTraffic = 0f;
        ObjectMap<Item, Building> receivedFrom;
        public int inFlight = 0;
        final int maxFlights = 24;
        Unit damageTarget;
        float damageTargetDst;
        final Cons<Unit> scanEnemies = unit -> {
            if (unit.dead() || !unit.targetable(team)) return;
            float d = unit.dst2(this.x, this.y);
            if (d < damageTargetDst) {
                damageTargetDst = d;
                damageTarget = unit;
            }
        };
        final Queue<NeoplasiaBuild> searchQueue = new Queue<>();
        final ObjectSet<NeoplasiaBuild> searchVisited = new ObjectSet<>();

        void needItem(Item item, int amount) {
            if (neededItems == null) neededItems = new ObjectSet<>();
            if (neededAmounts == null) neededAmounts = new ObjectIntMap<>();
            neededItems.add(item);
            neededAmounts.put(item, amount);
        }

        int neededAmount(Item item) {
            return neededAmounts != null ? neededAmounts.get(item, 0) : 0;
        }


        public boolean hasItemRoom(Item item) {
            if (block().perItemCapacity) return items.get(item) < block().itemCapacity;
            return items.total() < block().itemCapacity;
        }

        @Override
        public void handleItem(Building source, Item item){
            if (hasItemRoom(item)) {
                items.add(item, 1);
            }
            if (source != null) {
                if (receivedFrom == null) receivedFrom = new ObjectMap<>();
                receivedFrom.put(item, source);
            }
            itemTraffic = Math.min(100f, itemTraffic + 1f);
        }
        void expelItems() {
            if (base == null) {
                items.clear();
                return;
            }
            items.each((stack, amt) -> {
                Tile t = pickBestNeighbor(tile);
                if (t != null && t.block() == air) {
                    t.setBlock(base, team);
                    if (t.build instanceof NeoplasiaBuild nb) {
                        nb.amount += amt;
                        nb.current = stack;
                    }
                }
            });
            items.clear();
        }
        public boolean isProducing(Item item){
            return block().isProducing(item);
        }



        @Override
        public boolean acceptItem(Building source, Item item) {
            return hasItemRoom(item);
        }

        boolean hasItemCost(ItemStack[] cost) {
            if (cost == null) return true;
            for (ItemStack stack : cost) {
                if (items.get(stack.item) < stack.amount) {
                    return false;
                }
            }
            return true;
        }

        void consumeItemCost(ItemStack[] cost) {
            if (cost == null) return;
            for (ItemStack stack : cost) {
                items.remove(stack.item, stack.amount);
            }
        }

        @Override
        public void created() {
            super.created();
            amount = startMass;
            NeoplasiaGraph.register(this);
        }

        public void remove(NeoplasiaBuild build){
            if (build.tile == null) return;
            int cx = chunkX(build.tile.x);
            int cy = chunkY(build.tile.y);
            long key = chunkKey(cx, cy);
            NeoplasiaGraph.NeoplasiaChunk chunk = chunks.get(key);
            if(chunk != null){
                chunk.builds.remove(build);
                if (chunk.builds.isEmpty()) {
                    chunks.remove(key);
                }
            }
        }

        @Override
        public void updateTile() {
            if (tile == null) return;
            if (neededItems != null) neededItems.clear();
            if (neededAmounts != null) neededAmounts.clear();
            health = amount;
            maxHealth = amount;
            recentDamage = Math.max(0f, recentDamage - recentDamageDecay);
            if (spawnTime < spawnDuration) spawnTime += delta();
            if (amount <= 0f) {
                kill();
                return;
            }

            boolean connected = NeoplasiaGraph.isConnected(this);
            if (!connected) {
                disconnectionTime += delta();
                amount -= amount * (0.001f + disconnectionTime * 0.00005f) * delta();
            } else {
                disconnectionTime = 0f;
                grow();
            }
            itemTraffic = Math.max(0, itemTraffic - 0.1f * delta());
            if (itemTraffic > 20f && connected && veinBlock != null && block().getClass() == GenericNeoplasiaBlock.class) {
                upgradeTo(veinBlock);
                return;
            }
            for (int i = 0; i < bursts; i++) burstSpread();
            damageNearby();
            tryUpgrades();
            requestItems();
            pushItems();
            trySpawnTree(connected);
            if (items.total() > 0 && (neededItems == null || neededItems.isEmpty())) {
                clogTimer += delta();
                if (clogTimer >= clogThreshold) {
                    expelItems();
                    clogTimer = 0f;
                }
            } else {
                clogTimer = 0f;
            }
        }

        void trySpawnTree(boolean connected) {
            if (treeBlock == null || !connected || amount < maxAmount * 0.6f) return;
            if (Mathf.chance(0.02f * delta())) {
                for (int dx = -3; dx <= 3; dx++) {
                    for (int dy = -3; dy <= 3; dy++) {
                        Tile other = world.tile(tile.x + dx, tile.y + dy);
                        if (other != null && other.block() == Blocks.air && treeBlock.canPlaceOn(other, team, 0)) {
                            float cost = maxAmount * 0.25f;
                            if (amount >= cost) {
                                amount -= cost;
                                other.setBlock(treeBlock, team);
                                if (other.build instanceof NeoplasiaBuild nb) {
                                    nb.amount = Math.min(cost, nb.block().maxAmount);
                                }
                            }
                            return;
                        }
                    }
                }
            }
        }

        @Override
        public void onRemoved() {
            super.onRemoved();
            remove(this);
            NeoplasiaGraph.compMap.remove(this, 0);
            activeNeoplasia.remove(this);
        }
        public GenericNeoplasiaBlock block(){
            return (GenericNeoplasiaBlock) this.block;
        }
        void upgradeTo(GenericNeoplasiaBlock target) {
            float savedAmount = amount;
            for (Item item : content.items()) {
                while (items.get(item) > 0) {
                    items.remove(item, 1);
                    boolean didPush = false;
                    for (Building neighbor : proximity) {
                        if (!(neighbor instanceof NeoplasiaBuild)) continue;
                        if (neighbor.items.total() >= neighbor.block.itemCapacity) continue;
                        if (!neighbor.acceptItem(this, item)) continue;
                        neighbor.handleItem(this, item);
                        didPush = true;
                        break;
                    }
                    if (!didPush) {
                        items.add(item, 1);
                        break;
                    }
                }
            }
            items.clear();
            tile.setBlock(Blocks.air, team);
            tile.setBlock(target, team);
            if (tile.build instanceof NeoplasiaBuild nb) {
                nb.amount = Math.min(nb.block().maxAmount, savedAmount);
            }
        }

        void tryUpgrades() {
            if(oreUpgrade != null && isOre(tile) && NeoplasiaGraph.techLevel() >= oreUpgrade.requiredTech){
                if(oreUpgrade.itemCost != null){
                    for (ItemStack stack : oreUpgrade.itemCost) {
                        needItem(stack.item, stack.amount);
                    }
                    if(!hasItemCost(oreUpgrade.itemCost)){
                        return;
                    }
                }
                if(amount >= oreUpgrade.cost){
                    consumeItemCost(oreUpgrade.itemCost);
                    upgradeTo(oreUpgrade);
                }
                return;
            }

            if(amount >= emptyUpgradeCost && !isOre(tile) && shouldEmptyUpgrade && emptyUpgrade != null && emptyUpgrade.canUpgradeToThis(this) && NeoplasiaGraph.techLevel() >= emptyUpgrade.requiredTech){
                    if(emptyUpgrade.itemCost != null){
                        for (ItemStack stack : emptyUpgrade.itemCost) {
                            needItem(stack.item, stack.amount);
                        }
                        if(!hasItemCost(emptyUpgrade.itemCost)){
                            return;
                        }
                    }
                    consumeItemCost(emptyUpgrade.itemCost);
                    upgradeTo(emptyUpgrade);
            }

            if(empty2Upgrade != null && amount >= empty2UpgradeCost && !isOre(tile) && shouldEmpty2Upgrade && NeoplasiaGraph.techLevel() >= empty2Upgrade.requiredTech){
                upgradeTo(empty2Upgrade);
            }

            float chance = recentDamage * upgradeDamageScale;
            if(amount >= damageUpgradeCost && Mathf.chanceDelta(chance) && damageUpgrade != null && NeoplasiaGraph.techLevel() >= damageUpgrade.requiredTech){
                upgradeTo(damageUpgrade);
            }
        }

        void grow() {
            float growth = selfGrowRate;
            if (isOre(tile)) growth += oreGrowBonus;
            amount = Math.min(maxAmount, amount + growth * delta());
        }

        void burstSpread() {
            if (tile == null) return;
            float threshold = maxAmount * burstThresholdFraction;
            if (burstDir == -1) {
                if (amount < threshold || burstCooldown > 0f) {
                    burstCooldown -= delta();
                    return;
                }
                burstTile = tile;
                burstStep = 0;
                burstTimer = 0f;
                currentBurstLength = 1 + (int) (Mathf.pow(Mathf.random(), 0.6f) * burstLength);
                burstDir = 0;
            }
            burstTimer += delta();
            if (burstTimer < burstStepDelay) return;
            burstTimer = 0f;
            if (burstTile == null || amount <= 1f) {
                resetBurst();
                return;
            }
            burstStep++;
            if (burstStep > currentBurstLength) {
                resetBurst();
                return;
            }
            Tile next = pickBestNeighbor(burstTile);
            if (next == null) {
                resetBurst();
                return;
            }
            float transfer = Math.min(burstTransfer, amount * 0.15f);
            if (transfer <= 0f) {
                resetBurst();
                return;
            }
            if (next.build instanceof NeoplasiaBuild n) {
                float flow = Math.min(transfer, amount);
                amount -= flow;
                n.amount = Math.min(maxAmount, n.amount + flow);
            } else if (next.block() == air && base != null) {
                next.setBlock(base, team);
                if (next.build instanceof NeoplasiaBuild nb) {
                    float flow = Math.min(transfer, amount);
                    amount -= flow;
                    nb.amount = flow;
                    if (current != null) {
                        nb.current = current;
                        current = null;
                    }
                }
            }
            burstTile = next;
            if (amount <= 1f) resetBurst();
        }

        Tile pickBestNeighbor(Tile origin) {
            if (origin == null) return null;
            int bestDir = -1;
            float bestScore = -999f;
            for (int i = 0; i < 8; i++) {
                Tile check = world.tile(origin.x + Geometry.d8[i].x, origin.y + Geometry.d8[i].y);
                if (check == null || check.solid() || check.floor().isDeep()) continue;
                float score = 0f;
                if (check.build == null) {
                    score += 12f;
                    if (check.overlay() != null && check.overlay().itemDrop != null) score += 13f;
                }
                if (check.build instanceof NeoplasiaBuild) score += 1.5f;
                score += Mathf.random(0.5f);
                if (score > bestScore) {
                    bestScore = score;
                    bestDir = i;
                }
            }
            if (bestDir == -1) return null;
            return world.tile(origin.x + Geometry.d8[bestDir].x, origin.y + Geometry.d8[bestDir].y);
        }

        void resetBurst() {
            burstDir = -1;
            burstCooldown = burstDelay;
            burstTile = null;
        }

        boolean isOre(Tile t) {
            return t != null && t.overlay() != null && t.overlay().itemDrop != null;
        }

        void damageNearby() {
            if (tile == null) return;
            for (int i = 0; i < 4; i++) {
                Tile t = tile.nearby(i);
                if (t == null) continue;
                if (t.block().unitMoveBreakable) ConstructBlock.deconstructFinish(t, t.block(), null);
                Building other = t.build;
                if (other != null && other.team != team) other.damage(damage * delta());
            }
            float range = tilesize * 2f;
            damageTarget = null;
            damageTargetDst = range * range;
            Units.nearbyEnemies(team, x - range, y - range, range * 2f, range * 2f, scanEnemies);
            if (damageTarget != null) {
                damageTarget.damage(damage * delta());
            }
        }

        void pullItems(Item item, int desired) {
            int need = desired - items.get(item);
            if (need <= 0) return;
            for (Building neighbor : proximity) {
                if (!(neighbor instanceof NeoplasiaBuild nb)) continue;
                int have = nb.items.get(item);
                if (have <= 0) continue;
                int take = Math.min(need, have);
                nb.items.remove(item, take);
                items.add(item, take);
                need -= take;
                if (need <= 0) return;
            }
        }

        void pushItems() {
            if (items.total() <= 0) return;
            int pushed = 0;
            for (int i = 0; i < pushRate; i++) {
                Item item = null;
                for (Item candidate : content.items()) {
                    int count = items.get(candidate);
                    if (count <= 0) continue;
                    int keep = neededAmount(candidate);
                    if (count > keep) {
                        item = candidate;
                        break;
                    }
                }
                if (item == null) break;
                Building target = null;
                Building sender = receivedFrom != null ? receivedFrom.get(item) : null;
                if (sender != null && !sender.isValid()) {
                    receivedFrom.remove(item);
                    sender = null;
                }
                for (Building neighbor : proximity) {
                    if (!(neighbor instanceof NeoplasiaBuild n)) continue;
                    if (neighbor == sender) continue;
                    if (n.items.get(item) > 0 && n.neededAmount(item) <= n.items.get(item)) continue;
                    if (!n.acceptItem(this, item)) continue;
                    target = n;
                    break;
                }
                if (target == null) break;
                items.remove(item, 1);
                target.handleItem(this, item);
                pushed++;
            }
            if (pushed > 0) clogTimer = 0f;
        }

        void spawnItemFlight(Item item, Building target) {
            inFlight++;
            itemBullet.create(this, team, x, y, Mathf.angle(target.x - x, target.y - y), 0f, 1f, 1f, new NeoplasmItemBulletType.Cargo(item, this, target));
        }

        // non-vein blobs push more per tick */
        final int pushRate = 8;

        NeoplasiaBuild findProducer(Item item, int maxSteps) {
            Queue<NeoplasiaBuild> queue = searchQueue;
            ObjectSet<NeoplasiaBuild> visited = searchVisited;
            queue.clear();
            visited.clear();
            queue.addLast(this);
            visited.add(this);
            int steps = 0;
            while (queue.size > 0 && steps < maxSteps) {
                NeoplasiaBuild cur = queue.removeFirst();
                steps++;
                for (Building neighbor : cur.proximity) {
                    if (!(neighbor instanceof NeoplasiaBuild nb)) continue;
                    if (visited.contains(nb)) continue;
                    visited.add(nb);
                    if (nb.isProducing(item)) return nb;
                    queue.addLast(nb);
                }
            }
            return null;
        }

        Tile findEmptyTile(int maxSteps) {
            Queue<NeoplasiaBuild> queue = searchQueue;
            ObjectSet<NeoplasiaBuild> visited = searchVisited;
            queue.clear();
            visited.clear();
            queue.addLast(this);
            visited.add(this);
            int steps = 0;
            while (queue.size > 0 && steps < maxSteps) {
                NeoplasiaBuild cur = queue.removeFirst();
                steps++;
                for (int i = 0; i < 4; i++) {
                    Tile t = cur.tile.nearby(i);
                    if (t != null && t.block() == air) return t;
                }
                for (Building neighbor : cur.proximity) {
                    if (neighbor instanceof NeoplasiaBuild nb && !visited.contains(nb)) {
                        visited.add(nb);
                        queue.addLast(nb);
                    }
                }
            }
            return null;
        }

        void requestItems() {
            if (neededItems == null || neededItems.isEmpty()) return;
            requestTimer += delta();
            if (requestTimer < 120f) return;
            requestTimer = 0;
            for (Item item : neededItems) {
                GenericNeoplasiaBlock factory = itemProducers.get(item);
                if (factory == null) continue;
                if (findProducer(item, 15) != null) continue;
                Tile spawnTile = findEmptyTile(10);
                if (spawnTile != null) {
                    spawnTile.setBlock(factory, team);
                }
            }
        }

        @Override
        public void draw() {
            float scale = 1f;
            if (spawnTime < spawnDuration) {
                float progress = spawnTime / spawnDuration;
                scale = Interp.smooth.apply(progress);
            }
            Draw.z(Renderer.Layer.neoplasiaBase);
            Draw.scl(scale);
            float radius = baseSize / 2f * scale;
            Draw.color(colFrom, colTo, amount / maxAmount);
            Fill.circle(x, y, radius);
            Draw.z(Renderer.Layer.neoplasiaUnder);
            Fill.circle(x, y, radius);
            Draw.scl(1f);
            ;
            Draw.z(Renderer.Layer.neoplasiaBase - 0.2f);
            Draw.color();
            if (items.first() != null) Draw.rect(items.first().fullIcon, x, y, 0);
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(amount);
            write.f(spawnTime);
            write.f(disconnectionTime);
            write.f(itemTraffic);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            amount = read.f();
            spawnTime = read.f();
            disconnectionTime = read.f();
            itemTraffic = read.f();
            current = items.first();
        }
    }
}