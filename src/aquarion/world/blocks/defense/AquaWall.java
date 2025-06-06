package aquarion.world.blocks.defense;

import aquarion.world.graphics.Renderer;
import arc.Core;
import arc.graphics.Blending;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.gen.Building;
import mindustry.graphics.BlockRenderer;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.environment.StaticWall;

import static aquarion.world.graphics.Renderer.Layer.shadow;
import static mindustry.Vars.state;
import static mindustry.Vars.tilesize;
import static mindustry.graphics.Layer.blockOver;

public class AquaWall extends Wall {
    public TextureRegion customShadow;
    public AquaWall(String name) {
        super(name);
    }
    @Override
    public void load(){
        super.load();
        customShadow = Core.atlas.find(name + "-shadow");
    }
    public class AquaWallBuild extends WallBuild{
        @Override
        public void draw(){
            super.draw();
            Draw.z(shadow);
            Draw.rect(customShadow, x, y);
            Draw.z(blockOver);

            //draw flashing white overlay if enabled
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
