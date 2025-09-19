package aquarion.type;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.Scaled;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.abilities.Ability;
import mindustry.entities.part.DrawPart;
import mindustry.entities.units.WeaponMount;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;

import static mindustry.Vars.player;
import static mindustry.Vars.tilesize;

public class MechanicalUnitType extends UnitType {
    private static final Vec2 legOffset = new Vec2();
    public TextureRegion undamagedRegion;
    public TextureRegion damagedRegion;
    public TextureRegion veryDamagedRegion;
    public TextureRegion mineLaserRegion, mineLaserEndRegion;
    public float damageThreshold = 0.95f; // these are percentage based btw%
    public float severeDamageThreshold = 0.3f;
    public boolean mech;

    public MechanicalUnitType(String name) {
        super(name);
        outlineColor = Color.valueOf("232826");
        drawCell = false;
    }
    @Override
    public void load() {
        super.load();
        mineLaserRegion = Core.atlas.find("minelaser");
        mineLaserEndRegion = Core.atlas.find("minelaser-end");
        undamagedRegion = Core.atlas.find(name);
        damagedRegion = Core.atlas.find(name + "-damage" + 1);
        veryDamagedRegion = Core.atlas.find(name + "-damage" + 2);
    }
    public TextureRegion getArmorRegion() {
        return undamagedRegion == null ? (undamagedRegion = new TextureRegion()) : undamagedRegion;
    }

    public TextureRegion getDamagedArmorRegion() {
        return damagedRegion == null ? (damagedRegion = new TextureRegion()) : damagedRegion;
    }

    public TextureRegion getSeverelyDamagedArmorRegion() {
        return veryDamagedRegion == null ? (veryDamagedRegion = new TextureRegion()) : veryDamagedRegion;
    }
    @Override
    public void drawBody(Unit unit) {
        applyColor(unit);
        TextureRegion armorRegion;

        // Determine which set of textures to use based on health thresholds
        if (unit.health < unit.maxHealth * severeDamageThreshold) {
            armorRegion = getSeverelyDamagedArmorRegion();
        } else if (unit.health < unit.maxHealth * damageThreshold) {
            armorRegion = getDamagedArmorRegion();
        } else {
            armorRegion = getArmorRegion();
        }
        Draw.rect(armorRegion, unit.x, unit.y, unit.rotation - 90);
    }
    @Override
    public void draw(Unit unit){
        if(unit.inFogTo(player.team())) return;

        unit.drawBuilding();

        drawMining(unit);

        boolean isPayload = !unit.isAdded();

        Mechc mech = unit instanceof Mechc ? (Mechc)unit : null;
        float z = isPayload ? Draw.z() : unit.elevation > 0.5f ? (lowAltitude ? Layer.flyingUnitLow : Layer.flyingUnit) : groundLayer + Mathf.clamp(hitSize / 4000f, 0, 0.01f);


        if(!isPayload && (unit.isFlying() || shadowElevation > 0)){
            Draw.z(Math.min(Layer.darkness, z - 1f));
            drawShadow(unit);
        }

        Draw.z(z - 0.02f);

        if(mech != null){
            drawMech(mech);

            //side
            legOffset.trns(mech.baseRotation(), 0f, Mathf.lerp(Mathf.sin(mech.walkExtend(true), 2f/Mathf.PI, 1) * mechSideSway, 0f, unit.elevation));

            //front
            legOffset.add(Tmp.v1.trns(mech.baseRotation() + 90, 0f, Mathf.lerp(Mathf.sin(mech.walkExtend(true), 1f/Mathf.PI, 1) * mechFrontSway, 0f, unit.elevation)));

            unit.trns(legOffset.x, legOffset.y);
        }

        if(unit instanceof Tankc){
            drawTank((Unit & Tankc)unit);
        }

        if(unit instanceof Legsc && !isPayload){
            drawLegs((Unit & Legsc)unit);
        }

        Draw.z(Math.min(z - 0.01f, Layer.bullet - 1f));

        if(unit instanceof Payloadc){
            drawPayload((Unit & Payloadc)unit);
        }

        drawSoftShadow(unit);

        Draw.z(z);

        if(unit instanceof Crawlc c){
            drawCrawl(c);
        }

        if(drawBody) drawOutline(unit);
        drawWeaponOutlines(unit);
        if(engineLayer > 0) Draw.z(engineLayer);
        if(trailLength > 0 && !naval && (unit.isFlying() || !useEngineElevation)){
            drawTrail(unit);
        }
        if(engines.size > 0) drawEngines(unit);
        Draw.z(z);
        if(drawBody) drawBody(unit);
        if(drawCell) drawCell(unit);
        drawWeapons(unit);
        if(drawItems) drawItems(unit);
        if(!isPayload){
            drawLight(unit);
        }

        if(unit.shieldAlpha > 0 && drawShields){
            drawShield(unit);
        }

        //TODO how/where do I draw under?
        if(parts.size > 0){
            for(int i = 0; i < parts.size; i++){
                var part = parts.get(i);

                WeaponMount mount = unit.mounts.length > part.weaponIndex ? unit.mounts[part.weaponIndex] : null;
                if(mount != null){
                    DrawPart.params.set(mount.warmup, mount.reload / mount.weapon.reload, mount.smoothReload, mount.heat, mount.recoil, mount.charge, unit.x, unit.y, unit.rotation);
                }else{
                    DrawPart.params.set(0f, 0f, 0f, 0f, 0f, 0f, unit.x, unit.y, unit.rotation);
                }

                if(unit instanceof Scaled s){
                    DrawPart.params.life = s.fin();
                }

                applyColor(unit);
                part.draw(DrawPart.params);
            }
        }

        if(!isPayload){
            for(Ability a : unit.abilities){
                Draw.reset();
                a.draw(unit);
            }
        }

        if(mech != null){
            unit.trns(-legOffset.x, -legOffset.y);
        }

        Draw.reset();
    }

    public void drawMining(Unit unit){
        if(!unit.mining()) return;
        float focusLen = unit.hitSize / 2f + Mathf.absin(Time.time, 1.1f, 0.5f);
        float swingScl = 12f, swingMag = tilesize / 8f;
        float flashScl = 0.3f;

        float px = unit.x + Angles.trnsx(unit.rotation, focusLen);
        float py = unit.y + Angles.trnsy(unit.rotation, focusLen);

        float ex = unit.mineTile.worldx() + Mathf.sin(Time.time + 48, swingScl, swingMag);
        float ey = unit.mineTile.worldy() + Mathf.sin(Time.time + 48, swingScl + 2f, swingMag);

        Draw.z(Layer.flyingUnit + 0.1f);

        Draw.color(Color.lightGray, Color.white, 1f - flashScl + Mathf.absin(Time.time, 0.5f, flashScl));

        Drawf.laser(mineLaserRegion, mineLaserEndRegion, px, py, ex, ey, 0.75f);

        if(unit.isLocal()){
            Lines.stroke(1f, Pal.accent);
            Lines.poly(unit.mineTile.worldx(), unit.mineTile.worldy(), 4, tilesize / 2f * Mathf.sqrt2, Time.time);
        }

        Draw.color();
    }
    @Override
    public void killed(Unit unit) {
        super.killed(unit);

        // Check if the unit should transform into the derelict type
        if (unit.health <= 0) {
            unit.team = Team.derelict;
        }
    }
}