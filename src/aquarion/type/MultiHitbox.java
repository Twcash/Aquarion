package aquarion.type;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Rect;
import mindustry.gen.Healthc;
import mindustry.gen.Unit;
import mindustry.type.UnitType;

public class MultiHitbox {
    public Rect rect;
    public TextureRegion[] damageSprites;
    public float damageRatio = 0f;

    public MultiHitbox(float x, float y, float width, float height, TextureRegion[] sprites){
        this.rect = new Rect(x, y, x + width, y + height);
        this.damageSprites = sprites;
    }

    public void draw(Unit unit){
        TextureRegion region = damageSprites[Mathf.clamp((int)(damageRatio * (damageSprites.length - 1)), 0, damageSprites.length - 1)];
        float drawX = unit.x + rect.x;
        float drawY = unit.y + rect.y;
        Draw.rect(region, drawX, drawY, unit.rotation - 90f);
    }

    public boolean overlaps(float worldX, float worldY, float unitX, float unitY){
        return rect.contains(worldX - unitX, worldY - unitY);
    }
}