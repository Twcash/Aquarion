package aquarion.world.graphics;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.Pixmap;
import arc.graphics.g2d.Bloom;
import arc.graphics.g2d.Draw;
import arc.graphics.gl.FrameBuffer;
import arc.util.Nullable;


public class Renderer {
    public static FrameBuffer buffer;
    public static FrameBuffer prevBuffer;
    public static @Nullable Bloom bloom;
    public static class Layer extends mindustry.graphics.Layer {
        public static final float shadow = 29.8936f;
        public static final float heat = 39.7656f;
        public static final float deflector = 126.05f;

        public static final float glitch = 159f;
    }

    public static void draw() {
        if (bloom == null) bloom = new Bloom(true);

        int w = Core.graphics.getWidth();
        int h = Core.graphics.getHeight();

        if (buffer == null) buffer = new FrameBuffer(Pixmap.Format.rgba8888, w, h, false);
        if (prevBuffer == null) prevBuffer = new FrameBuffer(Pixmap.Format.rgba8888, w, h, false);

        buffer.resize(w, h);

        Draw.drawRange(Layer.shadow, 0.01f,
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

        // --- Glitch Layer ---
//        Draw.drawRange(Layer.glitch, 0f,
//                () -> buffer.begin(Color.clear),
//                () -> {
//                    buffer.end();
//                    prevBuffer.getTexture().bind(1);
//                    glitch.setUniformi("u_prevFrame", 1);
//                    // Draw current buffer with glitch shader
//                    buffer.blit(glitch);
//                }
//        );
    }
}