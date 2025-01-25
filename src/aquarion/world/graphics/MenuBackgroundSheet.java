package aquarion.world.graphics;
import aquarion.blocks.AquaEnv;
import aquarion.units.AquaUnitTypes;
import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.gl.FrameBuffer;
import arc.math.*;
import arc.math.geom.Rect;
import arc.scene.ui.layout.*;
import arc.util.Log;
import arc.util.Nullable;
import arc.util.Time;
import mindustry.Vars;
import mindustry.ai.types.CommandAI;
import mindustry.content.*;
import mindustry.core.GameState;
import mindustry.entities.Effect;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.graphics.LightRenderer;
import mindustry.graphics.Shaders;
import mindustry.type.Weather;
import mindustry.world.*;

import java.util.Random;

import static arc.Core.graphics;
import static arc.Core.settings;
import static mindustry.Vars.*;
import static mindustry.game.Team.*;

public class MenuBackgroundSheet extends MenuBackground {

    public @Nullable Bloom bloom;
    private final int width = mobile ? 60 : 100, height = mobile ? 40 : 50;
    private FrameBuffer shadows;
    private CacheBatch batch;
    private final Camera camera = new Camera();
    private final Mat mat = new Mat();
    private int cacheFloor, cacheWall;
    private float time = 0f;
    public Unit unit;
    public MenuBackgroundSheet() {
        init = menu -> {
            setupBloom();
            generateWorld();
            cacheTiles();
            spawnUnits();
        };
    }

    private void setupBloom() {
        if (settings.getBool("bloom", !ios)) {
            try {
                bloom = new Bloom(true);
            } catch (Throwable e) {
                settings.put("bloom", false);
                ui.showErrorMessage("@error.bloom");
                Log.err(e);
            }
        }
    }

    private void generateWorld() {
        world.setGenerating(true);
        player.team(sharded);

        Tiles tiles = world.resize(width, height);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Tile tile;
                tiles.set(x, y, (tile = new CachedTile()));
                tile.x = (short)x;
                tile.y = (short)y;
                tile.setFloor(Blocks.metalFloor.asFloor());
                if (tile.build != null) {
                    Building building = tile.block().newBuilding();
                    if (building != null) {
                        building.add(); // Add to Groups.build
                        Log.info("Added building: @ at (@, @)", building.block, tile.x, tile.y);
                    } else {
                        tile.setBlock(Blocks.copperWall, blue);
                    }
                }
            }
        }
        world.setGenerating(false);
    }

    private void cacheTiles() {
        // Prepare shadows
        Batch prev = Core.batch;

        batch = new CacheBatch(new SpriteCache(width * height * 6, false));
        Core.batch = batch;

        batch.beginCache();
        for (Tile tile : world.tiles) {
            if (tile.floor() != null) {
                tile.floor().drawBase(tile);
            }
        }
        cacheFloor = batch.endCache();

        batch.beginCache();
        for(Tile tile : world.tiles){
            tile.block().drawBase(tile);
            Groups.build.each(building -> {
                if (building != null && building.block != null) {
                    building.draw();
                }
            });
        }

        cacheWall = batch.endCache();

        Core.batch = prev;
    }


    private void spawnUnits() {
        Unit unit1 = UnitTypes.toxopid.spawn(sharded, 3 * width * tilesize / 4f - 10, height * tilesize / 2f);
        Unit unit2 = UnitTypes.toxopid.spawn(Team.crux, 3 * width * tilesize / 4f, height * tilesize / 2f);
        unit1.health = unit2.health = 200;
        unit1.add();
        unit2.add();
    }

    @Override
    public void render() {
        time += Time.delta;
        float scaling = Math.max(Scl.scl(4f), Math.max(
                Core.graphics.getWidth() / ((width - 1f) * tilesize),
                Core.graphics.getHeight() / ((height - 1f) * tilesize)
        ));
        camera.position.set(width * tilesize / 2f, height * tilesize / 2f);
        camera.resize(Core.graphics.getWidth() / scaling, Core.graphics.getHeight() / scaling);

        mat.set(Draw.proj());
        Draw.flush();
        Draw.proj(camera);

        batch.setProjection(camera.mat);
        batch.beginDraw();
        batch.drawCache(cacheFloor);
        batch.endDraw();

        Draw.color();
        Draw.flush();
        batch.beginDraw();
        batch.drawCache(cacheWall);
        batch.endDraw();
        Groups.bullet.each(Bullet::update);
        Tiles tiles = world.resize(width, height);

        Groups.build.each(building -> {
            if (building != null && building.block != null) {
                building.draw();
            }
        });
        Groups.bullet.each(Bullet::draw);

        if (bloom != null) {
            bloom.capture();
            bloom.render();
        }
        Draw.proj(mat); // Restore the original projection matrix
    }
    public void dispose() {
        batch.dispose();
        if (bloom != null) bloom.dispose();
    }
}
