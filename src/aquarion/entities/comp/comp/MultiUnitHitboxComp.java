package aquarion.entities.comp.comp;

import arc.math.geom.Vec2;
import arc.util.Nullable;
import ent.anno.Annotations;
import mindustry.content.UnitTypes;
import mindustry.entities.units.UnitController;
import mindustry.entities.units.WeaponMount;
import mindustry.game.Team;
import mindustry.gen.Bullet;
import mindustry.gen.Hitboxc;
import mindustry.gen.Unitc;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Rect;
import arc.struct.Seq;
import mindustry.gen.*;
import mindustry.type.ItemStack;
import mindustry.type.UnitType;
import mindustry.world.Tile;

@Annotations.EntityComponent
public abstract class MultiUnitHitboxComp implements Healthc, Hitboxc, Drawc, Unitc{
    @Annotations.Import boolean dead, disarmed;
    @Annotations.Import float x, y, rotation, maxHealth, drag, armor, hitSize, health, shield, ammo, dragMultiplier, armorOverride, speedMultiplier;
    @Annotations.Import Team team;
    @Annotations.Import int id;
    @Annotations.Import @Nullable Tile mineTile;
    @Annotations.Import Vec2 vel;
    @Annotations.Import WeaponMount[] mounts;
    @Annotations.Import
    ItemStack stack;
    private UnitController controller;
    UnitType type = UnitTypes.alpha;
    public static class MultiHitbox {
        public Rect rect;
        public TextureRegion[] damageSprites;
        public float damageRatio = 0f;

        public MultiHitbox(float x, float y, float width, float height, TextureRegion[] sprites){
            this.rect = new Rect(x, y, x + width, y + height);
            this.damageSprites = sprites;
        }

        public boolean overlaps(float worldX, float worldY, float unitX, float unitY){
            return rect.contains(worldX - unitX, worldY - unitY);
        }
    }

    public Seq<MultiHitbox> hitboxes = new Seq<>();

    public void setupHitboxes(UnitType type){
        hitboxes.clear();

        // now I wonder
        hitboxes.add(new MultiHitbox(-4, -4, 8, 8, new TextureRegion[]{
                type.region
        }));
    }

    @Override
    public boolean collides(Hitboxc other){
        if(!(other instanceof Bullet bullet)) return false;
        for(MultiHitbox box : hitboxes){
            if(box.overlaps(bullet.x, bullet.y, x, y)) return true;
        }
        return false;
    }

    @Override
    public void collision(Hitboxc other, float x, float y){
        if(other instanceof Bullet bullet){
            this.controller.hit(bullet);
        }
    }

    @Override
    public void draw(){
        float damageLevel = 1f - Mathf.clamp(health / maxHealth, 0f, 1f);
        for(MultiHitbox box : hitboxes){
            box.damageRatio = damageLevel;
        }
    }

}
