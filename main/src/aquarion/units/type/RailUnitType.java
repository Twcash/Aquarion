package aquarion.units.type;

import aquarion.world.AI.RailCommandAI;
import aquarion.world.blocks.distribution.RailPoint;
import aquarion.world.graphics.Renderer;
import aquarion.content.blocks.DistributionBlocks;
import arc.Core;
import arc.graphics.g2d.Draw;
import mindustry.graphics.Pal;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.ai.UnitCommand;
import mindustry.ai.types.CommandAI;
import mindustry.gen.Unit;
import mindustry.gen.UnitEntity;
import mindustry.io.TypeIO;
import mindustry.type.UnitType;
import mindustry.world.meta.Env;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class RailUnitType extends AquaUnitType{
    /** Car length in world units; zero derives it from the loaded sprite. */
    public float railLength;
    /** Minimum visual gap between neighboring cars, in world units. */
    public float railGap = 1f;
    public RailUnitType(String name){
        super(name);
        constructor = UnitEntity::create;
        aiController = RailCommandAI::new;
        controller = unit -> new RailCommandAI();
        commands.add(UnitCommand.moveCommand);
        defaultCommand = UnitCommand.moveCommand;
        if(mindustry.ai.UnitStance.patrol != null) stances.addUnique(mindustry.ai.UnitStance.patrol);
        if(mindustry.ai.UnitStance.pursueTarget != null) stances.addUnique(mindustry.ai.UnitStance.pursueTarget);
        playerControllable = false;
        logicControllable = false;
        physics = true;
        canAttack = false;
        canDrown = false;
        hovering = true;
        groundLayer = Renderer.Layer.blockOver  + 4f;
        allowedInPayloads = false;
        drawCell = false;
        shadowElevation = 1f;
        speed = 1.5f;
        rotateSpeed = 6f;
        drag = 1f;
        accel = 1f;
        hitSize = 8f;
        itemCapacity = 80;
        health = 200f;
        alwaysUnlocked = true;
        envEnabled = Env.any;
        envDisabled = Env.none;
    }
    @Override
    public void update(Unit unit){
        if(!(unit.controller() instanceof RailCommandAI)){
            RailCommandAI restored = new RailCommandAI();

            if(unit.controller() instanceof CommandAI previous){
                ByteArrayOutputStream data = new ByteArrayOutputStream();

                TypeIO.writeController(
                        new Writes(new DataOutputStream(data)),
                        previous
                );

                TypeIO.readController(
                        new Reads(new DataInputStream(
                                new ByteArrayInputStream(data.toByteArray())
                        )),
                        restored
                );
            }

            unit.controller(restored);
            restored.afterRead(unit);
            unit.vel.setZero();
        }

        super.update(unit);
    }

    @Override
    public void drawShadow(Unit unit){
        RailPoint rail = unit.controller() instanceof RailCommandAI ai ? ai.supportingRail() : null;
        if(rail == null && DistributionBlocks.railPoint instanceof RailPoint fallback) rail = fallback;
        float previousZ = Draw.z();
        Draw.z(Renderer.Layer.shadow);
        Draw.color(Pal.shadow);
        Draw.rect(shadowRegion, unit.x + (rail == null ? -10f : rail.railShadowX),
            unit.y + (rail == null ? -10f : rail.railShadowY), unit.rotation - 90f);
        Draw.reset();
        Draw.color();
        Draw.z(previousZ);
    }
}
