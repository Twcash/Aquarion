package aquarion.world.blocks.neoplasia;

import aquarion.world.graphics.AquaShaders;
import aquarion.world.graphics.Renderer;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.Items;
import mindustry.entities.Units;
import mindustry.gen.Building;
import mindustry.gen.Teamc;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.ConstructBlock;
import mindustry.world.blocks.environment.Prop;

import static aquarion.world.blocks.neoplasia.NeoplasiaGraph.producers;
import static mindustry.Vars.*;
import static mindustry.content.Blocks.*;

public class GenericNeoplasiaBlock extends Block {
    public static Seq<NeoplasiaBuild> activeNeoplasia = new Seq<>();

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
    //Total time before item requests fail and another producer is requested
    float requestTimeout = 600f;
    public float recentDamageDecay = 0.004f;

    public GenericNeoplasiaBlock oreUpgrade;
    public GenericNeoplasiaBlock damageUpgrade;
    public GenericNeoplasiaBlock empty2Upgrade;

    public Color colFrom = Color.valueOf("701e1e");
    public Color colTo = Color.valueOf("cf5a3b");

    public float oreUpgradeCost = 300;

    public float damageUpgradeCost = 10;

    public GenericNeoplasiaBlock(String name) {
        super(name);
        update = true;
        solid = false;
        destructible = true;
        rebuildable = false;
        drawTeamOverlay = false;
        hasItems = true;
        itemCapacity = 2;
    }

    public class NeoplasiaBuild extends Building {
        //TODO This is a LOT of variables for a single block, especially when there will be thousands of these.
        Item current;
        boolean requesting = false;
        public float progress = 0f;
        Tile burstTile;
        float requestTimer = 0f;
        float producerRequestCooldown = 0f;
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
        Seq<ItemStack> requestedQueue = new Seq<>();

        void requestItem(Item item) {
            Seq<NeoplasiaBuild> reqs = NeoplasiaGraph.itemRequesters.get(item);
            if (reqs == null) {
                reqs = new Seq<>();
                NeoplasiaGraph.itemRequesters.put(item, reqs);
            }
            reqs.addUnique(this);
            if (!requestedQueue.contains(i -> i.item == item)) {
                requestedQueue.add(new ItemStack(item, 1));
            }
            requesting = true;
            requestTimer = 0f;
        }

        //Need to register blocks with an item
        /*TODO registry should be saved as items would be "ghost" items if the map loads as
        none of the sequences are saved.*/
        @Override
        public void handleItem(Building source, Item item) {
            super.handleItem(source, item);
            NeoplasiaGraph.registerHolder(item, this);
            requestedQueue.remove(s -> s.item == item);
            if (requestedQueue.isEmpty()) {
                requesting = false;
                Seq<NeoplasiaBuild> reqs = NeoplasiaGraph.itemRequesters.get(item);
                if (reqs != null) {
                    reqs.remove(this);
                }
            }
        }

        /*
        Quite simple. Request a block to be made if it's deliverer does not exist
        or if the delivery takes too long.
         */
        void requestProducer(Item item) {
            Seq<GenericNeoplasiaBlock> producers = NeoplasiaGraph.itemProducers.get(item);
            if (producers == null || producers.isEmpty()) return;
            for (GenericNeoplasiaBlock b : producers) {
                int demand = NeoplasiaGraph.itemRequesters.get(item).size;
                int supply = NeoplasiaGraph.countProducers(b);
                if (supply >= demand) continue;
                NeoplasiaBuild builder = NeoplasiaGraph.findBlockBuilder(this);
                if (builder == null) return;
                if (builder.tile.floor().itemDrop != null) continue;
                if (builder.amount >= b.cost && builder.hasItemCost(b.itemCost)) {
                    builder.consumeItemCost(b.itemCost);
                    builder.amount -= b.cost;
                    builder.tile.setBlock(b, team);
                    if (b.itemCost != null && b.itemCost.length > 0) {
                        producers.get(b.itemCost[0].amount);
                    }
                    return;
                }
                if (!hasItems && b.itemCost != null) {
                    for (ItemStack stack : b.itemCost) {
                        int missing = stack.amount - builder.items.get(stack.item);
                        if (missing > 0) {
                            builder.requestItem(stack.item);
                        }
                    }
                }
            }
        }

        /*
        God awful.
        Basically try and move items every tick closer to the target, with large blobs
        this could cause a lot of lag. So I iterate over every requesting block within
        NeoplasiaGraph and try and "score" tiles closer to the requestee.
        This does not have pathfinding and WILL get stuck. hence why itemcapacity is above 1.
         */
        void moveItems() {
            items.each((item, amount) -> {
                if (amount <= 0) return;

                Seq<NeoplasiaBuild> requesters = NeoplasiaGraph.itemRequesters.get(item);
                if (requesters == null || requesters.isEmpty()) return;
                Seq<NeoplasiaBuild> validTargets = requesters.select(r -> r.requestedQueue.contains(s -> s.item == item));
                if (validTargets.isEmpty()) return;
                NeoplasiaBuild closest = null;
                float bestDst = Float.MAX_VALUE;
                for (NeoplasiaBuild req : validTargets) {
                    if (req == this) continue;
                    float d = dst2(req);
                    if (d < bestDst) {
                        bestDst = d;
                        closest = req;
                    }
                }

                if (closest != null) {
                    Building next = NeoplasiaGraph.stepToward(this, closest);
                    if (next == null) return;
                    if (!(next instanceof NeoplasiaBuild nextBuild)) return;
                    if (nextBuild.items.total() < nextBuild.block.itemCapacity) {
                        items.remove(item, 1);
                        nextBuild.items.add(item, 1);
                    }
                }
            });
        }

        //Hehe haha I overrid damage. This most likely breaks a a lot regarding armor and ect
        //But at this point I don't care.
        @Override
        public float handleDamage(float amt) {
            recentDamage += amt;
            return amt;
        }

        //Super ultra useful 1 usage method.
        //TODO merge??
        void request(Item item, float amt) {
            if (requesting) return;
            requestItem(item);//This *should* be moved somewhere else.
            requesting = true;
            NeoplasiaBuild producer = NeoplasiaGraph.findProducer(item, this);
            if (producer == null || dst2(producer) > 900f) {
                requestProducer(item);
                return;
            }
            if (NeoplasiaGraph.totalAvailable(item) < amt) {
                requestProducer(item);
            }
        }


        @Override
        public boolean acceptItem(Building source, Item item) {
            return items.total() < itemCapacity;
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
            activeNeoplasia.addUnique(this);
        }

        @Override
        public void remove() {
            super.remove();
            activeNeoplasia.remove(this);
        }

        @Override
        public void updateTile() {
            if (tile == null) return;
            producerRequestCooldown -= delta();//Preventing lag :tm:
            health = amount;
            maxHealth = amount;
            recentDamage = Math.max(0f, recentDamage - recentDamageDecay);
            requestTimer += delta();
            if (requestTimer > requestTimeout) {
                requesting = false;
            }
            if (spawnTime < spawnDuration) spawnTime += delta();
            if (amount <= 0f) {
                kill();
                return;
            }

            grow();
            for (int i = 0; i < bursts; i++) burstSpread();
            damageNearby();
            tryUpgrades();
            progress += delta() / 2;

            if (progress >= 1) {
                progress = 0f;
                moveItems();
            }
        }

        void tryUpgrades() {
            if (tile == null) return;
            if (oreUpgrade != null) {
                if (amount >= oreUpgradeCost && isOre(tile)) {
                    if (!hasItemCost(oreUpgrade.itemCost)) {
                        if (oreUpgrade.itemCost != null) {
                            for (ItemStack stack : oreUpgrade.itemCost) {
                                request(stack.item, stack.amount);
                            }
                        }
                        return;
                    }
                    if (amount >= oreUpgrade.cost) {
                        consumeItemCost(oreUpgrade.itemCost);
                        tile.setBlock(oreUpgrade, team);
                        return;
                    }
                }
            }
            if (amount >= emptyUpgradeCost && !isOre(tile) && shouldEmptyUpgrade) {
                if (emptyUpgrade != null) tile.setBlock(emptyUpgrade, team);
            }
            float chance = recentDamage * upgradeDamageScale;
            if (amount >= damageUpgradeCost && Mathf.chanceDelta(chance)) {
                if (damageUpgrade != null) tile.setBlock(damageUpgrade, team);
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
            for (int i = 0; i < 4; i++) {
                Tile check = world.tile(origin.x + Geometry.d4[i].x, origin.y + Geometry.d4[i].y);
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
            return world.tile(origin.x + Geometry.d4[bestDir].x, origin.y + Geometry.d4[bestDir].y);
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
            Units.closestEnemy(team, x - tilesize, y - tilesize, tilesize * 2f, unit -> {
                if (!unit.dead() && unit.targetable(team)) unit.damage(damage * delta());
                return true;
            });
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
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            amount = read.f();
            spawnTime = read.f();
            current = items.first();
        }
    }
}