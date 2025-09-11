package aquarion.world.type;
import aquarion.world.Uti.AquaStats;
import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.heat.HeatConsumer;
import mindustry.world.consumers.*;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.draw.DrawMulti;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

public class GroundDrill extends AquaBlock {
    public float hardnessDrillMultiplier = 50f;
    public float heatRequirement = 0f;
    public float overheatScale = 0.8f;
    public float maxEfficiency = 1f;
    public boolean flipHeatScale = false;
    public boolean hasHeat = false;
    public float baseEfficiency = 1f;

    public DrawBlock drawer = new DrawMulti(new DrawDefault());

    public int tier;
    public float drillTime = 300;
    public float liquidBoostIntensity = 1.6f, itemBoostIntensity = 1.5f;
    public float warmupSpeed = 0.015f;
    public @Nullable Item blockedItem;
    public @Nullable Seq<Item> blockedItems;
    public TextureRegion itemRegion;

    public boolean drawMineItem = true;
    public Effect drillEffect = Fx.mine;
    public float drillEffectRnd = -1f;
    public float drillEffectChance = 0.02f;
    public float rotateSpeed = 2f;
    public Effect updateEffect = Fx.pulverizeSmall;
    public float updateEffectChance = 0.02f;
    public float ItemBoostUseTime = 60f;

    public final int timerUse = timers++;
    public ObjectFloatMap<Item> drillMultipliers = new ObjectFloatMap<>();

    public GroundDrill(String name) {
        super(name);
        update = true;
        solid = true;
        group = BlockGroup.drills;
        hasLiquids = true;
        liquidCapacity = 5f;
        hasItems = true;
        ambientSound = Sounds.drill;
        ambientSoundVolume = 0.018f;
        envEnabled |= Env.space;
        flags = EnumSet.of(BlockFlag.drill);
    }

    @Override
    public void load() {
        super.load();
        itemRegion = Core.atlas.find(name + "-item");
        drawer.load(this);
    }

    @Override
    public void init() {
        super.init();
        if (blockedItems == null && blockedItem != null) {
            blockedItems = Seq.with(blockedItem);
        }
        if (drillEffectRnd < 0) drillEffectRnd = size;
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        drawer.drawPlan(this, plan, list);
    }

    @Override
    public void drawPlanConfigTop(BuildPlan plan, Eachable<BuildPlan> list) {
        if (!plan.worldContext) return;
        Tile tile = plan.tile();
        if (tile == null) return;

        DrillBuild fake = new DrillBuild();
        fake.tile = tile;
        countOre(tile, fake);
        if (fake.returnItems.isEmpty() || !drawMineItem) return;

        float r = 0, g = 0, b = 0;
        for(Item item : fake.returnItems){
            Color c = item.color;
            r += c.r;
            g += c.g;
            b += c.b;
        }
        int count = fake.returnItems.size;
        Draw.color(r/count, g/count, b/count);
        Draw.rect(itemRegion, plan.drawx(), plan.drawy());
        Draw.color();
    }

    @Override
    public void setBars() {
        super.setBars();
        if (heatRequirement != 0) {
            addBar("heat", (GroundDrill.DrillBuild entity) ->
                    new Bar(() ->
                            Core.bundle.format("bar.heatpercent",
                                    (int)(entity.heat + 0.01f),
                                    (int)(entity.efficiencyScale() * 100 + 0.01f)),
                            () -> {
                                float max = heatRequirement * 5f;
                                float heat = entity.heat;

                                if (heat < 0f) {
                                    float t = Mathf.clamp(1f + heat / max);
                                    return Tmp.c1.set(Color.black).lerp(Pal.techBlue, t);
                                } else {
                                    float t = Mathf.clamp(heat / max);
                                    return Tmp.c1.set(Pal.lightOrange).lerp(Color.white, t);
                                }
                            },
                            () -> Mathf.clamp(Math.abs(entity.heat) / Math.abs(heatRequirement))
                    )
            );
        }

        addBar("drillspeed", (DrillBuild e) ->
                new Bar(() -> Core.bundle.format("bar.drillspeed", Strings.fixed(e.lastDrillSpeed * 60 * e.timeScale(), 2)), () -> Pal.ammo, () -> e.warmup));
    }

    public Item getDrop(Tile tile) {
        return tile.drop();
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        if (isMultiblock()) {
            for (Tile other : tile.getLinkedTilesAs(this, tempTiles)) {
                if (canMine(other)) {
                    return true;
                }
            }
            return false;
        } else {
            return canMine(tile);
        }
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        Tile tile = world.tile(x, y);
        if (tile == null) return;

        DrillBuild fake = new DrillBuild();
        fake.tile = tile;
        countOre(tile, fake);

        float baseX = x * tilesize + offset;
        float baseY = y * tilesize + offset;
        float spacing = 12f;
        int i = 0;
        for (Item item : fake.returnItems) {
            if (item.hardness <= tier) {
                float width = drawPlaceText(
                        Core.bundle.formatFloat("bar.drillspeed",
                                60f / getDrillTime(item) * fake.returnCounts.get(item, 0), 2),
                        x, y + (int)(i * spacing / 4f), valid
                );
                float dx = baseX - width / 2f - 6f;
                float dy = baseY + size * tilesize / 2f + 8f + i * spacing;
                float s = iconSmall / 4f;

                Draw.mixcol(Color.darkGray, 1f);
                Draw.rect(item.fullIcon, dx, dy - 1f, s, s);
                Draw.reset();
                Draw.rect(item.fullIcon, dx, dy, s, s);
            } else {
                drawPlaceText(Core.bundle.get("bar.drilltierreq"),
                        x, y + (int)(i * spacing / 4f), valid);
            }
            i++;
        }
        if (drawMineItem && !fake.returnItems.isEmpty()) {
            Draw.color(averageItemColor(fake.returnItems));
            Draw.rect(itemRegion, baseX, baseY);
            Draw.color();
        }
    }

    protected Color averageItemColor(Seq<Item> items){
        if(items.isEmpty()) return Color.white;

        float r = 0, g = 0, b = 0;
        for(Item item : items){
            Color c = item.color;
            r += c.r;
            g += c.g;
            b += c.b;
        }
        int count = items.size;
        return Tmp.c1.set(r / count, g / count, b / count, 1f);
    }

    public float getDrillTime(Item item) {
        return (drillTime + hardnessDrillMultiplier * item.hardness) / drillMultipliers.get(item, 1f);
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.drillTier, StatValues.drillables(drillTime, hardnessDrillMultiplier, size * size, drillMultipliers, b -> b instanceof Floor f && !f.wallOre && f.itemDrop != null &&
                f.itemDrop.hardness <= tier && (blockedItems == null || !blockedItems.contains(f.itemDrop)) && (indexer.isBlockPresent(f) || state.isMenu())));

        stats.add(Stat.drillSpeed, 60f / drillTime * size * size, StatUnit.itemsSecond);
        if (baseEfficiency < 1 && heatRequirement != 0) {
            stats.add(Stat.input, heatRequirement, StatUnit.heatUnits);
        } else {
            stats.add(Stat.booster, AquaStats.heatBooster(heatRequirement, overheatScale, maxEfficiency, flipHeatScale));
        }
        if (itemBoostIntensity != 1 && findConsumer(f -> f instanceof ConsumeItems && f.booster) instanceof ConsumeItems coni) {
            stats.remove(Stat.booster);
            stats.add(Stat.booster, AquaStats.itemBoosters("{0}" + StatUnit.timesSpeed.localized(), stats.timePeriod, itemBoostIntensity, 0f, coni.items, ItemBoostUseTime));
        }
        if (liquidBoostIntensity != 1 && findConsumer(f -> f instanceof ConsumeLiquidBase && f.booster) instanceof ConsumeLiquidBase consBase) {
            stats.add(Stat.booster,
                    StatValues.speedBoosters("{0}" + StatUnit.timesSpeed.localized(),
                            consBase.amount,
                            liquidBoostIntensity * liquidBoostIntensity, false, liquid -> consBase instanceof ConsumeLiquid && ((ConsumeLiquid) consBase).liquid == liquid)
            );
        }
    }

    @Override
    public TextureRegion[] icons() {
        return drawer.finalIcons(this);
    }

    /** refactored to fill *per-build* item lists */
    protected void countOre(Tile tile, DrillBuild build) {
        build.returnItems.clear();
        build.returnCounts.clear();

        ObjectIntMap<Item> oreCount = new ObjectIntMap<>();
        Seq<Item> itemArray = new Seq<>();

        for (Tile other : tile.getLinkedTilesAs(this, tempTiles)) {
            if (canMine(other)) {
                oreCount.increment(getDrop(other), 0, 1);
            }
        }

        for (Item item : oreCount.keys()) {
            itemArray.add(item);
        }

        itemArray.sort((item1, item2) -> {
            int type = Boolean.compare(!item1.lowPriority, !item2.lowPriority);
            if (type != 0) return type;
            int amounts = Integer.compare(oreCount.get(item1, 0), oreCount.get(item2, 0));
            if (amounts != 0) return amounts;
            return Integer.compare(item1.id, item2.id);
        });

        for (Item item : itemArray) {
            build.returnItems.add(item);
            build.returnCounts.put(item, oreCount.get(item, 0));
        }
    }

    public boolean canMine(Tile tile) {
        if (tile == null || tile.block().isStatic()) return false;
        Item drops = tile.drop();
        return drops != null && drops.hardness <= tier && (blockedItems == null || !blockedItems.contains(drops));
    }

    public class DrillBuild extends Building implements HeatConsumer {
        public Seq<Item> returnItems = new Seq<>();
        public ObjectIntMap<Item> returnCounts = new ObjectIntMap<>();

        public float progress;
        public float totalProgress;
        public float warmup;
        public float timeDrilled;
        public float lastDrillSpeed;
        public float[] sideHeat = new float[4];
        public float heat = 0f;

        @Override
        public boolean shouldConsume() {
            for (Item item : returnItems) {
                int curItems = this.items.get(item);
                if (curItems < itemCapacity) return enabled;
            }
            return false;
        }

        @Override
        public boolean shouldAmbientSound() {
            return efficiency > 0;
        }

        @Override
        public float ambientVolume() {
            return efficiency * (size * size) / 4f;
        }

        @Override
        public void drawSelect() {
            int i = 0;
            for (Item item : returnItems) {
                float dx = x - block.size * tilesize / 2f, dy = y + block.size * tilesize / 2f, s = iconSmall / 4f * item.fullIcon.ratio(), h = iconSmall / 4f;
                Draw.mixcol(Color.darkGray, 1f);
                Draw.rect(item.fullIcon, dx + (i * 5), dy - 1, s, h);
                Draw.reset();
                Draw.rect(item.fullIcon, dx + (i * 5), dy, s, h);
                i++;
            }
        }

        @Override
        public void pickedUp() {
        }

        @Override
        public void onProximityUpdate() {
            super.onProximityUpdate();
            countOre(tile, this);
        }

        @Override
        public Object senseObject(LAccess sensor) {
            if (sensor == LAccess.firstItem && !returnItems.isEmpty()) return returnItems.first();
            return super.senseObject(sensor);
        }

        @Override
        public void updateTile() {
            heat = calculateHeat(sideHeat);

            for (Item dominantItem : returnItems) {
                if (timer(timerDump, dumpTime / timeScale)) {
                    dump(dominantItem);
                }
                int curItems = items.get(dominantItem);
                if (curItems >= itemCapacity) continue;

                float delay = getDrillTime(dominantItem);

                float liquidEff = 0f;
                float itemEff = 0f;

                Consume liquidBooster = findConsumer(c -> c instanceof ConsumeLiquidBase && c.booster);
                if (liquidBooster != null) liquidEff = liquidBooster.efficiency(this);

                Consume itemBooster = findConsumer(c -> c instanceof ConsumeItems && c.booster);
                if (itemBooster != null) itemEff = itemBooster.efficiency(this);

                float speed = Mathf.lerp(1f, liquidBoostIntensity, liquidEff) *
                        Mathf.lerp(1f, itemBoostIntensity, itemEff) *
                        efficiency;

                lastDrillSpeed = (speed * returnCounts.get(dominantItem, 1)) / delay;
                warmup = Mathf.approachDelta(warmup, speed, warmupSpeed);
                progress += delta() * returnCounts.get(dominantItem, 1) * speed * warmup;
                totalProgress += warmup * Time.delta;

                if (progress >= delay) {
                    int amount = (int) (progress / delay);
                    for (int i = 0; i < amount; i++) offload(dominantItem);
                    progress %= delay;

                    if (wasVisible && Mathf.chanceDelta(drillEffectChance * warmup))
                        drillEffect.at(x + Mathf.range(drillEffectRnd), y + Mathf.range(drillEffectRnd), dominantItem.color);
                }

                if (Mathf.chanceDelta(updateEffectChance * warmup))
                    updateEffect.at(x + Mathf.range(size * 2f), y + Mathf.range(size * 2f));
            }

            if (returnItems.isEmpty()) warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);
        }

        @Override
        public float progress() {
            if (returnItems.isEmpty()) return 0f;
            return Mathf.clamp(progress / getDrillTime(returnItems.first()));
        }

        @Override
        public double sense(LAccess sensor) {
            if (sensor == LAccess.progress && !returnItems.isEmpty()) return progress;
            return super.sense(sensor);
        }

        @Override
        public void drawCracks() {
        }

        public void drawDefaultCracks() {
            super.drawCracks();
        }

        @Override
        public void draw() {
            drawer.draw(this);
            Draw.z(Layer.blockCracks);
            drawDefaultCracks();

            Draw.z(Layer.blockAfterCracks);
            if (drawMineItem) {
                float r = 0, g = 0, b = 0;
                for (Item itemb : returnItems) {
                    Color c = itemb.color;
                    r += c.r;
                    g += c.g;
                    b += c.b;
                }
                int count = returnItems.size;
                Draw.color(r / count, g / count, b / count);
                Draw.rect(itemRegion, x, y);
            }
            Draw.color();
        }

        @Override
        public float efficiencyScale() {
            if (!hasHeat) return baseEfficiency;
            float eff;
            float over = Math.max(heat - heatRequirement, 0f);
            if (flipHeatScale) {
                float below = Mathf.clamp(-heat / heatRequirement, 0f, 1f);
                eff = baseEfficiency + below + over / heatRequirement * overheatScale;
            } else {
                float base = Mathf.clamp(heat / heatRequirement, 0f, 1f);
                eff = baseEfficiency + base + over / heatRequirement * overheatScale;
            }
            return Mathf.clamp(eff, 0f, maxEfficiency);
        }

        @Override
        public void incrementDump(int prox) {
            if (prox != 0) cdump = (int) ((cdump + 1 * efficiency) % prox);
        }

        @Override
        public byte version() {
            return 1;
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(progress);
            write.f(warmup);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            if (revision >= 1) {
                progress = read.f();
                warmup = read.f();
            }
        }

        public float totalProgress() {
            return totalProgress;
        }

        @Override
        public float heatRequirement() {
            return heatRequirement;
        }

        @Override
        public float[] sideHeat() {
            return sideHeat;
        }

        //Visual liquid is bugging out causing flickering issues.
        //No clue what's causing it, but it's easier to override the method than to find out.
        public float lastOpacity = 0f;
        @Override
        public void drawLight() {
            if (!block.hasLiquids || !block.drawLiquidLight) return;

            Liquid curLiq = maxLiquid();
            if(curLiq != null){
                float targetOpacity = curLiq.color.a * (liquids.get(curLiq)/liquidCapacity);
                lastOpacity = Mathf.lerpDelta(lastOpacity, targetOpacity, 0.1f);

                Drawf.light(x, y, block.size * 30f, curLiq.color, lastOpacity);
            }
        }
        public @Nullable Liquid maxLiquid(){
            Liquid[] max = new Liquid[1];
            float[] highest = new float[1];

            liquids.each((liq, amount) -> {
                if(amount > highest[0]){
                    highest[0] = amount;
                    max[0] = liq;
                }
            });
            return max[0];
        }
    }
}
