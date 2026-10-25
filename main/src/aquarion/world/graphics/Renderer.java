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
import aquarion.world.blocks.defense.AquaWall;
import aquarion.world.blocks.neoplasia.GenericNeoplasiaBlock;
import aquarion.world.blocks.neoplasia.NeoplasmTreeBase;
import mindustry.Vars;
import mindustry.graphics.Shaders;
import mindustry.world.Tile;

import static mindustry.Vars.tilesize;

public class Renderer {
    public static FrameBuffer buffer;
    private static final Seq<Tile> visibleWallShadows = new Seq<>();
    private static final arc.math.geom.Rect treeCamera = new arc.math.geom.Rect();
    private static boolean deflectorPassRequested;
    private static boolean glitchPassRequested;
    private static final Seq<Runnable> railPlacementAreas = new Seq<>();
    //public static @Nullable Bloom bloom;

    public static class Layer extends mindustry.graphics.Layer {
        public static final float shadow = 29.8936f;
        public static final float heat = 39.7656f;
        public static final float deflector = 126.05f;
        public static final float neoplasiaUnder = Layer.blockUnder + 0.011221232134f;
        public static final float neoplasiaBase = Layer.blockOver + 0.133262332134f;
        public static final float glitch = Layer.deflector+3;
        public static final float railPlacement = Layer.overlayUI - 1f;
    }

    public static void draw(){
        //if(bloom == null) bloom = new Bloom(true);
        int w = Core.graphics.getWidth();
        int h = Core.graphics.getHeight();
        if(buffer == null || buffer.getWidth() != w || buffer.getHeight() != h){
            if(buffer != null) buffer.dispose();
            buffer = new FrameBuffer(Pixmap.Format.rgba8888, w, h, false);
        }

        visibleWallShadows.clear();
        // Composite all placement sectors together before limiting opacity. Overlapping
        // rails must not build up into an opaque patch over the terrain.
        Draw.draw(Layer.railPlacement, () -> {
            if(railPlacementAreas.isEmpty()) return;
            if(AquaShaders.railPlacement == null){
                railPlacementAreas.clear();
                return;
            }
            Blending previousBlend = Draw.getBlend();
            Draw.blend(Blending.normal);
            buffer.begin(Color.clear);
            try{
                // Draw masks only while the target buffer is bound, never directly
                // to the screen or between separately scheduled layer callbacks.
                railPlacementAreas.each(Runnable::run);
            }finally{
                buffer.end();
                railPlacementAreas.clear();
                Draw.reset();
            }
            // blit() bypasses SpriteBatch, so set the GL blend state explicitly.
            // Otherwise the capped alpha can be ignored by the current blend mode.
            Blending.normal.apply();
            buffer.blit(AquaShaders.railPlacement);
            Draw.blend(previousBlend);
            previousBlend.apply();
        });
        if(Vars.world != null && Vars.world.width() > 0 && Vars.world.height() > 0){
            float halfWidth = Core.camera.width / 2f;
            float halfHeight = Core.camera.height / 2f;
            int minX = Math.max(0, Mathf.floor((Core.camera.position.x - halfWidth) / tilesize) - 1);
            int maxX = Math.min(Vars.world.width() - 1, Mathf.floor((Core.camera.position.x + halfWidth) / tilesize) + 1);
            int minY = Math.max(0, Mathf.floor((Core.camera.position.y - halfHeight) / tilesize) - 1);
            int maxY = Math.min(Vars.world.height() - 1, Mathf.floor((Core.camera.position.y + halfHeight) / tilesize) + 1);

            for(int tx = minX; tx <= maxX; tx++){
                for(int ty = minY; ty <= maxY; ty++){
                    Tile tile = Vars.world.tile(tx, ty);
                    if(tile == null) continue;
                    if(tile.build != null && tile.build.tile != tile) continue;
                    if(!(tile.block() instanceof AquaWall)) continue;
                    visibleWallShadows.add(tile);
                }
            }
        }

        Draw.drawRange(Layer.shadow, 0.0001f,
                () -> {
                    buffer.begin(Color.clear);
                    visibleWallShadows.each(tile -> ((AquaWall)tile.block()).drawShadow(tile));
                },
                () -> {
                    buffer.end();
                    if(AquaShaders.shadow != null) buffer.blit(AquaShaders.shadow);
                }
        );
//Heat is nigh Useless and is barely used at all
//        Draw.drawRange(Layer.heat, 0.1f,
//                () -> buffer.begin(Color.clear),
//                () -> {
//                    buffer.end();
//                    buffer.blit(AquaShaders.heat);
//                }
//        );

        if(deflectorPassRequested && Vars.renderer.animateShields){
            Draw.drawRange(Layer.deflector, 0.01f,
                    () -> buffer.begin(Color.clear),
                    () -> {
                        buffer.end();
                        buffer.blit(AquaShaders.deflectorShield);
                    }
            );
        }
        deflectorPassRequested = false;

        if(glitchPassRequested){
            Draw.drawRange(Layer.glitch, 0.0001f,
                    () -> buffer.begin(Color.clear),
                    () -> {
                        buffer.end();
                        Shader shader = AquaShaders.glitch;
                        if(shader != null){
                            shader.bind();
                            buffer.blit(shader);
                        }
                    }
            );
        }
        glitchPassRequested = false;
        if (!GenericNeoplasiaBlock.activeNeoplasia.isEmpty()) {
            // Block clipSize is shared content data. Cull spreading canopies per building instead.
            Core.camera.bounds(treeCamera);
            for(var tree : NeoplasmTreeBase.trees){
                if(tree.dead() || tree.tile == null || tree.tile.build != tree) continue;
                float size = tree.drawingClipSize();
                if(treeCamera.overlaps(tree.x - size / 2f, tree.y - size / 2f, size, size)) tree.draw();
            }
            Draw.drawRange(Layer.neoplasiaBase, 0.0001f,
                    () -> buffer.begin(Color.clear),
                    () -> {
                        buffer.end();
                        buffer.blit(AquaShaders.neoplasiaBaseShader);
                    }
            );
            Draw.drawRange(Layer.neoplasiaUnder, 0.0001f,
                    () -> buffer.begin(Color.clear),
                    () -> {
                        buffer.end();
                        buffer.blit(AquaShaders.neoplasiaBaseShader);
                    }
            );
        }
        AquaShaders.refreshLightShader();
        AquaShaders.light.ambient = Vars.state.rules.ambientLight;
    }

    public static void requestDeflectorPass(){
        deflectorPassRequested = true;
    }

    public static void requestGlitchPass(){
        glitchPassRequested = true;
    }

    public static void requestRailPlacementPass(Runnable area){
        railPlacementAreas.add(area);
    }
}
