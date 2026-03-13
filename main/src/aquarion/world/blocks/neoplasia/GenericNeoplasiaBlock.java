package aquarion.world.blocks.neoplasia;

import aquarion.world.graphics.AquaShaders;
import aquarion.world.graphics.Renderer;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
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

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;
import static mindustry.content.Blocks.*;

public class GenericNeoplasiaBlock extends Block {
    public static Seq<NeoplasiaBuild> activeNeoplasia = new Seq<>();

    public float wscl = 25f, wmag = 0.4f, wtscl = 1f, wmag2 = 1f;
    public float maxAmount = 1000f;
    public float startMass = 0.001f;
    public float selfGrowRate = 0.04f;
    public float oreGrowBonus = 0.25f;

    public Block base;

    public float emptyUpgradeCost = 400;
    public boolean shouldEmptyUpgrade = true;
    public Block emptyUpgrade;
    public float baseSize = 8 * 1.5f;
    public int bursts = 1;
    public float damage = 1.25f;

    public float burstThresholdFraction = 0.3f;
    public float burstDelay = 150f;
    public int burstLength = 5;
    public float burstTransfer = 35f;

    public float recentDamageDecay = 0.004f;

    public Block oreUpgrade;
    public Block damageUpgrade;
    public Block empty2Upgrade;

    public Color colFrom = Color.valueOf("701e1e");
    public Color colTo = Color.valueOf("cf5a3b");

    public float oreUpgradeCost = 300;

    public ItemStack oreUpgradeItemCost;


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
        Item current;
        boolean requesting = false;
        Item requestedItem = null;
        float requestAmount = 0f;
        public float progress = 0f;
        Tile burstTile;
        int burstStep = 0;
        float burstTimer = 0f;
        float burstCooldown = 0f;
        int currentBurstLength = 0;
        int burstDir = -1;

        public float amount = 0f;
        public float recentDamage = 0f;
        public float upgradeDamageScale = 0.9f;

        float spawnTime = 0f;
        float spawnDuration = 90f;
        float burstStepDelay = 4f;

        @Override
        public float handleDamage(float amt) {
            recentDamage += amt;
            return amt;
        }

        void request(Item item, float amt) {
            requesting = true;
            requestedItem = item;
            requestAmount = amt;
        }

        NeoplasiaBuild findSupplier() {
            NeoplasiaBuild best = null;
            float bestDst = Float.MAX_VALUE;
            for (NeoplasiaBuild other : activeNeoplasia) {
                if (other == this) continue;
                if (other.items.total() <= 0) continue;
                if (!other.items.has(requestedItem)) continue;

                float dst = dst2(other);
                if (dst < bestDst) {
                    bestDst = dst;
                    best = other;
                }
            }
            return best;
        }

        @Override
        public void created() {
            super.created();
            amount = startMass;
            activeNeoplasia.add(this);
        }

        @Override
        public void remove() {
            super.remove();
            activeNeoplasia.remove(this);
        }

        @Override
        public void updateTile() {
            if (tile == null) return;
            health = amount;
            maxHealth = amount;

            recentDamage = Math.max(0f, recentDamage - recentDamageDecay);

            if (spawnTime < spawnDuration) spawnTime += delta();
            if (amount <= 0f) { kill(); return; }

            grow();

            for (int i = 0; i < bursts; i++) {
                burstSpread();
            }


            damageNearby();
            tryUpgrades();
            progress += delta()/ 2f;
            if (progress >= 1f) {
                pullItem();
                progress = 0f;
            }
        }

        void tryUpgrades() {
            if (tile == null) return;
            if (amount >= oreUpgradeCost && isOre(tile)) {
                if(oreUpgradeItemCost == null){
                    if (oreUpgrade != null) tile.setBlock(oreUpgrade, team);
                } else if(items.get(oreUpgradeItemCost.item) > oreUpgradeItemCost.amount){
                    if (oreUpgrade != null) tile.setBlock(oreUpgrade, team);
                } else {
                    request(oreUpgradeItemCost.item, oreUpgradeItemCost.amount);
                }
            }

            if (amount >= emptyUpgradeCost && !isOre(tile) && shouldEmptyUpgrade) {
                if (emptyUpgrade != null) tile.setBlock(empty2Upgrade != null ? Mathf.random(0,1) > 0 ? emptyUpgrade : empty2Upgrade : emptyUpgrade, team);
            }

            float chance = recentDamage * upgradeDamageScale;
            if (amount >= damageUpgradeCost && Mathf.chanceDelta(chance)){
                if (current != Items.silicon) {
                    request(Items.silicon, 1);
                    return;
                }
                if (damageUpgrade != null) tile.setBlock(damageUpgrade, team);
            }
        }

        void grow() {
            float growth = selfGrowRate;
            if (isOre(tile)) growth += oreGrowBonus;
            amount = Math.min(maxAmount, amount + growth * delta());
        }
        void pullItem(){
            if(!requesting || requestedItem == null || requestAmount <= 0) return;

            NeoplasiaBuild supplier = findSupplier();

            if(supplier == null) return;

            if(supplier.items.has(requestedItem)){
                supplier.items.remove(requestedItem, 1);

                NeoplasiaGraph.send(requestedItem, supplier, this);

                requestAmount--;

                if(requestAmount <= 0){
                    requesting = false;
                }
            }
        }
        void burstSpread() {
            if (tile == null) return;

            float threshold = maxAmount * burstThresholdFraction;

            if (burstDir == -1) {
                if (amount < threshold) return;
                if (burstCooldown > 0f) {
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

            if (burstTile == null) {
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

            } else if (next.block() == air) {
                if (base != null) {
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
            Draw.z(Renderer.Layer.neoplasiaBase - 0.2f);
            if (current != null) Draw.rect(current.fullIcon, x, y, 0);
            Draw.color();
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return current == null && items.total() == 0;
        }

        @Override
        public void handleItem(Building source, Item item){
            items.add(item, 1);
            current = item;
        }
        @Override
        public int removeStack(Item item, int amt) {
            int removed = super.removeStack(item, amt);
            if (item == current) current = null;
            return removed;
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