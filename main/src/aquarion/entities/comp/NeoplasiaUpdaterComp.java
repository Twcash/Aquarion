package aquarion.entities.comp;
import aquarion.annotations.Annotations;
import aquarion.gen.NeoplasiaCell;
import aquarion.gen.NeoplasiaCellc;
import aquarion.world.blocks.neoplasia.NeoplasiaSource;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.struct.ObjectMap;
import arc.util.Time;
import mindustry.Vars;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.world.Tile;
@Annotations.EntityDef(value = NeoplasiaCellc.class, serialize = false, genio = false)
@Annotations.EntityComponent
abstract class NeoplasiaCellComp implements Entityc, Drawc, Posc {

    // Imported ECS fields
    @Annotations.Import float x, y;

    // Static map for O(1) neighbor lookup
    static final ObjectMap<Tile, NeoplasiaCell> cells = new ObjectMap<>();

    // Cell data
    Tile tile;
    float amount;

    // Tuning parameters
    private static final float flowRate = 0.02f;
    private static final float flowThreshold = 0.4f;
    private static final float maxFlow = 0.15f;
    private static final float maxHeight = 400f;
    private static final float resistancePerSize = 2f;
    private static final float damageScale = 50f; // increase for visible testing

    /** ECS update called every tick */
    @Override
    public void update() {
        if (tile == null) {
            remove();
            return;
        }

        if (amount <= 0.001f) {
            cells.remove(tile);
            remove();
            return;
        }

        // Damage block if overflowing
        if (tile.build != null && !(tile.build instanceof NeoplasiaSource.NeoplasiaSourceBuild)) {
            float resistance = tile.block().size * resistancePerSize;
            float overflow = amount - resistance;
            if (overflow > 0f) {
                tile.build.damage(overflow * damageScale * Time.delta);
            }
        }

        // Find lowest neighbor for flow
        Tile lowest = null;
        float lowestAmount = amount;

        for (Point2 p : Geometry.d4) {
            Tile other = Vars.world.tile(tile.x + p.x, tile.y + p.y);
            if (other == null) continue;

            NeoplasiaCell neighborCell = cells.get(other);
            float otherAmount = neighborCell == null ? 0f : neighborCell.amount;

            if (otherAmount < lowestAmount) {
                lowestAmount = otherAmount;
                lowest = other;
            }
        }

        // Spread to neighbor
        if (lowest != null) {
            float diff = amount - lowestAmount;
            if (diff > flowThreshold) {
                float flow = Math.min(diff * flowRate, maxFlow);
                amount -= flow;

                NeoplasiaCell neighbor = cells.get(lowest);
                if (neighbor == null) {
                    neighbor = createCell(lowest, 0f);
                }

                neighbor.amount = Mathf.clamp(neighbor.amount + flow, 0f, maxHeight);
            }
        }
    }

    /** Draw called automatically by ECS for each cell */
    @Override
    public void draw() {
        Draw.z(Layer.blockOver);

        float alpha = Mathf.clamp(amount/100f);
        Draw.color(0.5f, 0f, 0.1f, alpha);
        Fill.square(x, y, Vars.tilesize / 2f);

        Draw.reset();
    }

    /** Create a new NeoplasiaCell entity for a tile */
    static NeoplasiaCell createCell(Tile tile, float initialAmount) {
        NeoplasiaCell cell = NeoplasiaCell.create();

        // Assign tile and world position BEFORE adding to ECS
        cell.tile = tile;
        cell.x = tile.worldx();
        cell.y = tile.worldy();

        cell.amount = initialAmount;

        // Add to static map for neighbor lookup
        cells.put(tile, cell);

        // Add to ECS so update/draw will be called
        cell.add();

        return cell;
    }

    /** Helper to infect a tile from a source */
    static void infect(Tile tile, float amt) {
        NeoplasiaCell cell = cells.get(tile);
        if (cell == null) {
            cell = createCell(tile, 0f);
        }
        cell.amount = Mathf.clamp(cell.amount + amt, 0f, maxHeight);
    }
}