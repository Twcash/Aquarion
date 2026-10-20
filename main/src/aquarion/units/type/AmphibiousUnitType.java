package aquarion.units.type;

import aquarion.gen.AmphibiousUnit;
import aquarion.gen.Amphibiousc;
import aquarion.world.AI.AmphibiousAI;
import aquarion.world.AI.AmphibiousCommandAI;
import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import mindustry.ai.ControlPathfinder;
import mindustry.ai.Pathfinder;
import mindustry.gen.Unit;
import mindustry.gen.Sounds;
import mindustry.graphics.Pal;
import mindustry.Vars;

/** All tunable movement settings are inherited from AquaUnitType. */
public class AmphibiousUnitType extends AquaUnitType {
    public TextureRegion landRegion;

    public AmphibiousUnitType(String name){
        super(name);
        constructor = AmphibiousUnit::create;
        aiController = AmphibiousAI::new;
        controller = unit -> !playerControllable || (unit.team.isAI() && !unit.team.rules().rtsAi)
            ? aiController.get() : new AmphibiousCommandAI();
        flying = false;
        canDrown = false;
        shadowElevation = 0.2f;
        stepSound = Sounds.mechStepHeavy;
        flowfieldPathType = Pathfinder.costHover;
        pathCost = ControlPathfinder.costHover;
    }

    @Override
    public void init(){
        if(swimSpeed <= 0f || hopSpeed <= 0f || hopDistance < hopMinDistance || hopMinDistance <= 0f ||
            hopHeight < 0f || hopCooldown < 0f || hopRepathInterval <= 0f || hopStuckTime <= 0f || hopSearchBudget < 1 ||
            hopAirTargetThreshold <= 0f || hopAirTargetThreshold > 1f || hopShadowScale < 0f ||
            hopShadowAlpha < 0f || hopShadowAlpha > 1f || hopCrushDamage < 0f || hopCrushRadius < 0f){
            throw new IllegalArgumentException("Invalid amphibious movement settings for " + name);
        }
        super.init();
        //The vanilla naval preset disables this; hops must follow the requested direction.
        omniMovement = true;
    }

    @Override
    public void load(){
        super.load();
        landRegion = Core.atlas.find(name + "-land", region);
        shadowRegion = region;
    }

    @Override
    public void draw(Unit unit){
        float originalY = unit.y;
        if(unit instanceof Amphibiousc frog) unit.y += frog.hopVisualHeight();
        try{
            super.draw(unit);
        }finally{
            unit.y = originalY;
        }
    }

    @Override
    public void drawShadow(Unit unit){
        float arc = unit instanceof Amphibiousc frog ? frog.hopArc() : 0f;
        float height = unit instanceof Amphibiousc frog ? frog.hopVisualHeight() : 0f;
        float scale = Mathf.lerp(1f, hopShadowScale, arc);
        float opacity = Mathf.lerp(1f, hopShadowAlpha, arc);
        //No minimum shadow offset: grounded units have zero shadow elevation.
        float e = arc * shadowElevationScl * (1f - unit.drownTime);
        float sx = unit.x + shadowTX * e, sy = unit.y - height + shadowTY * e;
        float dest = Vars.world.floorWorld(sx, sy).canShadow ? 1f : 0f;
        unit.shadowAlpha = unit.shadowAlpha < 0f ? dest : Mathf.approachDelta(unit.shadowAlpha, dest, 0.11f);
        Draw.color(Pal.shadow, Pal.shadow.a * unit.shadowAlpha * opacity);
        Draw.rect(shadowRegion, sx, sy, shadowRegion.width * shadowRegion.scl() * Draw.xscl * scale,
            shadowRegion.height * shadowRegion.scl() * Draw.yscl * scale, unit.rotation - 90f);
        Draw.color();
    }

    @Override
    public void drawSoftShadow(Unit unit){
        float arc = unit instanceof Amphibiousc frog ? frog.hopArc() : 0f;
        float height = unit instanceof Amphibiousc frog ? frog.hopVisualHeight() : 0f;
        float scale = Mathf.lerp(1f, hopShadowScale, arc);
        float opacity = Mathf.lerp(1f, hopShadowAlpha, arc);
        float size = Math.max(region.width, region.height) * region.scl() * softShadowScl * 1.6f * scale;
        Draw.color(0f, 0f, 0f, 0.4f * opacity);
        Draw.rect(softShadowRegion, unit.x, unit.y - height, size, size, unit.rotation);
        Draw.color();
    }

    @Override
    public void drawBody(Unit unit){
        applyColor(unit);
        float height = unit instanceof Amphibiousc frog ? frog.hopVisualHeight() : 0f;
        boolean land = !mindustry.Vars.world.floorWorld(unit.x, unit.y - height).isLiquid;
        TextureRegion body = land ? landRegion : region;
        float squash = land && unit instanceof Amphibiousc frog && !frog.hopping() && hopCooldown > 0f
            ? Mathf.clamp(frog.hopCooldownRemaining() / hopCooldown) * Mathf.clamp(hopSquash, 0f, 0.8f) : 0f;
        Draw.rect(body, unit.x, unit.y, body.width * body.scl() * Draw.xscl * (1f + squash),
            body.height * body.scl() * Draw.yscl * (1f - squash), unit.rotation - 90f);
        Draw.reset();
    }
}
