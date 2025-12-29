package aquarion.world.graphics;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.Gl;
import arc.graphics.Pixmap;
import arc.graphics.g2d.Bloom;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.graphics.gl.FrameBuffer;
import arc.graphics.gl.Shader;
import arc.math.Angles;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Time;

import static mindustry.Vars.tilesize;

public class Renderer {
    public static FrameBuffer buffer;
    public static FrameBuffer prevBuffer;
    public static FrameBuffer glitchBuffer;
    public static @Nullable Bloom bloom;

    public static Seq<link> links1 = new Seq<>();
    public static FrameBuffer linkBuffer;

    public static class Layer extends mindustry.graphics.Layer {
        public static final float shadow = 29.8936f;
        public static final float heat = 39.7656f;
        public static final float deflector = 126.05f;
        public static final float glitch = Layer.deflector+3;
    }

    public static void draw(){
        if(bloom == null) bloom = new Bloom(true);

        int w = Core.graphics.getWidth();
        int h = Core.graphics.getHeight();

        if(buffer == null) buffer = new FrameBuffer(Pixmap.Format.rgba8888, w, h, false);
        if(prevBuffer == null) prevBuffer = new FrameBuffer(Pixmap.Format.rgba8888, w, h, false);
        if(glitchBuffer == null) glitchBuffer = new FrameBuffer(Pixmap.Format.rgba8888, w, h, false);
        if(linkBuffer == null) linkBuffer = new FrameBuffer(Pixmap.Format.rgba8888, w, h, false);


        buffer.resize(w, h);
        glitchBuffer.resize(w, h);
        if(!links1.isEmpty()){

            Draw.draw(Layer.power + 0.00012f, () -> {
                linkBuffer.begin(Color.clear);

                Gl.clear(Gl.stencilBufferBit);

                Gl.enable(Gl.stencilTest);
                Gl.stencilMask(0xFF);
                Gl.colorMask(false, false, false, false);
                Gl.stencilFunc(Gl.always, 1, 0xFF);
                Gl.stencilOp(Gl.replace, Gl.replace, Gl.replace);
                Draw.blend(new Blending(Gl.zero, Gl.oneMinusSrcAlpha));
                for(var link : links1){
                    if(link.tex == null) continue;
                    Lines.stroke(1);

                    Lines.line(link.tex, link.x1, link.y1, link.x2, link.y2, false);

                    //Draw.rect(cableEnd, link.x1, link.y1, angle);
                    //Draw.rect(cableEnd, link.x2, link.y2, angle + 180f);
                }
                Draw.blend();
                Draw.flush();
                linkBuffer.end();
                Draw.color(Color.white, mindustry.core.Renderer.unitLaserOpacity);
                Draw.rect(Draw.wrap(linkBuffer.getTexture()), Core.camera.position.x, Core.camera.position.y, Core.camera.width, -Core.camera.height);
                Draw.reset();
            });

        }
        Draw.drawRange(Layer.shadow, 0f,
                () -> buffer.begin(Color.clear),
                () -> {
                    buffer.end();
                    buffer.blit(AquaShaders.shadow);
                }
        );

        Draw.drawRange(Layer.heat, 0.1f,
                () -> buffer.begin(Color.clear),
                () -> {
                    buffer.end();
                    buffer.blit(AquaShaders.heat);
                }
        );

        Draw.drawRange(Layer.deflector, 0.01f,
                () -> buffer.begin(Color.clear),
                () -> {
                    buffer.end();
                    buffer.blit(AquaShaders.deflectorShield);
                }
        );

        Draw.drawRange(Layer.glitch, 0.0001f,
                () -> glitchBuffer.begin(Color.clear),
                () -> {
                    glitchBuffer.end();
                    Shader shader = AquaShaders.glitch;
                    if(shader != null){
                        shader.bind();
                        glitchBuffer.blit(shader);
                    }
                }
        );
    }
}
