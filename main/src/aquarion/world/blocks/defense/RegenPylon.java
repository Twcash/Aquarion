package aquarion.world.blocks.defense;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.struct.EnumSet;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.Lightning;
import mindustry.entities.Units;
import mindustry.entities.effect.WrapEffect;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.logic.LAccess;
import mindustry.logic.Ranged;
import mindustry.world.blocks.defense.MendProjector;
import mindustry.world.consumers.Consume;
import mindustry.world.consumers.ConsumeItems;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

public class RegenPylon extends MendProjector {
    public final int timerUse;
    public Color baseColor;
    public Color phaseColor;
    public float reload;
    public float range;
    public float healPercent;
    public float phaseBoost;
    public float phaseRangeBoost;

    public float lightningDamage = 12f;
    public float lightningReload = 60f;
    public int lightnings = 2;
    public float lightningIncaccuracy = 5;

    public float useTime;
    public Effect effect = new WrapEffect(Fx.regenParticle, Pal.heal);
    public RegenPylon(String name) {
        super(name);
        timerUse = timers++;
        baseColor = Pal.heal;
        phaseColor = Pal.heal;
        reload = 250.0F;
        range = 60.0F;
        healPercent = 12.0F;
        phaseBoost = 12.0F;
        phaseRangeBoost = 50.0F;
        useTime = 400.0F;
        solid = true;
        update = true;
        group = BlockGroup.projectors;
        hasPower = true;
        hasItems = true;
        emitLight = true;
        lightRadius = 50.0F;
        suppressable = true;
        envEnabled |= 2;
        flags = EnumSet.of(new BlockFlag[]{BlockFlag.blockRepair});
    }

    public boolean outputsItems() {
        return false;
    }
    @Override
    public void setStats() {
        stats.timePeriod = useTime;
        super.setStats();

        stats.remove(Stat.repairTime);
        stats.remove(Stat.range);
        stats.add(Stat.repairTime, (float)((int)(100.0F / healPercent * reload / 60.0F)), StatUnit.seconds);
        stats.add(Stat.range, range / 8.0F, StatUnit.blocks);

        Consume var2 = this.findConsumer((c) -> c instanceof ConsumeItems);
        if (var2 instanceof ConsumeItems) {
            ConsumeItems cons = (ConsumeItems)var2;
            this.stats.remove(Stat.booster);
            this.stats.add(Stat.booster, StatValues.itemBoosters("{0}" + StatUnit.timesSpeed.localized(), stats.timePeriod, (this.phaseBoost + this.healPercent) / this.healPercent, this.phaseRangeBoost, cons.items));
        }

    }
    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        x *= tilesize;
        y *= tilesize;
        x += offset;
        y += offset;
        Drawf.dashSquare(baseColor, x, y, range/2f * tilesize);
        indexer.eachBlock(player.team(), Tmp.r1.setCentered(x, y, range/2f * tilesize), b -> true, t -> {
            Drawf.selected(t, Tmp.c1.set(baseColor).a(Mathf.absin(4f, 1f)));
        });
        Draw.reset();
    }

    public class RegenPylonBuild extends Building implements Ranged {
        public float heat;
        public float charge;
        public float phaseHeat;
        public float smoothEfficiency;
        int zapTimer = timers++;

        public RegenPylonBuild() {
            this.charge = Mathf.random(RegenPylon.this.reload);
        }
        @Override
        public float range() {
            return range/2f;
        }

        @Override
        public void updateTile(){
            boolean suppressed = checkSuppression();
            boolean enemyClose = enemiesNearby(range*1.1f*2f);

            boolean canHeal = !suppressed && !enemyClose;

            this.smoothEfficiency = Mathf.lerpDelta(smoothEfficiency, this.efficiency, 0.08f);
            this.heat = Mathf.lerpDelta(
                    this.heat,
                    this.efficiency > 0f && canHeal ? 1f : 0f,
                    0.08f
            );

            if(canHeal){
                this.charge += this.heat * this.delta();
            }

            this.phaseHeat = Mathf.lerpDelta(this.phaseHeat, this.optionalEfficiency, 0.1f);

            if(canHeal && this.optionalEfficiency > 0f &&
                    this.timer(RegenPylon.this.timerUse, RegenPylon.this.useTime)){
                this.consume();
            }

            if(enemyClose && this.timer(zapTimer, lightningReload)){
                Units.nearbyEnemies(team, x, y, range * 2, u -> {
                    if(u.dead || !u.isValid()) return;
                    for(int i = 1; i <= lightnings; i++) {
                        Lightning.create(
                                team,
                                Pal.health,
                                lightningDamage,
                                x, y,
                                angleTo(u) + Mathf.range(lightningIncaccuracy),
                                (int) range
                        );
                    }
                });
            }

            if(this.charge >= RegenPylon.this.reload && canHeal && !enemyClose){
                float realRange = RegenPylon.this.range / 2f
                        + this.phaseHeat * RegenPylon.this.phaseRangeBoost;

                this.charge = 0f;

                indexer.eachBlock(
                        this.team,
                        Tmp.r1.setCentered(x, y, realRange * tilesize),
                        b -> b.damaged() && !b.isHealSuppressed(),
                        other -> {
                            other.heal(
                                    other.maxHealth() *
                                            (RegenPylon.this.healPercent + this.phaseHeat * RegenPylon.this.phaseBoost)
                                            / 100f * this.efficiency
                            );
                            other.recentlyHealed();
                            Fx.healBlockFull.at(
                                    other.x, other.y,
                                    other.block.size,
                                    RegenPylon.this.baseColor,
                                    other.block
                            );
                        }
                );
            }
        }

        boolean enemiesNearby(float radius){
            final boolean[] found = {false};
            Units.nearbyEnemies(team, x, y, radius, u -> {
                if(!u.dead){
                    found[0] = true;
                }
            });
            return found[0];
        }

        @Override
        public double sense(LAccess sensor) {
            return sensor == LAccess.progress ? (double)Mathf.clamp(this.charge / RegenPylon.this.reload) : super.sense(sensor);
        }
        @Override
        public void drawSelect() {
            float realRange = RegenPylon.this.range/2f + this.phaseHeat * RegenPylon.this.phaseRangeBoost;
            indexer.eachBlock(this.team, Tmp.r1.setCentered(x, y, realRange * tilesize), (b) -> {
                return true;
            }, (other) -> {
                Drawf.selected(other, Tmp.c1.set(RegenPylon.this.baseColor).a(Mathf.absin(4.0F, 1.0F)));
            });
            Drawf.dashSquare(baseColor.lerp(phaseColor, phaseHeat), x, y, realRange * tilesize);
            indexer.eachBlock(player.team(), Tmp.r1.setCentered(x, y, range/2f * tilesize), b -> true, t -> {
                Drawf.selected(t, Tmp.c1.set(baseColor).a(Mathf.absin(4f, 1f)));
            });
        }
        @Override
        public void draw() {
            super.draw();
            float f = 1.0F - Time.time / 100.0F % 1.0F;
            Draw.color(enemiesNearby(range * 1.15f*2f) ? Pal.health : RegenPylon.this.baseColor);
            Draw.alpha(this.heat * Mathf.absin(Time.time, 7.957747F, 1.0F) * 0.5F);
            Draw.rect(RegenPylon.this.topRegion, this.x, this.y);
            Draw.alpha(1.0F);
            Lines.stroke((2.0F * f + 0.2F) * this.heat);
            Lines.square(this.x, this.y, Math.min(1.0F + (1.0F - f) * (float)RegenPylon.this.size * 8.0F / 2.0F, (float)(RegenPylon.this.size * 8) / 2.0F));
            Draw.reset();
        }
        @Override
        public void drawLight() {
            Drawf.light(this.x, this.y, RegenPylon.this.lightRadius * this.smoothEfficiency, RegenPylon.this.baseColor, 0.7F * this.smoothEfficiency);
        }
        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(this.heat);
            write.f(this.phaseHeat);
        }
        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            this.heat = read.f();
            this.phaseHeat = read.f();
        }
    }
}
