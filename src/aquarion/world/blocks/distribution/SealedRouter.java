package aquarion.world.blocks.distribution;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Vec2;
import arc.util.Nullable;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.type.Item;
import mindustry.world.Tile;
import mindustry.world.blocks.distribution.Router;

import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static arc.math.Mathf.lerp;
import static mindustry.Vars.*;


public class SealedRouter extends Router {
    public float speed = 8f;
    public TextureRegion topRegion;

    public SealedRouter(String name) {
        super(name);
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.itemsMoved, 60f / speed * itemCapacity, StatUnit.itemsSecond);
    }

    @Override
    public void load() {
        super.load();
        topRegion = Core.atlas.find(name + "-top");
    }

    @Override
    public TextureRegion[] icons() {
        return new TextureRegion[]{region, topRegion};
    }

    public class SealedRouterBuild extends RouterBuild {
        public float progress;
        public @Nullable Item current;
        public int recDir = 0;
        public Tile lastInput;
        public int r = 0;
        public Tile source;
        Vec2 currentItemPos = new Vec2();
        public @Nullable Building target;

        @Override
        public void updateTile() {
            // Increment the progress based on the time
            progress += edelta() / speed * 2f;

            // Continue item movement even if there's no target yet
            if (current != null) {
                // Try to find a target if it hasn't been set yet
                if (target == null) {
                    target = getTileTarget(current, lastInput, false);  // Try to find a target again
                    if (target != null) {
                        r = relativeToEdge(target.tile);  // Get the direction for the output
                    } else {
                        // If no target is available, set progress to halfway point (0.5f)
                        progress = 0.5f;
                    }
                }

                // Continue moving the item visually
                if (progress >= 1f) {
                    // If a valid target is found, transfer the item
                    if (target != null) {
                        target.handleItem(this, current);
                        items.remove(current, 1);
                        current = null;
                        target = null;  // Reset the target for the next item
                    }

                    // Always reset the progress, even if no target is found
                    progress = 0f;
                }
            }
        }

        @Override
        public void draw() {
            // Draw the block layers
            Draw.z(Layer.blockUnder + 0.3f);
            Draw.rect(topRegion, x, y);
            Draw.z(Layer.blockUnder + 0.1f);
            Draw.rect(region, x, y);

            // Draw the current item
            if (current != null) {
                Draw.z(Layer.blockUnder + 0.2f);

                // Position of the item, start by assuming it's at the center
                float itemX = this.x;
                float itemY = this.y;

                // First phase: from input direction (recDir) to the center
                if (progress <= 0.5f) {
                    // Lerp from the input direction (recDir) to the block's center (this.x, this.y)
                    Tmp.v1.set(Geometry.d4x(recDir) * tilesize / 2f, Geometry.d4y(recDir) * tilesize / 2f)
                            .lerp(0f, 0f, Mathf.clamp(progress * 2f));  // progress * 2f to normalize the first half
                } else {
                    // Second phase: from the block's center to the output direction (r)
                    Tmp.v1.set(0f, 0f)  // Start from the center
                            .lerp(Geometry.d4x(r) * tilesize / 2f, Geometry.d4y(r) * tilesize / 2f, Mathf.clamp((progress - 0.5f) * 2f));  // progress - 0.5f for the second half
                }

                // Update item position based on the calculated vector
                itemX += Tmp.v1.x;
                itemY += Tmp.v1.y;

                // Draw the item at the computed position
                Draw.rect(current.fullIcon, itemX, itemY, itemSize, itemSize);
            }
        }

        @Override
        public void handleItem(Building source, Item item) {
            current = item;
            progress = -1f;
            recDir = relativeToEdge(source.tile);
            items.add(item, 1);
            lastInput = source.tile();
            target = null;
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.b(recDir);             
            write.f(progress);
            write.bool(current != null);
            if (current != null) {
                write.str(current.name);
            }
            write.i(lastInput == null ? -1 : lastInput.pos());
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            recDir = read.b();
            progress = read.f();
            if (read.bool()) {
                current = content.item(read.str());
            } else {
                current = null;
            }
            int lastInputPos = read.i();
            if (lastInputPos != -1) {
                lastInput = world.tile(lastInputPos);
            } else {
                lastInput = null;
            }
        }
    }
}