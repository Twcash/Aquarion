package aquarion.world.AI;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import mindustry.core.World;
import mindustry.graphics.Layer;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class FishAIDangerMap {
    public static final float[][] dangerMap = new float[world.width()][world.height()];
    private static final float decayRate = 0.000001f;
    private static final float maxDanger = 1f;

    public static void update(){
        for(int x = 0; x < world.width(); x++){
            for(int y = 0; y < world.height(); y++){
                float value = dangerMap[x][y];

                if(value > 0.9f){
                    overflow(x, y, value);
                }

                if(value > 0f){
                    dangerMap[x][y] = Math.max(0f, value - decayRate);
                }
            }
        }
    }

    public static void addDeath(float worldX, float worldY){
        int tx = World.toTile(worldX), ty = World.toTile(worldY);
        if(!withinMap(tx, ty)) return;

        float added = 0.2f;
        dangerMap[tx][ty] = Math.min(maxDanger, dangerMap[tx][ty] + added);

        if(dangerMap[tx][ty] >= 0.9f){
            overflow(tx, ty, dangerMap[tx][ty]);
        }
    }
    private static void overflow(int x, int y, float value){
        float spread = value * 0.25f;
        float reduced = value * 0.75f;

        dangerMap[x][y] = Mathf.clamp(reduced, 0f, maxDanger);

        for(int dx = -1; dx <= 1; dx++){
            for(int dy = -1; dy <= 1; dy++){
                if(dx == 0 && dy == 0) continue;

                int nx = x + dx, ny = y + dy;
                if(!withinMap(nx, ny)) continue;

                dangerMap[nx][ny] = Mathf.clamp(dangerMap[nx][ny] + spread / 8f, 0f, maxDanger);

                if(dangerMap[nx][ny] >= 0.9f){
                    overflow(nx, ny, dangerMap[nx][ny]);
                }
            }
        }
    }

    public static float getDanger(float worldX, float worldY){
        int tx = World.toTile(worldX), ty = World.toTile(worldY);
        if(!withinMap(tx, ty)) return 0f;

        return dangerMap[tx][ty];
    }
    public static void draw(){
        Draw.z(Layer.fogOfWar + 1);

        for(int x = 0; x < world.width(); x++){
            for(int y = 0; y < world.height(); y++){
                float danger = dangerMap[x][y];
                if(danger > 0.01f){
                    float alpha = Mathf.clamp(danger, 0f, 1f);

                    Draw.color(Color.red, alpha);
                    Fill.rect(x * tilesize + tilesize / 2f, y * tilesize + tilesize / 2f, tilesize, tilesize);
                }
            }
        }

        Draw.reset();
    }
    private static boolean withinMap(int x, int y){
        return x >= 0 && y >= 0 && x < world.width() && y < world.height();
    }
}