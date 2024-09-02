package aquarion.type;

import aquarion.world.Uti.AquaStats;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import mindustry.Vars;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.LegsUnit;
import mindustry.gen.Legsc;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.type.UnitType;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;

import static mindustry.Vars.tilesize;

public class GerbUnitType extends UnitType {
    public TextureRegion[] variantRegions;
    public TextureRegion[] damagedVariantRegions;
    public TextureRegion[] severelyDamagedVariantRegions;
    public TextureRegion armorRegion;
    public TextureRegion damagedArmorRegion;
    public TextureRegion severelyDamagedArmorRegion;

    public TextureRegion[][] legSegments; // 2D array: [leg index][segment index]

    public int variants = 7;
    public float damageThreshold = 0.8f; // these are percentage based btw%
    public float severeDamageThreshold = 0.3f;
    public float kineticResistance;
    public float heatResistance;
    public float energyResistance;
    public float concussionResistance;
    public GerbUnitType(String name) {
        super(name);
        drawCell = false;
        outlineColor = Color.valueOf("37261d");
    }
    // Method to apply damage to the unit
    public void applyDamage(Unit unit, AquaBulletType bullet) {
        float effectiveDamage = bullet.calculateEffectiveDamage(this);

        if (effectiveDamage > 0) {
            unit.health -= effectiveDamage;
        }
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

        // Determine which set of textures to use based on health thresholds
        if (unit.health < unit.maxHealth * severeDamageThreshold) {
            regions = getSeverelyDamagedVariantRegions();
            armorRegion = getSeverelyDamagedArmorRegion();
        } else if (unit.health < unit.maxHealth * damageThreshold) {
            regions = getDamagedVariantRegions();
            armorRegion = getDamagedArmorRegion();
        } else {
            regions = getVariantRegions();
            armorRegion = getArmorRegion();
        }

        // Draw body and armor
        if (regions.length > 0) {
            float bodyLayerOffset = Mathf.randomSeed(unit.id, -0.01f, 0.01f);
            Draw.z(Layer.legUnit + bodyLayerOffset);
            int index = Mathf.randomSeed(unit.id, 0, regions.length - 1);
            Draw.rect(regions[index], unit.x, unit.y, unit.rotation - 90);

            Draw.z(Layer.legUnit + bodyLayerOffset + 0.00000001f);
            Draw.rect(armorRegion, unit.x, unit.y, unit.rotation - 90);
        }
        Draw.z(Layer.legUnit + 0.0000002f);
        drawWeapons(unit);
    }
    @Override
    public void setStats(){
        super.setStats();
        // resistances
        stats.add(AquaStats.kineticResistance, kineticResistance * 100, StatUnit.percent);
        stats.add(AquaStats.heatResistance, heatResistance * 100, StatUnit.percent);
        stats.add(AquaStats.energyResistance, energyResistance * 100, StatUnit.percent);
        stats.add(AquaStats.concussionResistance, concussionResistance * 100, StatUnit.percent);
    }
    }