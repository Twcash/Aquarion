package aquarion.world.blocks.neoplasia;

import arc.math.geom.Geometry;
import arc.struct.IntSet;
import mindustry.gen.Building;

import static mindustry.Vars.*;

/** One-ring damage transfer; transferred hits never start another bleed. */
final class NeoplasiaDamage{
    private static boolean bleeding;
    private static final IntSet affected = new IntSet();

    static void bleed(Building source, float damage){
        if(bleeding || damage <= 0f || net.client() || source.tile == null) return;
        bleeding = true;
        affected.clear();
        try{
            for(var offset : Geometry.d8){
                Building other = world.build(source.tile.x + offset.x, source.tile.y + offset.y);
                if(other == null || other == source || other.team != source.team || other.dead()
                    || !(other instanceof GenericNeoplasiaBlock.NeoplasiaBuild || other instanceof NeoplasmHeart.NeoplasmHeartBuild)
                    || !affected.add(other.pos())) continue;
                // handleDamage receives effective damage, so avoid applying the same health multiplier twice.
                other.damage(damage * 0.5f * state.rules.blockHealth(other.team));
            }
        }finally{
            bleeding = false;
            affected.clear();
        }
    }
}
