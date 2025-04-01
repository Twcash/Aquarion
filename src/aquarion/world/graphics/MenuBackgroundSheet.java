package aquarion.world.graphics;
import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.gl.FrameBuffer;
import arc.math.*;
import arc.math.geom.Geometry;
import arc.scene.ui.layout.*;
import arc.util.Log;
import arc.util.Nullable;
import arc.util.Time;
import mindustry.ai.types.CommandAI;
import mindustry.content.*;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.CacheLayer;
import mindustry.graphics.Layer;
import mindustry.graphics.Shaders;
import mindustry.world.*;

import static arc.Core.graphics;
import static arc.Core.settings;
import static mindustry.Vars.*;
import static mindustry.game.Team.*;
import static mindustry.graphics.CacheLayer.slag;

public class MenuBackgroundSheet extends MenuBackground {

    public @Nullable Bloom bloom;
    private final int width = mobile ? 60 : 100, height = mobile ? 40 : 50;
    private CacheBatch batch;
    private final Camera camera = new Camera();
    private final Mat mat = new Mat();
    private int cacheFloor, cacheWall;
    public Unit unit;
    public MenuBackgroundSheet() {
        init = menu -> {
            renderer.init();
            generateWorld();
            cacheTiles();
            spawnUnits();
            Shaders.init();
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
                tile.setFloor(Blocks.slag.asFloor());
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
        Unit unit1 = UnitTypes.flare.spawn(sharded, 3 * width * tilesize / 4f - 10, height * tilesize / 2f);
        Unit unit2 = UnitTypes.flare.spawn(Team.crux, 3 * width * tilesize / 4f, height * tilesize / 2f);
        unit1.health = unit2.health = 200;
        unit1.add();
        unit2.add();
    }

    @Override
    public void render(){
        float scaling = Math.max(Scl.scl(4f), Math.max(
                graphics.getWidth() / ((width - 1f) * tilesize),
                graphics.getHeight() / ((height - 1f) * tilesize)
        ));
        camera.position.set(width * tilesize / 2f, height * tilesize / 2f);
        camera.resize(graphics.getWidth() / scaling, graphics.getHeight() / scaling);

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
        renderer.bloom.render();

        Groups.bullet.each(Bullet::draw);
        Draw.proj(mat);
        renderer.blocks.processBlocks();
        renderer.blocks.drawBlocks();
        renderer.blocks.drawDestroyed();
        renderer.blocks.floor.beginDraw();
        renderer.lights.draw();
        Draw.draw(Layer.floor, renderer.blocks.floor::drawFloor);
        Draw.draw(Layer.block - 1, renderer.blocks::drawShadows);
        Draw.draw(Layer.light, renderer.lights::draw);
        Draw.draw(Layer.block - 0.09f, () -> {
            renderer.blocks.floor.beginDraw();
            renderer.blocks.floor.drawLayer(CacheLayer.walls);
            renderer.blocks.floor.endDraw();
        });
        if(renderer.bloom != null){
            renderer.bloom.resize(graphics.getWidth(), graphics.getHeight());
            renderer.bloom.setBloomIntensity(settings.getInt("bloomintensity", 6) / 4f + 1f);
            renderer.bloom.blurPasses = settings.getInt("bloomblur", 1);
            Draw.draw(Layer.bullet - 0.02f, renderer.bloom::capture);
            Draw.draw(Layer.effect + 0.02f, renderer.bloom::render);
        }
        Draw.drawRange(Layer.buildBeam, 1f, () -> renderer.effectBuffer.begin(Color.clear), () -> {
            renderer.effectBuffer.end();
            renderer.effectBuffer.blit(Shaders.buildBeam);
        });
        renderer.blocks.drawBlocks();

        Groups.draw.draw(Drawc::draw);

        Draw.reset();
        Draw.flush();
        Draw.sort(false);

    }
    public void dispose() {
        batch.dispose();
        renderer.bloom.dispose();
    }
}