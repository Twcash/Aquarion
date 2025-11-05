package aquarion.world.graphics;

import aquarion.content.AquaSounds;
import aquarion.world.entities.AquaLightning;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.game.Team;
import mindustry.gen.WeatherState;
import mindustry.graphics.Layer;

import static mindustry.gen.WeatherState.fadeTime;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import mindustry.content.*;
import mindustry.type.*;

public class Monsoon extends Weather {
    public float yspeed = 20f, xspeed = 15f;
    public float density = 500f;
    public float stroke = 1.3f;

    public float sizeMin = 10f, sizeMax = 50f;
    public float splashTimeScale = 35f;
    public Liquid liquid = Liquids.water;
    public TextureRegion[] splashes = new TextureRegion[12];
    public Color color = Color.valueOf("6a7affff");

    public float lightningChance = 0.005f;

    public Monsoon(String name) {
        super(name);
        sound = AquaSounds.monsoon;
        status = StatusEffects.wet;
        statusDuration = 240;
        soundVol = 1;
        soundVolMin = 0.8f;
    }

    @Override
    public void load() {
        super.load();
        for (int i = 0; i < splashes.length; i++) {
            splashes[i] = Core.atlas.find("splash-" + i);
        }
    }

    @Override
    public void drawOver(WeatherState state) {
        drawRain(sizeMin, sizeMax, xspeed, yspeed, density, state.intensity, stroke, color);

        if (Mathf.chanceDelta(lightningChance * state.intensity) && !Vars.state.isPaused()) {
            float x = Core.camera.position.x + Mathf.range(Vars.world.width() / 2f);
            float y = Core.camera.position.y + Mathf.range(Vars.world.height() / 2f);

            Fx.lightning.at(x, y);
            AquaSounds.thunder.at(x, y, Mathf.random(0.8f, 0.9f), Mathf.random(0.7f, 0.95f));
            AquaLightning.create(Team.derelict, Color.valueOf("bef8ff"), 35f, x, y, Mathf.random(360f), 30);
        }

        drawMist(state);
    }

    @Override
    public void drawUnder(WeatherState state) {
        drawSplashes(splashes, sizeMax, density, state.intensity, state.opacity, splashTimeScale, stroke, color, liquid);
    }

    public void drawMist(WeatherState state){
        if(state.life < fadeTime){
            state.opacity = Math.min(state.life / fadeTime, state.opacity);
        } else {
            state.opacity = Mathf.lerpDelta(state.opacity, 1f, 0.004f);
        }

        float intensity = state.intensity * state.opacity;

        Draw.blit(AquaShaders.monsoon);
        Draw.draw(Layer.light + 1, () -> {
            Draw.color(Color.valueOf("515573"), 0.6f*intensity);
            Fill.rect(Core.camera.position.x, Core.camera.position.y, Core.camera.width, Core.camera.height);
            Draw.reset();
            Blending.additive.apply();
            AquaShaders.monsoon.setIntensity(Mathf.clamp(intensity-.2f));
            Draw.blit(AquaShaders.monsoon);
            Blending.normal.apply();
        });
        Draw.reset();
    }


}