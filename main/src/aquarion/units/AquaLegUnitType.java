package aquarion.units;

import aquarion.gen.AquaLegsc;
import aquarion.world.entities.AquaLeg;
import aquarion.world.entities.AquaLegConfig;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
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

    public AquaLegUnitType(String name) {
        super(name);
    }
    @Override
    public void update(Unit unit){
//        if(this instanceof AquaLegsc){
//            ((AquaLegsc)this).update();
//        }
    }
    @Override
    public void draw(Unit unit) {
        super.draw(unit);

        if (!(unit instanceof AquaLegsc aquaUnit)) return;
        if (!(unit.type instanceof AquaLegUnitType aquaType)) return;

        for (int i = 0; i < aquaType.legSequence.length; i++) {
            AquaLegConfig cfg = aquaType.legSequence[i];
            AquaLeg[] lege = aquaUnit.legs();

            if (lege == null || i >= lege.length) continue; // <-- skip if leg doesn't exist

            AquaLeg leg = lege[i];
            Vec2[] joints = leg.joints;

            if (joints == null || joints.length == 0) continue; // <-- skip if no joints

            // Draw each segment between joints
            for (int j = 0; j < joints.length - 1; j++) {
                Lines.stroke(aquaType.legRegion.height * aquaType.legRegion.scl());
                Lines.line(aquaType.legRegion, joints[j].x, joints[j].y, joints[j + 1].x, joints[j + 1].y, false);
            }

            // Draw foot (last joint)
            Vec2 foot = leg.base;
            if (aquaType.footRegion.found())
                Draw.rect(aquaType.footRegion, foot.x, foot.y);
        }

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
