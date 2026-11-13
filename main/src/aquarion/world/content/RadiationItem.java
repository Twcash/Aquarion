package aquarion.world.content;

import aquarion.world.items.AquaItemEntity;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import mindustry.content.Items;
import mindustry.type.Item;

public class RadiationItem extends AquaItem {
    public float decayTime = 60f * 10f;
    public Item decayItem = Items.lead;
    public boolean enrichProduct = false;
    public RadiationItem(String name, Color color) {
        super(name, color);
        itemType = RadiationComp::new;
    }

    public class RadiationComp extends ItemComp {
        public boolean enriched;

        @Override
        public boolean hasTag(String tag) {
            return ("enriched".equals(tag) && enriched) || super.hasTag(tag);
        }

        public void enrich() {
            if(enriched) return;
            enriched = true;
            age = 0f;
        }

        @Override
        public boolean tracked() {
            return true;
        }

        @Override
        public boolean update(float delta) {
            if(!enriched || decayItem == null || decayTime <= 0f) return false;
            age += Math.max(0f, delta);
            if(age < decayTime) return false;
            item = decayItem;
            return true;
        }

        @Override
        public AquaItemEntity replacement() {
            AquaItemEntity result = super.replacement();
            if(enrichProduct && result instanceof RadiationComp comp) {
                comp.enrich();
            }
            return result;
        }

        @Override
        public void draw(float x, float y, float size) {
            Draw.rect(item.fullIcon, x, y, size, size);
            if(enriched && decayItem != null && decayTime > 0f) {
                Draw.alpha(Mathf.clamp(age / decayTime));
                Draw.rect(decayItem.uiIcon, x, y, size, size);
                Draw.reset();
            }
        }

        @Override
        public int writeState() {
            return enriched ? 1 : 0;
        }

        @Override
        public void readState(int state) {
            enriched = state != 0;
        }
    }
}
