package aquarion.world.blocks.core;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.scene.ui.layout.Scl;
import arc.util.Align;
import arc.util.pooling.Pools;
import mindustry.core.UI;
import mindustry.graphics.Layer;
import mindustry.ui.Fonts;
import mindustry.world.blocks.logic.MessageBlock;

import static mindustry.Vars.renderer;
import static mindustry.Vars.tilesize;

public class InfomaticBlock extends MessageBlock {
    public InfomaticBlock(String name) {
        super(name);
    }
    public TextureRegion topRegion;
    @Override
    public void load(){
        super.load();
        topRegion = Core.atlas.find(name + "-top");
    }
    public class infoBuild extends MessageBlock.MessageBuild{
        @Override
        public void draw(){
            super.draw();
            Draw.color(Color.scarlet.cpy());
            Draw.rect(topRegion, x, y, 0);
            Draw.color();
            if(!enabled) return;
            Draw.z(Layer.overlayUI+1);
            drawMessage();
        }
        public void drawMessage(){
            if(renderer.pixelate) return;
            if(Core.settings.getBool("hidedisplays")) return;
            Font font = Fonts.outline;
            GlyphLayout l = Pools.obtain(GlyphLayout.class, GlyphLayout::new);

            boolean ints = font.usesIntegerPositions();
            font.getData().setScale(1 / 4f / Scl.scl(1f));
            font.setUseIntegerPositions(false);

            String raw = (message == null ? "" : message.toString());
            String text;
            if(raw.startsWith("@")){
                text = Core.bundle.get(raw.substring(1));
            }else{
                text = raw;
            }
            text = UI.formatIcons(text);

            if(raw.isEmpty()){
                text = "[lightgray]" + Core.bundle.get("empty");
            }
            l.setText(font, text, Color.white, 90f, Align.center, true);
            float offset = 1f;
            float drawY = y + tilesize/2f + l.height/2f + 2f;
            Draw.color(0f, 0f, 0f, 0.2f);
            Fill.rect(x, drawY, l.width + offset*2f, l.height + offset*2f);
            Draw.color();
            font.draw(text, x - l.width/2f, drawY + l.height/2f, 90f, Align.left, true);

            font.setUseIntegerPositions(ints);
            font.getData().setScale(1f);

            Pools.free(l);
        }
        @Override
        public void drawSelect(){
            drawMessage();
        }
    }

}
