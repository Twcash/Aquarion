package aquarion.units.type;

import aquarion.annotations.Annotations;
import aquarion.gen.DropShipc;
import aquarion.world.entities.parts.AquaPart;
import aquarion.world.graphics.AquaFx;
import aquarion.world.graphics.Renderer;
import arc.Core;
import arc.graphics.Pixmap;
import arc.graphics.Pixmaps;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureAtlas;
import arc.graphics.g2d.TextureRegion;
import arc.struct.Seq;
import mindustry.entities.Effect;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.MultiPacker;
import mindustry.type.UnitType;
import mindustry.world.meta.Env;

public class AquaUnitType extends UnitType {
    //Drop pod drop effect.
    public Effect dropEffect = AquaFx.droppod;


    public float landDamage = 0f;
    public float landRange = 0f;
    public @Annotations.Load("@-droppod") TextureRegion dropPod;
    public AquaUnitType(String name) {
        super(name);
        this.envDisabled = Env.none;
    }

    @Override
    public void draw(Unit unit){
        AquaPart.aquaParams.set(unit);

        super.draw(unit);
        if(unit instanceof DropShipc drop && !drop.didDrop()){
            applyColor(unit);
            Draw.z(lowAltitude ? Renderer.Layer.flyingUnitLow : Renderer.Layer.flyingUnit);
            Draw.rect(dropPod, unit.x, unit.y, unit.rotation-90);
            drawWeapons(unit);
        }
    }
}
