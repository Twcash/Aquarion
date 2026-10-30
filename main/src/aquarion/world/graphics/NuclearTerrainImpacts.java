package aquarion.world.graphics;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.math.Angles;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.content.Blocks;
import mindustry.game.EventType.*;
import mindustry.gen.Bullet;
import mindustry.world.Tile;

import static mindustry.Vars.*;

/** Actual terrain-change impacts, independent of the normal effect entity pool. */
public final class NuclearTerrainImpacts{
    private static final Seq<Watch> watches = new Seq<>();
    private static final Seq<Hit> hits = new Seq<>();
    private static boolean registered;

    private NuclearTerrainImpacts(){}

    public static void watch(Bullet bullet, float range, float end){
        if(headless) return;
        if(!registered){
            registered = true;
            Events.on(ResetEvent.class, e -> { watches.clear(); hits.clear(); });
            Events.on(TilePreChangeEvent.class, e -> {
                if(e.tile.block() != Blocks.air) hit(e.tile, e.tile.block().mapColor);
            });
            Events.on(TileFloorChangeEvent.class, e -> hit(e.tile, e.previous.mapColor));
            Events.run(Trigger.update, () -> {
                watches.removeAll(w -> !w.bullet.isAdded() || w.bullet.time > w.end);
                if(!state.isGame() || state.isPaused()) return;
                for(Hit hit : hits) hit.age += Time.delta;
                hits.removeAll(hit -> hit.age > 90f);
            });
            Events.run(Trigger.draw, NuclearTerrainImpacts::draw);
        }
        for(Watch watch : watches) if(watch.bullet == bullet) return;
        watches.add(new Watch(bullet, range, end));
    }

    private static void hit(Tile tile, Color color){
        if(hits.size >= 2048) return;
        if(Math.abs(tile.worldx() - Core.camera.position.x) > Core.camera.width / 2f + 100f
            || Math.abs(tile.worldy() - Core.camera.position.y) > Core.camera.height / 2f + 100f) return;
        for(Watch watch : watches){
            Bullet bullet = watch.bullet;
            if(!bullet.isAdded() || bullet.time > watch.end || !tile.within(bullet.x, bullet.y, watch.range)) continue;
            Hit hit = new Hit();
            hit.x = tile.worldx(); hit.y = tile.worldy();
            hit.angle = Angles.angle(bullet.x, bullet.y, hit.x, hit.y);
            hit.seed = tile.pos() * 31L + bullet.id;
            hit.color.set(color).lerp(Color.lightGray, 0.4f);
            hits.add(hit);
            break;
        }
    }

    private static void draw(){
        for(Hit hit : hits){
            AquaFx.drawNuclearTerrainImpact(hit.seed, hit.x, hit.y, hit.angle, hit.age, hit.color);
        }
    }
    private static class Watch{
        final Bullet bullet;
        final float range, end;
        Watch(Bullet bullet, float range, float end){ this.bullet = bullet; this.range = range; this.end = end; }
    }
    private static class Hit{
        float x, y, angle, age;
        long seed;
        final Color color = new Color();
    }
}
