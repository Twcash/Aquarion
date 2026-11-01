package aquarion.world.blocks.environment;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.graphics.MultiPacker;
import mindustry.world.Block;
import mindustry.world.Tile;

public class rokBlock extends Block {

    public float layer = Layer.power + 10;
    public float rotationRand = 10;
    public rokBlock(String name) {
        super(name);
        breakable = true;
        solid = true;
        breakEffect = Fx.breakProp;
        update = true;
        breakSound = Sounds.rockBreak;
        destroySound = Sounds.rockBreak;
        drawTeamOverlay = false;
        hasColor = true;
    }

    @Override
    public void createIcons(MultiPacker packer) {
        super.createIcons(packer);
        var image = packer.get(fullIcon);
        int pixel = image.get(fullIcon.width / 2, fullIcon.height / 2);
        Color col = Color.rgb(Color.ri(pixel), Color.gi(pixel), Color.bi(pixel));
        Tmp.c3.set(col);
        mapColor.set(Tmp.c3);
    }

    @Override
    public int minimapColor(Tile tile){
        return mapColor.rgba();
    }

    @Override
    public void drawBase(Tile tile) {
        Draw.z(layer);
        float rot = Mathf.randomSeed(tile.pos(), -rotationRand, rotationRand);
        Draw.rect(variants > 0 ? variantRegions[Mathf.randomSeed(tile.pos(), 0, Math.max(0, variantRegions.length - 1))] : region,  size % 2 == 0 ? tile.worldx() + size*2 : tile.worldx(),size % 2 == 0 ? tile.worldy() + size*2 : tile.worldy(), rot);
    }

    @Override
    public TextureRegion[] icons() {
        return variants == 0 ? super.icons() : new TextureRegion[]{Core.atlas.find(name + "1")};
    }
    public class rokBuild extends Building{
        @Override
        public void drawTeam(){
            //no
        }

    }
    public class rokBlockBuild extends Building {
        //prevents the tree from breaking during wave shockwave
        //shamelessly stolen from Minedustry
        //i did this after flora, so it was very fast
        @Override
        public void damage(float amount){
            if(amount >= 1e7f){
                return;
            }
            super.damage(amount);
        }
    }
}
