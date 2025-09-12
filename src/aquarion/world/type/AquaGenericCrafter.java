package aquarion.world.type;
import aquarion.world.Uti.AquaStats;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.blocks.heat.HeatConsumer;
import mindustry.world.blocks.liquid.Conduit.*;
import mindustry.world.consumers.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;
import mindustry.world.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.logic.*;

import static mindustry.Vars.*;

public class AquaGenericCrafter extends AquaBlock{
    /** Written to outputItems as a single-element array if outputItems is null. */
    public @Nullable ItemStack outputItem;
    /** Overwrites outputItem if not null. */
    public @Nullable ItemStack[] outputItems;

    /** Written to outputLiquids as a single-element array if outputLiquids is null. */
    public @Nullable LiquidStack outputLiquid;
    /** Overwrites outputLiquid if not null. */
    public @Nullable LiquidStack[] outputLiquids;
    /** Liquid output directions, specified in the same order as outputLiquids. Use -1 to dump in every direction. Rotations are relative to block. */
    public int[] liquidOutputDirections = {-1};

    /** if true, crafters with multiple liquid outputs will dump excess when there's still space for at least one liquid type */
    public boolean dumpExtraLiquid = true;
    public boolean ignoreLiquidFullness = false;

    public boolean hasHeat = false;
    public float craftTime = 80;
    public Effect craftEffect = Fx.none;
    public Effect updateEffect = Fx.none;
    public float updateEffectChance = 0.04f;
    public float updateEffectSpread = 4f;
    public float warmupSpeed = 0.019f;
    /** Only used for legacy cultivator blocks. */
    public boolean legacyReadWarmup = false;
    /**Base heat requirement for 100% efficiency. if put into negatives it will invert*/
    public float heatRequirement = 0f;
    /**After heat meets this requirement, excess heat will be scaled by this number.*/
    public float overheatScale = 0.8f;
    /**Maximum possible efficiency after overheat.*/
    public float maxEfficiency = 1f;
    /** Base heat efficiency*/
    public float baseEfficiency = 1;
    /**if you go below the heat req eff goes up*/
    public boolean flipHeatScale = false;
    /** Boost intensities for respective item and liquid boosters*/
    public float liquidBoostIntensity = 1.6f, itemBoostIntensity = 1.5f;
    
    public DrawBlock drawer = new DrawDefault();

    public AquaGenericCrafter(String name){
        super(name);
        update = true;
        solid = true;
        hasItems = true;
        ambientSound = Sounds.machine;
        sync = true;
        ambientSoundVolume = 0.03f;
        flags = EnumSet.of(BlockFlag.factory);
        drawArrow = false;
    }

    @Override
    public void setStats(){
        stats.timePeriod = craftTime;
        super.setStats();
        if((hasItems && itemCapacity > 0) || outputItems != null){
            stats.add(Stat.productionTime, craftTime / 60f, StatUnit.seconds);
        }

        if(outputItems != null){
            stats.add(Stat.output, StatValues.items(craftTime, outputItems));
        }

        if(outputLiquids != null){
            stats.add(Stat.output, StatValues.liquids(1f, outputLiquids));
        }
        if(hasHeat) {
            if (baseEfficiency < 1) {
                stats.add(Stat.input, heatRequirement, StatUnit.heatUnits);
            } else {
                stats.add(Stat.booster, AquaStats.heatBooster(heatRequirement, overheatScale, maxEfficiency + baseEfficiency, flipHeatScale));
            }
            stats.add(Stat.maxEfficiency, (int) (maxEfficiency * 100f), StatUnit.percent);
            if (itemBoostIntensity != 1 && findConsumer(f -> f instanceof ConsumeItems && f.booster) instanceof ConsumeItems coni) {
                stats.remove(Stat.booster);
                stats.add(Stat.booster, AquaStats.itemBoosters("{0}" + StatUnit.timesSpeed.localized(), stats.timePeriod, itemBoostIntensity, 0f, coni.items, craftTime));
            }
        }
    }

    @Override
    public void setBars(){
        super.setBars();

        if(outputLiquids != null && outputLiquids.length > 0){
            removeBar("liquid");
            for(var stack : outputLiquids){
                addLiquidBar(stack.liquid);
            }
        }

        if(hasHeat && (heatRequirement > 0 || heatRequirement < 0)){
            addBar("efficiency", (AquaGenericCrafter.AquaGenericCrafterBuild entity) ->
                    new Bar(() ->
                            Core.bundle.format("bar.heatpercent",
                                    (int)(entity.heat + 0.01f),
                                    (int)(entity.efficiencyScale() * 100 + 0.01f)),
                            () -> {
                                float max = heatRequirement * 5f;
                                float heat = entity.heat;

                                if(heat < 0f){
                                    float t = Mathf.clamp(1f + heat / max);
                                    return Tmp.c1.set(Color.black).lerp(Pal.techBlue, t);
                                }else{
                                    float t = Mathf.clamp(heat / max);
                                    return Tmp.c1.set(Pal.lightOrange).lerp(Color.white, t);
                                }
                            },
                            () -> Mathf.clamp(Math.abs(entity.heat) / Math.abs(heatRequirement) + entity.efficiency-baseEfficiency)
                    )
            );
        }
    }


    @Override
    public boolean rotatedOutput(int fromX, int fromY, Tile destination){
        if(!(destination.build instanceof ConduitBuild)) return false;

        Building crafter = world.build(fromX, fromY);
        if(crafter == null) return false;
        int relative = Mathf.mod(crafter.relativeTo(destination) - crafter.rotation, 4);
        for(int dir : liquidOutputDirections){
            if(dir == -1 || dir == relative) return false;
        }

        return true;
    }

    @Override
    public void load(){
        super.load();

        drawer.load(this);
    }

    @Override
    public void init(){
        if(outputItems == null && outputItem != null){
            outputItems = new ItemStack[]{outputItem};
        }
        if(outputLiquids == null && outputLiquid != null){
            outputLiquids = new LiquidStack[]{outputLiquid};
        }
        //write back to outputLiquid, as it helps with sensing
        if(outputLiquid == null && outputLiquids != null && outputLiquids.length > 0){
            outputLiquid = outputLiquids[0];
        }
        outputsLiquid = outputLiquids != null;

        if(outputItems != null) hasItems = true;
        if(outputLiquids != null) hasLiquids = true;

        super.init();
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        drawer.drawPlan(this, plan, list);
    }

    @Override
    public TextureRegion[] icons(){
        return drawer.finalIcons(this);
    }

    @Override
    public boolean outputsItems(){
        return outputItems != null;
    }

    @Override
    public void getRegionsToOutline(Seq<TextureRegion> out){
        drawer.getRegionsToOutline(this, out);
    }

    @Override
    public void drawOverlay(float x, float y, int rotation){
        if(outputLiquids != null){
            for(int i = 0; i < outputLiquids.length; i++){
                int dir = liquidOutputDirections.length > i ? liquidOutputDirections[i] : -1;

                if(dir != -1){
                    Draw.rect(
                            outputLiquids[i].liquid.fullIcon,
                            x + Geometry.d4x(dir + rotation) * (size * tilesize / 2f + 4),
                            y + Geometry.d4y(dir + rotation) * (size * tilesize / 2f + 4),
                            8f, 8f
                    );
                }
            }
        }
    }

    public class AquaGenericCrafterBuild extends Building implements HeatConsumer {
        public float progress;
        public float totalProgress;
        public float warmup;
        public float[] sideHeat = new float[4];
        public float heat = 0f;

        @Override
        public void draw(){
            drawer.draw(this);
        }

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

        @Override
        public boolean shouldConsume(){
            if(outputItems != null){
                for(var output : outputItems){
                    if(items.get(output.item) + output.amount > itemCapacity){
                        return false;
                    }
                }
            }
            if(outputLiquids != null && !ignoreLiquidFullness){
                boolean allFull = true;
                for(var output : outputLiquids){
                    if(liquids.get(output.liquid) >= liquidCapacity - 0.001f){
                        if(!dumpExtraLiquid){
                            return false;
                        }
                    }else{
                        //if there's still space left, it's not full for all liquids
                        allFull = false;
                    }
                }

                //if there is no space left for any liquid, it can't reproduce
                if(allFull){
                    return false;
                }
            }

            return enabled;
        }

        @Override
        public void updateTile(){
            if(hasHeat){
                heat = calculateHeat(sideHeat);
            }

            if(efficiency > 0){
                // boosters + crafting speed
                float liquidEff = 0f, itemEff = 0f;
                Consume liquidBooster = findConsumer(c -> c instanceof ConsumeLiquidBase && c.booster);
                if(liquidBooster != null) liquidEff = liquidBooster.efficiency(this);
                Consume itemBooster = findConsumer(c -> c instanceof ConsumeItems && c.booster);
                if(itemBooster != null) itemEff = itemBooster.efficiency(this);

                float speed = Mathf.lerp(1f, liquidBoostIntensity, liquidEff) *
                        Mathf.lerp(1f, itemBoostIntensity, itemEff) *
                        efficiency;

                warmup = Mathf.approachDelta(warmup, speed > 0 ? 1f : 0f, warmupSpeed);
                progress += getProgressIncrease(craftTime);

                if(outputLiquids != null) {
                    float inc = getProgressIncrease(1f);
                    for (var output : outputLiquids) {
                        handleLiquid(this, output.liquid, Math.min(output.amount * inc, liquidCapacity - liquids.get(output.liquid)));
                    }
                }

                if(wasVisible && Mathf.chanceDelta(updateEffectChance)){
                    updateEffect.at(x + Mathf.range(size * updateEffectSpread),
                            y + Mathf.range(size * updateEffectSpread));
                }
            }else{
                warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);
            }

            totalProgress += warmup * Time.delta;
            if(progress >= 1f){
                craft();
            }
            dumpOutputs();
        }

        @Override
        public float getProgressIncrease(float baseTime){
            if(ignoreLiquidFullness){
                return super.getProgressIncrease(baseTime);
            }
            float scaling = 1f, max = 1f;
            if(outputLiquids != null){
                max = 0f;
                for(var s : outputLiquids){
                    float value = (liquidCapacity - liquids.get(s.liquid)) / (s.amount * edelta());
                    scaling = Math.min(scaling, value);
                    max = Math.max(max, value);
                }
            }
            return super.getProgressIncrease(baseTime) * (dumpExtraLiquid ? Math.min(max, 1f) : scaling);
        }


        public float warmupTarget(){
            return 1f;
        }

        @Override
        public float warmup(){
            return warmup;
        }

        @Override
        public float totalProgress(){
            return totalProgress;
        }

        public void craft(){
            consume();

            if(outputItems != null){
                for(var output : outputItems){
                    for(int i = 0; i < output.amount; i++){
                        offload(output.item);
                    }
                }
            }

            if(wasVisible){
                craftEffect.at(x, y);
            }
            progress %= 1f;
        }

        public void dumpOutputs(){
            if(outputItems != null && timer(timerDump, dumpTime / timeScale)){
                for(ItemStack output : outputItems){
                    dump(output.item);
                }
            }

            if(outputLiquids != null){
                for(int i = 0; i < outputLiquids.length; i++){
                    int dir = liquidOutputDirections.length > i ? liquidOutputDirections[i] : -1;

                    dumpLiquid(outputLiquids[i].liquid, 2f, dir);
                }
            }
        }

        @Override
        public double sense(LAccess sensor){
            if(sensor == LAccess.progress) return progress();
            //attempt to prevent wild total liquid fluctuation, at least for crafters
            if(sensor == LAccess.totalLiquids && outputLiquid != null) return liquids.get(outputLiquid.liquid);
            return super.sense(sensor);
        }

        @Override
        public float progress(){
            return Mathf.clamp(progress);
        }

        @Override
        public int getMaximumAccepted(Item item){
            return itemCapacity;
        }

        @Override
        public boolean shouldAmbientSound(){
            return efficiency > 0;
        }
        @Override
        public float efficiencyScale() {
            if(!hasHeat) return 1f; // default full efficiency

            float eff;
            float over = Math.max(heat - heatRequirement, 0f);

            if(flipHeatScale){
                eff = -Math.min((heat / -heatRequirement) + over / -heatRequirement * overheatScale, maxEfficiency);
                if(eff > 1) eff = Math.min(((eff - 1) * overheatScale) + 1, maxEfficiency);
            }else{
                eff = Math.min(Mathf.clamp(heat / heatRequirement) + over / heatRequirement * overheatScale, maxEfficiency) + baseEfficiency;
            }

            return Math.max(eff, 0f);
        }
        @Override
        public float heatRequirement() {
            return hasHeat ? heatRequirement : 0f;
        }

        @Override
        public float[] sideHeat() {
            return hasHeat ? sideHeat : new float[4];
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(progress);
            write.f(warmup);
            if(legacyReadWarmup) write.f(0f);
        }
        public float lastOpacity = 0;



        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            progress = read.f();
            warmup = read.f();
            if(legacyReadWarmup) read.f();
        }
    }
}
