package aquarion.world.content;

import aquarion.world.Uti.Statf;
import arc.graphics.Color;
import arc.struct.ObjectSet;
import mindustry.type.Item;
import aquarion.world.items.AquaItemEntity;
import arc.func.Prov;

public class AquaItem extends Item {
    public final ObjectSet<String> tags = new ObjectSet<>();
    public Prov<? extends ItemComp> itemType = ItemComp::new;

    public ItemComp createBuild() {
        return itemType.get();
    }

    public class ItemComp extends AquaItemEntity {
        public ItemComp() {
            super(AquaItem.this);
        }

        @Override
        public boolean hasTag(String tag) {
            return tags.contains(tag);
        }
    }
    public AquaItem(String name, Color color) {
        super(name, color);
    }
    public Item decayTo;
    public float magnetism = 0;
    public boolean biomass = false;
    @Override
    public void setStats(){
        super.setStats();
        stats.addPercent(Statf.magnetism, magnetism);
    }
}
