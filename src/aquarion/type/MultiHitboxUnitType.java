package aquarion.type;

import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.struct.Seq;
import ent.anno.Annotations;
import mindustry.gen.Bullet;
import mindustry.gen.Hitboxc;
import mindustry.gen.Unit;
import mindustry.type.UnitType;

public class MultiHitboxUnitType extends UnitType {

    public final Seq<MultiHitbox> hitboxes = new Seq<>();

    public MultiHitboxUnitType(String name) {
        super(name);
    }

    public void addHitbox(float x, float y, float w, float h, TextureRegion[] regions){
        hitboxes.add(new MultiHitbox(x, y, w, h, regions));
    }

    @Override
    public void draw(Unit unit){
        float damageLevel = 1f - Mathf.clamp(unit.health / unit.maxHealth, 0f, 1f);
        for(MultiHitbox box : hitboxes){
            box.damageRatio = damageLevel;
            box.draw(unit);
        }
    }

    @Override
    public boolean hittable(Unit unit){
        return hittable;
    }


}