package aquarion.world.graphics.weather;

import aquarion.content.AquaAttributes;
import aquarion.content.AquaSounds;
import aquarion.world.entities.AquaLightning;
import aquarion.world.blocks.power.LightningRod;
import aquarion.world.graphics.AquaShaders;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.scene.ui.layout.Scl;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Fires;
import mindustry.game.Team;
import mindustry.gen.WeatherState;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.world.meta.Attribute;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import mindustry.content.*;
import mindustry.type.*;
public class Monsoon extends Weather {

    public float yspeed = 20f, xspeed = 15f;
    public float density = 500f;
    public float stroke = 1.3f;
    public Color stormColor;
    public float particleDensity;
    public float sizeMin = 10f, sizeMax = 50f;
    public float splashTimeScale = 35f;
    public Liquid liquid = Liquids.water;
    public TextureRegion[] splashes = new TextureRegion[12];
    public Color color = Color.valueOf("6a7affff");
    public Color particleColor;
    public float lightningChance = 0.005f;
    public float fireExtinguishChance = 0.01f;
    public int fireSamplesPerFrame = 50;
    public String particleRegion = "particle";
    public TextureRegion region;
    public float speed = 5;
    public float minLightningSize = 5;
    public float maxLightningSize = 50;
    public float lightningDamageMultiplier = 1f;
    public float minLightningLength = 5;
    public float maxLightningLength = 50;
    public float sinSclMin = 30f, sinSclMax = 80f, sinMagMin = 1f, sinMagMax = 7f;
    public Monsoon(String name) {
        super(name);
        sound = AquaSounds.monsoon;
        status = StatusEffects.wet;
        attrs.set(Attribute.light, -0.7f);
        attrs.set(AquaAttributes.wind, 1f);
        statusDuration = 240;
        soundVol = 1;
        duration = 10 * Time.toMinutes;
        soundVolMin = 0f;
    }

    private int lightningCounter = 0;
    private float lightningTimer = 0f;

    @Override
    public void update(WeatherState state){
        float intensity = state.intensity * state.opacity;

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
            float size = Mathf.randomSeed(seed + 2, minLightningSize, maxLightningSize);
            Color lightningColor = Color.valueOf("bef8ff").cpy();
            lightningColor.a = Mathf.randomSeed(seed + 3, 0.4f, 1f); // vary Sigma alpha
            //Super sigma alpha code
            var rod = LightningRod.find(x, y);
            if(rod != null){
                rod.capture();
                Fx.chainLightning.at(x, y, 0f, lightningColor, new arc.math.geom.Vec2(rod.x, rod.y));
                AquaSounds.thunder.at(rod.x, rod.y, size/100*2, size/100*2);
            }else{
                Fx.lightning.at(x, y, size);
                AquaSounds.thunder.at(x, y, size/100*2, size/100*2);
                AquaLightning.create(Team.derelict, lightningColor, size * lightningDamageMultiplier, x, y, Mathf.randomSeed(seed + 6, 0f, 360f), (int)Mathf.randomSeed(seed + 7, minLightningLength, maxLightningLength));
            }
        }

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
        region = Core.atlas.find(particleRegion);
    }
    @Override
    public void drawOver(WeatherState state) {
        drawRain(6, 12, xspeed, yspeed, density, state.intensity, stroke, color);
        drawMist(state);
        float windx = state.windVector.x * speed, windy = state.windVector.y * speed;

        drawParticles(region, color, sizeMin, sizeMax, particleDensity, state.intensity, state.opacity, windx, windy, 0.2f, 0.5f, sinSclMin, sinSclMax, sinMagMin, sinMagMax, false);
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
    public float zoomVisible = .5f, zoomGone = 1.7f;
    public float zoomFade(){
        float scale = Vars.renderer.getDisplayScale();
        float def = Scl.scl(4f);
        return Mathf.clamp((zoomGone * def - scale) / ((zoomGone - zoomVisible) * def));
    }
    public void drawMist(WeatherState state){
        float intensity = state.intensity * state.opacity;

        Draw.draw(Layer.light + 1, () -> {
            Draw.color(Color.valueOf("515573"), 0.6f*intensity);
            Fill.rect(Core.camera.position.x, Core.camera.position.y, Core.camera.width, Core.camera.height);
            Draw.reset();
            Blending.additive.apply();
            if(stormColor!=null)Draw.color(stormColor);
            AquaShaders.monsoon.setIntensity(Mathf.clamp((intensity * 0.5f) * zoomFade()));
            Draw.blit(AquaShaders.monsoon);
            Blending.normal.apply();
        });
        Draw.reset();
    }
}
