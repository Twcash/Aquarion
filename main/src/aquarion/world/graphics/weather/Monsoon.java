package aquarion.world.graphics.weather;

import aquarion.content.AquaAttributes;
import aquarion.content.AquaSounds;
import aquarion.world.entities.AquaLightning;
import aquarion.world.graphics.AquaShaders;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Fires;
import mindustry.game.Team;
import mindustry.gen.WeatherState;
import mindustry.graphics.Layer;
import mindustry.world.meta.Attribute;
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
    public float fireExtinguishChance = 0.01f;
    public int fireSamplesPerFrame = 50;

    public Monsoon(String name) {
        super(name);
        sound = AquaSounds.monsoon;
        status = StatusEffects.wet;
        attrs.set(Attribute.light, -0.7f);
        attrs.set(AquaAttributes.wind, 1f);
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

        if (!Vars.state.isPaused()) {
            // Random lightning strikes with varying size
            if (Mathf.chanceDelta(lightningChance * state.intensity)) {
                float x = Core.camera.position.x + Mathf.range(Vars.world.width() * 4f);
                float y = Core.camera.position.y + Mathf.range(Vars.world.height() * 4f);
                float size = Mathf.random(15f, 50f);
                Color lightningColor = Color.valueOf("bef8ff").cpy();
                lightningColor.a = Mathf.random(0.4f, 1f); // vary alpha

                Fx.lightning.at(x, y, size);
                AquaSounds.thunder.at(x, y, Mathf.random(0.8f, 0.95f), Mathf.random(0.6f, 1f));
                AquaLightning.create(Team.derelict, lightningColor, size, x, y, Mathf.random(360f), (int) Mathf.random(15f, 50f));
            }

            // Randomly extinguish fires across the map
            extinguishFires(state);
        }

        drawMist(state);
    }

    private void extinguishFires(WeatherState state) {
        int width = Vars.world.width();
        int height = Vars.world.height();

        for (int i = 0; i < fireSamplesPerFrame; i++) {
            int x = Mathf.random(0, width - 1);
            int y = Mathf.random(0, height - 1);

            if (Fires.has(x, y) && Mathf.chance(fireExtinguishChance * state.intensity)) {
                Fires.extinguish(Fires.get(x, y).tile, Mathf.random(0.5f, 2f)); // random intensity
            }
        }
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
            AquaShaders.monsoon.setIntensity(Mathf.clamp(intensity - 0.2f));
            Draw.blit(AquaShaders.monsoon);
            Blending.normal.apply();
        });
        Draw.reset();
    }
}