package aquarion.units.type;

import aquarion.gen.BurrowUnit;
import aquarion.gen.Burrowc;
import aquarion.world.AI.BurrowAI;
import aquarion.world.AI.BurrowCommandAI;
import arc.Core;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.graphics.g2d.TextureRegion;
import arc.struct.Seq;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.units.AIController;
import mindustry.gen.Unit;
import mindustry.gen.Groups;
import mindustry.graphics.Pal;

public class BurrowUnitType extends AquaUnitType{
    public boolean bodySegment;
    public BurrowUnitType bodyType;
    public String bodySprite;
    public int bodySegments = 7;
    public float bodyDamageMultiplier = 0.2f;
    public float burrowInterval = 120f, breachRange = 104f, breachDistance = 72f;
    public float orbitRadius = 48f, minPounceDistance = 20f;
    public float surfaceTime = 20f, riseTime = 28f, diveTime = 22f;
    public float breachHeight = 24f, slamDamage = 160f, slamRadius = 28f, slamShake = 2f;
    /** Height is conveyed by sprite scale, without a screen-space vertical offset. */
    public float breachHeightScl = 0.3f;
    public float burialPushDistance = 1f;
    public Effect emergeEffect = Fx.unitLandSmall, diveEffect = Fx.unitLandSmall;
    public Effect slamEffect = Fx.unitLand;
    private boolean drawingBodyPass;

    public BurrowUnitType(String name, boolean bodySegment){
        super(name);
        this.bodySegment = bodySegment;
        constructor = BurrowUnit::create;
        flying = false;
        canDrown = false;
        physics = false;
        drawCell = false;
        drawSoftShadow = false;
        //Keep the raw head/body textures untouched. Vanilla otherwise bakes the
        //outline into region itself, which defeats any separate outline pass.
        alwaysCreateOutline = true;
        segmentSpacing = 3f;
        shadowElevation = 0.2f;
        allowedInPayloads = false;
        faceTarget = false;
        aiController = bodySegment ? () -> new AIController(){ @Override public void updateUnit(){} } : BurrowAI::new;
        controller = unit -> bodySegment || !playerControllable || (unit.team.isAI() && !unit.team.rules().rtsAi)
            ? aiController.get() : new BurrowCommandAI();
        if(bodySegment){
            internal = hidden = true;
            useUnitCap = playerControllable = logicControllable = hoverable = drawMinimap = false;
            createWreck = false;
        }
    }

    public float headDuration(){ return surfaceTime + riseTime + diveTime; }
    public float cycleDuration(float arcLength){
        return headDuration() + bodySegments * segmentSpacing / Math.max(arcLength, 0.001f) * (riseTime + diveTime);
    }

    @Override
    public void init(){
        if(bodySegments < 0 || bodySegments > 32 || segmentSpacing <= 0f || bodyDamageMultiplier < 0f ||
            burrowInterval <= 0f || breachRange <= 0f || breachDistance <= 0f || orbitRadius <= 0f ||
            minPounceDistance < 8f || minPounceDistance > breachDistance || surfaceTime <= 0f ||
            riseTime <= 0f || diveTime <= 0f || breachHeight < 0f || breachHeightScl < 0f || burialPushDistance < 0f || slamDamage < 0f || slamRadius < 0f){
            throw new IllegalArgumentException("Invalid burrow settings for " + name);
        }
        super.init();
        if(!bodySegment) clipSize = Math.max(clipSize, (bodySegments * segmentSpacing + hitSize * 2f) * 2f);
    }

    @Override
    public void load(){
        super.load();
        String sprite = bodySegment ? bodySprite : name + "-head";
        region = Core.atlas.find(sprite, region);
        outlineRegion = Core.atlas.find(sprite + "-outline", outlineRegion);
        shadowRegion = region;
    }

    @Override
    public boolean hittable(Unit unit){ return unit instanceof Burrowc worm && worm.burrowExposure() > 0.1f && super.hittable(unit); }

    @Override
    public void getRegionsToOutline(Seq<TextureRegion> out){
        super.getRegionsToOutline(out);
        if(!bodySegment){
            out.add(Core.atlas.find(name), Core.atlas.find(name + "-head"));
        }
    }

    @Override
    public boolean targetable(Unit unit, mindustry.game.Team team){ return hittable(unit) && super.targetable(unit, team); }

    @Override
    public void draw(Unit unit){
        //Only the head submits the worm. Body entities remain independent hitboxes.
        //The head must still draw the tail after its own sprite has gone underground.
        if(bodySegment || !(unit instanceof BurrowUnit head)) return;
        if(mindustry.Vars.net.client()){
            head.burrowBodies.removeAll(body -> body == null || !body.isAdded());
            if(head.burrowBodies.size != bodySegments){
                head.burrowBodies.clear();
                for(Unit other : Groups.unit){
                    if(other instanceof BurrowUnit body && body.burrowOwnerId == head.id && body.isAdded()) head.burrowBodies.add(body);
                }
                head.burrowBodies.sort(body -> body.burrowSegmentIndex);
            }
        }
        float z = Draw.z(), sx = Draw.xscl, sy = Draw.yscl;
        try{
            for(int pass = 0; pass < 3; pass++){
                //Tail first, head last. Crucially, finish all outlines before any body.
                for(int i = head.burrowBodies.size; i >= 0; i--){
                    BurrowUnit part = i == 0 ? head : head.burrowBodies.get(i - 1);
                    if(part == null || !part.isAdded() || part.burrowExposure() <= 0f ||
                        part.inFogTo(mindustry.Vars.player.team())) continue;
                    ((BurrowUnitType)part.type).drawWormPart(part, pass);
                }
            }
        }finally{
            Draw.reset();
            Draw.scl(sx, sy);
            Draw.z(z);
        }
    }

    private void drawWormPart(BurrowUnit part, int pass){
        float rotation = part.rotation;
        part.rotation = part.burrowDrawRotation();
        Draw.scl(1f, 1f);
        BurrowUnit head = part.burrowHead();
        float layer = head == null ? groundLayer : head.type.groundLayer;
        try{
            if(pass == 0){ Draw.z(layer - 1f); drawShadow(part); }
            else if(pass == 1){ Draw.z(layer - 0.01f); drawOutline(part); }
            else{
                drawingBodyPass = true;
                super.draw(part);
            }
        }finally{
            drawingBodyPass = false;
            part.rotation = rotation;
        }
    }

    public float burrowScale(Unit unit){
        if(!(unit instanceof Burrowc worm) || worm.burrowHead() == null) return 1f;
        BurrowUnitType cfg = (BurrowUnitType)worm.burrowHead().type;
        return 1f + Mathf.clamp(worm.burrowHeight() / Math.max(cfg.breachHeight, 0.001f)) * cfg.breachHeightScl;
    }

    @Override
    public void drawBody(Unit unit){
        float z = Draw.z();
        BurrowUnit head = unit instanceof Burrowc worm ? worm.burrowHead() : null;
        float layer = head == null ? groundLayer : head.type.groundLayer;
        int index = unit instanceof BurrowUnit part ? part.burrowSegmentIndex : 0;
        Draw.z(layer + 0.02f - Math.min(index, 32) * 0.0001f);
        applyColor(unit);
        float scl = burrowScale(unit);
        Draw.rect(region, unit.x, unit.y, region.width * region.scl() * scl * Draw.xscl,
            region.height * region.scl() * scl * Draw.yscl, unit.rotation - 90f);
        Draw.reset();
        Draw.z(z);
    }

    @Override
    public void drawOutline(Unit unit){
        if(drawingBodyPass || !outlineRegion.found()) return;
        float z = Draw.z();
        BurrowUnit head = unit instanceof Burrowc worm ? worm.burrowHead() : null;
        BurrowUnitType cfg = head == null ? this : (BurrowUnitType)head.type;
        float outlineLayer = Math.min(cfg.groundLayer, cfg.bodyType == null ? cfg.groundLayer : cfg.bodyType.groundLayer) - 0.01f;
        //All outlines precede every body, independent of segment entity draw order.
        Draw.z(Math.min(z, outlineLayer));
        applyColor(unit);
        applyOutlineColor(unit);
        float scl = burrowScale(unit);
        Draw.rect(outlineRegion, unit.x, unit.y, outlineRegion.width * outlineRegion.scl() * scl * Draw.xscl,
            outlineRegion.height * outlineRegion.scl() * scl * Draw.yscl, unit.rotation - 90f);
        Draw.reset();
        Draw.z(z);
    }

    @Override
    public void applyColor(Unit unit){
        super.applyColor(unit);
        if(unit instanceof Burrowc worm) Draw.alpha(worm.burrowExposure());
    }

    @Override
    public void drawShadow(Unit unit){
        if(drawingBodyPass || !(unit instanceof Burrowc worm)) return;
        float maxHeight = worm.burrowHead() == null ? breachHeight : ((BurrowUnitType)worm.burrowHead().type).breachHeight;
        float arc = Mathf.clamp(worm.burrowHeight() / Math.max(maxHeight, 0.001f));
        Draw.color(Pal.shadow, Pal.shadow.a * worm.burrowExposure() * (1f - arc * 0.45f));
        float scale = 1f - arc * 0.25f;
        Draw.rect(region, unit.x, unit.y,
            region.width * region.scl() * scale, region.height * region.scl() * scale, unit.rotation - 90f);
        Draw.reset();
    }

}
