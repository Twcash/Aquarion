package aquarion.content;

import aquarion.AquaSounds;
import aquarion.world.graphics.AquaParticleWeather;
import arc.graphics.Color;
import arc.util.Time;
import mindustry.content.StatusEffects;
import mindustry.gen.Sounds;
import mindustry.type.Weather;
import mindustry.type.weather.ParticleWeather;

public class AquaWeathers {
    public static Weather currents, sedimentDisturance, volcanicFallout, bioluminescentBlooms;
    public static void load(){
        bioluminescentBlooms = new ParticleWeather("bioluminescent-blooms"){{
            color =   Color.valueOf("currents");
            noiseColor = Color.valueOf("63f0b2");
            color = Color.valueOf("48fff0");
            noisePath = "fog";
            noiseLayers = 3;
            noiseLayerAlphaM = 0.3f;
            noiseLayerSpeedM = 0.9f;
            noiseLayerSclM = 0.6f;
            noiseScale = 3000;
            xspeed = 1f;
            yspeed = 0.01f;
            drawNoise = true;
            opacityMultiplier = 2f;
            statusGround = false;
            useWindVector = true;
            hidden = false;
            sizeMax = 700;
            sizeMin = 10;
            minAlpha = 0.1f;
            maxAlpha = 0.9f;
            density = 90000;
            baseSpeed = 0.5f;
            duration = 2 * Time.toMinutes;
        }};
        volcanicFallout = new AquaParticleWeather("volcanic-fallout"){{
            color =   Color.valueOf("393837");
            noiseColor = Color.valueOf("22201e");
            noiseLayers = 1;
            noiseLayerColorM = 0.9f;
            noiseLayerSclM = 1.1f;
            particleRegion = "circle";
            maxAlpha = 0.6f;
            minAlpha = 0.2f;
            sizeMax = 15;
            sizeMin = 4;
            noiseLayerAlphaM = 1.2f;
            noiseLayerSpeedM = 3.5f;
            noiseScale = 1700;
            density = 45000;
            force = 0.4f;
            opacityMultiplier = 0.001f;
            baseSpeed = 3;
            xspeed = 45;
            yspeed = 8;
            useWindVector = true;
            status = StatusEffects.slow;
            fogger = true;
            duration = 10 * Time.toMinutes;
        }};
        sedimentDisturance = new ParticleWeather("sediment-disturbance"){{
            noiseColor = Color.valueOf("e56f26");
            noisePath = "noise";
            noiseLayers = 2;
            noiseLayerAlphaM = 0.8f;
            status = StatusEffects.muddy;
            noiseLayerSpeedM = 0.6f;
            noiseLayerSclM = 0.8f;
            noiseScale = 900;
            xspeed = 0.6f;
            yspeed = 0.02f;
            drawNoise = true;
            drawParticles = false;
            opacityMultiplier = 0.25f;
            statusGround = false;
            useWindVector = true;
            hidden = false;
            baseSpeed = 1.5f;
            soundVol = 0f;
            duration = 7 * Time.toMinutes;
        }};
        currents = new ParticleWeather("currents"){{
            color =   Color.valueOf("99abd3");
            noiseColor = Color.valueOf("6678a1");
            noisePath = "fog";
            noiseLayers = 4;
            noiseLayerAlphaM = 0.7f;
            noiseLayerSpeedM = 0.4f;
            noiseLayerSclM = 0.6f;
            noiseScale = 1100f;
            xspeed = 0.8f;
            yspeed = 0.01f;
            drawNoise = true;
            opacityMultiplier = 0.35f;

            particleRegion = "particle";
            statusGround = false;
            useWindVector = true;
            hidden = false;
            sizeMax = 20f;
            sizeMin = 1f;
            minAlpha = 0.1f;
            maxAlpha = 0.9f;
            density = 18000f;
            baseSpeed = 2.5f;
            sound = Sounds.wind2;
            soundVol = 0f;
            soundVolOscMag = 1.5f;
            soundVolOscScl = 1100f;
            soundVolMin = 0.02f;
            duration = 8 * Time.toMinutes;
        }};
    }
}
