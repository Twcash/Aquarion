package aquarion.type;

import aquarion.gen.Derelictc;
import aquarion.world.AquaTeams;
import arc.func.Cons;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Position;
import arc.math.geom.QuadTree;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.scene.ui.layout.Table;
import arc.struct.Bits;
import arc.struct.Queue;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.ai.types.CommandAI;
import mindustry.async.PhysicsProcess;
import mindustry.content.UnitTypes;
import mindustry.ctype.Content;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.EntityCollisions;
import mindustry.entities.abilities.Ability;
import mindustry.entities.units.BuildPlan;
import mindustry.entities.units.UnitController;
import mindustry.entities.units.WeaponMount;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Trail;
import mindustry.logic.LAccess;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.StatusEffect;
import mindustry.type.UnitType;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.payloads.Constructor;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.meta.Env;

import java.nio.FloatBuffer;

import static mindustry.Vars.state;
import static mindustry.gen.Nulls.unit;

public class DerelictUnit extends UnitType {

    public boolean preventDeath = false;  // Flag to control whether death is allowed
    public float timeUntilDerelict = 100;  // Timer before becoming derelict

    public DerelictUnit(String name) {
        super(name);
        speed = 0;
        rotateSpeed = 0;
        hidden = true;
        health = 210;
        drag = 0.99f;
        envEnabled |= Env.terrestrial | Env.underwater;
        envDisabled = Env.none;
        accel = 0;
        isEnemy = false;
        drawCell = false;
        outlineColor = Color.valueOf("141615");
        flying = false;
        logicControllable = false;
        playerControllable = false;
        targetable = false;
        hittable = true;

    }

    @Override
    public void update(Unit unit) {
        // Countdown for derelict timer
        timeUntilDerelict -= Time.delta;

        // Custom death prevention logic
        if (timeUntilDerelict <= 0) {
            unit.team(AquaTeams.wrecks);
        }
    }
    @Override
    public void killed(Unit unit) {
        // Override killed method to prevent automatic death unless allowed
        if (!preventDeath) {
            super.killed(unit);  // Call the default killed logic
        }
    }
}