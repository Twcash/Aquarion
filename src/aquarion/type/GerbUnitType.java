package aquarion.type;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
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
    // effect that happens beyond the damage threshold
    // effect that happens beyond extreme damage threshold
    public Color color = Color.white;
    public int variants = 7;
    public float damageThreshold = 0.3f; // these are percentage based btw%
    public float severeDamageThreshold = 0.8f;
    // Chain-related fields
    public int chainLength = 12; // Number of chain segments
    public TextureRegion chainSegmentRegion; // Texture for the chain segment
    public Seq<Vec2> chainPositions = new Seq<>(); // Sequence to store chain segment positions

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
        chainSegmentRegion = Core.atlas.find(name + "-chain");

        // Initialize chain segment positions at the unit's initial position
        for (int i = 0; i < chainLength; i++) {
            chainPositions.add(new Vec2());
        }
        if (variants != 0) {
            variantRegions = new TextureRegion[variants];
            damagedVariantRegions = new TextureRegion[variants];
            severelyDamagedVariantRegions = new TextureRegion[variants];
            for (int i = 0; i < variants; i++) {
                variantRegions[i] = Core.atlas.find(name + (1 + i));
                //Do not ask. Do not whine. Do not even mention this to me.
                damagedVariantRegions[i] = Core.atlas.find(name + (1 + i) + "-1");
                severelyDamagedVariantRegions[i] = Core.atlas.find(name + (1 + i) + "-2");
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
        if (unit.health < Math.abs(unit.maxHealth * .3f)) {
            // Severe damage state: use "variant-2" textures
            regions = getSeverelyDamagedVariantRegions();
            armorRegion = getSeverelyDamagedArmorRegion();
        } else if (unit.health < Math.abs(unit.maxHealth * .8f)) {
            // Moderate damage state: use "variant-1" textures
            regions = getDamagedVariantRegions();
            armorRegion = getDamagedArmorRegion();
        } else {
            // Normal state: use standard textures
            regions = getVariantRegions();
            armorRegion = getArmorRegion();
        }

        // Draw body and armor
        float bodyLayerOffset = Mathf.randomSeed(unit.id, -0.01f, 0.01f);
        if (regions.length > 0) {
            Draw.z(Layer.legUnit + bodyLayerOffset);
            int index = Mathf.randomSeed(unit.id, 0, regions.length - 1);
            Draw.rect(regions[index], unit.x, unit.y, unit.rotation - 90);

            Draw.z(Layer.legUnit + bodyLayerOffset + 0.00000001f);
            Draw.rect(armorRegion, unit.x, unit.y, unit.rotation - 90);
        }
        drawWeapons(unit);
    }
}
