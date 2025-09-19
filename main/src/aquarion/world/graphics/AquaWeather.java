package aquarion.world.graphics;

import mindustry.content.StatusEffects;
import mindustry.gen.Sounds;
import mindustry.type.Weather;
import mindustry.type.weather.RainWeather;
import mindustry.world.meta.Attribute;

public class AquaWeather {
    public static Weather
            torrentialRain;
    public static void load() {
        torrentialRain = new RainWeather("torrential-Rain") {{
            attrs.set(Attribute.light, -0.45f);
            attrs.set(Attribute.water, 0.3f);
            status = StatusEffects.wet;
            sound = Sounds.rain;
            soundVol = 0.35f;
            density = 3500;
            yspeed = 9;
            xspeed = 3;
        }};
    }
}
