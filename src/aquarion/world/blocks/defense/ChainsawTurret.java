package aquarion.world.blocks.defense;

import aquarion.world.Uti.AquaUnitsUtil;
import arc.Core;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.Seq;
import arc.util.*;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.ai.BlockIndexer;
import mindustry.content.Fx;
import mindustry.entities.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.Block;

import static aquarion.world.Uti.AquaUnitSort.closest;
import static mindustry.Vars.*;

//ok I lied it's not really a turret /:
public class ChainsawTurret extends Block {

    public ChainsawTurret(String name) {
        super(name);
        update = true;
        destructible = true;
        breakable = true;
        solid = true;

        ambientSound = Sounds.cutter;
        ambientSoundVolume = 0.1f;
    }

    public float damage = 10;
    public float rotateSpeed = 0.7f;
    public float range = 90;
    public boolean targetUnderBlocks = true;
    public boolean targetBlocks = true;
    public boolean targetGround = true;

    public AquaUnitsUtil.Sortf unitSort = closest;
    public Boolf<Unit> unitFilter = u -> true;  // GUH I can't seem to make it target derelict stuff whatsoever. I can't find another tripwire tho
    public Boolf<Building> buildingFilter = b -> true;

    public Color selectColor = Pal.redDust;
    public TextureRegion turretRegion, armRegion, capRegion, sawRegion;

    @Override
    public void load() {
        super.load();
        turretRegion = Core.atlas.find(name + "-turret");
        armRegion = Core.atlas.find(name + "-arm");
        capRegion = Core.atlas.find(name + "-cap");
        sawRegion = Core.atlas.find(name + "-saw");
        region = Core.atlas.find(name);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        Draw.color(selectColor);
        Drawf.dashCircle(x * tilesize, y * tilesize, range, Pal.accent);
        indexer.eachBlock(Team.derelict, x * tilesize + offset, y * tilesize + offset,
                range, other -> true, other -> Drawf.selected(other, Tmp.c1.set(selectColor).a(Mathf.absin(4f, 1f))));
        Units.nearbyEnemies(Team.derelict, x, y, range * 2, range * 2, e -> {
            Drawf.dashSquareBasic(e.x, e.y, e.hitSize * Mathf.absin(4f, 1));
        });
    }

    public class ChainsawTurretBuild extends Building {
        public float rotation = 90;
        public float sawx, sawy;
        public float sawRange = 15;
        public float sawSpeed = 0.1f;
        public int damageTick = 0;

        public Posc target;
        public Vec2 targetPos = new Vec2();
        private boolean sawInitialized = false;
        private Seq<Team> activeTeams = new Seq<>(Team.class);

        @Override
        public void updateTile() {
            activeTeams.clear();
            super.updateTile();
            if (!sawInitialized) {
                sawx = this.x + Angles.trnsx(rotation, 20);
                sawy = this.y + Angles.trnsy(rotation, 20);
                sawInitialized = true;
            }
            if (!validateTarget()) {
                target = null;
                targetPos.set(x, y);
            }

            if (target != null) {

                targetPos.set(target.x(), target.y());
                applySawDamage();
            } else {
                findTarget();
            }

            moveSaw();
        }
        //TODO possibly switch to vectors
        protected void moveSaw() {
            float baseOffset = 20;
            float maxExtend = range;

            if (target == null) {
                float restingX = x + Angles.trnsx(rotation, baseOffset);
                float restingY = y + Angles.trnsy(rotation, baseOffset);
                sawx = Mathf.lerpDelta(sawx, restingX, sawSpeed * efficiency);
                sawy = Mathf.lerpDelta(sawy, restingY, sawSpeed* efficiency);
            } else {
                float targetAngle = Angles.angle(x, y, target.x(), target.y());
                turnToTarget(targetAngle);

                float targetDistance = Mathf.dst(x, y, target.x(), target.y());
                float targetExtend = Mathf.clamp(targetDistance, baseOffset, maxExtend);
                float currentExtend = Mathf.dst(x, y, sawx, sawy);
                float newExtend = Mathf.lerpDelta(currentExtend, targetExtend, sawSpeed* efficiency);

                sawx = x + Angles.trnsx(rotation, newExtend);
                sawy = y + Angles.trnsy(rotation, newExtend);
                float wobbleIntensity = Mathf.clamp(1f - (Math.abs(newExtend - targetExtend) / maxExtend)); // Stronger when closer
                float wobbleAmount = 4f * wobbleIntensity;
                float wobbleSpeed = (id() % 10) * 12 + Time.time * 15f * efficiency;

                sawx += Mathf.sin(wobbleSpeed, wobbleAmount, 0.1f) * Time.delta * efficiency;
                sawy += Mathf.cos(wobbleSpeed, wobbleAmount, 0.1f) * Time.delta * efficiency;
            }
        }
        @Override
        public void drawSelect(){
            Draw.color(selectColor);
            Drawf.dashCircle(x * tilesize, y * tilesize, range, Pal.accent);
            indexer.eachBlock(Team.derelict, x * tilesize + offset, y * tilesize + offset,
                    range, other -> true, other -> Drawf.selected(other, Tmp.c1.set(selectColor).a(Mathf.absin(4f, 1f))));
            Units.nearbyEnemies(Team.derelict, x, y, range * 2, range * 2, e -> {
                Drawf.dashSquareBasic(e.x, e.y, e.hitSize * Mathf.absin(4f, 1));
            });
        }
        @Override
        public boolean shouldAmbientSound(){
            return efficiency > 0;
        }
        protected void applySawDamage() {
            if (damageTick++ % 10 == 0) {
                Damage.damage(this.team, sawx, sawy, sawRange, damage);
                if(wasVisible && Mathf.chanceDelta(0.1f)){
                    Fx.colorSpark.at(sawx + Mathf.range(sawRange), sawy + Mathf.range(sawRange));
                }
            }
        }

        protected boolean validateTarget() {
            if(target == null) {
                return false;
            }
            boolean invalid = invalidateTarget(target, team, x, y, range);
            if(invalid) {
            }
            return !invalid;
        }

        public boolean invalidateTarget(Posc target, Team team, float x, float y, float range) {
            return target == null ||
                    (range != Float.MAX_VALUE && !target.within(x, y, range + (target instanceof Sized hb ? hb.hitSize() / 2f : 0f))) ||
                    (target instanceof Healthc h && !h.isValid());
        }

        protected void findTarget() {
            target = AquaUnitsUtil.bestTarget(
                    this.team, x, y, range,
                    e -> !e.dead() && unitFilter.get(e),
                    b -> targetGround && targetBlocks && buildingFilter.get(b), unitSort);
            if (target != null) {
                targetPos.set(target.x(), target.y());
            }
        }

        protected void turnToTarget(float targetRot) {
            rotation = Angles.moveToward(rotation, targetRot, rotateSpeed * delta() * efficiency);
        }

        @Override
        public void draw() {
            Draw.z(Layer.block);
            Draw.rect(region, x, y);
            Draw.z(Layer.block + 2 );
            Draw.rect(turretRegion, x, y, rotation - 90);
            Draw.z(Layer.block + 1.1f);
            Lines.stroke(8);
            Lines.line(armRegion, x, y, sawx, sawy, false);
            Draw.rect(capRegion, sawx, sawy, rotation);
            Draw.z(Layer.block + 1.2f);
            Drawf.spinSprite(sawRegion, sawx, sawy, 1 + Time.time * 10 * efficiency);

            super.draw();
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(sawx);
            write.f(sawy);
            write.f(rotation);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read);
            sawx = read.f();
            sawy = read.f();
            rotation = read.f();
        }
    }
}