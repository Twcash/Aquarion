package aquarion.world.graphics;

import arc.graphics.g2d.TextureRegion;

//Solely just for storing positions for pylons
public class link{
    public float x1, y1, x2, y2;
    public TextureRegion tex;
    public link(float x1, float x2, float y1, float y2, TextureRegion tex){
        x1 = this.x1;
        x2 = this.x2;
        y1 = this.y1;
        y2 = this.y2;
        tex = this.tex;
    }
}
