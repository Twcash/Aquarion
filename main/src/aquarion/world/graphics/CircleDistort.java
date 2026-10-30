package aquarion.world.graphics;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Gl;
import arc.graphics.Texture;
import arc.graphics.g2d.Draw;
import arc.util.Buffers;
import mindustry.graphics.Layer;

import java.nio.IntBuffer;

import static mindustry.Vars.*;

/** GPU scene capture for circular distortion and caustic smoke; shader lives in AquaShaders. */
public final class CircleDistort{
    private static Texture scene;
    private static final IntBuffer viewport = Buffers.newIntBuffer(4);

    private CircleDistort(){}

    public static void draw(float x, float y, float radius, float strength){
        render(x, y, radius, strength, 0f, false);
    }

    public static void drawSmoke(float x, float y, float radius, float time, float opacity){
        render(x, y, radius, opacity, time, true);
    }

    private static void render(float x, float y, float radius, float strength, float time, boolean smoke){
        if(headless || radius < 1f) return;
        if(Math.abs(x - Core.camera.position.x) > Core.camera.width / 2f + radius + 100f
            || Math.abs(y - Core.camera.position.y) > Core.camera.height / 2f + radius + 100f) return;
        Draw.draw(Layer.light + 1f, () -> {
            Draw.flush();
            if(AquaShaders.circleDistort == null) AquaShaders.circleDistort = new AquaShaders.CircleDistortShader();
            var shader = AquaShaders.circleDistort;
            viewport.clear();
            Gl.getIntegerv(Gl.viewport, viewport);
            int width = viewport.get(2), height = viewport.get(3);
            if(width <= 0 || height <= 0) return;
            if(scene == null || scene.width != width || scene.height != height){
                if(scene != null) scene.dispose();
                scene = new Texture(width, height);
                scene.setFilter(Texture.TextureFilter.linear);
                scene.setWrap(Texture.TextureWrap.clampToEdge);
            }
            scene.bind(0);
            Gl.copyTexSubImage2D(Gl.texture2d, 0, 0, 0, viewport.get(0), viewport.get(1), width, height);
            shader.x = x;
            shader.y = y;
            shader.radius = radius;
            shader.strength = strength;
            shader.time = time;
            shader.smoke = smoke;
            Blending previous = Draw.getBlend();
            Blending.disabled.apply();
            try{
                Draw.blit(scene, shader);
            }finally{
                previous.apply();
                Draw.reset();
            }
        });
    }

    public static void dispose(){
        if(scene != null){ scene.dispose(); scene = null; }
    }
}
