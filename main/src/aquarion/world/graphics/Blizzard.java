package aquarion.world.graphics;

import aquarion.AquaStatuses;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.core.Renderer;
import mindustry.entities.Effect;
import mindustry.entities.effect.ParticleEffect;
import mindustry.gen.*;
import mindustry.type.weather.ParticleWeather;
import mindustry.world.Tile;

import static mindustry.gen.WeatherState.fadeTime;

public class Blizzard extends ParticleWeather {

    public Effect hitEffect = AquaFx.snow;
    public float hitChance = 0.4f;
    public float effectInterval = 6f;
    private float effectTimer = 0f;

    public float targetBlueTint = 0.08f;

    private final Color originalAmbient = new Color();
    private boolean savedAmbient = false;

    public Blizzard(String name){
        super(name);

        particleRegion = "circle-shadow";
        color = Color.valueOf("ffffff");
        yspeed = -10f;
        xspeed = 4f;
        sizeMin = 3f;
        sizeMax = 22f;
        density = 3500f;
        minAlpha = 0.4f;
        maxAlpha = 0.8f;
        sound = Sounds.wind2;
        baseSpeed = 35f;
        force = 1f;
        drawNoise = true;
        drawParticles = true;
        useWindVector = true;
        randomParticleRotation = true;

        noiseScale = 1200f;
        noiseLayers = 3;
        noiseLayerSpeedM = 1.3f;
        noiseLayerAlphaM = 0.85f;
        status = AquaStatuses.cold;
        statusDuration = 120;
        noiseLayerSclM = 0.92f;
        noiseLayerColorM = 0.96f;
        noisePath = "noiseAlpha";
        noiseColor = Color.valueOf("d8f1ff");
    }

    @Override
    public void update(WeatherState state){
        super.update(state);

        float push = force * state.intensity * Time.delta;
        if(push > 0.001f){
            float windx = state.windVector.x * push;
            float windy = state.windVector.y * push;

            for(Unit u : Groups.unit){
                u.impulse(windx, windy);
            }
        }
        effectTimer += Time.delta;
        if(effectTimer >= effectInterval){
            effectTimer = 0f;
            spawnImpactEffects(state);
        }
        updateAmbient(state);
    }

    private void spawnImpactEffects(WeatherState state){
        float intensity = state.intensity;
        if(intensity <= 0.05f) return;

        int tileCount = Mathf.ceil(Vars.world.width() * Vars.world.height() * 0.0015f);
        float windx = state.windVector.x;
        float windy = state.windVector.y;

        if(windx == 0 && windy == 0) return;

        float invLen = 1f / Mathf.sqrt(windx * windx + windy * windy);

        for(int i = 0; i < tileCount; i++){
            Tile tile = Vars.world.tile(Mathf.random(Vars.world.width() - 1), Mathf.random(Vars.world.height() - 1));
            if(tile == null || tile.block() == Blocks.air) continue;
            if(!Mathf.chance(hitChance * intensity)) continue;

            Building build = tile.build;
            if(build == null) continue;

            float ex = build.x + Mathf.range(build.block.size*8/2f);
            float ey = build.y + Mathf.range(build.block.size*8/2f);

            hitEffect.at(ex, ey,  color);
        }
    }

    private void updateAmbient(WeatherState state){
//
//        Color ambient = Vars.state.rules.ambientLight;
//
//        if(!savedAmbient){
//            originalAmbient.set(ambient);
//            savedAmbient = true;
//        }
//
//        if(state.intensity > 0.01f){
//            if(state.life < fadeTime){
//                state.opacity = Math.min(state.life / fadeTime, state.opacity);
//            }else{
//                state.opacity = Mathf.lerpDelta(state.opacity, 1f, 0.004f);
//            }
//            float intensity = state.intensity * state.opacity;
//            float blue = targetBlueTint * intensity;
//
//            Tmp.c2.set(originalAmbient);
//
//            Tmp.c1.set(Color.valueOf("b0b4c3").a((float) (state.opacity)));
//
//            Tmp.c1.b = Mathf.clamp(Tmp.c1.b + blue, 0f, 1f);
//            Tmp.c1.a = 0.6f;
//            ambient.lerp(Tmp.c1, state.opacity);
//        }else{
//            ambient.lerp(originalAmbient, state.opacity);
//        }
    }

    @Override
    public void drawOver(WeatherState state){
        super.drawOver(state);
        if(state.intensity > 0.15f){
            //TODO needs a shader or removal. Yet to decide
            float alpha = state.opacity * 0.2f * state.intensity;
            Draw.color(1f, 1f, 1f, alpha);
            Fill.rect(Core.camera.position.x, Core.camera.position.y, Core.camera.width, Core.camera.height);
            Draw.color();
        }
    }
}