package aquarion.world.blocks.defense;

import aquarion.annotations.Annotations.Load;
import aquarion.world.graphics.AquaFx;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;
import mindustry.world.Block;
import mindustry.world.Tile;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class StatusProjector extends Block {
    public StatusProjector(String name) {
        super(name);
        update = true;
    }
    // Only apply to the enemy team if true. Else applies to same team.
    public boolean applyEnemy = true;
    public @Load("@-top") TextureRegion topRegion;
    public StatusEffect effect = StatusEffects.slow;
    public Effect applyEffect = Fx.sapped;
    public Effect shootEffect = AquaFx.diminutionRing;
    public float statusTime = 600;
    public float range = 100;
    public float reload = 360;

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        Tile tile = world.tile(x, y);

        Draw.color(Pal.placing);
        Drawf.circles(x * tilesize + offset, y * tilesize + offset, range*2);

        Draw.reset();
    }

    public class StatusProjectorBuild extends Building{
        public float progress = 0;
        public float smoothProgress = 0;

        @Override
        public void updateTile() {
            progress += getProgressIncrease(reload);
            smoothProgress = Mathf.lerpDelta(smoothProgress, progress, 0.1f);

            if (progress >= 1) {
                progress %= 1;
                shootEffect.at(x,y);
                if(applyEnemy){Units.nearbyEnemies(this.team, x, y, range*2, unit -> {
                    if (unit != null && !unit.dead) {
                        unit.apply(effect, statusTime);
                        applyEffect.at(unit.x, unit.y);
                    };
                });} else {
                    Units.nearby(this.team, x, y, range*2, unit -> {
                        if (unit != null && !unit.dead) {
                            unit.apply(effect, statusTime);
                            applyEffect.at(unit.x, unit.y);
                        };
                    });
                }
            }
        }
        @Override
        public void drawSelect(){
            Drawf.circles(x, y, range*2, team.color);
        }
        @Override
        public void draw(){
            super.draw();
            Draw.alpha(smoothProgress);
            Draw.rect(topRegion,x,y);
        }
        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(progress);
        }
        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            progress = read.f();
        }
    }
}
