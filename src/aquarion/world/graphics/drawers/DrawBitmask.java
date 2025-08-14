package aquarion.world.graphics.drawers;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.geom.Geometry;
import arc.struct.*;
import arc.util.*;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.draw.*;

public class DrawBitmask extends DrawBlock {
    public String suffix = "-tiles";
    public TextureRegion[][] regions;
    public float x = 0, y = 0;
    public TextureRegion icon;
    public Seq<Block> blendTypes = new Seq<>();
    public float layer = -1;

    public boolean validBlock(Building build){
        return blendTypes.contains(build.block);
    }
    public int getConnectionType(Building build) {

        int connectionBits = 0;
        for (int i = 0; i < 4; i++) {
            Tile other = build.tile.nearby(Geometry.d4[i]);
            if (other != null) {
                if (other.block() != null) {
                    if (other.build != null) {
                        if (validBlock(other.build)) {
                            connectionBits |= (1 << i);
                        }
                    }
                }
            }
        }
        return connectionBits;
    }

    @Override
    public void draw(Building build){
        float z = Draw.z();
        if(layer > 0) Draw.z(layer);

        int type = getConnectionType(build);
        int rot = build.rotation;

        Draw.rect(regions[type][0], build.x + x, build.y + y);

        Draw.z(z);
    }

    @Override
    public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list){
        Draw.rect(regions[0][0], plan.drawx() + x, plan.drawy() + y);
    }

    @Override
    public void load(Block block){
        regions = Core.atlas.find(block.name + suffix).split(block.size * 32, block.size * 32);
        icon = Core.atlas.find(block.name + suffix + "-icon");
    }

    @Override
    public TextureRegion[] icons(Block block){
        return new TextureRegion[]{icon};
    }
}