package aquarion.world.blocks.production;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.struct.ObjectMap;
import arc.util.Nullable;
import arc.util.Time;
import mindustry.content.Blocks;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.meta.BlockStatus;

import static mindustry.Vars.*;

public class WallCrafter extends GenericCrafter {
    public ObjectMap<Block, WallRecipe> recipes = new ObjectMap<>();
    public @Nullable WallRecipe wallFallback;
    public int scanSize = 4;

    public WallCrafter(String name) {
        super(name);
        update = true;
        solid = true;
        hasItems = true;
        hasLiquids = true;
        hasPower = true;
        outputsLiquid = true;
        rotate = true;
        craftTime = 60f;
        liquidCapacity = 480f;
        itemCapacity = 120;
        ambientSound = mindustry.gen.Sounds.loopMachine;
        ambientSoundVolume = 0.03f;
    }

    public WallCrafter add(Block wall, WallRecipe recipe) {
        recipes.put(wall, recipe);
        return this;
    }

    public WallCrafter fallback(WallRecipe recipe) {
        wallFallback = recipe;
        return this;
    }

    public @Nullable WallRecipe findRecipe(Block wall, Block floor) {
        WallRecipe direct = recipes.get(wall);
        if (direct != null) return direct;

        direct = recipes.get(floor);
        if (direct != null) return direct;

        if (wall instanceof Wall && wallFallback != null) {
            return wallFallback;
        }

        return null;
    }

    public @Nullable WallRecipe scanArea(int bx, int by, int rotation, @Nullable Block[] foundWall) {
        int dx = Geometry.d4(rotation).x;
        int dy = Geometry.d4(rotation).y;

        int startX, startY, w, h;
        if (dx != 0) {
            startX = (dx > 0 ? bx + size : bx - scanSize) - 1;
            startY = by - 1;
            w = scanSize;
            h = size;
        } else {
            startX = bx - 1;
            startY = (dy > 0 ? by + size : by - scanSize) - 1;
            w = size;
            h = scanSize;
        }

        for (int iy = 0; iy < h; iy++) {
            for (int ix = 0; ix < w; ix++) {
                Tile tile = world.tile(startX + ix, startY + iy);
                if (tile == null) continue;
                Block wall = tile.block();
                Block floor = tile.floor();
                Block key = wall != Blocks.air ? wall : floor;
                WallRecipe recipe = findRecipe(wall, floor);
                if (recipe != null) {
                    if (foundWall != null) foundWall[0] = key;
                    return recipe;
                }
            }
        }
        return null;
    }

    public static class WallRecipe {
        public final ItemStack[] outputItems;
        public final @Nullable LiquidStack[] inputLiquids;
        public final float craftTime;

        public WallRecipe(ItemStack[] outputItems, @Nullable LiquidStack[] inputLiquids, float craftTime) {
            this.outputItems = outputItems;
            this.inputLiquids = inputLiquids;
            this.craftTime = craftTime;
        }
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);

        int dx = Geometry.d4(rotation).x;
        int dy = Geometry.d4(rotation).y;

        int startX, startY, w, h;
        if (dx != 0) {
            startX = (dx > 0 ? x + size : x - scanSize) - 1;
            startY = y - 1;
            w = scanSize;
            h = size;
        } else {
            startX = x - 1;
            startY = (dy > 0 ? y + size : y - scanSize) - 1;
            w = size;
            h = scanSize;
        }

        Block[] foundWall = {null};
        WallRecipe recipe = scanArea(x, y, rotation, foundWall);
        if (recipe == null) return;

        for (int iy = 0; iy < h; iy++) {
            for (int ix = 0; ix < w; ix++) {
                Tile tile = world.tile(startX + ix, startY + iy);
                if (tile != null) {
                    Drawf.selected(tile, Pal.accent);
                }
            }
        }

        float s = iconSmall / 4f;
        int colSep = 3;
        Block key = foundWall[0];

        Draw.color(Color.white);
        drawPlaceText(key.localizedName, x, y, valid);
        Draw.color();

        int leftCol = x - colSep;
        int rightCol = x + colSep;
        int dataRow = 1;

        if (recipe.inputLiquids != null) {
            for (LiquidStack input : recipe.inputLiquids) {
                float rate = 60f * input.amount / recipe.craftTime;
                String text = input.liquid.localizedName + ": " + formatRate(rate*60) + "/s";
                float tw = drawPlaceText(text, leftCol, y + dataRow, Pal.accent, false);
                float cx = leftCol * tilesize + offset;
                float idx = cx - tw / 2f - 4f;
                float idy = (y + dataRow) * tilesize+25;
                Draw.rect(input.liquid.fullIcon, idx, idy, s, s);
                dataRow++;
            }
        }

        dataRow = 1;
        if (recipe.outputItems != null) {
            for (ItemStack output : recipe.outputItems) {
                float rate = 60f * output.amount / recipe.craftTime;
                String text = output.item.localizedName + ": " + formatRate(rate) + "/s";
                float tw = drawPlaceText(text, rightCol, y + dataRow, Color.white, false);
                float cx = rightCol * tilesize + offset;
                float idx = cx - tw / 2f - 4f;
                float idy = (y + dataRow) * tilesize+25;
                Draw.rect(output.item.fullIcon, idx, idy, s, s);
                dataRow++;
            }
        }
    }

    private String formatRate(float rate) {
        if (rate >= 100) return String.valueOf((int)rate);
        if (rate == (int)rate) return String.valueOf((int)rate);
        return String.format("%.1f", rate);
    }

    public class WallCrafterBuild extends GenericCrafterBuild {
        public Block currentWall;
        public WallRecipe currentRecipe;

        @Override
        public void updateTile() {
            Block[] foundWall = {null};
            WallRecipe recipe = scanArea(tileX(), tileY(), rotation, foundWall);
            Block key = foundWall[0];

            if (key != currentWall) {
                currentWall = key;
                currentRecipe = recipe;
            }

            if (currentRecipe == null || !canWork()) {
                warmup = Mathf.approachDelta(warmup, 0f, 0.02f);
                return;
            }

            float liquidEfficiency = 1f;
            if (currentRecipe.inputLiquids != null) {
                for (LiquidStack input : currentRecipe.inputLiquids) {
                    float available = liquids.get(input.liquid);
                    float required = input.amount * edelta();
                    liquidEfficiency = Math.min(liquidEfficiency, required <= 0f ? 1f : Mathf.clamp(available / required));
                }
            }

            if (liquidEfficiency <= 0.01f || !hasOutputSpace()) {
                warmup = Mathf.approachDelta(warmup, 0f, 0.02f);
                return;
            }

            warmup = Mathf.approachDelta(warmup, 1f, 0.02f);
            progress += getProgressIncrease(currentRecipe.craftTime) * liquidEfficiency;
            totalProgress += warmup * Time.delta;

            if (wasVisible && Mathf.chanceDelta(updateEffectChance)) {
                updateEffect.at(x + Mathf.range(size * updateEffectSpread), y + Mathf.range(size * updateEffectSpread));
            }

            if (progress >= 1f) {
                craft();
            }

            dumpOutputs();
        }

        private boolean hasOutputSpace() {
            if (currentRecipe.outputItems != null) {
                for (ItemStack output : currentRecipe.outputItems) {
                    if (items.get(output.item) + output.amount > itemCapacity) return false;
                }
            }
            return true;
        }
        //I, I AM A STURGEON, I AM A STURGEON
        private boolean canWork() {
            return enabled && hasOutputSpace();
        }

        @Override
        public float getProgressIncrease(float baseTime) {
            return super.getProgressIncrease(baseTime);
        }

        public void craft() {
            if (currentRecipe.inputLiquids != null) {
                for (LiquidStack input : currentRecipe.inputLiquids) {
                    liquids.remove(input.liquid, input.amount);
                }
            }

            if (currentRecipe.outputItems != null) {
                for (ItemStack output : currentRecipe.outputItems) {
                    for (int i = 0; i < output.amount; i++) {
                        offload(output.item);
                    }
                }
            }

            progress %= 1f;

            if (wasVisible) {
                craftEffect.at(x, y);
            }
        }

        public void dumpOutputs() {
            if (currentRecipe != null && currentRecipe.outputItems != null) {
                if (timer(timerDump, dumpTime / timeScale)) {
                    for (ItemStack output : currentRecipe.outputItems) {
                        dump(output.item);
                    }
                }
            }
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return false;
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            if (currentRecipe == null || currentRecipe.inputLiquids == null) return false;
            for (LiquidStack input : currentRecipe.inputLiquids) {
                if (input.liquid == liquid && liquids.get(liquid) < liquidCapacity - 0.001f) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public BlockStatus status() {
            if (currentRecipe == null) return BlockStatus.noInput;
            if (!enabled) return BlockStatus.logicDisable;
            if (warmup > 0.5f) return BlockStatus.active;
            if (currentRecipe.inputLiquids != null) {
                for (LiquidStack input : currentRecipe.inputLiquids) {
                    if (liquids.get(input.liquid) < input.amount * 2f) return BlockStatus.noInput;
                }
            }
            return BlockStatus.noOutput;
        }

        @Override
        public boolean shouldAmbientSound() {
            return efficiency > 0 && currentRecipe != null;
        }
    }
}
