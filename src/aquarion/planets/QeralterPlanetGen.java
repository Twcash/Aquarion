package aquarion.planets;

import arc.graphics.Color;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.math.geom.Vec3;
import arc.struct.Seq;
import arc.util.noise.Simplex;
import mindustry.ai.Astar;
import mindustry.content.Blocks;
import mindustry.maps.generators.PlanetGenerator;
import mindustry.world.Block;
import mindustry.world.Tiles;
//this fucking sucks
public class QeralterPlanetGen extends PlanetGenerator {
    // Single wall and floor blocks for simplicity
    Block floorBlock = Blocks.sand;
    Block wallBlock = Blocks.stoneWall;

    @Override
    public void generate() {
        cells(4);
        distort(10f, 12f);

        // Define player spawn area in the center
        int centerX = width / 2;
        int centerY = height / 2;
        int spawnRadius = 8;
        erase(centerX, centerY, spawnRadius);

        // Surround the map with walls
        createEnclosure();

        // Define enemy spawn points along the edges (in wall gaps)
        int gapCount = 4;
        int gapSize = 3;
        Seq<Vec2> spawnPoints = new Seq<>();
        createWallGaps(gapCount, gapSize, spawnPoints);

        // Create paths from enemy spawns to the player spawn
        for (Vec2 spawn : spawnPoints) {
            createPath((int) spawn.x, (int) spawn.y, centerX, centerY, 5);
        }

        // Set all tiles to the default floor block
        pass((x, y) -> floor = floorBlock);

        // Place random obstacles inside the sector
        float wallChance = 0.1f;
        pass((x, y) -> {
            if (rand.chance(wallChance) && !Mathf.within(x, y, centerX, centerY, spawnRadius)) {
                block = wallBlock;
            }
        });

        // Mark enemy spawns
        for (Vec2 spawn : spawnPoints) {
            tiles.getn((int) spawn.x, (int) spawn.y).setOverlay(Blocks.spawn);
        }
    }

    private void createEnclosure() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (x == 0 || y == 0 || x == width - 1 || y == height - 1) {
                    tiles.getn(x, y).setBlock(wallBlock);
                }
            }
        }
    }

    private void createWallGaps(int gapCount, int gapSize, Seq<Vec2> spawnPoints) {
        for (int i = 0; i < gapCount; i++) {
            float angle = i * (360f / gapCount);
            int edgeX = (int) ((float) width / 2 + Angles.trnsx(angle, (float) width / 2 - 1));
            int edgeY = (int) ((float) height / 2 + Angles.trnsy(angle, (float) height / 2 - 1));

            // Create gaps in the wall
            for (int j = -gapSize / 2; j <= gapSize / 2; j++) {
                int gapX = (int) (edgeX + Angles.trnsx(angle + 90, j));
                int gapY = (int) (edgeY + Angles.trnsy(angle + 90, j));
                tiles.getn(gapX, gapY).setBlock(Blocks.air);
            }

            // Add a spawn point at the center of the gap
            spawnPoints.add(new Vec2(edgeX, edgeY));
        }
    }

    private void createPath(int x1, int y1, int x2, int y2, int pathWidth) {
        brush(pathfind(x1, y1, x2, y2, tile -> tile.solid() ? 50f : 0f, Astar.manhattan), pathWidth);
    }

    @Override
    public void postGenerate(Tiles tiles) {
        // No additional steps needed for this simple generator
    }
//End Of Sector Gen Planet Mesh Gen Starts Here
    //TODO make the planet gen look better in general
    Color c1 = Color.valueOf("5057a6"), c2 = Color.valueOf("272766"), out = new Color();
    Color c3 = Color.valueOf("727be2"), c4 = Color.valueOf("#90aae4");
    Color c5 = Color.valueOf("485256"), c6 = Color.valueOf("#909fa5");
    Color c7 = Color.valueOf("45526f"), c8 = Color.valueOf("#96a2bd");
    float scl = 5f;
    float waterOffset = 0.5f; // Offset for water level (adjust as needed)

    float water = 0.5f ;
    @Override
    public float getHeight(Vec3 position){
        // Base height using noise
        float poles = Math.abs(position.y);
        float depth = Simplex.noise3d(seed, 4, 0.55f, 1.7f, position.x, position.y, position.z);
        float baseHeight = rawHeight(position);

        // Calculate final height with clamping for oceans and smooth transitions
        float height = baseHeight + poles * 0.4f - depth * 0.3f;
        return height < 0.4f ? 0.2f : Mathf.clamp(height);
    }

    @Override
    public Color getColor(Vec3 position){
        float height = getHeight(position);

        if (height <= 0.2f) {
            // Ocean color
            return c1.write(out).lerp(c2, height / 0.2f).a(0.8f);
        } else if (height <= 0.4f) {
            // Coastal/low spots near water
            return c7.write(out).lerp(c8, (height - 0.2f) / 0.2f).a(0.9f);
        } else {
            // Land color
            return c5.write(out).lerp(c6, (height - 0.4f) / 0.6f).a(1f);
        }
    }

    float rawHeight(Vec3 position){
        // Noise function for raw height data
        return Simplex.noise3d(seed, 8, 0.7f, 1f, position.x, position.y, position.z);
    }
}