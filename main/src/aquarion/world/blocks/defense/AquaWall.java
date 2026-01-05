package aquarion.world.blocks.defense;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.Wall;

import static aquarion.world.graphics.Renderer.Layer.shadow;
import static mindustry.Vars.state;
import static mindustry.Vars.tilesize;
import static mindustry.graphics.Layer.blockOver;

public class AquaWall extends Wall {
    public TextureRegion custShad;
    public AquaWall(String name) {
        super(name);
        hasShadow = true;
        customShadow = true;
    }
    @Override
    public void load(){
        super.load();
        custShad = Core.atlas.find(name + "-shadow");
    }
    @Override
    public void drawShadow(Tile tile){
        Draw.z(shadow);
        Draw.rect(custShad, tile.drawx(), tile.drawy());
        Draw.z();
    }

    public class AquaWallBuild extends WallBuild{
        @Override
        public void draw(){
            super.draw();
            Draw.z(blockOver);

            if(flashHit){
                if(hit < 0.0001f) return;

                Draw.color(flashColor);
                Draw.alpha(hit * 0.5f);
                Draw.blend(Blending.additive);
                Fill.rect(x, y, tilesize * size, tilesize * size);
                Draw.blend();
                Draw.reset();

                if(!state.isPaused()){
                    hit = Mathf.clamp(hit - Time.delta / 10f);
                }
            }
        }
    }
}
