package aquarion.world.graphics;

import arc.Core;
import arc.func.Cons;
import arc.graphics.Color;
import arc.graphics.Pixmap.Format;
import arc.graphics.g2d.Draw;
import arc.graphics.gl.FrameBuffer;
import arc.math.Mat;
import arc.struct.ObjectMap;
import arc.util.Log;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.gen.Drawc;
import mindustry.gen.EffectStatec;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.type.UnitType;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.meta.BlockGroup;

import static mindustry.Vars.tilesize;

public class WaterReflections {

    public static FrameBuffer buffer;

    public static boolean disabled;

    public static boolean captureReflections = false;

    private static final Mat baseline = new Mat();

    public static final float reflectionGroundGap = 3f;
    public static final float reflectionFlyerGap = 10f;

    public static final Color refTint = new Color(0x29619bff);
    public static final float refTintAmount = 0.42f;
    public static final float refOpacity = 0.9f;

    private static final ObjectMap<Block, ReflectConfig> config = new ObjectMap<>();

    private static int reflectErrors;

    public static void captureScreen(){
        if(disabled || Core.graphics.getWidth() <= 0 || Core.graphics.getHeight() <= 0) return;

        if(buffer == null){
            buffer = new FrameBuffer(Format.rgba8888, Core.graphics.getWidth(), Core.graphics.getHeight(), false);
        }else{
            buffer.resizeCheck(Core.graphics.getWidth(), Core.graphics.getHeight());
        }

        float halfW = Core.camera.width / 2f;
        float halfH = Core.camera.height / 2f;
        float cx = Core.camera.position.x;
        float cy = Core.camera.position.y;
        float margin = tilesize * 8f;

        Draw.sort(false);
        buffer.begin(Color.clear);
        captureReflections = true;
        try{
            Draw.reset();
            Draw.z(0);

            forEachBuildIn(cx, cy, halfW, halfH, margin, WaterReflections::drawReflected);

            Groups.unit.each(u -> !u.dead() && Math.abs(u.x() - cx) <= halfW + margin && Math.abs(u.y() - cy) <= halfH + margin,
                WaterReflections::drawReflectedUnit);

            Groups.draw.each(e -> e instanceof EffectStatec
                    && Math.abs(e.x() - cx) <= halfW + margin && Math.abs(e.y() - cy) <= halfH + margin,
                WaterReflections::drawReflectedMoving);

            Groups.bullet.each(b -> Math.abs(b.x() - cx) <= halfW + margin && Math.abs(b.y() - cy) <= halfH + margin,
                WaterReflections::drawReflectedMoving);
        }finally{
            captureReflections = false;
            Draw.flush();
            Draw.reset();
            Draw.z(0);
            Draw.trans(baseline);
            buffer.end();
            Draw.sort(true);
        }
    }
    private static void forEachBuildIn(float cx, float cy, float halfW, float halfH, float margin, Cons<Building> cons){
        if(Vars.world == null || Vars.world.tiles == null) return;
        int x0 = Math.max(0, (int)Math.floor((cx - halfW - margin) / tilesize));
        int x1 = Math.min(Vars.world.width() - 1, (int)Math.floor((cx + halfW + margin) / tilesize));
        int y0 = Math.max(0, (int)Math.floor((cy - halfH - margin) / tilesize));
        int y1 = Math.min(Vars.world.height() - 1, (int)Math.floor((cy + halfH + margin) / tilesize));
        for(int x = x0; x <= x1; x++){
            for(int y = y0; y <= y1; y++){
                Tile tile = Vars.world.tile(x, y);
                Building b = tile.build;
                if(b != null && b.tile == tile) cons.get(b);
            }
        }
    }

    private static void drawReflected(Building b){
        try{
            Draw.flush();
            Draw.reset();
            Draw.z(0);
            Block block = b.block;
            float ax = b.x();
            float base = b.tile.drawy() - block.size * tilesize / 2f;

            ReflectConfig c = configFor(block);
            float yScl = c.reflectionFlip ? -c.reflectYdisplace : c.reflectYdisplace;

            Tmp.m1.setToTranslation(ax, base).scale(c.reflectXdisplace, yScl).translate(-ax, -base);
            Draw.trans(Tmp.m1);
            b.drawCached();
        }catch(Throwable t){
            if(reflectErrors++ < 5) Log.err("[reflect] block draw failed", t);//i hope this ends as useless code and not exception happens
        }
    }

    private static void drawReflectedUnit(Unit u){
        try{
            Draw.flush();
            Draw.reset();
            Draw.z(0);
            float gap = reflectionGroundGap + (reflectionFlyerGap - reflectionGroundGap) * u.elevation();

            UnitType type = u.type;
            float shadowElev = type.shadowElevation;
            boolean softShadow = type.drawSoftShadow;
            float unitElev = u.elevation();
            try{
                type.shadowElevation = -1f;
                type.drawSoftShadow = false;
                u.elevation(0f);

                ReflectConfig c = configForUnit(type);
                float yScl = c.reflectionFlip ? -c.reflectYdisplace : c.reflectYdisplace;
                Tmp.m1.setToTranslation(u.x(), u.y() - 2f * gap)
                     .scale(c.reflectXdisplace, yScl)
                     .translate(-u.x(), -u.y());
                Draw.trans(Tmp.m1);
                u.draw();
            }finally{
                u.elevation(unitElev);
                type.shadowElevation = shadowElev;
                type.drawSoftShadow = softShadow;
            }
        }catch(Throwable t){
            if(reflectErrors++ < 5) Log.err("[reflect] unit draw failed", t);
        }
    }

    private static void drawReflectedMoving(Drawc e){
        try{
            Draw.flush();
            Draw.reset();
            Draw.z(0);
            Draw.trans(Tmp.m1.setToTranslation(0f, -2f * reflectionGroundGap));
            e.draw();
        }catch(Throwable t){
            if(reflectErrors++ < 5) Log.err("[reflect] draw failed", t);
        }
    }

    private static ReflectConfig configFor(Block block){
        ReflectConfig c = config.get(block);
        if(c != null) return c;

        ReflectConfig d = new ReflectConfig();
        if(block.group == BlockGroup.transportation || block.group == BlockGroup.liquids){
            d.reflectYdisplace = 0.3f;
        }
        return d;
    }

    private static ReflectConfig configForUnit(UnitType type){
        ReflectConfig d = new ReflectConfig();
        d.reflectionFlip = false;
        return d;
    }

    public static void set(Block block, float xdisplace, float ydisplace, boolean flip){
        ReflectConfig c = config.get(block);
        if(c == null) config.put(block, c = new ReflectConfig());
        c.reflectXdisplace = xdisplace;
        c.reflectYdisplace = ydisplace;
        c.reflectionFlip = flip;
    }

    public static class ReflectConfig {
        public float reflectXdisplace = 1f;
        public float reflectYdisplace = 0.75f;
        public boolean reflectionFlip = true;

        public ReflectConfig(){}

        public ReflectConfig(float x, float y, boolean flip){
            this.reflectXdisplace = x;
            this.reflectYdisplace = y;
            this.reflectionFlip = flip;
        }

        public ReflectConfig copy(){
            return new ReflectConfig(reflectXdisplace, reflectYdisplace, reflectionFlip);
        }
    }
}