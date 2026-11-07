package aquarion.world.AI;

import aquarion.world.blocks.neoplasia.GenericNeoplasiaBlock;
import aquarion.world.blocks.neoplasia.NeoplasiaGraph;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.struct.IntSet;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.content.Blocks;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.world.Tile;

import static mindustry.Vars.world;

/** Share colony-edge discovery so deep-spawned spewers need not scan an entire map individually. */
final class SpewerFrontier{
    private static final ObjectMap<Team, Frontier> frontiers = new ObjectMap<>();
    private static Object tiles;

    private static class Frontier{
        final Seq<Tile> sites = new Seq<>();
        final IntSet seen = new IntSet();
        float checked = Float.NEGATIVE_INFINITY;

        void add(Building build, Team team){
            if(build.team != team || build.tile == null || build.dead() || world.build(build.pos()) != build) return;
            for(var offset : Geometry.d8){
                Tile tile = world.tile(build.tile.x + offset.x, build.tile.y + offset.y);
                if(tile != null && tile.block() == Blocks.air && tile.build == null
                    && !tile.floor().isDeep() && tile.floor().placeableOn && seen.add(tile.pos())) sites.add(tile);
            }
        }
    }

    static Tile closest(SpewerAI ai){
        if(tiles != world.tiles){ frontiers.clear(); tiles = world.tiles; }
        Frontier frontier = frontiers.get(ai.unit().team, Frontier::new);
        if(Time.time < frontier.checked || Time.time - frontier.checked >= 120f){
            frontier.checked = Time.time;
            frontier.sites.clear();
            frontier.seen.clear();
            for(var build : GenericNeoplasiaBlock.activeNeoplasia) frontier.add(build, ai.unit().team);
            for(var heart : NeoplasiaGraph.hearts) frontier.add(heart, ai.unit().team);
        }
        Tile best = null;
        float score = Float.POSITIVE_INFINITY;
        for(Tile tile : frontier.sites){
            if(tile.block() != Blocks.air || !ai.validSeedTile(tile)) continue;
            float candidate = ai.unit().dst(tile) + Mathf.randomSeed(ai.unit().id * 31L + tile.pos(), 0f, 64f);
            if(candidate < score){ score = candidate; best = tile; }
        }
        return best;
    }
}
