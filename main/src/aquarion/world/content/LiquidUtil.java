package aquarion.world.content;

import aquarion.world.blocks.distribution.ModifiedLiquidJunction;
import aquarion.world.blocks.distribution.SiphonSorter;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.type.Liquid;
import mindustry.world.blocks.liquid.LiquidBlock;
import mindustry.world.modules.LiquidModule;

public class LiquidUtil {
    /** Total amount of liquid stored in a liquid module. */
    public static float total(LiquidModule liquids){
        return liquids == null ? 0f : liquids.sum((liquid, amount) -> amount);
    }

    /** Amount of free space left in a building's liquid storage. */
    public static float freeSpace(Building build){
        if(build == null || build.liquids == null) return 0f;
        return build.block.liquidCapacity - total(build.liquids);
    }

    /** Amount of free space left in a liquid module with the given capacity. */
    public static float freeSpace(LiquidModule liquids, float capacity){
        if(liquids == null) return 0f;
        return capacity - total(liquids);
    }

    /**
     * Conduit-style pressure flow between two storages, capped by the amount available
     * in {@code from} and the free space of {@code to}. Returns a per-second rate; callers
     * should scale it by {@code delta()}.
     */
    public static float flow(Building from, Liquid liquid, Building to){
        return flow(from.liquids, from.block.liquidCapacity, from, to, liquid);
    }

    /**
     * Conduit-style pressure flow out of a side buffer with its own {@code fromCapacity}
     * into a building. Returns a per-second rate; callers should scale it by {@code delta()}.
     */
    public static float flow(LiquidModule from, float fromCapacity, Building fromBuild, Building to, Liquid liquid){
        float levelHere = from.get(liquid) / fromCapacity;
        float levelNext = to.liquids.get(liquid) / to.block.liquidCapacity;
        float deltaLevel = Math.max(levelHere - levelNext, 0f) * 50;

        float rho = 1f;
        float viscosityFactor = Mathf.clamp(1f - liquid.viscosity * 0.5f, 0.2f, 1f);
        float Cd = 0.8f;
        float A = 1f;

        float flow = Cd * A * Mathf.sqrt(2f * deltaLevel / rho) * viscosityFactor;
        flow *= 10f;

        flow = Math.min(flow, from.get(liquid));
        flow = Math.min(flow, Math.max(freeSpaceFor(to, fromBuild), 0f));
        return flow;
    }

    /** The amount of liquid {@code to} will actually store when {@code from} pushes into it, honoring per-side buffers. */
    public static float freeSpaceFor(Building to, Building from){
        if(to == null) return 0f;
        if(to instanceof ModifiedLiquidJunction.wtfBuild j){
            return j.freeSpaceFor(from);
        }
        if(to instanceof SiphonSorter.SiphonSorterBuild s){
            return s.freeSpaceFor(from);
        }
        return to.block.liquidCapacity - total(to.liquids);
    }

    /** Provides the texture region to draw a liquid with. */
    public interface LiquidRegionProvider{
        TextureRegion region(Liquid liquid);
    }

    /**
     * Draws every present liquid as a full flat fill, each one stacked directly on
     * top of the previous one, so a mix of liquids is visible as layered overlays
     * instead of only the dominant liquid. {@code alpha} is applied to each liquid.
     */
    public static void drawOverlayed(LiquidRegionProvider provider, float x, float y, float alpha, LiquidModule liquids){
        if(liquids == null) return;
        liquids.each((liquid, amount) -> {
            if(amount > 0.001f){
                Drawf.liquid(provider.region(liquid), x, y, alpha, liquid.color);
            }
        });
    }

    /** Variant of {@link #drawOverlayed(LiquidRegionProvider, float, float, float, LiquidModule)} that scales each liquid's alpha by its own fill fraction. */
    public static void drawOverlayedLiquid(LiquidRegionProvider provider, float x, float y, float capacity, LiquidModule liquids){
        if(liquids == null) return;
        liquids.each((liquid, amount) -> {
            if(amount > 0.001f){
                Drawf.liquid(provider.region(liquid), x, y, Mathf.clamp(amount / capacity, 0f, 1f), liquid.color);
            }
        });
    }

    /** Convenience overload with uniform padding on all sides, mirroring {@code drawTiledFrames}. */
    public static void drawOverlayedFrames(int size, float x, float y, float padding, LiquidModule liquids, float capacity){
        drawOverlayedFrames(size, x, y, padding, padding, padding, padding, liquids, capacity);
    }

    /** Draws the tiled flat fill for every present liquid in a router/tank-style block, stacked over each other. */
    public static void drawOverlayedFrames(int size, float x, float y, float padLeft, float padRight, float padTop, float padBottom, LiquidModule liquids, float capacity){
        if(liquids == null) return;
        liquids.each((liquid, amount) -> {
            if(amount > 0.001f){
                LiquidBlock.drawTiledFrames(size, x, y, padLeft, padRight, padTop, padBottom, liquid, Mathf.clamp(amount / capacity, 0f, 1f));
            }
        });
    }

    /** Gives the blended color of all liquids, weighted by amount; used for stable block colors. */
    public static Color mixedColor(LiquidModule liquids, Color out){
        float total = total(liquids);
        if(total <= 0.001f){
            out.set(1f, 1f, 1f, 1f);
            return out;
        }

        out.set(0f, 0f, 0f, 0f);
        liquids.each((liquid, amount) -> {
            if(amount > 0f){
                out.r += liquid.color.r * amount;
                out.g += liquid.color.g * amount;
                out.b += liquid.color.b * amount;
            }
        });
        out.r /= total;
        out.g /= total;
        out.b /= total;
        out.a = 1f;
        return out;
    }
}
