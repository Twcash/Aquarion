package aquarion.world.graphics.weather;

import aquarion.content.AquaAttributes;
import aquarion.content.AquaSounds;
import aquarion.world.entities.AquaLightning;
import aquarion.world.graphics.AquaShaders;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.util.Time;
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
        duration = 15 * Time.toMinutes;
        soundVolMin = 0.8f;
    }

    private int lightningCounter = 0;
    private float lightningTimer = 0f;

    @Override
    public void update(WeatherState state){
        float intensity = state.intensity;

        //deterministic lightning: seeded by the synced weather state id, so clients
        //draw the exact same strikes while the server handles the damage
        lightningTimer += Time.delta;
        float interval = 1f / Math.max(0.0001f, lightningChance * intensity);
        if(lightningTimer >= interval){
            lightningTimer = 0f;
            lightningCounter++;
            long seed = state.id * 7L + lightningCounter;
            float x = Mathf.randomSeed(seed, 0f, Vars.world.unitWidth());
            float y = Mathf.randomSeed(seed + 1, 0f, Vars.world.unitHeight());
            float size = Mathf.randomSeed(seed + 2, 15f, 50f);
            Color lightningColor = Color.valueOf("bef8ff").cpy();
            lightningColor.a = Mathf.randomSeed(seed + 3, 0.4f, 1f); // vary alpha

            Fx.lightning.at(x, y, size);
            AquaSounds.thunder.at(x, y, Mathf.randomSeed(seed + 4, 0.8f, 0.95f), Mathf.randomSeed(seed + 5, 0.6f, 1f));
            AquaLightning.create(Team.derelict, lightningColor, size, x, y, Mathf.randomSeed(seed + 6, 0f, 360f), (int)Mathf.randomSeed(seed + 7, 15f, 50f));
        }

        //extinguishing fires is a world state change - only the server does it
        if(!Vars.net.client()){
            extinguishFires(state);
        }
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
            AquaShaders.monsoon.setIntensity(Mathf.clamp(intensity - 0.4f));
            Draw.blit(AquaShaders.monsoon);
            Blending.normal.apply();
        });
        Draw.reset();
    }
}