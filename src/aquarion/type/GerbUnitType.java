package aquarion.type;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import mindustry.Vars;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.type.UnitType;

public class GerbUnitType extends UnitType {
    public TextureRegion[] variantRegions;
    public TextureRegion[] damagedVariantRegions;
    public TextureRegion[] severelyDamagedVariantRegions;
    public TextureRegion armorRegion;
    public TextureRegion damagedArmorRegion;
    public TextureRegion severelyDamagedArmorRegion;
    public int variants = 7;
    public float damageThreshold = 0.8f; // 80%
    public float severeDamageThreshold = 0.3f; // 30%

    public GerbUnitType(String name) {
        super(name);
        drawCell = false;
        outlineColor = Color.valueOf("37261d");
    }

    @Override
    public void load() {
        super.load();
        armorRegion = Core.atlas.find(name + "-armor");
        damagedArmorRegion = Core.atlas.find(name + "-armor" + 2);
        severelyDamagedArmorRegion = Core.atlas.find(name + "-armor" + 1);

        if (variants != 0) {
            variantRegions = new TextureRegion[variants];
            damagedVariantRegions = new TextureRegion[variants];
            severelyDamagedVariantRegions = new TextureRegion[variants];
            for (int i = 0; i < variants; i++) {
                variantRegions[i] = Core.atlas.find(name + (1 + i));
                //Do not ask. Do not whine. Do not even mention this to me.
                damagedVariantRegions[i] = Core.atlas.find(name + (1 + i) + "-2");
                severelyDamagedVariantRegions[i] = Core.atlas.find(name + (1 + i) + "-1");
            }
        } else {
            variantRegions = new TextureRegion[]{fullIcon};
            damagedVariantRegions = new TextureRegion[]{fullIcon};
            severelyDamagedVariantRegions = new TextureRegion[]{fullIcon};
        }
    }

    public TextureRegion[] getVariantRegions() {
        return variantRegions == null ? (variantRegions = new TextureRegion[]{fullIcon}) : variantRegions;
    }

    public TextureRegion[] getDamagedVariantRegions() {
        return damagedVariantRegions == null ? (damagedVariantRegions = new TextureRegion[]{fullIcon}) : damagedVariantRegions;
    }

    public TextureRegion[] getSeverelyDamagedVariantRegions() {
        return severelyDamagedVariantRegions == null ? (severelyDamagedVariantRegions = new TextureRegion[]{fullIcon}) : severelyDamagedVariantRegions;
    }

    public TextureRegion getArmorRegion() {
        return armorRegion == null ? (armorRegion = new TextureRegion()) : armorRegion;
    }

    public TextureRegion getDamagedArmorRegion() {
        return damagedArmorRegion == null ? (damagedArmorRegion = new TextureRegion()) : damagedArmorRegion;
    }

    public TextureRegion getSeverelyDamagedArmorRegion() {
        return severelyDamagedArmorRegion == null ? (severelyDamagedArmorRegion = new TextureRegion()) : severelyDamagedArmorRegion;
    }

    @Override
    public void drawBody(Unit unit) {
        applyColor(unit);
        TextureRegion[] regions;
        TextureRegion armorRegion;

        //Why won't this work??? I have no words to describe my confusion.
        if (unit.health < unit.maxHealth * severeDamageThreshold) {
            // huh huh huh huh
            regions = getDamagedVariantRegions();
            armorRegion = getDamagedArmorRegion();
        } else if (unit.health < unit.maxHealth * damageThreshold) {
            // Damaged

            regions = getSeverelyDamagedVariantRegions();
            armorRegion = getSeverelyDamagedArmorRegion();
        } else {
            // full hp
            regions = getVariantRegions();
            armorRegion = getArmorRegion();
        }

        if (regions.length > 0) {
            Draw.z(Layer.legUnit);
            int index = Mathf.randomSeed(unit.id, 0, regions.length - 1);
            Draw.rect(regions[index], unit.x, unit.y, unit.rotation - 90);

            // Draw armor region
            Draw.z(Layer.legUnit + 0.01f);
            Draw.rect(armorRegion, unit.x, unit.y, unit.rotation - 90);
        }
    }
    }