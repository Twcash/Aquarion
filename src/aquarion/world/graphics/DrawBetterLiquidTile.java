package aquarion.world.graphics;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.Tmp;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.type.Liquid;
import mindustry.world.Block;
import mindustry.world.blocks.liquid.LiquidBlock;
import mindustry.world.draw.DrawBlock;

import static mindustry.Vars.renderer;
import static mindustry.Vars.tilesize;

public class DrawBetterLiquidTile extends DrawBlock {
    public Liquid drawLiquid;
    public float padding;
    public float padLeft = -1, padRight = -1, padTop = -1, padBottom = -1;
    public float alpha = 1f;
    public float rotation;

    public DrawBetterLiquidTile(Liquid drawLiquid, float padding) {
        this.drawLiquid = drawLiquid;
        this.padding = padding;
    }

    public DrawBetterLiquidTile(Liquid drawLiquid) {
        this.drawLiquid = drawLiquid;
    }

    public DrawBetterLiquidTile() {
    }

    @Override
    public void draw(Building build) {
        Liquid drawn = drawLiquid != null ? drawLiquid : build.liquids.current();
        float liquidAlpha = build.liquids.get(drawn) / build.block.liquidCapacity * alpha;
        drawTiledFrames(build.block.size, build.x, build.y, padLeft, padRight, padTop, padBottom, drawn, liquidAlpha, rotation);
    }

    @Override
    public void load(Block block) {
        if (padLeft < 0) padLeft = padding;
        if (padRight < 0) padRight = padding;
        if (padTop < 0) padTop = padding;
        if (padBottom < 0) padBottom = padding;
    }

    public static void drawTiledFrames(int size, float x, float y, float padLeft, float padRight, float padTop, float padBottom, Liquid liquid, float alpha, float rotation) {
        TextureRegion region = renderer.fluidFrames[liquid.gas ? 1 : 0][liquid.getAnimationFrame()];
        TextureRegion toDraw = Tmp.tr1;

        float halfSize = size / 2f * tilesize;
        float leftBounds = -halfSize + padLeft;
        float rightBounds = halfSize - padRight;
        float bottomBounds = -halfSize + padBottom;
        float topBounds = halfSize - padTop;

        Color color = Tmp.c1.set(liquid.color).a(alpha);

        for (int sx = 0; sx < size; sx++) {
            for (int sy = 0; sy < size; sy++) {
                float relx = sx - (size - 1) / 2f;
                float rely = sy - (size - 1) / 2f;

                float tileX = x + relx * tilesize;
                float tileY = y + rely * tilesize;

                // Apply rotation to each tile position
                float[] rotatedPos = rotatePosition(tileX, tileY, x, y, rotation);
                tileX = rotatedPos[0];
                tileY = rotatedPos[1];

                // Adjust the clipping for negative padding (expansion beyond bounds)
                float clipX = Math.max(0, leftBounds - (tileX - tilesize / 2f));
                float clipY = Math.max(0, bottomBounds - (tileY - tilesize / 2f));
                float clipWidth = Math.min(tilesize, rightBounds - (tileX - tilesize / 2f)) - clipX;
                float clipHeight = Math.min(tilesize, topBounds - (tileY - tilesize / 2f)) - clipY;

                if (clipWidth <= 0 || clipHeight <= 0) continue;

                toDraw.set(region);

                // Adjust region dimensions
                toDraw.setWidth(clipWidth * 4f); // Scaling for clipping
                toDraw.setHeight(clipHeight * 4f);
                toDraw.setX(region.getX() + clipX * 4f);
                toDraw.setY(region.getY() + clipY * 4f);

                Drawf.liquid(toDraw, tileX, tileY, alpha, color, rotation);
            }
        }
    }

    public static float[] rotatePosition(float px, float py, float cx, float cy, float rotation) {
        double radians = Math.toRadians(rotation);
        float cos = (float) Math.cos(radians);
        float sin = (float) Math.sin(radians);

        // Translate point to origin
        float dx = px - cx;
        float dy = py - cy;

        // Rotate point
        float nx = cos * dx - sin * dy;
        float ny = sin * dx + cos * dy;

        // Translate point back
        return new float[]{cx + nx, cy + ny};
    }
}
