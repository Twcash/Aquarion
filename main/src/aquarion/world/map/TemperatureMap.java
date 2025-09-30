package aquarion.world.map;

import aquarion.gen.HeatMapUpdater;
import aquarion.gen.HeatMapUpdaterc;
import arc.Events;
import arc.math.Mathf;
import arc.util.Nullable;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.core.World;
import mindustry.game.EventType;
import mindustry.io.SaveFileReader;
import mindustry.io.SaveVersion;
import mindustry.world.meta.Env;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

import static mindustry.Vars.player;
import static mindustry.Vars.world;

//Loosely Based on FOS' bug death map.
public class TemperatureMap implements SaveFileReader.CustomChunk {
    public volatile short[] tempMap;
    public boolean shouldSave;
    private final @Nullable HeatMapUpdater entity;

    public TemperatureMap(){
        this.entity = HeatMapUpdater.create();
        this.entity.map = this;
        Events.on(EventType.ResetEvent.class, e -> tempMap = null);
        Events.on(EventType.WorldLoadEvent.class, e -> {
            tempMap = new short[world.width() * world.height()];
//            if((Vars.state.rules.env & Env.scorching)!= 0){Arrays.fill(map, (short) 100); curAmbient = 100;}else{
//                Arrays.fill(map, (short) 30);
//                curAmbient = 30;
//            };
        });
        Events.on(EventType.BlockDestroyEvent.class, e -> {
            int ti = e.tile.array();
            tempMap[ti] += 25;

            e.tile.circle(3, (tile) -> {
                if (tile == null) return;
                tempMap[tile.array()] += 25;

                if (tempMap[tile.array()] < 0) {
                    tempMap[tile.array()] = Short.MAX_VALUE;
                }
                shouldSave = true;
            });
        });
        SaveVersion.addCustomChunk("aqua-heat-map", this);
    }
    public void addHeat(float x, float y, float amount){
        if(Vars.world == null || Vars.world.width() == 0) return;

        tempMap[world.tile((int)x, (int)y).array()] += (short) amount;
    }
    public void update(){
        if(player != null)addHeat(player.x, player.y, 1);
        if(tempMap == null) return;
        int width = world.width();
        int height = world.height();
        short[] newMap = new short[tempMap.length];
        //TODO. Make certain planets/lack of dissipate and diffuse heat at different rates
        float diffusionRate = 0.005f;
        float dissipationRate = 0.003f;
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                int i = x + y * width;
                float temp = tempMap[i];
                float total = temp;
                int count = 1;
                if(x > 0){ total += tempMap[i - 1]; count++; }
                if(x < width - 1){ total += tempMap[i + 1]; count++; }
                if(y > 0){ total += tempMap[i - width]; count++; }
                if(y < height - 1){ total += tempMap[i + width]; count++; }
                float avg = total / count;
                float diffused = Mathf.lerp(temp, avg, diffusionRate);
                float dissipated = Mathf.lerp(diffused, 0, dissipationRate);
                newMap[i] = (short)dissipated;
            }
        }
        tempMap = newMap;
    }

    @Override
    public boolean shouldWrite() {
        return tempMap != null && shouldSave;
    }
    @Override
    public void write(DataOutput dataOutput) throws IOException {
        Writes writes = new Writes(dataOutput);
        writes.i(tempMap.length);
        for (short s : tempMap) {
            writes.s(s);
        }
    }
    @Override
    public void read(DataInput dataInput) throws IOException {
        Reads reads = new Reads(dataInput);
        for (int i = 0; i < tempMap.length; i++) {
            tempMap[i] = reads.s();
        }

    }
}
