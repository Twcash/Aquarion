package aquarion.world.graphics;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.Pixmap.Format;
import arc.graphics.g2d.Draw;
import arc.graphics.gl.FrameBuffer;
import arc.math.Mat;
import arc.struct.ObjectMap;
import arc.util.Log;
import arc.util.Tmp;
import mindustry.gen.Building;
import mindustry.gen.Drawc;
import mindustry.gen.EffectStatec;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.type.UnitType;
import mindustry.world.Block;
import mindustry.world.meta.BlockGroup;

import static mindustry.Vars.tilesize;

public class WaterReflections {
    //todo twacsh your beloved block shadows looks ugly on the reflections i hope you read this and take care of your shadows, thx u, with love Sentinel/OwO

    public static FrameBuffer buffer;

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
        if(Core.graphics.getWidth() <= 0 || Core.graphics.getHeight() <= 0) return;

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

            Groups.build.each(b -> Math.abs(b.tile.drawx() - cx) <= halfW + margin && Math.abs(b.tile.drawy() - cy) <= halfH + margin,
                WaterReflections::drawReflected);

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
            Draw.trans(baseline);
            buffer.end();
            Draw.sort(true);
        }
    }

    private static void drawReflected(Building b){
        try{
            Draw.flush();
            Draw.reset();
            Block block = b.block;
            float ax = b.x();
            float base = b.tile.drawy() - block.size * tilesize / 2f;

            ReflectConfig c = configFor(block);
            float yScl = c.reflectionFlip ? -c.reflectYdisplace : c.reflectYdisplace;

            Tmp.m1.setToTranslation(ax, base).scale(c.reflectXdisplace, yScl).translate(-ax, -base);
            Draw.trans(Tmp.m1);
            b.drawCached(); // todo cached draws still no working
        }catch(Throwable t){
            if(reflectErrors++ < 5) Log.err("[reflect] block draw failed", t);//i hope this ends as useless code and not exception happens
        }
    }

    private static void drawReflectedUnit(Unit u){
        try{
            Draw.flush();
            Draw.reset();
            float gap = reflectionGroundGap + (reflectionFlyerGap - reflectionGroundGap) * u.elevation();

            UnitType type = u.type;
            float shadowElev = type.shadowElevation;
            boolean softShadow = type.drawSoftShadow;
            float unitElev = u.elevation();
            try{
                type.shadowElevation = -1f;
                type.drawSoftShadow = false;
                u.elevation(0f);
                Draw.trans(Tmp.m1.setToTranslation(0f, -2f * gap));
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

    public static void set(Block block, float xdisplace, float ydisplace, boolean flip){
        ReflectConfig c = config.get(block);
        if(c == null) config.put(block, c = new ReflectConfig());
        c.reflectXdisplace = xdisplace;
        c.reflectYdisplace = ydisplace;
        c.reflectionFlip = flip;
    }

    private static class ReflectConfig {
        float reflectXdisplace = 1f;
        float reflectYdisplace = 0.75f;
        boolean reflectionFlip = true;
    }
}
