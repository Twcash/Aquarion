package aquarion.world.blocks.distribution;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.logic.Ranged;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.Build;
import mindustry.world.blocks.defense.OverdriveProjector;
import mindustry.world.blocks.distribution.ArmoredConveyor;
import mindustry.world.blocks.distribution.Conveyor;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Env;

import static mindustry.Vars.*;
//unoriginal ass...
public class DistributionCatalyst extends Block {
    public float reload = 60f;
    public float range = 80f;
    public TextureRegion topRegion;
    public float speedBoost = 1.5f;
    public float speedBoostPhase = 0.75f;
    public float useTime = 400f;
    public float phaseRangeBoost = 20f;
    public boolean hasBoost = true;
    public Color baseColor = Color.valueOf("feb380");
    public Color phaseColor = Color.valueOf("ffd59e");

    @Override
    public void load(){
        region = Core.atlas.find(name);
        topRegion = Core.atlas.find(name + "-top");
        teamRegion  = Core.atlas.find(name + "-team");
    }

    public DistributionCatalyst(String name){
        super(name);
        solid = true;
        update = true;
        group = BlockGroup.projectors;
        hasPower = true;
        hasItems = true;
        canOverdrive = false;
        emitLight = true;
        lightRadius = 25f;
        envEnabled |= Env.space;
    }

    @Override
    public boolean outputsItems(){
        return false;
    }

    @Override
    public void setBars(){
        super.setBars();
        addBar("boost", (DistributionCatalystBuild entity) -> new Bar(() -> Core.bundle.format("bar.boost",
                Mathf.round(Math.max((entity.realBoost() * 100 - 100), 0))),
                () -> Pal.surge,
                () -> entity.realBoost() / (hasBoost ? speedBoost + speedBoostPhase : speedBoost)
        ));
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);

        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range, baseColor);

        indexer.eachBlock(
                player.team(),
                x * tilesize + offset,
                y * tilesize + offset,
                range,
                other -> other instanceof Conveyor.ConveyorBuild, // Ensure it's a ConveyorBuild
                other -> Drawf.selected(other, Tmp.c1.set(baseColor).a(Mathf.absin(4f, 1f)))
        );
    }

    public class DistributionCatalystBuild extends Building implements Ranged {
        public float heat, charge = Mathf.random(reload), phaseHeat, smoothEfficiency, useProgress;

        @Override
        public float range(){
            return range;
        }

        @Override
        public void drawLight(){
            Drawf.light(x, y, lightRadius * smoothEfficiency, baseColor, 0.7f * smoothEfficiency);
        }

        @Override
        public void updateTile() {
            smoothEfficiency = Mathf.lerpDelta(smoothEfficiency, efficiency, 0.08f);
            heat = Mathf.lerpDelta(heat, efficiency > 0 ? 1f : 0f, 0.08f);
            charge += heat * Time.delta;

            if (hasBoost) {
                phaseHeat = Mathf.lerpDelta(phaseHeat, optionalEfficiency, 0.1f);
            }

            if (charge >= reload) {
                float realRange = range + phaseHeat * phaseRangeBoost;

                charge = 0f;

                // Boost only blocks in the transportation group within range
                indexer.eachBlock(
                        this,
                        realRange,
                        other -> other.block.group == BlockGroup.transportation,
                        other -> {
                            if (other instanceof Conveyor.ConveyorBuild conveyor) {
                                conveyor.applyBoost(realBoost(), reload + 1f);
                            }
                        }
                );
            }

            if (efficiency > 0) {
                useProgress += delta();
            }

            if (useProgress >= useTime) {
                consume();
                useProgress %= useTime;
            }
        }

        public float realBoost(){
            return (speedBoost + phaseHeat * speedBoostPhase) * efficiency;
        }

        @Override
        public void drawSelect(){
            float realRange = range + phaseHeat * phaseRangeBoost;

            indexer.eachBlock(this, realRange,
                    other -> other.block.canOverdrive && other instanceof Conveyor.ConveyorBuild,
                    other -> Drawf.selected(other, Tmp.c1.set(baseColor).a(Mathf.absin(4f, 1f)))
            );

            Drawf.dashCircle(x, y, realRange, baseColor);
        }

        @Override
        public void draw(){
            super.draw();

            float f = 1f - (Time.time / 100f) % 1f;

            Draw.color(baseColor, phaseColor, phaseHeat);
            Draw.alpha(heat * Mathf.absin(Time.time, 50f / Mathf.PI2, 1f) * 0.5f);
            Draw.rect(topRegion, x, y);
            Draw.alpha(1f);
            Lines.stroke((2f * f + 0.1f) * heat);

            float r = Math.max(0f, Mathf.clamp(2f - f * 2f) * size * tilesize / 2f - f - 0.2f),
                    w = Mathf.clamp(0.5f - f) * size * tilesize;
            Lines.beginLine();
            for(int i = 0; i < 4; i++){
                Lines.linePoint(x + Geometry.d4(i).x * r + Geometry.d4(i).y * w, y + Geometry.d4(i).y * r - Geometry.d4(i).x * w);
                if(f < 0.5f)
                    Lines.linePoint(x + Geometry.d4(i).x * r - Geometry.d4(i).y * w, y + Geometry.d4(i).y * r + Geometry.d4(i).x * w);
            }
            Lines.endLine(true);

            Draw.reset();
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(heat);
            write.f(phaseHeat);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            heat = read.f();
            phaseHeat = read.f();
        }
    }
}