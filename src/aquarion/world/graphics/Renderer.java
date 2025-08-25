package aquarion.world.graphics;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.Pixmap;
import arc.graphics.g2d.Bloom;
import arc.graphics.g2d.Draw;
import arc.graphics.gl.FrameBuffer;
import arc.util.Nullable;
import mindustry.game.EventType;

import java.util.HashMap;
import java.util.Map;
import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.gl.*;
import arc.util.*;
import mindustry.game.EventType.*;
import mindustry.graphics.*;

import static arc.Core.*;
import static mindustry.Vars.*;
import static aquarion.world.graphics.AquaShaders.glitch;


public class Renderer {
    public static FrameBuffer buffer;
    public static FrameBuffer prevBuffer;
    public static @Nullable Bloom bloom;
    public static class Layer extends mindustry.graphics.Layer {
        public static final float shadow = 29.8936f;
        public static final float heat = 39.7656f;
        public static final float glitch = 159f;
    }

    public static void draw() {
        if (bloom == null) bloom = new Bloom(true);

        int w = Core.graphics.getWidth();
        int h = Core.graphics.getHeight();

        // Initialize frame buffers if needed
        if (buffer == null) buffer = new FrameBuffer(Pixmap.Format.rgba8888, w, h, false);
        if (prevBuffer == null) prevBuffer = new FrameBuffer(Pixmap.Format.rgba8888, w, h, false);

        buffer.resize(w, h);

        // --- Shadow Layer ---
        Draw.drawRange(Layer.shadow, 0.01f,
                () -> buffer.begin(Color.clear),
                () -> {
                    buffer.end();
                    buffer.blit(AquaShaders.shadow);
                }
        );

        // --- Heat Layer ---
        Draw.drawRange(Layer.heat, 0.1f,
                () -> buffer.begin(Color.clear),
                () -> {
                    buffer.end();
                    buffer.blit(AquaShaders.heat);
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