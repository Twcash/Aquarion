package aquarion.units;

import aquarion.gen.AquaLegsc;
import aquarion.world.entities.AquaLeg;
import aquarion.world.entities.AquaLegConfig;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.Scaled;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.entities.Leg;
import mindustry.entities.abilities.Ability;
import mindustry.entities.part.DrawPart;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;

import static arc.graphics.g2d.Draw.xscl;

public class AquaLegUnitType extends UnitType {
    public AquaLegConfig[] legSequence = new AquaLegConfig[0];
    private static final Vec2 legOffset = new Vec2();

    public AquaLegUnitType(String name) {
        super(name);
    }

    @Override
    public void draw(Unit unit){
        super.draw(unit);
        if(!(unit instanceof AquaLegsc unite)) return;
        applyColor(unit);
        Tmp.c3.set(Draw.getMixColor());

        Leg[] legs = unite.legs();

        float ssize = footRegion.width * footRegion.scl() * 1.5f;
        float rotation = unite.baseRotation();
        float invDrown = 1f - unit.drownTime;

        if(footRegion.found()){
            for(Leg leg : legs){
                Drawf.shadow(leg.base.x, leg.base.y, ssize, invDrown);
            }
        }

        //legs are drawn front first
        for(int j = legs.length - 1; j >= 0; j--){
            int i = (j % 2 == 0 ? j/2 : legs.length - 1 - j/2);
            Leg leg = legs[i];
            boolean flip = i >= legs.length/2f;
            int flips = Mathf.sign(flip);

            Vec2 position = unite.legOffset(legOffset, i).add(unit);

            Tmp.v1.set(leg.base).sub(leg.joint).inv().setLength(legExtension);

            if(footRegion.found() && leg.moving && shadowElevation > 0){
                float scl = shadowElevation * invDrown;
                float elev = Mathf.slope(1f - leg.stage) * scl;
                Draw.color(Pal.shadow);
                Draw.rect(footRegion, leg.base.x + shadowTX * elev, leg.base.y + shadowTY * elev, position.angleTo(leg.base));
                Draw.color();
            }

            Draw.mixcol(Tmp.c3, Tmp.c3.a);

            if(footRegion.found()){
                Draw.rect(footRegion, leg.base.x, leg.base.y, position.angleTo(leg.base));
            }

            if(legBaseUnder){
                Lines.stroke(legBaseRegion.height * legRegion.scl() * flips);
                Lines.line(legBaseRegion, leg.joint.x + Tmp.v1.x, leg.joint.y + Tmp.v1.y, leg.base.x, leg.base.y, false);

                Lines.stroke(legRegion.height * legRegion.scl() * flips);
                Lines.line(legRegion, position.x, position.y, leg.joint.x, leg.joint.y, false);
            }else{
                Lines.stroke(legRegion.height * legRegion.scl() * flips);
                Lines.line(legRegion, position.x, position.y, leg.joint.x, leg.joint.y, false);

                Lines.stroke(legBaseRegion.height * legRegion.scl() * flips);
                Lines.line(legBaseRegion, leg.joint.x + Tmp.v1.x, leg.joint.y + Tmp.v1.y, leg.base.x, leg.base.y, false);
            }

            if(jointRegion.found()){
                Draw.rect(jointRegion, leg.joint.x, leg.joint.y);
            }
        }

        //base joints are drawn after everything else
        if(baseJointRegion.found()){
            for(int j = legs.length - 1; j >= 0; j--){
                //TODO does the index / draw order really matter?
                Vec2 position = unite.legOffset(legOffset, (j % 2 == 0 ? j/2 : legs.length - 1 - j/2)).add(unit);
                Draw.rect(baseJointRegion, position.x, position.y, rotation);
            }
        }

        if(baseRegion.found()){
            Draw.rect(baseRegion, unit.x, unit.y, rotation - 90);
        }

        Draw.reset();
    }






    public Color shieldColor(Unit unit){
        return shieldColor == null ? unit.team.color : shieldColor;
    }

    public void drawMining(Unit unit){
        if(drawMineBeam){
            float focusLen = mineBeamOffset + Mathf.absin(Time.time, 1.1f, 0.5f);
            float px = unit.x + Angles.trnsx(unit.rotation, focusLen);
            float py = unit.y + Angles.trnsy(unit.rotation, focusLen);

            drawMiningBeam(unit, px, py);
        }
    }


}
/*
Your actions make me so miserable,
every aberrant action in contrast to my intentions.
I was always expected to not hate you,
But I never found a reason not to.

Was I never really in control,
or was this always my grave?
I have tried every radical way,
every mental play.
But I could never rid myself of you.

And now I have to spend the rest of my eternity with you.
Are you a consequence of my actions?
Did I create you or were you always there?
If we spoke would we say the same?

I'm losing myself, but are you part of me?
Will you be gone when I falter and decay?
Or will you stay with me till the very end of my day?
My desperation to cope with you has failed.
 */
