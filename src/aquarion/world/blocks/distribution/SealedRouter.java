package aquarion.world.blocks.distribution;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
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
        public @Nullable Building target;

        @Override
        public void updateTile() {
            // Increment the progress based on the time
            progress += edelta() / speed * 2f;

            // Continue item movement even if there's no target yet
            if (current != null) {
                if (target == null) {
                    target = getTileTarget(current, lastInput, false);
                    if (target != null) {
                        r = relativeToEdge(target.tile);
                    } else {
                        // If no target is available, set progress to halfway point (0.5f)
                        progress = 0.5f;
                    }
                }

                // Continue moving the item visually
                if (progress >= 1f) {
                    // If a valid target is found transfer the item
                    if (target != null) {
                        target.handleItem(this, current);
                        items.remove(current, 1);
                        current = null;
                        target = null;  // Reset the target for the next item
                    }

                    // Always reset the progress
                    progress = 0f;
                }
            }
        }

        @Override
        public void draw() {
            Draw.z(Layer.blockUnder + 0.3f);
            Draw.rect(topRegion, x, y);
            Draw.z(Layer.blockUnder + 0.1f);
            Draw.rect(region, x, y);

            // Draw the current item
            if (current != null) {
                Draw.z(Layer.blockUnder + 0.2f);

                Draw.z(Layer.blockUnder + 0.1f);
                Tmp.v1.set(Geometry.d4x(recDir) * tilesize / 2f, Geometry.d4y(recDir) * tilesize / 2f)
                        .lerp(Geometry.d4x(r) * tilesize / 2f, Geometry.d4y(r) * tilesize / 2f,
                                Mathf.clamp((progress + 1f) / 2f));


                Draw.rect(current.fullIcon, x + Tmp.v1.x, y + Tmp.v1.y, itemSize, itemSize);
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