package aquarion.units.type;

import aquarion.annotations.Annotations;
import aquarion.gen.DropShipc;
import aquarion.world.entities.parts.AquaPart;
import aquarion.world.graphics.AquaFx;
import aquarion.world.graphics.Renderer;
import aquarion.world.graphics.StyledTrail;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.Core;
import arc.graphics.Pixmap;
import arc.graphics.Pixmaps;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureAtlas;
import arc.graphics.g2d.TextureRegion;
import arc.struct.Seq;
import mindustry.entities.Effect;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.MultiPacker;
import mindustry.type.UnitType;
import mindustry.world.meta.Env;

public class AquaUnitType extends UnitType {
    //Drop pod drop effect.
    public Effect dropEffect = AquaFx.droppod;
    /** Null uses the normal trail/team color at both ends. */
    public Color trailTailColor;
    public float trailTailAlpha = 0f;
    public float trailWaveAmplitude = 0f;
    public float trailWaveScale = 12f;
    public float trailWaveCycles = 2f;
    /** Sine-wave phase offset in radians; PI shifts it by half a cycle. */
    public float trailWavePhase = 0f;
    /** Additional trails, placed independently of the unit's default engine trail. */
    public Seq<UnitTrail> trails = new Seq<>();

    public float landDamage = 0f;
    public float landRange = 0f;
    public @Annotations.Load("@-droppod") TextureRegion dropPod;
    public AquaUnitType(String name) {
        super(name);
        this.envDisabled = Env.none;
    }

    /** Adds individual trail mounts without creating mirrored copies. */
    public void setTrails(UnitTrail... mounts){
        trails.addAll(mounts);
    }

    public void setTrailsMirror(UnitTrail... mounts){
        for(UnitTrail mount : mounts){
            trails.add(mount);
            if(mount.x != 0f){
                UnitTrail mirrored = mount.copy();
                mirrored.x = -mount.x;
                mirrored.wavePhase += Mathf.PI;
                mirrored.swayPhase += Mathf.PI;
                trails.add(mirrored);
            }
        }
    }

    @Override
    public void init(){
        for(UnitTrail trail : trails){
            if(!abilities.contains(trail, true)) abilities.add(trail);
        }
        super.init();
    }

    @Override
    public void drawTrail(Unit unit){
        if(!(unit.trail instanceof StyledTrail)){
            unit.trail = new StyledTrail(trailLength);
        }
        StyledTrail trail = (StyledTrail)unit.trail;
        trail.tailColor = trailTailColor;
        trail.tailAlpha = trailTailAlpha;
        trail.waveAmplitude = trailWaveAmplitude;
        trail.waveScale = trailWaveScale;
        trail.waveCycles = trailWaveCycles;
        trail.wavePhase = unit.id * 0.37f + trailWavePhase;
        super.drawTrail(unit);
    }

    @Override
    public void draw(Unit unit){
        AquaPart.aquaParams.set(unit);

        super.draw(unit);
        if(unit instanceof DropShipc drop && !drop.didDrop()){
            applyColor(unit);
            Draw.z(lowAltitude ? Renderer.Layer.flyingUnitLow : Renderer.Layer.flyingUnit);
            Draw.rect(dropPod, unit.x, unit.y, unit.rotation-90);
            drawWeapons(unit);
        }
    }
}
