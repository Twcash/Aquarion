package aquarion.world.graphics;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Bloom;
import arc.graphics.g2d.Draw;
import arc.graphics.gl.FrameBuffer;
import arc.util.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Renderer {
    public static FrameBuffer buffer;
    public static @Nullable Bloom bloom;
    public static class Layer extends mindustry.graphics.Layer {
        public static final float shadow = 29.8936f;
        public static final float heat = 39.7656f;
    }
    public static void draw() {
        if (bloom == null) bloom = new Bloom(true);
        if (buffer == null) buffer = new FrameBuffer();
        buffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
        Draw.drawRange(Layer.shadow, 0.01f,
                () -> {
                    buffer.begin(Color.clear);
                },
                () -> {
                    buffer.end();
                    buffer.blit(AquaShaders.shadow);
                });
        Draw.drawRange(Layer.heat, 0.1f,
                () -> {
                    buffer.begin(Color.clear);
                },
                () -> {
                    buffer.end();
                    buffer.blit(AquaShaders.heat);
                });
    }
}