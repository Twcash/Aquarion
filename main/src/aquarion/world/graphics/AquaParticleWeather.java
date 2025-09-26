package aquarion.world.graphics;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.Texture;
import arc.graphics.g2d.TextureRegion;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.game.Rules;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.gen.WeatherState;
import mindustry.type.Weather;

import java.util.Iterator;

public class AquaParticleWeather extends Weather {
    public String particleRegion = "circle-shadow";
    public Color color;
    public TextureRegion region;
    public float yspeed;
    public float xspeed;
    public float padding;
    public float sizeMin;
    public float sizeMax;
    public float density;
    public float minAlpha;
    public float maxAlpha;
    public float force;
    public float noiseScale;
    public float baseSpeed;
    public float sinSclMin;
    public float sinSclMax;
    public float sinMagMin;
    public float sinMagMax;
    public Color noiseColor;
    public boolean drawNoise;
    public boolean drawParticles;
    public boolean useWindVector;
    public boolean randomParticleRotation;
    public int noiseLayers;
    public float noiseLayerSpeedM;
    public float noiseLayerAlphaM;
    public float noiseLayerSclM;
    public float noiseLayerColorM;
    public String noisePath;
    public boolean fogger = true;
    @Nullable
    public Texture noise;
    public boolean noiseOverride = false;
    public Seq<Rules> rules = new Seq<>();
    public AquaParticleWeather(String name) {
        super(name);
        this.color = Color.white.cpy();
        this.yspeed = -2.0F;
        this.xspeed = 0.25F;
        this.padding = 16.0F;
        this.sizeMin = 2.4F;
        this.sizeMax = 12.0F;
        this.density = 1200.0F;
        this.minAlpha = 1.0F;
        this.maxAlpha = 1.0F;
        this.force = 0.0F;
        this.noiseScale = 2000.0F;
        this.baseSpeed = 6.1F;
        this.sinSclMin = 30.0F;
        this.sinSclMax = 80.0F;
        this.sinMagMin = 1.0F;
        this.sinMagMax = 7.0F;
        this.noiseColor = this.color;
        this.drawNoise = false;
        this.drawParticles = true;
        this.useWindVector = false;
        this.randomParticleRotation = false;
        this.noiseLayers = 1;
        this.noiseLayerSpeedM = 1.1F;
        this.noiseLayerAlphaM = 0.8F;
        this.noiseLayerSclM = 0.99F;
        this.noiseLayerColorM = 1.0F;
        this.noisePath = "noiseAlpha";
    }

    public void load() {
        super.load();
        this.region = Core.atlas.find(this.particleRegion);
        if (this.noiseOverride && drawNoise) {
            Core.atlas.find(noisePath);
        } else {
            if (this.drawNoise && Core.assets != null) {
                Core.assets.load("sprites/" + this.noisePath + ".png", Texture.class);
            }
        }
    }

    public void update(WeatherState state) {
        float speed = this.force * state.intensity * Time.delta;
        if (speed > 0.001F) {
            float windx = state.windVector.x * speed;
            float windy = state.windVector.y * speed;
            Iterator var5 = Groups.unit.iterator();
            if (fogger) {
                Vars.state.rules.fog = true;
            }
            while(var5.hasNext()) {
                Unit unit = (Unit)var5.next();
                unit.impulse(windx, windy);
            }
        } else {
            Vars.state.rules.fog = false;
        }

    }

    public void drawOver(WeatherState state) {
        float windx;
        float windy;
        float sspeed;
        if (this.useWindVector) {
            sspeed = this.baseSpeed * state.intensity;
            windx = state.windVector.x * sspeed;
            windy = state.windVector.y * sspeed;
        } else {
            windx = this.xspeed;
            windy = this.yspeed;
        }

        if (this.drawNoise) {
            if (this.noise == null) {
                this.noise = (Texture)Core.assets.get("sprites/" + this.noisePath + ".png", Texture.class);
                this.noise.setWrap(Texture.TextureWrap.repeat);
                this.noise.setFilter(Texture.TextureFilter.linear);
            }

            sspeed = 1.0F;
            float sscl = 1.0F;
            float salpha = 1.0F;
            float offset = 0.0F;
            Color col = Tmp.c1.set(this.noiseColor);

            for(int i = 0; i < this.noiseLayers; ++i) {
                drawNoise(this.noise, this.noiseColor, this.noiseScale * sscl, state.opacity * salpha * this.opacityMultiplier, sspeed * (this.useWindVector ? 1.0F : this.baseSpeed), state.intensity, windx, windy, offset);
                sspeed *= this.noiseLayerSpeedM;
                salpha *= this.noiseLayerAlphaM;
                sscl *= this.noiseLayerSclM;
                offset += 0.29F;
                col.mul(this.noiseLayerColorM);
            }
        }

        if (this.drawParticles) {
            drawParticles(this.region, this.color, this.sizeMin, this.sizeMax, this.density, state.intensity, state.opacity, windx, windy, this.minAlpha, this.maxAlpha, this.sinSclMin, this.sinSclMax, this.sinMagMin, this.sinMagMax, this.randomParticleRotation);
        }

    }
}
