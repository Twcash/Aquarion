package aquarion.content;

import aquarion.AquaSounds;
import arc.graphics.Color;
import arc.util.Time;
import mindustry.gen.Sounds;
import mindustry.type.Weather;
import mindustry.type.weather.ParticleWeather;

public class AquaWeathers {
    public static Weather currents;
    public static void load(){
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
