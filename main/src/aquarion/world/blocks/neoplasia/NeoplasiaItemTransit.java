package aquarion.world.blocks.neoplasia;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.Vars;
import mindustry.entities.Effect;
import mindustry.gen.Building;
import mindustry.type.Item;
import aquarion.world.graphics.Renderer;
import aquarion.world.blocks.neoplasia.GenericNeoplasiaBlock.NeoplasiaBuild;

/** Cosmetic packets: inventory transfers remain authoritative and instantaneous. */
final class NeoplasiaItemTransit {
    private static final float[] occupiedUntil = new float[128];
    private static float lastTime = -1f;
    private static int emitted;

    static final class Route {
        final float[] points;
        final Item item;
        final int entry;
        final boolean endsInVein;
        final float stepTime, arrival, duration;

        Route(Seq<? extends Building> path, Item item){
            this.item = item;
            points = new float[path.size * 2];
            int firstVein = -1;
            for(int i = 0; i < path.size; i++){
                Building build = path.get(i);
                points[i * 2] = build.tile.worldx() + build.block.offset;
                points[i * 2 + 1] = build.tile.worldy() + build.block.offset;
                if(firstVein < 0 && build instanceof NeoplasmVein.NeoplasmVeinBuild) firstVein = i;
            }
            entry = firstVein;
            endsInVein = path.peek() instanceof NeoplasmVein.NeoplasmVeinBuild;
            stepTime = Math.min(8f, 160f / Math.max(1, path.size - 1));
            arrival = entry < 0 ? (path.size - 1) * stepTime : Math.max(8f, entry * stepTime) + 12f;
            duration = arrival + 10f;
        }

        /** Position and size at a point in the animation; zero size means hidden in the vein. */
        void sample(float time, float[] result){
            int last = points.length - 2;
            if(time >= arrival){
                result[0] = points[last];
                result[1] = points[last + 1];
                result[2] = endsInVein ? 0f : (entry < 0 ? 1f : Mathf.clamp((time - arrival) / 5f)) * Mathf.clamp((duration - time) / 4f);
                return;
            }
            float distance = time / stepTime;
            int segment = Math.min((int)distance, points.length / 2 - 2);
            float progress = Mathf.clamp(distance - segment);
            result[0] = Mathf.lerp(points[segment * 2], points[segment * 2 + 2], progress);
            result[1] = Mathf.lerp(points[segment * 2 + 1], points[segment * 2 + 3], progress);
            result[2] = Mathf.clamp(time / 3f);
            if(entry == 0){
                result[0] = points[0]; result[1] = points[1];
                result[2] = Mathf.clamp(1f - time / 8f);
            }else if(entry > 0){
                result[2] *= Mathf.clamp(entry - distance);
            }
        }
    }

    private static final float[] sample = new float[3];
    private static final Effect packet = new Effect(180f, 4096f, e -> {
        if(!(e.data instanceof Route route)) return;
        e.lifetime = route.duration;
        route.sample(e.time, sample);
        float size = sample[2];
        if(size <= 0f) return;
        Draw.color();
        Draw.alpha(size);
        Draw.rect(route.item.fullIcon, sample[0], sample[1], 5.5f * size, 5.5f * size);
        Draw.reset();
    }).layer(Renderer.Layer.neoplasiaBase + 0.04f);

    static boolean canAnimate(){
        if(Vars.headless) return false;
        if(Time.time != lastTime){ lastTime = Time.time; emitted = 0; }
        return emitted < 8;
    }

    static void play(Seq<? extends Building> path, Item item){
        if(item == null || !canAnimate() || path.size < 2 || path.size > 256) return;
        for(Building build : path) if(build == null || !NeoplasiaGraph.isCurrent(build)) return;
        for(int i = 0; i < occupiedUntil.length; i++){
            if(occupiedUntil[i] > Time.time) continue;
            Route route = new Route(path, item);
            occupiedUntil[i] = Time.time + route.duration;
            emitted++;
            packet.at((path.first().x + path.peek().x) / 2f, (path.first().y + path.peek().y) / 2f, 0f, Color.white, route);
            return;
        }
        emitted = 8;
    }

    static void between(Building source, NeoplasiaBuild target, Item item){
        if(source == null || source == target || !canAnimate()) return;
        play(Seq.with(source, target), item);
    }

    static void reset(){
        java.util.Arrays.fill(occupiedUntil, 0f);
        lastTime = -1f;
        emitted = 0;
    }
}
