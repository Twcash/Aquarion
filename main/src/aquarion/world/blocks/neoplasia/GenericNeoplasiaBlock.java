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
import arc.util.Time;
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
import mindustry.world.blocks.environment.SteamVent;
import mindustry.world.meta.BuildVisibility;

import static aquarion.world.blocks.neoplasia.NeoplasiaGraph.*;
import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;
import static mindustry.Vars.*;
import static mindustry.content.Blocks.*;

public class GenericNeoplasiaBlock extends Block {
    static final byte transportSaveRevision = 16;
    static final arc.struct.ObjectFloatMap<mindustry.game.Team> nextTreeSeed = new arc.struct.ObjectFloatMap<>();
    // Searches run synchronously on the simulation thread; never retain a colony-sized cache per blob.
    static final Queue<NeoplasiaBuild> searchQueue = new Queue<>();
    static final ObjectSet<NeoplasiaBuild> searchVisited = new ObjectSet<>();
    static final ObjectMap<NeoplasiaBuild, NeoplasiaBuild> searchParents = new ObjectMap<>();

    static void clearSearch(){
        searchQueue.clear();
        searchVisited.clear();
        searchParents.clear();
    }
    public static Seq<NeoplasiaBuild> activeNeoplasia = new Seq<>();
    public static ObjectMap<Item, GenericNeoplasiaBlock> itemProducers = new ObjectMap<>();
    public static GenericNeoplasiaBlock veinBlock;
    public static GenericNeoplasiaBlock treeBlock;
    public float treeSeedInterval = 600f, treeSeedChance = 0.04f, treeSeedCooldown = 5400f;
    public static NeoplasmTentaclePit tentaclePitBlock;
    public static NeoplasmLiquidSlurper liquidSlurperBlock;
    public boolean veinCandidate = false;
    public static final NeoplasmItemBulletType itemBullet = new NeoplasmItemBulletType();
    public GenericNeoplasiaBlock ventBlock;
    public float wscl = 25f, wmag = 0.4f, wtscl = 1f, wmag2 = 1f;
    public float maxAmount = 1000f;
    public float startMass = 0.001f;
    /** Mass per tick available to feed connected factories and trees. */
    public float massExportRate = 0f;
    public float disconnectGrace = 120f;
    /** Passive mass growth and outward spread run at half their base rates. */
    public float massGrowthMultiplier = 0.5f;
    public float spreadSpeedMultiplier = 0.5f;
    public float selfGrowRate = 0.04f;
    public float oreGrowBonus = 0.25f;
    /** Ordinary tissue digests surplus items into mass, reserving crafting and upgrade stock. */
    public float itemMassGain = 25f, itemDigestionTime = 60f;
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
        itemCapacity = 20;
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
        int burstCount = 0;
        int treeRolls = 0;
        int damageRolls = 0;
        public float amount = 0f;
        public float recentDamage = 0f;
        float defenseTime;
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
        // Only actual routed deliveries grow veins. Old saved balancing traffic is ignored.
        float routeTraffic = 0f;
        float veinRegrowDelay;
        float veinGrowthTime;
        float veinCheckTimer;
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
        ObjectIntMap<Item> shortages;
        final Seq<NeoplasiaBuild> adjacent = new Seq<>();
        float pullTimer;
        float liquidRequestTimer;
        float treeSeedTimer;
        Tile pendingTreeSite;
        float pendingTreeValidation;
        float enemyScanTimer;
        float itemDigestionTimer;
        boolean itemRequestsReady;
        private int foodStock, foodSpace;
        private final mindustry.world.modules.ItemModule.ItemConsumer countFood = (item, count) ->
            foodStock += Math.max(0, count - neededAmount(item));
        private final mindustry.world.modules.ItemModule.ItemConsumer discardUnusable = (item, count) -> {
            int extra = Math.max(0, count - neededAmount(item));
            int food = Math.min(extra, foodSpace);
            foodSpace -= food;
            int unused = extra - food;
            if(unused > 0) items.remove(item, unused);
        };

        /** Spreading uses eight directions, so transport and heart connectivity must do so too. */
        Seq<NeoplasiaBuild> neighbors(){
            adjacent.clear();
            for(Building other : proximity){
                if(other instanceof NeoplasiaBuild nb && nb.team == team && !nb.dead()) adjacent.addUnique(nb);
            }
            if(tile != null) for(var offset : Geometry.d8){
                Tile other = world.tile(tile.x + offset.x, tile.y + offset.y);
                if(other != null && other.build instanceof NeoplasiaBuild nb && nb != this && nb.team == team && !nb.dead()) adjacent.addUnique(nb);
                if(other != null && other.build instanceof NeoplasmHeart.NeoplasmHeartBuild heart && heart.team == team && !heart.dissolving && !heart.dead()){
                    for(NeoplasiaBuild tissue : heart.neighbors()) if(tissue != this) adjacent.addUnique(tissue);
                }
            }
            return adjacent;
        }

        public float lifeReserve(){ return Math.max(1f, maxAmount * 0.05f); }
        public void receiveMass(float value){ amount += value; }
        public float massCapacity(){ return maxAmount; }

        int digestionCapacity(){
            return block().veinCandidate && itemMassGain > 0f ? (int)Math.floor(Math.max(0f, maxAmount - amount) / itemMassGain) : 0;
        }

        int digestionStock(){
            foodStock = 0;
            items.each(countFood);
            return foodStock;
        }

        int digestionDemand(){
            int capacity = digestionCapacity();
            return capacity <= 0 ? 0 : Math.max(0, capacity - digestionStock());
        }

        void digestItems(){
            if(net.client() || !block().veinCandidate || itemMassGain <= 0f) return;
            itemDigestionTimer = Math.min(itemDigestionTime, itemDigestionTimer + delta());
            if(itemDigestionTimer < itemDigestionTime || digestionCapacity() <= 0) return;
            for(Item item : content.items()){
                if(items.get(item) <= neededAmount(item)) continue;
                items.remove(item, 1);
                receiveMass(itemMassGain);
                itemDigestionTimer = 0f;
                return;
            }
        }

        void needItem(Item item, int amount) {
            itemRequestsReady = true;
            if (neededItems == null) neededItems = new ObjectSet<>();
            if (neededAmounts == null) neededAmounts = new ObjectIntMap<>();
            neededItems.add(item);
            neededAmounts.put(item, Math.max(amount, neededAmounts.get(item, 0)));
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
                NeoplasiaItemTransit.between(source, this, item);
            }
            if (source != null) {
                if (receivedFrom == null) receivedFrom = new ObjectMap<>();
                receivedFrom.put(item, source);
            }
            itemTraffic = Math.min(100f, itemTraffic + 1f);
        }
        void expelItems() {
            if(items.total() == 0) return;
            foodSpace = digestionCapacity();
            items.each(discardUnusable);
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
            enemyScanTimer = Math.floorMod(tile.pos(), 10);
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
                if(disconnectionTime > disconnectGrace){
                    float disconnected = disconnectionTime - disconnectGrace;
                    amount -= amount * (0.001f + disconnected * 0.00005f) * delta();
                }
            } else {
                disconnectionTime = 0f;
                grow();
            }
            itemTraffic = Math.max(0, itemTraffic - 0.1f * delta());
            routeTraffic = Math.max(0, routeTraffic - 0.002f * delta());
            veinRegrowDelay = Math.max(0f, veinRegrowDelay - delta());
            if(veinRegrowDelay <= 0f && routeTraffic >= 3f && connected && block().veinCandidate) veinGrowthTime += delta();
            else veinGrowthTime = 0f;
            veinCheckTimer += delta();
            if (veinGrowthTime >= 600f && veinCheckTimer >= 30f && veinBlock != null && block().veinCandidate) {
                veinCheckTimer = 0f;
                if(NeoplasiaGraph.canGrowVein(this)){
                NeoplasiaGraph.recordVeinGrowth(this);
                upgradeTo(veinBlock);
                return;
                }
            }
            // Advancing one shared burst repeatedly accelerated its timers without increasing growth.
            if(bursts > 0) burstSpread();
            damageNearby();
            tryLiquidSlurper();
            if(tile.build != this) return;
            tryUpgrades();
            if(tile.build != this) return;
            trySpawnTree(connected);
            itemRequestsReady = true;
            if(connected) digestItems();
            requestItems();
            pushItems();
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

        void tryLiquidSlurper(){
            if(liquidSlurperBlock == null || block() instanceof NeoplasmLiquidSlurper) return;
            liquidRequestTimer += delta();
            if(liquidRequestTimer < 120f || amount < liquidSlurperBlock.cost + lifeReserve()) return;
            liquidRequestTimer = 0f;
            if(!liquidSlurperBlock.hasLiquid(tile)) return;
            if(block().veinCandidate && !isOre(tile) && !(tile.floor() instanceof SteamVent)){
                upgradeTo(liquidSlurperBlock);
                return;
            }
            for(var offset : Geometry.d8){
                Tile nearby = world.tile(tile.x + offset.x, tile.y + offset.y);
                if(nearby != null && nearby.build != null && nearby.build.team == team && nearby.block() instanceof NeoplasmLiquidSlurper) return;
            }
            for(var offset : Geometry.d8){
                Tile site = world.tile(tile.x + offset.x, tile.y + offset.y);
                if(site == null || site.block() != air || site.floor().isLiquid || !site.floor().placeableOn || !liquidSlurperBlock.hasLiquid(site)) continue;
                site.setBlock(liquidSlurperBlock, team);
                if(site.build instanceof NeoplasiaBuild gland){
                    amount -= liquidSlurperBlock.cost;
                    gland.amount = liquidSlurperBlock.cost;
                }
                return;
            }
        }

        void trySpawnTree(boolean connected) {
            if(net.client() || treeBlock == null || !connected || !block().veinCandidate
                || amount < Math.max(maxAmount * 0.85f, treeBlock.cost + lifeReserve())){
                pendingTreeSite = null;
                return;
            }
            if(Time.time < nextTreeSeed.get(team, 0f)){ pendingTreeSite = null; return; }
            if(pendingTreeSite == null){
                treeSeedTimer += delta();
                if(treeSeedTimer < treeSeedInterval) return;
                treeSeedTimer = 0f;
                treeRolls++;
                if(Mathf.randomSeed(tile.pos() * 17L + treeRolls, 0f, 1f) >= treeSeedChance) return;
                search: for(int dx = -3; dx <= 3; dx++) for(int dy = -3; dy <= 3; dy++){
                    Tile other = world.tile(tile.x + dx, tile.y + dy);
                    if(other != null && other.block() == Blocks.air && treeBlock.canPlaceOn(other, team, 0)){
                        pendingTreeSite = other;
                        pendingTreeValidation = 60f;
                        break search;
                    }
                }
                if(pendingTreeSite == null) return;
            }
            if(world.tile(pendingTreeSite.x, pendingTreeSite.y) != pendingTreeSite || pendingTreeSite.block() != Blocks.air){
                pendingTreeSite = null;
                return;
            }
            pendingTreeValidation -= delta();
            if(pendingTreeValidation <= 0f){
                pendingTreeValidation = 60f;
                if(!treeBlock.canPlaceOn(pendingTreeSite, team, 0)){ pendingTreeSite = null; return; }
            }
            if(treeBlock.itemCost != null) for(ItemStack stack : treeBlock.itemCost) needItem(stack.item, stack.amount);
            if(!hasItemCost(treeBlock.itemCost)) return;
            if(!treeBlock.canPlaceOn(pendingTreeSite, team, 0)){ pendingTreeSite = null; return; }
            Tile site = pendingTreeSite;
            pendingTreeSite = null;
            site.setBlock(treeBlock, team);
            if(site.build instanceof NeoplasiaBuild tree){
                consumeItemCost(treeBlock.itemCost);
                amount -= treeBlock.cost;
                tree.amount = Math.min(treeBlock.cost, tree.block().maxAmount);
                nextTreeSeed.put(team, Time.time + treeSeedCooldown);
            }
        }

        @Override
        public void onRemoved() {
            super.onRemoved();
            remove(this);
            NeoplasiaGraph.compMap.remove(this, 0);
            NeoplasiaGraph.dirty = true;
            activeNeoplasia.remove(this);
            if(this instanceof NeoplasmTreeBase.NeoplasmTreeBaseBuild tree) NeoplasmTreeBase.trees.remove(tree);
            adjacent.clear();
            if(receivedFrom != null) receivedFrom.clear();
            damageTarget = null;
        }
        public GenericNeoplasiaBlock block(){
            return (GenericNeoplasiaBlock) this.block;
        }
        boolean upgradeTo(Block target) {
            if(net.client() || world.isGenerating() || target == null || !NeoplasiaGraph.isCurrent(this)) return false;
            float savedAmount = amount;
            Seq<ItemStack> inventory = new Seq<>();
            items.each((item, count) -> inventory.add(new ItemStack(item, count)));
            items.clear();
            tile.setBlock(target, team);
            if(tile.build instanceof NeoplasiaBuild nb){
                nb.amount = savedAmount;
                for(ItemStack stack : inventory) nb.items.add(stack.item, stack.amount);
            }else if(tile.build instanceof NeoplasmHeart.NeoplasmHeartBuild heart){
                heart.amount = savedAmount;
                heart.health = savedAmount;
                for(ItemStack stack : inventory) heart.items.add(stack.item, stack.amount);
            }
            return true;
        }
        void tryUpgrades() {
            if(tryGrowTentaclePit()) return;
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
            if(ventBlock!=null && tile.floor() instanceof SteamVent){
                if(ventBlock.itemCost != null){
                    for (ItemStack stack : ventBlock.itemCost) {
                        needItem(stack.item, stack.amount);
                    }
                    if(!hasItemCost(ventBlock.itemCost)){
                        return;
                    }
                }
                if(amount >= ventBlock.cost){
                    consumeItemCost(ventBlock.itemCost);
                    upgradeTo(ventBlock);
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
                    return;
            }

            if(empty2Upgrade != null && amount >= empty2UpgradeCost && !isOre(tile) && shouldEmpty2Upgrade && NeoplasiaGraph.techLevel() >= empty2Upgrade.requiredTech){
                upgradeTo(empty2Upgrade);
                return;
            }

            float chance = recentDamage * upgradeDamageScale;
            damageRolls++;
            if(amount >= damageUpgradeCost && Mathf.randomSeed(tile.pos() * 23L + damageRolls, 0f, 1f) < chance * delta() && damageUpgrade != null && NeoplasiaGraph.techLevel() >= damageUpgrade.requiredTech){
                upgradeTo(damageUpgrade);
            }
        }

        @Override
        public float handleDamage(float damage){
            float actual = super.handleDamage(damage);
            amount = Math.max(0f, amount - actual);
            recentDamage += actual;
            if(actual > 0f) NeoplasmTentaclePit.alertDamage(this);
            NeoplasiaDamage.bleed(this, actual);
            return actual;
        }

        boolean tryGrowTentaclePit(){
            if(defenseTime <= 0f || net.client() || tentaclePitBlock == null) return false;
            defenseTime = Math.max(0f, defenseTime - delta());
            if(!NeoplasiaGraph.isCurrent(this) || !NeoplasiaGraph.isConnected(this)) return false;
            NeoplasmTentaclePit target = tentaclePitBlock;
            ItemStack[] payment = target.itemCost;
            if(block() instanceof NeoplasmTentaclePit pit){
                if(pit.largeVariant == null) return false;
                target = pit.largeVariant;
                payment = pit.upgradeItemCost;
            }else{
                if(!block().veinCandidate || !target.canUpgradeToThis(this)) return false;
                if(target.largeVariant != null && hasItemCost(target.largeVariant.itemCost)){
                    target = target.largeVariant;
                    payment = target.itemCost;
                }
            }
            if(NeoplasiaGraph.techLevel() < target.requiredTech) return false;
            if(payment != null) for(ItemStack stack : payment) needItem(stack.item, stack.amount);
            if(hasItemCost(payment) && upgradeTo(target)){
                ((NeoplasiaBuild)tile.build).consumeItemCost(payment);
            }
            // Keep requesting and reserving defense items before unrelated upgrades.
            return true;
        }

        void grow() {
            float growth = selfGrowRate;
            if (isOre(tile)) growth += oreGrowBonus;
            // Downgrades may retain a larger store; stop growing rather than discard the excess.
            amount = Math.max(amount, Math.min(maxAmount, amount + growth * massGrowthMultiplier * delta()));
        }

        void burstSpread() {
            if (tile == null) return;
            float threshold = maxAmount * burstThresholdFraction;
            if (burstDir == -1) {
                if (amount < threshold || burstCooldown > 0f) {
                    burstCooldown -= delta() * spreadSpeedMultiplier;
                    return;
                }
                burstTile = tile;
                burstStep = 0;
                burstTimer = 0f;
                burstCount++;
                currentBurstLength = 1 + (int) (Mathf.pow(Mathf.randomSeed(tile.pos() * 11L + burstCount, 0f, 1f), 0.6f) * burstLength);
                burstDir = 0;
            }
            burstTimer += delta() * spreadSpeedMultiplier;
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
            float transfer = Math.min(Math.min(burstTransferAmount(), amount * 0.15f), Math.max(0f, amount - lifeReserve()));
            if (transfer <= 0f) {
                resetBurst();
                return;
            }
            if (next.build instanceof NeoplasiaBuild n) {
                float flow = Math.min(Math.min(transfer, amount), Math.max(0f, n.massCapacity() - n.amount));
                amount -= flow;
                n.receiveMass(flow);
            } else if (next.block() == air && base != null) {
                next.setBlock(base, team);
                if (next.build instanceof NeoplasiaBuild nb) {
                    float flow = Math.min(Math.min(transfer, amount), nb.block().maxAmount);
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

        float burstTransferAmount(){
            return burstTransfer;
        }

        Tile pickBestNeighbor(Tile origin) {
            if (origin == null) return null;
            int bestDir = -1;
            float bestScore = -999f;
            for (int i = 0; i < 8; i++) {
                Tile check = world.tile(origin.x + Geometry.d8[i].x, origin.y + Geometry.d8[i].y);
                if (check == null || check.solid() || check.floor().isDeep()) continue;
                if(check.build != null && (!(check.build instanceof NeoplasiaBuild) || check.team() != team)) continue;
                float score = 0f;
                if (check.build == null) {
                    score += 12f;
                    if (check.overlay() != null && check.overlay().itemDrop != null) score += 13f;
                }
                if (check.build instanceof NeoplasiaBuild) score += 1.5f;
                score += Mathf.randomSeed(origin.pos() * 8L + i, 0f, 0.5f);
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
            if (tile == null || damage <= 0f) return;
            for (int i = 0; i < 4; i++) {
                Tile t = tile.nearby(i);
                if (t == null) continue;
                if (t.block().unitMoveBreakable) ConstructBlock.deconstructFinish(t, t.block(), null);
                Building other = t.build;
                if (other != null && other.team != team) other.damage(damage * delta());
            }
            float range = tilesize * 2f;
            enemyScanTimer += delta();
            if(enemyScanTimer >= 10f){
                enemyScanTimer = 0f;
                damageTarget = null;
                damageTargetDst = range * range;
                Units.nearbyEnemies(team, x - range, y - range, range * 2f, range * 2f, scanEnemies);
            }
            if(damageTarget != null && !damageTarget.dead() && damageTarget.team != team && damageTarget.within(x, y, range)){
                damageTarget.damage(damage * delta());
            }
        }

        void pullItems(Item item, int desired) {
            int room = perItemCapacity ? itemCapacity - items.get(item) : itemCapacity - items.total();
            int need = Math.min(desired - items.get(item), room);
            if (need <= 0) return;
            searchQueue.clear();
            searchVisited.clear();
            searchParents.clear();
            searchQueue.addLast(this);
            searchVisited.add(this);
            try{
            int scanned = 0;
            while(!searchQueue.isEmpty() && scanned++ < 4096){
                NeoplasiaBuild supplier = searchQueue.removeFirst();
                if(supplier != this){
                    int available = Math.max(0, supplier.items.get(item) - supplier.neededAmount(item));
                    int take = Math.min(need, available);
                    supplier.items.remove(item, take);
                    items.add(item, take);
                    if(take > 0){
                        if(NeoplasiaItemTransit.canAnimate()){
                            Seq<NeoplasiaBuild> route = new Seq<>();
                            for(NeoplasiaBuild node = supplier; node != null && route.size <= 256; node = searchParents.get(node)) route.add(node);
                            NeoplasiaItemTransit.play(route, item);
                        }
                        NeoplasiaBuild path = supplier;
                        while(path != null){
                            path.routeTraffic = Math.min(100f, path.routeTraffic + take);
                            path = searchParents.get(path);
                        }
                    }
                    need -= take;
                    if(need <= 0) return;
                }
                for(NeoplasiaBuild nb : supplier.neighbors()){
                    if(searchVisited.add(nb)){
                        searchParents.put(nb, supplier);
                        searchQueue.addLast(nb);
                    }
                }
            }
            }finally{
                clearSearch();
            }
        }

        void pushItems() {
            // NeoplasiaGraph batches transfers to actual consumers. Idle items must not bounce between neighbors.
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
            try{
            int steps = 0;
            while (queue.size > 0 && steps < maxSteps) {
                NeoplasiaBuild cur = queue.removeFirst();
                steps++;
                for (NeoplasiaBuild nb : cur.neighbors()) {
                    if (visited.contains(nb)) continue;
                    visited.add(nb);
                    if (nb.isProducing(item)) return nb;
                    queue.addLast(nb);
                }
            }
            return null;
            }finally{ clearSearch(); }
        }

        Tile findEmptyTile(int maxSteps) {
            Queue<NeoplasiaBuild> queue = searchQueue;
            ObjectSet<NeoplasiaBuild> visited = searchVisited;
            queue.clear();
            visited.clear();
            queue.addLast(this);
            visited.add(this);
            try{
            int steps = 0;
            while (queue.size > 0 && steps < maxSteps) {
                NeoplasiaBuild cur = queue.removeFirst();
                steps++;
                for (int i = 0; i < 4; i++) {
                    Tile t = cur.tile.nearby(i);
                    if (t != null && t.block() == air) return t;
                }
                for (NeoplasiaBuild nb : cur.neighbors()) {
                    if (!visited.contains(nb)) {
                        visited.add(nb);
                        queue.addLast(nb);
                    }
                }
            }
            return null;
            }finally{ clearSearch(); }
        }

        void requestItems() {
            if (neededItems == null || neededItems.isEmpty()) return;
            if(shortages == null) shortages = new ObjectIntMap<>();
            pullTimer += delta();
            if(pullTimer >= 15f){
                pullTimer = 0f;
                // The colony batches indexed deliveries instead of each consumer searching the blob.
                if(!NeoplasiaGraph.hasDeliveryNetwork(this)) for(Item item : neededItems) pullItems(item, neededAmount(item));
            }
            requestTimer += delta();
            if (requestTimer < 120f) return;
            requestTimer = 0;
            for (Item item : neededItems) {
                if(items.get(item) >= neededAmount(item)){
                    shortages.remove(item, 0);
                    continue;
                }
                if(!hasItemRoom(item)) continue;
                GenericNeoplasiaBlock factory = itemProducers.get(item);
                if (factory == null) continue;
                int failures = shortages.get(item, 0) + 1;
                shortages.put(item, failures);
                if (findProducer(item, 15) != null && failures < 5) continue;
                Tile spawnTile = findEmptyTile(10);
                if (spawnTile != null) {
                    spawnTile.setBlock(factory, team);
                    shortages.remove(item, 0);
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
            Draw.color(colFrom, colTo, Mathf.clamp(amount / maxAmount));
            Fill.circle(x, y, radius);
            Draw.z(Renderer.Layer.neoplasiaUnder);
            Fill.circle(x, y, radius);
            Draw.scl(1f);
            ;
            Draw.z(Renderer.Layer.neoplasiaBase - 0.2f);
            Draw.color();
            if (items.first() != null) Draw.rect(items.first().fullIcon, x, y, 5.5f, 5.5f);
        }

        @Override
        public byte version(){ return transportSaveRevision; }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(amount);
            write.f(spawnTime);
            write.f(disconnectionTime);
            write.f(itemTraffic);
            write.f(veinRegrowDelay);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            amount = read.f();
            spawnTime = read.f();
            disconnectionTime = read.f();
            itemTraffic = read.f();
            veinRegrowDelay = revision >= transportSaveRevision ? read.f() : 0f;
            current = items.first();
        }
    }
}
