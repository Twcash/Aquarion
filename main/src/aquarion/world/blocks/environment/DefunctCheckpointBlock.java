package aquarion.world.blocks.environment;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.struct.Seq;
import mindustry.editor.EditorTile;
import mindustry.world.Tile;

import static mindustry.Vars.world;

/** Checkpoint markers followed by defunct flying units. Kept in a separate list from
 *  the ground checkpoints so air and ground routes can be laid out independently. */
public class DefunctCheckpointBlock extends CheckpointBlock {
    public static final Seq<Tile> checkpoints = new Seq<>();

    public DefunctCheckpointBlock(String name) {
        super(name);
    }

    public static void rebuildCheckpoints() {
        checkpoints.clear();
        if (world == null || world.tiles == null) return;
        for (Tile tile : world.tiles) {
            if (tile.overlay() instanceof DefunctCheckpointBlock) {
                checkpoints.add(tile);
            }
        }
        checkpoints.sort(t -> t.extraData);
    }

    @Override
    public void load() {
        super.load();
        //reuse the normal checkpoint sprite if no dedicated one exists
        if (region == Core.atlas.find("error") && Core.atlas.has("aquarion-checkpoint")) {
            region = Core.atlas.find("aquarion-checkpoint");
        }
    }

    @Override
    public void drawBase(Tile tile) {
        if (!(tile instanceof EditorTile)) return;
        //tinted so mappers can tell air checkpoints apart from ground ones
        Draw.color(Color.valueOf("d99d73"));
        Draw.rect(region, tile.worldx(), tile.worldy());
        Draw.color();
        drawNumber(tile);
    }

    @Override
    protected Color numberColor() {
        return Color.valueOf("a34c2d");
    }
}
