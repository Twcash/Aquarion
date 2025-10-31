package aquarion.world.graphics;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.LightningBulletType;
import mindustry.game.Team;
import mindustry.gen.Groups;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import mindustry.gen.WeatherState;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.graphics.Shaders;
import mindustry.type.weather.RainWeather;
import mindustry.world.Tile;

import static mindustry.Vars.tilesize;
import static mindustry.gen.WeatherState.fadeTime;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
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
    public Color flashColor = Color.valueOf("fffad6");

    public float lightningChance = 0.005f;

    public Monsoon(String name){
        super(name);
        sound = Sounds.rain;
        status = StatusEffects.wet;
        statusDuration = 240;
    }

    @Override
    public void load(){
        super.load();
        for(int i = 0; i < splashes.length; i++){
            splashes[i] = Core.atlas.find("splash-" + i);
        }
    }
    @Override
    public void drawOver(WeatherState state){
        // 1. Heavy angled rain
        drawRain(sizeMin, sizeMax, xspeed, yspeed, density, state.intensity, stroke, color);

        // 2. Flash + lightning strike
        if(Mathf.chanceDelta(lightningChance * state.intensity)){
            float x = Core.camera.position.x + Mathf.range(Core.camera.width / 2f);
            float y = Core.camera.position.y + Mathf.range(Core.camera.height / 2f);

            Draw.color(flashColor, Mathf.random(0.45f, 0.75f));
            Fill.rect(Core.camera.position.x, Core.camera.position.y, Core.camera.width, Core.camera.height);
            Draw.reset();

            Fx.lightning.at(x, y);
            Lightning.create(Team.derelict, Pal.techBlue, 35f, x, y, Mathf.random(360f), 25);
        }

        // 3. Mist layer
        drawMist(state);
    }

    @Override
    public void drawUnder(WeatherState state){
        // Ground splashes
        drawSplashes(splashes, sizeMax, density, state.intensity, state.opacity, splashTimeScale, stroke, color, liquid);
    }

    // === Custom mist drawing ===
    public void drawMist(WeatherState state){
        if(state.life < fadeTime){
            state.opacity = Math.min(state.life / fadeTime, state.opacity);
        }else{
            state.opacity = Mathf.lerpDelta(state.opacity, 1f, 0.004f);
        }
        float intensitye = state.intensity * state.opacity;
        Draw.z(Layer.light+2 + 1f);
        Draw.color(Color.valueOf("151227"), 0.6f * intensitye);
        Blending.additive.apply();
        Fill.rect(Core.camera.position.x, Core.camera.position.y, Core.camera.width, Core.camera.height);
        Draw.blit(AquaShaders.monsoon);
        Blending.normal.apply();
        Draw.reset();
    }
}