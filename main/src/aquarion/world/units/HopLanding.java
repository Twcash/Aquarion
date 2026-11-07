package aquarion.world.units;

import aquarion.gen.Amphibiousc;
import aquarion.units.type.AquaUnitType;
import arc.Events;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.math.geom.Vec2;
import arc.struct.IntMap;
import arc.struct.Seq;
import mindustry.entities.Units;
import mindustry.game.EventType;
import mindustry.gen.Unit;

/** Spatially indexed landing reservations; moving allies must not share a touchdown. */
public class HopLanding{
    private static final float cellSize = 32f;
    private static final IntMap<Seq<Unit>> reservations = new IntMap<>();
    private static float largestRadius;
    static{
        Events.on(EventType.ResetEvent.class, e -> clear());
        Events.on(EventType.WorldLoadEvent.class, e -> clear());
    }

    private static void clear(){ reservations.clear(); largestRadius = 0f; }
    private static float radius(Unit unit){ return unit.physicSize() / 2f + 1f; }
    private static int key(float x, float y){ return Point2.pack(Mathf.floor(x / cellSize), Mathf.floor(y / cellSize)); }

    public static void release(Unit unit){
        if(!(unit instanceof Amphibiousc frog)) return;
        int key = key(frog.hopEndX(), frog.hopEndY());
        Seq<Unit> cell = reservations.get(key);
        if(cell != null){
            cell.remove(unit, true);
            if(cell.isEmpty()) reservations.remove(key);
        }
    }

    public static void reserve(Unit unit){
        Amphibiousc frog = (Amphibiousc)unit;
        largestRadius = Math.max(largestRadius, radius(unit));
        reservations.get(key(frog.hopEndX(), frog.hopEndY()), Seq::new).addUnique(unit);
    }

    public static boolean clear(Unit unit, float x, float y){
        float radius = radius(unit);
        boolean[] occupied = {false};
        Units.nearby(unit.team, x - radius, y - radius, radius * 2f, radius * 2f, other -> {
            if(other != unit && !other.dead && other.isGrounded() &&
                !(other instanceof Amphibiousc frog && frog.hopping()) &&
                other.within(x, y, radius + radius(other))) occupied[0] = true;
        });
        if(occupied[0]) return false;
        float extent = radius + largestRadius;
        for(int cx = Mathf.floor((x - extent) / cellSize); cx <= Mathf.floor((x + extent) / cellSize); cx++){
            for(int cy = Mathf.floor((y - extent) / cellSize); cy <= Mathf.floor((y + extent) / cellSize); cy++){
                Seq<Unit> cell = reservations.get(Point2.pack(cx, cy));
                if(cell == null) continue;
                for(Unit other : cell){
                    if(other == unit || other.dead || other.team != unit.team ||
                        !(other instanceof Amphibiousc frog) || !frog.hopping()) continue;
                    if(Mathf.within(x, y, frog.hopEndX(), frog.hopEndY(), radius + radius(other))) return false;
                }
            }
        }
        return true;
    }

    public static boolean find(Unit unit, float sourceX, float sourceY, float targetX, float targetY, Vec2 result){
        AquaUnitType type = (AquaUnitType)unit.type;
        if(AmphibiousPathfinder.canHop(type, sourceX, sourceY, targetX, targetY) && clear(unit, targetX, targetY)){
            result.set(targetX, targetY);
            return true;
        }
        float spacing = Math.max(4f, radius(unit));
        for(int ring = 1; ring <= 3; ring++){
            for(int direction = 0; direction < 12; direction++){
                float angle = direction * 30f;
                float x = targetX + Mathf.cosDeg(angle) * spacing * ring;
                float y = targetY + Mathf.sinDeg(angle) * spacing * ring;
                if(AmphibiousPathfinder.canHop(type, sourceX, sourceY, x, y) && clear(unit, x, y)){
                    result.set(x, y);
                    return true;
                }
            }
        }
        return false;
    }
}
