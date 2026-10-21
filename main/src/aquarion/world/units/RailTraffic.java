package aquarion.world.units;

import aquarion.units.type.RailUnitType;
import aquarion.world.AI.RailCommandAI;
import aquarion.world.units.RailPathfinder.Edge;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import mindustry.gen.Groups;
import mindustry.gen.Unit;

/** Live route and body reservations, rebuilt from active trains after removal or loading. */
/** Slightly based off of https://wiki.factorio.com/Railway/Train_path_finding (largely chain signals) However this is more procedural than factorio*/
public class RailTraffic{
    private static final Vec2 projected = new Vec2();

    public static float clearance(Unit a, Unit b){
        return (a.hitSize + b.hitSize) / 2f + 1f;
    }

    public static boolean present(Unit unit){
        if(unit == null || unit.dead || Groups.unit == null) return false;
        for(Unit other : Groups.unit) if(other == unit) return true;
        return false;
    }

    private static boolean train(Unit unit, Unit mover){
        return unit != mover && !unit.dead && unit.type instanceof RailUnitType;
    }


    /** A train giving way owns its return through the junction until every car is in the refuge. */
    public static Unit reservationBlocker(Unit mover, Edge edge){
        Unit head = RailConsist.leader(mover);
        boolean yielding = head.controller() instanceof RailCommandAI own && own.trafficYielding();
        for(Unit other : Groups.unit){
            if(!train(other, mover) || RailConsist.same(mover, other) || RailConsist.leader(other) != other) continue;
            if(other.controller() instanceof RailCommandAI ai && ai.trafficYielding() && ai.reserves(edge)
                && (!yielding || other.id < head.id)) return other;
        }
        return null;
    }

    public static Unit blocker(Unit mover, Edge edge){
        if(Groups.unit == null) return null;
        for(Unit other : Groups.unit){
            if(!train(other, mover) || RailConsist.same(mover, other)) continue;
            float gap = clearance(mover, other);
            if(other.controller() instanceof RailCommandAI ai){
                Unit head = RailConsist.leader(other);
                if(!RailConsist.same(mover, other) && head.controller() instanceof RailCommandAI owner && owner.reserves(edge)
                    && (!(mover.controller() instanceof RailCommandAI moving) || !moving.trafficYielding() || owner.trafficYielding())
                    && (owner.trafficEdge() != null || head.id < RailConsist.leader(mover).id)) return head;
                Edge occupied = ai.trafficEdge();
                if(occupied != null){
                    // Convoys share a direction; swept body clearance maintains their spacing.
                    if(occupied.from == edge.from && occupied.to == edge.to) continue;
                    if(occupied.from == edge.to && occupied.to == edge.from){
                        if(mover.controller() instanceof RailCommandAI moving){
                            Edge own = moving.trafficEdge();
                            if(own != null && ((own.from == edge.from && own.to == edge.to)
                                || (own.from == edge.to && own.to == edge.from))
                                && edge.project(other.x, other.y) < edge.project(mover.x, mover.y) - gap) continue;
                        }
                        return other;
                    }
                    // Reserve the destination junction until the incoming train leaves it.
                    if(occupied.to == edge.to && ai.sectionCommitted()
                        && !(mover.controller() instanceof RailCommandAI passing && passing.passingFor(other) && ai.trafficSpeed() < 0.01f)) return other;
                }
            }
            if(other.within(edge.to, gap)) return other;
            // Stopped trains spawned/saved mid-section still occupy that track.
            edge.sample(edge.project(other.x, other.y), projected);
            if(other.within(projected, gap)){
                // A train behind this departure must not hold up the train leaving it.
                if(edge.project(other.x, other.y) < gap && !other.within(edge.to, gap)){
                    edge.sample(Math.min(edge.length, 0.1f), projected);
                    if(other.dst2(projected) > other.dst2(edge.from)) continue;
                }
                // Let overlapping spawn positions separate by leaving their shared node.
                if(other.within(edge.from, 0.5f) && mover.within(edge.from, 0.5f)) continue;
                return other;
            }
        }
        return null;
    }

    public static boolean parkingPassable(Unit mover, Edge edge){
        Unit other = blocker(mover, edge);
        if(other == null) return true;
        if(!(other.controller() instanceof RailCommandAI ai) || (!ai.trafficParked() && ai.hasCommand())) return false;
        Edge occupied = ai.trafficEdge();
        if(occupied != null && occupied.from == edge.to && occupied.to == edge.from) return false;
        return edge.project(other.x, other.y) > clearance(mover, other) + mover.hitSize + 2f;
    }

    /** Brake behind any train ahead, including trains with a different final destination. */
    public static float followingSpeed(Unit mover, Edge edge, float distance, float acceleration){
        float limit = mover.speed();
        for(Unit other : Groups.unit){
            if(!train(other, mover) || RailConsist.same(mover, other) || !(other.controller() instanceof RailCommandAI ai)) continue;
            Edge occupied = ai.trafficEdge();
            float ahead;
            if(occupied != null && occupied.from == edge.from && occupied.to == edge.to){
                ahead = ai.trafficDistance() - distance;
                if(ahead <= 0f) continue;
            }else if(occupied != null && occupied.from == edge.to && occupied.to != edge.from){
                ahead = edge.length - distance + ai.trafficDistance();
            }else if(other.within(edge.to, 0.5f)){
                ahead = edge.length - distance;
            }else continue;
            float space = Math.max(0f, ahead - clearance(mover, other) - 0.5f);
            float speed = occupied == null ? 0f : ai.trafficSpeed();
            limit = Math.min(limit, Mathf.sqrt(speed * speed + 2f * acceleration * space));
        }
        return limit;
    }

    /** Swept body clearance also prevents direct curve positioning bypassing unit physics. */
    public static boolean canMove(Unit mover, float x, float y){
        return movementBlocker(mover, x, y) == null;
    }

    public static Unit movementBlocker(Unit mover, float x, float y){
        if(Groups.unit == null) return null;
        float dx = x - mover.x, dy = y - mover.y, length2 = dx * dx + dy * dy;
        for(Unit other : Groups.unit){
            if(!train(other, mover)) continue;
            float gap = clearance(mover, other), gap2 = gap * gap;
            float before = other.dst2(mover), after = other.dst2(x, y);
            if(before < gap2 && after > before + 0.0001f) continue;
            float t = length2 == 0f ? 0f : Mathf.clamp(((other.x - mover.x) * dx + (other.y - mover.y) * dy) / length2);
            if(other.dst2(mover.x + t * dx, mover.y + t * dy) < gap2) return other;
        }
        return null;
    }
}








