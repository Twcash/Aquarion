package aquarion.world.blocks.distribution;

import arc.math.Mathf;
import arc.struct.ObjectSet;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.world.blocks.liquid.LiquidJunction;

public class OverflowValve extends LiquidJunction {
    public boolean invert = false;
    public OverflowValve(String name) {
        super(name);
        canOverdrive = false;
        solid = false;
        update = true;
    }

    public class OverflowValveBuild extends LiquidJunctionBuild {

        public ObjectSet<Building> lastSources = new ObjectSet<>();

        public boolean leftFirst = false;

        @Override
        public void updateTile() {
            //toggle left/right every tick
            this.leftFirst ^= true;
        }

        @Override
        public Building getLiquidDestination(Building from, Liquid liquid) {
            if(!enabled) return this;
            if(this.lastSources.contains(from)){
                return this;
            }
            lastSources.add(from);

            int fromDir = this.relativeTo(from);
            if(fromDir == -1) return this;
            int toDir = (fromDir + 2) % 4;
            Building right = nearby(Mathf.mod(toDir - 1, 4));
            Building left = nearby(Mathf.mod(toDir + 1, 4));
            Building next = nearby(toDir);

            Building first;
            Building second;
            Building third;

            if(invert){
                first = (leftFirst)? left: right;
                second = (!leftFirst)? left: right;
                third = next;
            } else {
                first = next;
                second = (leftFirst)? left: right;
                third = (!leftFirst)? left: right;
            }

            //What the search found, if any.
            Building result = attemptDestination(first, liquid);
            if(result == null) result = attemptDestination(second, liquid);
            if(result == null) result = attemptDestination(third, liquid);
            //fallback if no paths are viable.
            if(result == null) result = this;
            lastSources.remove(from);
            return result;
        }

        /** Not even valid to send getLiquidDestination to*/
        public boolean invalidTarget(Building next, Liquid liquid){
            return next == null || (!next.acceptLiquid(this, liquid) && !(next.block instanceof LiquidJunction));
        }

        /** Checks if a getLiquidDestination pathfind failed; because all junctions return themselves if they fail*/
        public boolean failedDestination(Building build){
            return build instanceof LiquidJunctionBuild || build == null;
        }

        public Building attemptDestination(Building a, Liquid liquid){
            if(!invalidTarget(a, liquid)){
                Building other = a.getLiquidDestination(this, liquid);
                if(!failedDestination(other)){
                    return other;
                }
            }
            return null;
        }
    }
}
