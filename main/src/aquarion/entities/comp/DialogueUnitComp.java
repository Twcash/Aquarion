package aquarion.entities.comp;

import aquarion.annotations.Annotations;
import aquarion.annotations.Annotations.*;

import aquarion.gen.DialogueUnitc;
import aquarion.units.DefunctUnitType;
import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.ui.layout.*;
import arc.struct.Seq;
import arc.util.*;
import arc.util.pooling.Pools;
import mindustry.ai.types.*;
import mindustry.async.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.ctype.*;
import mindustry.entities.*;
import mindustry.entities.abilities.*;
import mindustry.entities.units.*;
import mindustry.game.EventType.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;
import static mindustry.logic.GlobalVars.*;

@Annotations.EntityComponent
abstract class DialogueUnitComp implements Unitc, DialogueUnitc, Legsc{
    transient String currentDialogue;
    transient float dialogueTime;
    transient float lastHealth;
    @Import transient UnitType type;
    @Import transient  boolean dead, wasPlayer ;
    @Import transient Team team;
    @SyncLocal float wound = 240;
    @Import transient Seq<Ability> abilities;
    @Import transient Seq<WeaponMount> mounts;
    @Import transient float x, y, hitSize, health, maxHealth, rotation, speedMultiplier, drag;
    @Import transient int id;
    public String random(String[] arr){
        return arr[Mathf.random(arr.length - 1)];
    }

    public void say(String text, float duration){
        currentDialogue = text;
        dialogueTime = duration;
    }
    @Override
    public void add(){
        team.data().updateCount(type, 1);

        if(type instanceof DefunctUnitType t) say(random(t.spawnLines), 120f);
    }

    @Override
    public void update(){
        Events.on(EventType.SectorCaptureEvent.class, t ->{
            if(type instanceof DefunctUnitType f){
                say(random(f.victorLines), 500f);
            }
        });
        if(health < maxHealth / 2f && dialogueTime <= 0 && !dead){
            if(health < maxHealth / 4f){
                if(type instanceof DefunctUnitType t){
                    say(random(t.deathLines), 500f);
                }
            } else {
                if(type instanceof DefunctUnitType t){
                    say(random(t.hurtLines), 240f);
                }
            }
        }
        if(dead || health <= 0){
            drag = 0.01f;
            speedMultiplier = 0;
            if(Mathf.chanceDelta(0.1)){
                Tmp.v1.rnd(Mathf.range(hitSize));
                type.fallEffect.at(x + Tmp.v1.x, y + Tmp.v1.y);
            }

            if(Mathf.chanceDelta(0.2)){
                float offset = type.engineOffset/2f + type.engineOffset/2f * wound;
                float range = Mathf.range(type.engineSize);
                type.fallEngineEffect.at(
                        x + Angles.trnsx(rotation + 180, offset) + Mathf.range(range),
                        y + Angles.trnsy(rotation + 180, offset) + Mathf.range(range),
                        Mathf.random()
                );
            }
            //health -= type.fallSpeed * Time.delta;

            if( health <= -maxHealth * 2){
                Call.unitDestroy(id);
            }
        }
    }

    @Override
    public void draw(){
        type.draw(self());
        drawDialogue();
    }
    public boolean isTrueDead(){
        return wound >= 0.09f;
    }
    public void drawDialogue(){
        if(currentDialogue == null || dialogueTime <= 0) return;
        if(renderer.pixelate) return;
        Draw.z(Layer.overlayUI+1);
        Font font = Fonts.outline;
        GlyphLayout l = Pools.obtain(GlyphLayout.class, GlyphLayout::new);

        boolean ints = font.usesIntegerPositions();
        font.getData().setScale(1 / 4f / Scl.scl(1f));
        font.setUseIntegerPositions(false);
        String raw = currentDialogue;
        if(raw.startsWith("@")){
            raw = Core.bundle.get(raw.substring(1));
        }
        String text = UI.formatIcons(raw);

        l.setText(font, text, Color.white, 120f, Align.center, true);

        float drawY = y + hitSize + 6f;

        Draw.color(0f, 0f, 0f, 0.2f);
        Fill.rect(x, drawY, l.width + 4f, l.height + 4f);

        Draw.color();
        font.setColor(Color.white);
        font.draw(text, x - l.width/2f, drawY + l.height/2f, 120f, Align.left, true);

        font.setUseIntegerPositions(ints);
        font.getData().setScale(1f);

        Pools.free(l);

        dialogueTime -= Time.delta;
    }
}