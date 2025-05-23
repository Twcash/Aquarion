package aquarion.world.units;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Tmp;
import mindustry.gen.Tankc;
import mindustry.gen.Unit;

import mindustry.type.unit.TankUnitType;
//Basic override for treads due to mirroring frustration.
public class newTankUnitType extends TankUnitType {
    public newTankUnitType(String name) {
        super(name);
    }
    //Yes. I literally overrode load() so I can use -tread-0-0 rather than -treads0-0
    @Override
    public void load(){
        super.load();
        if(treadRegion.found()){
            treadRegions = new TextureRegion[treadRects.length][treadFrames];
            for(int r = 0; r < treadRects.length; r++){
                for(int i = 0; i < treadFrames; i++){
                    treadRegions[r][i] = Core.atlas.find(name + "-tread-" + r + "-" + i);
                }
            }
        }
    }

    //Simple removal of the for side loop. Effectively removing mirroring
    @Override
    public <T extends Unit & Tankc> void drawTank(T unit){
        applyColor(unit);
        Draw.rect(treadRegion, unit.x, unit.y, unit.rotation - 90);

        if(treadRegion.found()){
            int frame = (int)(unit.treadTime()) % treadFrames;
            for(int i = 0; i < treadRects.length; i ++){
                var region = treadRegions[i][frame];
                var treadRect = treadRects[i];
                float xOffset = (treadRect.x + treadRect.width/2f);
                float yOffset = -(treadRect.y + treadRect.height/2f);
                Tmp.v1.set(xOffset, yOffset).rotate(unit.rotation - 90);
                Draw.rect(region, unit.x + Tmp.v1.x / 4f, unit.y + Tmp.v1.y / 4f, treadRect.width / 4f, region.height * region.scale / 4f, unit.rotation - 90);
            }
        }
    }
}
