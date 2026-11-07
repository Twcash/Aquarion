package aquarion.world.drawers;

import aquarion.world.graphics.Renderer;

/** Draws a block's shadow sprite on Aquarion's shadow layer. */
public class DrawBlockShadow extends DrawBetterRegion {
    public DrawBlockShadow(){
        super("-shadow");
        layer = Renderer.Layer.shadow;
        drawPlan = false;
        drawIcon = false;
    }
}
