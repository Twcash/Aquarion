package aquarion.units.type;

import aquarion.world.graphics.StyledTrail;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;

/** A configurable trail mount. Mindustry copies its hidden ability state for each unit. */
public class UnitTrail extends Ability{
    /** Local offsets, using the same coordinate system as UnitEngine. */
    public float x, y;
    public float width = 1f;
    public int length = 20;
    /** Null inherits the unit's normal trail/team color. */
    public Color color, tailColor;
    /** Negative values inherit the corresponding AquaUnitType setting. */
    public float tailAlpha = -1f, waveAmplitude = -1f, waveScale = -1f, waveCycles = -1f;
    /** Additional ribbon-wave phase in radians, on top of AquaUnitType.trailWavePhase. */
    public float wavePhase;
    /** Sideways motion of the trail's origin, in the unit's local coordinates. */
    public float swayAmplitude = 0f, swayScale = 12f;
    /** Origin-sway phase in radians; PI gives opposite motion to a zero-phase mount. */
    public float swayPhase = 0f;
    /** Why would you use this, ever??? */
    public float swayYAmplitude = 0f;
    /** Disable for ground trails that should remain visible at zero elevation. */
    public boolean useElevation = true;
    public float pulseAmplitude = 0f, pulseScale = 10f;
    public Effect trailEffect = Fx.none;
    public float effectInterval = 4f;

    public transient StyledTrail trail;
    private transient float effectTimer;

    public UnitTrail(){
        display = false;
    }

    public UnitTrail(float x, float y, float width, int length){
        this();
        this.x = x;
        this.y = y;
        this.width = width;
        this.length = length;
    }

    public float worldX(Unit unit){
        return unit.x + Angles.trnsx(unit.rotation - 90f, localX(Time.time), localY(Time.time));
    }

    public float worldY(Unit unit){
        return unit.y + Angles.trnsy(unit.rotation - 90f, localX(Time.time), localY(Time.time));
    }

    public float localX(float time){
        return x + (swayScale <= 0f ? 0f : Mathf.sin(time / swayScale + swayPhase) * swayAmplitude);
    }

    public float localY(float time){
        return y + (swayScale <= 0f ? 0f : Mathf.cos(time / swayScale + swayPhase) * swayYAmplitude);
    }

    public Color headColor(Unit unit){
        return color != null ? color : unit.type.trailColor != null ? unit.type.trailColor : unit.team.color;
    }

    public float widthScale(Unit unit){
        float elevation = useElevation && unit.type.useEngineElevation ? unit.elevation : 1f;
        float pulse = pulseScale <= 0f ? 0f : Mathf.sin(Time.time / pulseScale + wavePhase) * pulseAmplitude;
        return Math.max(0f, elevation * (1f + pulse));
    }

    public void configureTrail(Unit unit){
        AquaUnitType type = (AquaUnitType)unit.type;
        if(trail == null) trail = new StyledTrail(Math.max(1, length));
        trail.length = Math.max(1, length);
        trail.tailColor = tailColor == null ? type.trailTailColor : tailColor;
        trail.tailAlpha = tailAlpha < 0f ? type.trailTailAlpha : tailAlpha;
        trail.waveAmplitude = waveAmplitude < 0f ? type.trailWaveAmplitude : waveAmplitude;
        trail.waveScale = waveScale < 0f ? type.trailWaveScale : waveScale;
        trail.waveCycles = waveCycles < 0f ? type.trailWaveCycles : waveCycles;
        trail.wavePhase = unit.id * 0.37f + type.trailWavePhase + wavePhase;
    }

    @Override
    public void update(Unit unit){
        if(Vars.headless) return;
        configureTrail(unit);
        float scale = widthScale(unit);
        float px = worldX(unit), py = worldY(unit);
        if(scale <= 0.0001f || length <= 0){
            trail.shorten();
            return;
        }
        trail.update(px, py, scale);
        if(trailEffect != Fx.none && effectInterval > 0f && unit.moving()){
            effectTimer += Time.delta;
            if(effectTimer >= effectInterval){
                effectTimer %= effectInterval;
                trailEffect.at(px, py, unit.rotation, headColor(unit));
            }
        }else{
            effectTimer = 0f;
        }
    }

    @Override
    public void draw(Unit unit){
        if(trail == null || length <= 0 || unit.inFogTo(Vars.player.team())) return;
        float previousZ = Draw.z();
        float layer = unit.type.engineLayer > 0f ? unit.type.engineLayer
            : (unit.elevation > 0.5f || (unit.type.flying && unit.dead) ? unit.type.flyingLayer : unit.type.groundLayer) - 0.001f;
        Draw.z(layer);
        trail.draw(headColor(unit), width);
        Draw.z(previousZ);
    }

    @Override
    public void death(Unit unit){
        if(!Vars.headless && trail != null && trail.size() > 0){
            Fx.trailFade.at(worldX(unit), worldY(unit), width, headColor(unit), trail.copy());
            trail.clear();
        }
    }

    @Override
    public UnitTrail copy(){
        UnitTrail out = (UnitTrail)super.copy();
        out.trail = null;
        out.effectTimer = 0f;
        return out;
    }
}
