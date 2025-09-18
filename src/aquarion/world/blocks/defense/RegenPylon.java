package aquarion.world.blocks.defense;

import arc.graphics.Color;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Fx;
import mindustry.entities.effect.WrapEffect;
import mindustry.logic.LAccess;
import mindustry.logic.Ranged;
import mindustry.world.blocks.defense.MendProjector;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
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
    public float useTime;
    public Effect effect = new WrapEffect(Fx.regenParticle, Pal.heal);
    public RegenPylon(String name) {
        super(name);
        this.timerUse = this.timers++;
        this.baseColor = Pal.heal;
        this.phaseColor = Pal.heal;
        this.reload = 250.0F;
        this.range = 60.0F;
        this.healPercent = 12.0F;
        this.phaseBoost = 12.0F;
        this.phaseRangeBoost = 50.0F;
        this.useTime = 400.0F;
        this.solid = true;
        this.update = true;
        this.group = BlockGroup.projectors;
        this.hasPower = true;
        this.hasItems = true;
        this.emitLight = true;
        this.lightRadius = 50.0F;
        this.suppressable = true;
        this.envEnabled |= 2;
        this.flags = EnumSet.of(new BlockFlag[]{BlockFlag.blockRepair});
    }

    public boolean outputsItems() {
        return false;
    }
    @Override
    public void setStats() {
        this.stats.timePeriod = useTime;
        super.setStats();

        stats.remove(Stat.repairTime);
        stats.remove(Stat.range);
        this.stats.add(Stat.repairTime, (float)((int)(100.0F / this.healPercent * this.reload / 60.0F)), StatUnit.seconds);
        this.stats.add(Stat.range, this.range / 8.0F, StatUnit.blocks);

        Consume var2 = this.findConsumer((c) -> c instanceof ConsumeItems);
        if (var2 instanceof ConsumeItems) {
            ConsumeItems cons = (ConsumeItems)var2;
            this.stats.remove(Stat.booster);
            this.stats.add(Stat.booster, StatValues.itemBoosters("{0}" + StatUnit.timesSpeed.localized(), this.stats.timePeriod, (this.phaseBoost + this.healPercent) / this.healPercent, this.phaseRangeBoost, cons.items));
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

        public RegenPylonBuild() {
            this.charge = Mathf.random(RegenPylon.this.reload);
        }
        @Override
        public float range() {
            return RegenPylon.this.range/2f;
        }
        @Override
        public void updateTile() {
            boolean canHeal = !this.checkSuppression();
            this.smoothEfficiency = Mathf.lerpDelta(this.smoothEfficiency, this.efficiency, 0.08F);
            this.heat = Mathf.lerpDelta(this.heat, this.efficiency > 0.0F && canHeal ? 1.0F : 0.0F, 0.08F);
            this.charge += this.heat * this.delta();
            this.phaseHeat = Mathf.lerpDelta(this.phaseHeat, this.optionalEfficiency, 0.1F);
            if (this.optionalEfficiency > 0.0F && this.timer(RegenPylon.this.timerUse, RegenPylon.this.useTime) && canHeal) {
                this.consume();
            }

            if (this.charge >= RegenPylon.this.reload && canHeal) {
                float realRange = RegenPylon.this.range/2f + this.phaseHeat * RegenPylon.this.phaseRangeBoost;
                this.charge = 0.0F;

                    indexer.eachBlock(this.team, Tmp.r1.setCentered(x, y, realRange * tilesize), (b) -> {
                    return b.damaged() && !b.isHealSuppressed();
                }, (other) -> {
                    other.heal(other.maxHealth() * (RegenPylon.this.healPercent + this.phaseHeat * RegenPylon.this.phaseBoost) / 100.0F * this.efficiency);
                    other.recentlyHealed();
                    Fx.healBlockFull.at(other.x, other.y, (float)other.block.size, RegenPylon.this.baseColor, other.block);
                });
            }

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
            Draw.color(RegenPylon.this.baseColor, RegenPylon.this.phaseColor, this.phaseHeat);
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
