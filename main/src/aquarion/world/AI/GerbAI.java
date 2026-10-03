package aquarion.world.AI;

import aquarion.content.AquaSounds;
import aquarion.gen.Gerbc;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Tmp;
import mindustry.ai.Pathfinder;
import mindustry.entities.Units;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.world.blocks.defense.turrets.Turret;

import static mindustry.Vars.tilesize;

/**
 * Base class for every Gerb AI. Runs the shared decision loop on every update:
 * periodically evaluates the situation and, if a different role fits better,
 * swaps the unit's controller to the matching behavior AI. The per-unit state
 * (personality, role, damage tracking) lives in {@link Gerbc} so it survives swaps.
 *
 * <p>Behavior AIs only implement {@link #updateMovement()}; the brain logic is
 * inherited from here. Player-commanded units are never hijacked.</p>
 */
public abstract class GerbAI extends AquaAI {
    /** Minimum ticks between controller swaps (anti-flap). */
    protected static final float swapInterval = 45f;
    /** Health fraction below which a Gerb hides for cover. */
    protected static final float coverThreshold = 0.35f;
    /** Range in which a Gerb will run to reinforce a wounded ally or pressured heavy. */
    protected static final float reinforceRange = tilesize * 40f;
    /** Turret count ahead that counts as a "wall" worth flanking. */
    protected static final int turretWallCount = 4;
    /** Squad formation range: units close back up on allies beyond this. */
    protected static final float groupRange = tilesize * 20f;

    @Override
    public void updateUnit(){
        if(unit == null || !unit.isValid()) return;
        if(maybeSwap()) return;
        super.updateUnit();
    }

    /** Periodically re-decides the role and swaps controllers to match. Returns whether it swapped. */
    protected boolean maybeSwap(){
        if(!(unit instanceof Gerbc g)) return false;
        //never hijack a player-commanded unit
        if(!(unit.controller() instanceof GerbAI)) return false;
        if(g.swapCooldown() > 0f) return false;
        if(!aiTimer.get(tScan, 20f)) return false;

        GerbAI next = decide(g);
        if(next == null || next.getClass() == unit.controller().getClass()) return false;

        g.swapCooldown(swapInterval);
        unit.controller(next);
        return true;
    }

    /** Picks the best role for the current situation. */
    protected GerbAI decide(Gerbc g){
        //1. under fire and hurt (or skittish) - take cover. Reactionary: once the
        //   "being shot" stimulus fades this role is dropped and the unit pushes on.
        if(g.justHit() && (g.lowHealth(coverThreshold) || g.personality() < 0.4f)){
            setRole(g, GerbRole.COVER);
            return new GerbCoverAI();
        }
        //2. a weaker/little ally is being shot up - the bigger units body-shield it
        if(!g.lowHealth(coverThreshold) && findReinforceTarget(reinforceRange) != null){
            setRole(g, GerbRole.REINFORCE);
            return new GerbReinforceAI();
        }
        //3. a wall of turrets blocks the way - go around it
        if(heavyDefenseAhead(turretWallCount)){
            setRole(g, GerbRole.FLANK);
            return new GerbFlankAI();
        }
        //4. default: advance and fight
        setRole(g, GerbRole.ASSAULT);
        return new GerbAssaultAI();
    }

    /** Whether this unit is currently under fire (took a hit recently). */
    protected boolean underFire(){
        return unit instanceof Gerbc g && g.justHit();
    }

    /**
     * Steadily pushes toward the enemy core. Used as a floor by every behavior so
     * units never idle in a corner away from the fight: unless they are actively
     * being shot at, they keep closing on the core. Also keeps the squad in
     * formation by regrouping first if the unit has drifted off. Returns whether it moved.
     */
    protected boolean approachCore(){
        Building core = nearestEnemyCore();
        if(core == null || !core.isValid()) return false;
        //being shot at - stop and let the current behavior react instead
        if(underFire()) return false;
        //grouping: close up with the squad before charging ahead
        if(groupUp()) return true;
        //flow-field march that stops on the core's tile, instead of pathing onto
        //the core building and getting wedged against it
        try{
            pathfind(Pathfinder.fieldCore, true, isStuck());
        }catch(Throwable ignored){
            //no core field on this map - close in to weapon range instead of beelining into it
            pathMoveTo(Tmp.v1.set(core), unit.range() * 0.5f);
        }
        faceMovement();
        return true;
    }

    /** Whether this unit has drifted out of squad formation and should close back up. */
    protected boolean groupUp(){
        if(underFire()) return false;
        //rally around the nearest stronger ally (a heavy) if one is near the front
        Unit heavy = findStrongerAlly(groupRange * 2f);
        Unit mate = heavy != null ? heavy : nearestAlly(groupRange * 2f);
        if(mate == null) return false;
        //already in formation
        if(unit.within(mate, groupRange)) return false;
        //only close up if the mate is roughly ahead of us (toward the core);
        //never march backward to regroup, or fast units oscillate around one spot
        Building core = nearestEnemyCore();
        if(core != null && Math.abs(Angles.angleDist(unit.angleTo(core), unit.angleTo(mate))) > 90f){
            return false;
        }
        pathMoveTo(Tmp.v1.set(mate), groupRange * 0.5f);
        faceMovement();
        return true;
    }

    /** Whether an enemy unit or turret is pressuring the given position. */
    protected boolean underThreat(float x, float y){
        return Units.closestEnemy(unit.team, x, y, 80f, e -> true) != null
            || Units.findEnemyTile(unit.team, x, y, 100f, b -> true) != null;
    }

    /**
     * Nearest ally worth shielding within {@code range}, or null. Body-shielding is
     * done by the big units: this picks a weaker/little ally that is under fire, so
     * the larger Gerb moves between it and the threat and soaks the shots. A wounded
     * ally of roughly our own size is also shieldable. Smaller units never shield
     * larger ones - they hide behind them instead (see {@link GerbCoverAI}).
     */
    protected Unit findReinforceTarget(float range){
        Unit best = null;
        float bestScore = Float.MAX_VALUE;
        for(Unit u : Groups.unit){
            if(u.team != unit.team || u == unit || u.dead() || !u.isValid()) continue;
            if(!u.within(unit, range)) continue;
            if(!underThreat(u.x, u.y)) continue;
            boolean wounded = u.healthf() < 0.5f;
            //a weaker/little ally being shot up is the main target
            boolean weaker = u.maxHealth < unit.maxHealth;
            //a wounded ally of roughly our size can also be shielded
            boolean woundedShieldable = wounded && u.maxHealth <= unit.maxHealth * 1.25f;
            if(!weaker && !woundedShieldable) continue;
            float d = unit.dst2(u);
            //badly wounded little allies are the highest priority
            float score = d * (wounded ? 1f : 4f);
            if(score < bestScore){
                bestScore = score;
                best = u;
            }
        }
        return best;
    }

    /** Whether a turret sits near the given world point. */
    protected boolean turretNear(float x, float y, float range){
        return Units.findEnemyTile(unit.team, x, y, range, b -> b.block instanceof Turret) != null;
    }

    /** Stores the role, resetting flank state and announcing changes with a sound. */
    protected void setRole(Gerbc g, GerbRole role){
        if(g.role() != role){
            if(role != GerbRole.FLANK) g.flankAngle(0f);
            playTacticSound(role);
        }
        g.role(role);
    }

    protected void playTacticSound(GerbRole role){
        switch(role){
            case ASSAULT -> AquaSounds.advance.at(unit.x, unit.y, 1 - Mathf.random(0, .3f), .4f - Mathf.random(0, .3f));
            case REINFORCE -> AquaSounds.rally.at(unit.x, unit.y, 1 - Mathf.random(0, .3f), .4f - Mathf.random(0, .3f));
            case COVER -> AquaSounds.retreat.at(unit.x, unit.y, 1 - Mathf.random(0, .3f), .4f - Mathf.random(0, .3f));
            case FLANK -> AquaSounds.hold.at(unit.x, unit.y, 1 - Mathf.random(0, .3f), .4f - Mathf.random(0, .3f));
            case HOLD -> AquaSounds.hold.at(unit.x, unit.y, 1 - Mathf.random(0, .3f), .4f - Mathf.random(0, .3f));
        }
    }
}