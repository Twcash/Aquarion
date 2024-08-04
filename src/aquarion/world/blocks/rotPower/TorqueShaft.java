package aquarion.world.blocks.rotPower;

import aquarion.utilities.AquaUtil;
import aquarion.world.graphs.RTGraph;
import aquarion.world.interfaces.HasRT;
import aquarion.world.meta.RTConfig;
import aquarion.world.meta.RTModule;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.util.*;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;

import static arc.Core.atlas;

public class TorqueShaft extends Block {
    public RTConfig rtConfig = new RTConfig();
    public float maxVisualTorque = 15f;
    public TextureRegion botRegion;
    public TextureRegion bit;
    public TextureRegion white;
    public TextureRegion gear;
    public static final int[][] tiles = new int[][]{
            {},
            {0, 2}, {1, 3}, {0, 1},
            {0, 2}, {0, 2}, {1, 2},
            {0, 1, 2}, {1, 3}, {0, 3},
            {1, 3}, {0, 1, 3}, {2, 3},
            {0, 2, 3}, {1, 2, 3}, {0, 1, 2, 3}
    };
    public TextureRegion[][] topRegion;

    public TorqueShaft(String name) {
        super(name);
        update = solid = rotate = true;
        rotateDraw = false;
        replaceable = true;
        size = 1;
        sync = true;
    }
    @Override
    public void setBars() {
        super.setBars();
        rtConfig.addBars(this);
    }
    @Override
    public boolean canReplace(Block other) {
        return other instanceof TorqueShaft || super.canReplace(other);
    }



    @Override
    public void load() {
        super.load();
        topRegion = AquaUtil.splitLayers(name + "-sheet", 32, 1);
        uiIcon = atlas.find(name + "-icon");
        botRegion = atlas.find(name + "-bottom");
        bit = atlas.find(name + "-bit");
        white = atlas.find("white");
        gear = atlas.find(name + "-gear");
    }

    public class TorqueShaftBuild extends Building implements HasRT {
        public int tiling = 0;
        public RTModule rotationPower = new RTModule();

        @Override
        public void draw() {
            float rotationSpeed = 1;
            float height = 6;
            float teeth = 6;
            float width = 6;

            float shaftCount = 12;
            if (rotation == 1 || rotation == 3) {
                height = 8;
                width = 7;
            } else {
                height = 7;
                width = 8;
            }
            Color[] shaftColors = {Color.valueOf("c9a58f"), Color.valueOf("b28768"), Color.valueOf("8f665b")};
            float rot = (rotationSpeed * Time.time + rotation) % (float) (Math.PI * 2);

            float wheelX = this.x - 4;
            float wheelY = this.y - 4;
            if (rotation == 1 || rotation == 3) {
                wheelX = this.x - 3.5f;
                wheelY = this.y - 4f;
            } else {
                wheelX = this.x - 4f;
                wheelY = this.y - 3.5f;
            }

            for (int j = 0; j < teeth; j++) {
                float r = (rot + (float) Math.PI * 2 / teeth * j) % (float) (Math.PI * 2);
                float r2 = (rot + (float) Math.PI * 2 / teeth * (j + 1)) % (float) (Math.PI * 2);

                if (r2 > r) r2 -= Math.PI * 2;

                float toothX = ((float) Math.cos(r) + 1) * width / 2 + wheelX;
                float tooth2X = ((float) Math.cos(r2) + 1) * width / 2 + wheelX;
                float toothY = ((float) Math.cos(r) + 1) * height / 2 + wheelY;
                float tooth2Y = ((float) Math.cos(r2) + 1) * height / 2 + wheelY;

                float a = Math.min(1 + (float) Math.cos(r + r2), 1);
                if (rotation == 1 || rotation == 3) {
                    if (tooth2X - toothX < 0) continue;
                    Draw.z(Layer.block - 0.15f);

                    Draw.color(shaftColors[1]);
                    Draw.rect(white, toothX + (tooth2X - toothX) / 2, wheelY + height / 2, tooth2X - toothX, height);

                    Draw.color(shaftColors[toothX + tooth2X - wheelX - wheelX > width ? 0 : 2].cpy().a(a));
                    Draw.rect(white, toothX + (tooth2X - toothX) / 2, wheelY + height / 2, tooth2X - toothX, height);
                } else {
                    Draw.z(Layer.block - 0.15f);
                    if (tooth2Y - toothY < 0) continue;
                    Draw.color(shaftColors[1]);
                    Draw.rect(white, wheelX + width / 2, toothY + (tooth2Y - toothY) / 2, width, tooth2Y - toothY);

                    Draw.color(shaftColors[toothY + tooth2Y - wheelY - wheelY > height ? 0 : 2].cpy().a(a));
                    Draw.rect(white, wheelX + width / 2, toothY + (tooth2Y - toothY) / 2, width, tooth2Y - toothY);
                }
            }

            Draw.color(Color.white);
            Draw.z(Layer.block - 0.1f);
            Draw.rect(topRegion[0][tiling], x, y, 0);
            Draw.z(Layer.block - 0.2f);
            Draw.rect(botRegion, x, y, 0);
        }
        @Override
        public void onProximityUpdate() {
            super.onProximityUpdate();
            tiling = 0;

            for (int i = 0; i < 4; i++) {
                Building nearbyBuild = nearby(i);
                HasRT build = nearbyBuild instanceof HasRT ? (HasRT) nearbyBuild : null;

                if (build != null) {
                    int relativeDir = (i - rotation + 4) % 4;
                    boolean isFrontOrBack = relativeDir == 0 || relativeDir == 2;
                    boolean isDirectlyToSide = (relativeDir == 1 || relativeDir == 3) && !(nearbyBuild instanceof TorqueShaftBuild);

                    if (isFrontOrBack || isDirectlyToSide) {
                        tiling |= (1 << i);
                    }

                    rTGraph().addBuilding((Building) build);
                }
            }
        }

        @Override
        public void onProximityRemoved() {
            super.onProximityRemoved();
            rTGraph().removeBuild(this, false);
        }



        @Override public RTModule rotationPower() {
            return rotationPower;
        }

        @Override
        public RTConfig rTConfig() {
            return new RTConfig();
        }
        @Override
        public void updateTile() {
            super.updateTile();
        }

        @Override
        public RTGraph rTGraph() {
            return rotationPower().graph;
        }
    }
}