package aquarion.planets;

import aquarion.content.AquaLiquids;
import aquarion.content.AquaWeathers;
import aquarion.world.content.AquaLiquid;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Vec3;
import arc.struct.ObjectIntMap;
import arc.struct.ObjectSet;
import arc.struct.Seq;
import arc.util.noise.*;
import mindustry.content.Blocks;
import mindustry.content.Liquids;
import mindustry.content.Weathers;
import mindustry.ctype.UnlockableContent;
import mindustry.game.Rules;
import mindustry.maps.generators.PlanetGenerator;
import mindustry.type.Liquid;
import mindustry.type.Sector;
import mindustry.type.Weather;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.meta.Env;

import static mindustry.Vars.world;

public class AquaPlanetGenerator extends PlanetGenerator {

    Color w1 = Color.valueOf("#6d7cdf"), w2 = Color.valueOf("#6058ba"),
    iw1 = Color.valueOf("#9fa7de"), iw2 = Color.valueOf("#828edd"),
    ice1 = Color.valueOf("#b2c3cb"), ice2 = Color.valueOf("#97adb5"),
    bice1 = Color.valueOf("#aab2d2"), bice2 = Color.valueOf("#8e9fc1"),
    cap1 = Color.valueOf("#d3dce7"), cap2 = Color.valueOf("#bad2e2");

    public AquaPlanetGenerator() {
        baseSeed = 12345;
    }

    public Color makeColor(Vec3 position) {
        float poles = Math.abs(position.y);
        float base = Simplex.noise3d(seed, 6, 0.7f, 0.8f, position.x, position.y, position.z);
        float raw = rawHeight(position);
        float t = Mathf.clamp(Mathf.round(base, 0.15f));
        float slope = getSlope(position, 0.05f);
        float cliffs = Ridged.noise3d(baseSeed, position.x, position.y, position.z, 5, 2f);

        boolean isIce = raw > 0f || slope > 0.08f;
        // Land masses
        if(isIce){
            if(poles + base * 0.5f > capThresh){
                return cap1.lerp(cap2, t);
            };
            float factice = (poles - ridgeThresh) / ((capThresh - 0.2f) - ridgeThresh);
            if(factice * cliffs > 0.2f){
                return bice1.lerp(bice2, t);
            }
            return ice1.lerp(ice2, t);
        }

        float factwater = (poles - ridgeThresh * 0.8f) / ((capThresh) - ridgeThresh * 0.8f);
        float spike = Simplex.noise3d(seed + 2, 8, 0.55f, 1.4f, position.x, position.y, position.z);
        // ice water
        if(factwater > 0f && cliffs > 0.18f){
            return iw1.lerp(iw2, cliffs);
        }
        // Random ice water patches
        if(spike * base * factwater * 2f > 0.23f){
            return iw1.lerp(iw2, spike);
        }

        return w1.lerp(w2, t);
    }
    
    float capThresh = 1.13f;
    float ridgeThresh = 0.42f;

    float spikeThresh = 0.8f;
    float bergThresh = 0.65f;

    float rawHeight(Vec3 pos){
        float poles = Math.abs(pos.y);
        float base = Simplex.noise3d(seed, 6, 0.7f, 0.8f, pos.x, pos.y, pos.z);
        float cliffs = Ridged.noise3d(baseSeed, pos.x, pos.y, pos.z, 5, 2f);
        
        // pole caps
        if(poles + base * 0.5f > capThresh){
            return 0.23f + Simplex.noise3d(seed, 5, 0.7f, 0.86f, pos.x, pos.y, pos.z) * 0.7f;
        }
        // ridges near pole (bumpy mountains)
        if(poles > ridgeThresh){
            float fact = (poles - ridgeThresh) / ((capThresh - 0.2f) - ridgeThresh);
            return (cliffs * fact > 0.05f) ? cliffs * 0.37f : 0f;
        }
        // Ice spikes near the pole caps
        if(poles > 0.3f){
            float spike = Simplex.noise3d(seed + 2, 8, 0.55f, 1.4f, pos.x, pos.y, pos.z);
            if(spike > spikeThresh){
                float fact = Mathf.clamp((poles - 0.4f) / (ridgeThresh - 0.4f));
                return (spike - spikeThresh) / (1f - spikeThresh) * 0.3f * fact;
            }
        }
        // bergurs
        float berg = Simplex.noise3d(seed + 1, 5, 0.6f, 1.3f, pos.x, pos.y, pos.z);
        if(berg > bergThresh){
            return Mathf.pow((berg - bergThresh) / (1f - bergThresh), 0.5f) * 0.5f;
        }

        return 0f;
    }

    // I knew this will come in handy from minedusty :D
    /** Get slope of position with sample radius */
	float getSlope(Vec3 position, float sampleRadius) {
		float center = getHeight(position);
		Vec3[] samples = {
			position.cpy().add(sampleRadius, 0, 0),
			position.cpy().add(-sampleRadius, 0, 0),
			position.cpy().add(0, 0, sampleRadius),
			position.cpy().add(0, 0, -sampleRadius)
		};
		
		float slopeSum = 0f;
		for(Vec3 sample : samples) {
			slopeSum += Math.abs(center - getHeight(sample));
		}
		return slopeSum / samples.length;
	}

    @Override
    public float getHeight(Vec3 position){
        return rawHeight(position);
    }

    @Override
    public void getColor(Vec3 position, Color out) {
        out.set(makeColor(position));
    }
    @Override
    public void addWeather(Sector sector, Rules rules){

        //apply weather based on terrain
        ObjectIntMap<Block> floorc = new ObjectIntMap<>();
        ObjectSet<UnlockableContent> content = new ObjectSet<>();

        for(Tile tile : world.tiles){
            if(world.getDarkness(tile.x, tile.y) >= 3){
                continue;
            }

            Liquid liquid = tile.floor().liquidDrop;
            if(tile.floor().itemDrop != null) content.add(tile.floor().itemDrop);
            if(tile.overlay().itemDrop != null) content.add(tile.overlay().itemDrop);
            if(liquid != null) content.add(liquid);

            if(!tile.block().isStatic()){
                floorc.increment(tile.floor());
                if(tile.overlay() != Blocks.air){
                    floorc.increment(tile.overlay());
                }
            }
        }

        //sort counts in descending order
        Seq<ObjectIntMap.Entry<Block>> entries = floorc.entries().toArray();
        entries.sort(e -> -e.value);
        //remove all blocks occurring < 30 times - unimportant
        entries.removeAll(e -> e.value < 30);

        Block[] floors = new Block[entries.size];
        for(int i = 0; i < entries.size; i++){
            floors[i] = entries.get(i).key;
        }

        boolean underwater = rules.hasEnv(Env.underwater);
        boolean hasSnow = floors.length > 0 && !underwater && (floors[0].name.contains("ice") || floors[0].name.contains("snow"));
        boolean hasRain = floors.length > 0 && !hasSnow && !underwater && content.contains(AquaLiquids.halideWater) && !floors[0].name.contains("sand");

        if(underwater){
            rules.weather.add(new Weather.WeatherEntry(AquaWeathers.sedimentDisturance));
            rules.weather.add(new Weather.WeatherEntry(AquaWeathers.currents));
            rules.weather.add(new Weather.WeatherEntry(AquaWeathers.bioluminescentBlooms));
        }
        if(hasSnow){
            rules.weather.add(new Weather.WeatherEntry(Weathers.snow));
            rules.weather.add(new Weather.WeatherEntry(AquaWeathers.blizzard));
        }
        if(hasRain){
            rules.weather.add(new Weather.WeatherEntry(Weathers.rain));
            rules.weather.add(new Weather.WeatherEntry(AquaWeathers.monsoon));
        }
    }
}