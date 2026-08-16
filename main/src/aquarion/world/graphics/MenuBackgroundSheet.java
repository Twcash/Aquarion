package aquarion.world.graphics;

import aquarion.AquaLoader;
import aquarion.content.AquaUnitTypes;
import arc.Core;
import arc.Events;
import arc.files.Fi;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mat;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.IntSeq;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.noise.Simplex;
import mindustry.ai.Astar;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.core.World;
import mindustry.entities.Units;
import mindustry.entities.units.AIController;
import mindustry.entities.units.WeaponMount;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.game.Teams.TeamData;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Decal;
import mindustry.gen.Drawc;
import mindustry.gen.EffectState;
import mindustry.gen.Entityc;
import mindustry.gen.Fire;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.gen.Puddle;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.graphics.CacheLayer;
import mindustry.graphics.Layer;
import mindustry.io.MapIO;
import mindustry.io.SaveIO;
import mindustry.maps.Map;
import mindustry.mod.Mods;
import mindustry.type.UnitType;
import mindustry.world.Block;
import mindustry.world.CachedTile;
import mindustry.world.Tile;
import mindustry.world.Tiles;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.blocks.storage.CoreBlock.CoreBuild;
import mindustry.world.blocks.units.Reconstructor;
import mindustry.world.blocks.units.UnitAssembler;
import mindustry.world.blocks.units.UnitFactory;
import mindustry.world.meta.Env;

import static arc.Core.settings;
import static mindustry.Vars.*;
import static mindustry.game.Team.sharded;

public class MenuBackgroundSheet extends MenuBackground {

    private int width = mobile ? 60 : 84, height = mobile ? 40 : 50;
    private float time = 0f;

    private boolean battles = true;
    private int presetIndex = -1;

    /** Map-based spawn anchors - one per fighting team. */
    private final Seq<Vec2> spawns = new Seq<>();
    private boolean hasMapSpawns = false;
    /** Camera view rectangle. */
    private float camX, camY, camW, camH;

    /** The active instance, used by the per-frame uiDrawBegin hook. */
    private static MenuBackgroundSheet instance;
    private static boolean registered = false;

    /** Whether the battleground is currently loaded into the world. */
    private boolean arenaActive = false;

    /** The random teams fighting this round, one per spawn. */
    private final Seq<Team> teams = new Seq<>();
    /** Current day/night lighting. */
    private Color ambient = Color.valueOf("fff2dd");
    /** Screen fade state for the round transition. */
    private float fade = 0f;
    private boolean fading = false, fadingOut = true;
    private static final float fadeDuration = 50f;
    private final Mat mat = new Mat();

    /** Preset unit matchups that are fought out in the menu background. */
    public static final Seq<BattlePreset> presets = Seq.with(
            new BattlePreset("Ground Skirmish")
                    .blue(new UnitWave(AquaUnitTypes.bulwark, 3), new UnitWave(AquaUnitTypes.pugnate, 4), new UnitWave(AquaUnitTypes.crest, 3))
                    .crux(new UnitWave(AquaUnitTypes.reave, 4), new UnitWave(AquaUnitTypes.raze, 1), new UnitWave(AquaUnitTypes.soar, 1), new UnitWave(AquaUnitTypes.crest, 2)),
            new BattlePreset("Air Superiority")
                    .blue(new UnitWave(AquaUnitTypes.crest, 10))
                    .crux(new UnitWave(AquaUnitTypes.crest, 10)),
            new BattlePreset("Heavy Assault")
                    .blue(new UnitWave(AquaUnitTypes.shatter, 2), new UnitWave(AquaUnitTypes.rampart, 4), new UnitWave(AquaUnitTypes.soar, 2))
                    .crux(new UnitWave(AquaUnitTypes.bulwark, 2), new UnitWave(AquaUnitTypes.reave, 4), new UnitWave(AquaUnitTypes.pillage, 2)),
            new BattlePreset("Artillery Line")
                    .blue(new UnitWave(AquaUnitTypes.pillage, 3), new UnitWave(AquaUnitTypes.pugnate, 3), new UnitWave(AquaUnitTypes.crest, 4))
                    .crux(new UnitWave(AquaUnitTypes.shatter, 3), new UnitWave(AquaUnitTypes.rampart, 3), new UnitWave(AquaUnitTypes.crest, 4)),
            new BattlePreset("Naval Skirmish")
                    .blue(new UnitWave(AquaUnitTypes.weld, 4), new UnitWave(AquaUnitTypes.crest, 3))
                    .crux(new UnitWave(AquaUnitTypes.solder, 3), new UnitWave(AquaUnitTypes.crest, 3))
    );

    public MenuBackgroundSheet() {
        instance = this;
        init = menu -> setupArena();
        register();
    }

    /** Generates/loads the battleground and spawns the first battle. */
    private void setupArena() {
        battles = settings.getBool("aquaMenuBattles", true) && !headless;
        spawnUnits();
        arenaActive = true;
    }

    /**
     * The world is rendered on uiDrawBegin - before the scene draws - so the menu UI
     * renders on top with a clean GL/batch state instead of being drawn mid-scene.
     */
    private static void register() {
        if (registered) return;
        registered = true;
        Events.run(EventType.Trigger.uiDrawBegin, () -> {
            if (instance != null) {
                instance.updateAndRender();
            }
        });
    }

    private void generateWorld() {
        player.team(sharded);
        //50/50 day or night
        ambient = Mathf.chance(0.5f) ? Color.valueOf("fff2dd") : Color.valueOf("6b6b8c");
        Log.info("Menu battleground lighting: @", ambient);
        setupRules();

        hasMapSpawns = false;
        Map map = findMenuMap();
        if (map != null && loadBattleMap(map)) {
            width = world.width();
            height = world.height();
            setupMapSpawns();
        } else {
            generateProcedural();
            width = world.width();
            height = world.height();
        }

        computeCamera();
    }

    private void setupRules() {
        //menu rules default to unitCap 0 with unitCapVariable on, so the (non-wave) blue team
        //gets cap-killed instantly while crux (the wave team) does not - disable the cap entirely
        state.rules.disableUnitCap = true;
        //be permissive with environment so no unit is env-killed on spawn
        state.rules.env = Env.any;
        //render a normal lit world (day or night)
        state.rules.lighting = true;
        state.rules.ambientLight = ambient;
        //no waves / spawn knockback on the battleground
        state.rules.waves = false;
        state.rules.attackMode = false;
    }

    /** Picks a random battleground map from the mod's bundled assets/maps/menus folder. */
    private @Nullable Map findMenuMap() {
        Mods.LoadedMod loaded = mods.getMod(AquaLoader.class);
        if (loaded == null) loaded = mods.getMod("aquarion");
        if (loaded == null) return null;

        //navigate segment by segment - ZipFi.child("a/b") does not resolve multi-segment paths
        Fi dir = loaded.root.child("maps").child("menus");
        if (!dir.exists() || !dir.isDirectory()) {
            Log.warn("No maps in mod assets/maps/menus; using procedural terrain.");
            return null;
        }

        Seq<Map> found = new Seq<>();
        for (Fi file : dir.list()) {
            if (file.extEquals("msav")) {
                try {
                    found.add(MapIO.createMap(file, false));
                    Log.info("Found menu battleground map: @", file.nameWithoutExtension());
                } catch (Exception e) {
                    Log.err("Failed to read menu battleground map @", file.name(), e);
                }
            }
        }
        if (found.isEmpty()) {
            Log.warn("No readable .msav maps in assets/maps/menus; using procedural terrain.");
            return null;
        }
        Map selected = found.random();
        Log.info("Selected menu battleground map: @", selected.name());
        return selected;
    }

    private boolean loadBattleMap(Map map) {
        try {
            SaveIO.load(map.file, world.filterContext(map));
            state.map = map;
            //the save may have overwritten the rules; re-apply the battle rules
            setupRules();
            //deep liquid drowns ground units - replace it with shallow water so nothing dies
            for (Tile tile : world.tiles) {
                if (tile.floor().isDeep()) {
                    tile.setFloor(Blocks.water.asFloor());
                }
            }
            //scrub any living entities from the save - keep the terrain and structures
            clearAll();
            Log.info("Loaded menu battleground map: @", map.name());
            return true;
        } catch (Throwable e) {
            Log.err("Failed to load battle map @", map.name(), e);
            return false;
        }
    }

    /**
     * Finds the team spawn points on the map: one per team with a core, or clusters of the map's
     * spawn blocks. Maps with more than two spawns produce more fighting teams.
     */
    private void setupMapSpawns() {
        spawns.clear();
        hasMapSpawns = false;

        //one anchor per team with a core on the map
        for (Team team : Team.all) {
            if (team == Team.derelict || team == Team.neoplastic) continue;
            TeamData data = state.teams.get(team);
            if (data.hasCore()) {
                spawns.add(new Vec2(data.core().x, data.core().y));
            }
        }
        if (spawns.size >= 2) {
            capSpawns();
            hasMapSpawns = true;
            return;
        }

        //no/one core - cluster the map's spawn points into team anchors
        spawns.clear();
        Seq<Vec2> centers = new Seq<>();
        IntSeq counts = new IntSeq();
        for (Tile tile : spawner.getSpawns()) {
            Vec2 p = new Vec2(tile.worldx(), tile.worldy());
            int idx = -1;
            float bestD = 28f; //4 tiles apart is a separate spawn
            for (int i = 0; i < centers.size; i++) {
                float d = centers.get(i).dst(p);
                if (d < bestD) {
                    bestD = d;
                    idx = i;
                }
            }
            if (idx == -1) {
                centers.add(p);
                counts.add(1);
            } else {
                Vec2 c = centers.get(idx);
                int n = counts.get(idx);
                c.set((c.x * n + p.x) / (n + 1), (c.y * n + p.y) / (n + 1));
                counts.set(idx, n + 1);
            }
        }
        if (centers.size >= 2) {
            centers.truncate(4);
            spawns.addAll(centers);
            hasMapSpawns = true;
        }
    }

    /** Keeps the number of teams manageable. */
    private void capSpawns() {
        if (spawns.size > 4) {
            spawns.truncate(4);
        }
    }

    /** Points the camera so the entire map is visible. */
    private void computeCamera() {
        camX = width * tilesize / 2f;
        camY = height * tilesize / 2f;
        camW = width * tilesize;
        camH = height * tilesize;
    }

    private void generateProcedural() {
        world.setGenerating(true);

        Tiles tiles = world.resize(width, height);

        //seeded terrain so every session gets a slightly different battlefield
        int seed = Mathf.rand.nextInt(100000);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Tile tile;
                tiles.set(x, y, (tile = new CachedTile()));
                tile.x = (short)x;
                tile.y = (short)y;

                //border walls keep the battle contained
                if (x == 0 || y == 0 || x == width - 1 || y == height - 1) {
                    tile.setFloor(Blocks.metalFloor.asFloor());
                    tile.setBlock(Blocks.darkMetal);
                    continue;
                }

                //meandering river connecting both sides. shallow water only - drownTime is 0, so nothing drowns
                if (Math.abs(y - riverY(x)) <= 3f) {
                    tile.setFloor(Blocks.water.asFloor());
                    continue;
                }

                //natural terrain
                Block floor = Blocks.stone;
                if (Simplex.noise2d(seed, 3, 0.5f, 1 / 25.0, x, y) > 0.4f) {
                    floor = Blocks.moss;
                }
                if (Simplex.noise2d(seed + 1, 3, 0.5f, 1 / 35.0, x, y) > 0.65f) {
                    floor = Blocks.sand;
                }
                if (Simplex.noise2d(seed + 2, 3, 0.5f, 1 / 22.0, x, y) > 0.75f) {
                    floor = Blocks.craters;
                }
                if (Simplex.noise2d(seed + 3, 3, 0.5f, 1 / 18.0, x, y) > 0.85f) {
                    floor = Blocks.charr;
                }

                tile.setFloor(floor.asFloor());

                //scattered ore veins for visual variety
                if (Simplex.noise2d(seed + 4, 2, 0.5f, 1 / 18.0, x, y) > 0.7f) {
                    tile.setOverlay(Blocks.oreCopper);
                } else if (Simplex.noise2d(seed + 5, 2, 0.5f, 1 / 20.0, x, y) > 0.72f) {
                    tile.setOverlay(Blocks.oreLead);
                }
            }
        }
        world.setGenerating(false);

        //rebuild the real renderer's caches for this menu world (no WorldLoadEvent fires here)
        renderer.blocks.reload();
        renderer.blocks.floor.reload();
    }

    /**
     * River centerline in tile y-coordinates. The meander is gentle enough that the straight line
     * between the two spawn columns stays inside the (6-tile-wide) water band, so naval units can
     * always traverse from one team to the other.
     */
    private float riverY(float tileX) {
        return height / 2f + Mathf.sin(tileX * 0.25f) * 1.5f;
    }

    private void spawnUnits() {
        presetIndex = -1;
        if (battles) {
            nextBattle();
        }
    }

    private void nextBattle() {
        clearBattle();
        presetIndex = (presetIndex + 1) % presets.size;
        //a fresh random map (or procedural terrain) every round
        generateWorld();
        spawnBattle(presets.get(presetIndex));
    }

    private void spawnBattle(BattlePreset preset) {
        Seq<Vec2> anchors = hasMapSpawns ? spawns : proceduralAnchors();
        pickTeams(anchors.size);
        for (int i = 0; i < anchors.size; i++) {
            Vec2 anchor = anchors.get(i);
            spawnWave(squadFor(preset, i, anchors.size), teams.get(i), anchor.x, anchor.y);
        }
    }

    /** The two procedural spawn points on the river banks. */
    private Seq<Vec2> proceduralAnchors() {
        Seq<Vec2> out = new Seq<>();
        float blueX = width / 4f;
        float cruxX = width * 3f / 4f;
        out.add(new Vec2(blueX * tilesize, (riverY(blueX) + 4f) * tilesize));
        out.add(new Vec2(cruxX * tilesize, (riverY(cruxX) - 4f) * tilesize));
        return out;
    }

    /**
     * Splits the preset's unit waves across the teams. With exactly two teams, team 0 gets the
     * "blue" squad and team 1 the "crux" squad; with more teams the waves are distributed round-robin.
     */
    private Seq<UnitWave> squadFor(BattlePreset preset, int index, int count) {
        if (count <= 2) {
            return index == 0 ? preset.blue : preset.crux;
        }
        Seq<UnitWave> squad = new Seq<>();
        Seq<UnitWave> all = new Seq<>();
        all.addAll(preset.blue);
        all.addAll(preset.crux);
        for (int w = index; w < all.size; w += count) {
            squad.add(all.get(w));
        }
        return squad;
    }

    /** Picks distinct, non-derelict teams for each spawn. */
    private void pickTeams(int count) {
        teams.clear();
        Seq<Team> pool = new Seq<>();
        for (Team team : Team.baseTeams) {
            //derelict units can't target/fight - never use it as a battle team
            if (team != Team.derelict) {
                pool.add(team);
            }
        }
        count = Math.min(count, pool.size);
        for (int i = 0; i < count; i++) {
            Team team = pool.random();
            pool.remove(team);
            teams.add(team);
        }
        if (teams.isEmpty()) {
            teams.add(Team.blue);
            teams.add(Team.crux);
        }
        Log.info("Menu battle teams: @", teams.toString(", "));
    }

    private void spawnWave(Seq<UnitWave> waves, Team team, float centerX, float centerY) {
        int index = 0;
        for (UnitWave wave : waves) {
            if (wave.type == null) continue;
            boolean naval = wave.type.naval;
            try {
                for (int i = 0; i < wave.count; i++) {
                    float dx = ((index % 3) - 1) * 2f * tilesize;
                    float dy = (index / 3) * 2f * tilesize - tilesize;
                    float x = centerX + dx;
                    //naval units must start in the water, so drop them onto the procedural river centerline
                    float y = (naval && !hasMapSpawns) ? riverY(x / tilesize) * tilesize : centerY + dy;
                    Unit unit = wave.type.spawn(team, x, y);
                    unit.controller(new MenuBattleAI());
                    Fx.spawn.at(unit.x(), unit.y());
                    index++;
                }
            } catch (Throwable t) {
                //a broken/blocked unit type shouldn't take down the menu background
                Log.err("Failed to spawn menu battle unit @", wave.type, t);
            }
        }
    }

    private void simulate() {
        //rebuild team spatial trees so unit AI can find enemies
        state.teams.updateTeamStats();

        Groups.updatePooling();
        Groups.unit.updatePhysics();
        Groups.unit.update();
        separateUnits();
        Groups.bullet.updatePhysics();
        Groups.bullet.update();
        Groups.bullet.collide();

        //power, then buildings - keeps bases alive so things like overheated reactors actually explode.
        //unit-spawning blocks (cores/factories) are skipped so the arena doesn't flood with units.
        Groups.powerGraph.update();
        Groups.build.each(b -> {
            if (b.block.update && !spawnsUnits(b)) {
                b.update();
            }
        });

        //effects/decals live in the 'all' group
        Groups.all.each(e -> {
            if (e instanceof EffectState || e instanceof Decal) {
                e.update();
            }
        });
    }

    /** Whether this building would endlessly spawn units, which would ruin the arena. */
    private static boolean spawnsUnits(Building b) {
        return b.block instanceof CoreBlock
            || b.block instanceof UnitFactory
            || b.block instanceof Reconstructor
            || b.block instanceof UnitAssembler;
    }

    /** Soft unit-vs-unit separation so units don't clip into each other. Air units fly over ground units. */
    private void separateUnits() {
        Seq<Unit> units = Groups.unit.copy();
        for (int i = 0; i < units.size; i++) {
            Unit a = units.get(i);
            if (a.dead()) continue;
            for (int j = i + 1; j < units.size; j++) {
                Unit b = units.get(j);
                if (b.dead()) continue;
                //air units pass over grounded ones
                if (a.isGrounded() != b.isGrounded()) continue;
                float rs = a.hitSize + b.hitSize;
                float d2 = a.dst2(b);
                if (d2 < rs * rs && d2 > 0.001f) {
                    float push = (rs - Mathf.sqrt(d2)) * 0.5f;
                    Tmp.v1.set(b.x - a.x, b.y - a.y).setLength(push);
                    a.x -= Tmp.v1.x;
                    a.y -= Tmp.v1.y;
                    b.x += Tmp.v1.x;
                    b.y += Tmp.v1.y;
                }
            }
        }
    }

    private void checkBattle() {
        if (fading) {
            fade = Mathf.clamp(fade + (fadingOut ? 1f : -1f) * Time.delta / fadeDuration);
            if (fadingOut && fade >= 1f) {
                fade = 1f;
                //fully black - roll the next map
                nextBattle();
                fadingOut = false;
            } else if (!fadingOut && fade <= 0f) {
                fade = 0f;
                fading = false;
            }
            return;
        }
        if (battleOver()) {
            fading = true;
            fadingOut = true;
        }
    }

    private boolean battleOver() {
        int alive = 0;
        for (Team team : teams) {
            for (Unit u : Groups.unit) {
                if (u.team == team && !u.dead()) {
                    alive++;
                    break;
                }
            }
        }
        //over when at most one team still has units
        return alive <= 1;
    }

    /** Removes every entity belonging to the menu simulation. Safe to call from anywhere. */
    public static void clearAll() {
        Seq<Unit> units = Groups.unit.copy();
        for (Unit u : units) u.remove();

        Seq<Bullet> bullets = Groups.bullet.copy();
        for (Bullet b : bullets) b.remove();

        Seq<Entityc> all = Groups.all.copy();
        for (Entityc e : all) {
            if (e instanceof EffectState || e instanceof Decal || e instanceof Fire || e instanceof Puddle) {
                e.remove();
            }
        }

        Seq<Building> builds = Groups.build.copy();
        for (Building b : builds) {
            if (b.team == Team.derelict) b.remove();
        }

        Groups.updatePooling();
    }

    /**
     * Only clears the sim if every unit still present is a menu battle unit, so it can never
     * touch a real game's units.
     */
    private static void clearIfMenuSim() {
        boolean any = false, allMenu = true;
        for (Unit u : Groups.unit) {
            any = true;
            if (!(u.controller() instanceof MenuBattleAI)) {
                allMenu = false;
                break;
            }
        }
        if (any && allMenu) {
            clearAll();
        }
    }

    private void clearBattle() {
        clearAll();
    }

    /** Called every frame on uiDrawBegin, before the scene (menu UI) is drawn. */
    private void updateAndRender() {
        if (!state.isMenu()) {
            //left the menu into a game; the battleground no longer owns the world
            arenaActive = false;
            //safety net: if the menu sim somehow survived, scrub it (never touches game units)
            clearIfMenuSim();
            return;
        }

        //the map editor owns world.tiles; never touch it or the edited map gets clobbered
        if (ui.editor.isShown()) {
            arenaActive = false;
            return;
        }

        //(re)load the battleground lazily whenever the main menu owns the world again
        if (!arenaActive) {
            setupArena();
        }

        time += Time.delta;
        if (battles) {
            simulate();
            checkBattle();
        }

        //point the game camera at the whole map and render it with the normal world pipeline
        float scaling = Math.max(Core.graphics.getWidth() / camW, Core.graphics.getHeight() / camH);
        Core.camera.position.set(camX, camY);
        Core.camera.resize(Core.graphics.getWidth() / scaling, Core.graphics.getHeight() / scaling);
        Core.camera.update();

        renderWorld();
        drawFade();
    }

    /** Screen-space black overlay for the round transition. */
    private void drawFade() {
        if (fade <= 0f) return;
        mat.set(Draw.proj());
        Draw.proj().setOrtho(0, 0, Core.graphics.getWidth(), Core.graphics.getHeight());
        Draw.color(0f, 0f, 0f, fade);
        Fill.rect(Core.graphics.getWidth() / 2f, Core.graphics.getHeight() / 2f, Core.graphics.getWidth(), Core.graphics.getHeight());
        Draw.color();
        Draw.flush();
        Draw.proj(mat);
    }

    /** No-op fill callback - the world is already rendered on uiDrawBegin. */
    @Override
    public void render() {
    }

    private void renderWorld() {
        Mat prev = Draw.proj();
        Draw.proj(Core.camera);

        //water/shaders render into the effect buffer; make sure it matches the screen
        //(in-game the renderer resizes it every frame, but that never happens in the menu)
        if (renderer.animateWater || renderer.animateShields) {
            renderer.effectBuffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
        }

        renderer.blocks.checkChanges();
        renderer.blocks.processBlocks();

        //lock in our day/night lighting - saves/maps reset state.rules to a near-black default
        state.rules.lighting = true;
        state.rules.ambientLight = ambient;

        Draw.sort(true);

        //floors, edges and animated water shader
        Draw.draw(Layer.floor, renderer.blocks.floor::drawFloor);
        //wall shadows and the walls themselves
        Draw.draw(Layer.block - 1, renderer.blocks::drawShadows);
        Draw.draw(Layer.block - 0.09f, () -> {
            renderer.blocks.floor.beginDraw();
            renderer.blocks.floor.drawLayer(CacheLayer.walls);
        });
        //dynamic lighting
        if (state.rules.lighting && renderer.drawLight) {
            Draw.draw(Layer.light, renderer.lights::draw);
        }
        //map darkness
        if (enableDarkness) {
            Draw.draw(Layer.darkness, renderer.blocks::drawDarkness);
        }
        //bloom
        if (renderer.bloom != null) {
            renderer.bloom.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
            renderer.bloom.setBloomIntensity(settings.getInt("bloomintensity", 6) / 4f + 1f);
            renderer.bloom.blurPasses = settings.getInt("bloomblur", 1);
            Draw.draw(Layer.bullet - 0.02f, renderer.bloom::capture);
            Draw.draw(Layer.effect + 0.02f, renderer.bloom::render);
        }

        //blocks/buildings, then all entities (units, bullets, effects)
        renderer.blocks.drawBlocks();
        Groups.draw.each(d -> {
            if (!(d instanceof Player)) {
                d.draw();
            }
        });

        Draw.reset();
        Draw.flush();
        Draw.sort(false);

        Draw.proj(prev);
    }

    /** Nothing to clean up - all GPU resources are owned by the game renderer. */
    public void dispose() {
    }

    /**
     * Battler that moves directly when the path is clear, and uses synchronous A* pathfinding
     * (works in the menu, unlike the game's flowfield pathfinding which only runs while playing).
     */
    public static class MenuBattleAI extends AIController {
        private Seq<Tile> path;
        private float pathTimer;

        @Override
        public Teamc findMainTarget(float x, float y, float range, boolean air, boolean ground){
            //weapon range never reaches the opposite spawn, so search the whole arena for the closest enemy
            //that this unit's type flags AND at least one controllable weapon can actually engage
            return Units.closestTarget(unit.team, x, y, Float.MAX_VALUE,
                u -> u.checkTarget(air, ground) && canHit(u), t -> true);
        }

        private boolean canHit(Unit other){
            for(WeaponMount mount : unit.mounts()){
                if(mount.weapon.aiControllable && !mount.weapon.noAttack &&
                other.checkTarget(mount.weapon.bullet.collidesAir, mount.weapon.bullet.collidesGround)){
                    return true;
                }
            }
            return false;
        }

        @Override
        public void updateMovement(){
            if (!(target instanceof Unit enemy) || !enemy.isValid() || !unit.hasWeapons()) return;

            float range = Math.max(unit.type.range, 8f);
            //bombers keep flying over the target; everyone else holds at weapon range
            float circle = unit.type.autoDropBombs ? 0f : Math.max(range * 0.6f, 10f);
            if (unit.dst(enemy) <= circle) return; //in range, hold

            Tile from = unit.tileOn();
            Tile goal = enemy.tileOn();
            if (from == null || goal == null) return;

            //direct path clear? just move straight
            if (raycastClear(unit.x, unit.y, enemy.x, enemy.y)){
                vec.set(enemy.x - unit.x, enemy.y - unit.y).setLength(unit.speed());
                unit.movePref(vec);
                path = null;
            }else{
                //recompute the path occasionally (copy it - Astar reuses a shared static seq)
                if (path == null || pathTimer <= 0f){
                    path = Astar.pathfind(from.x, from.y, goal.x, goal.y, tile -> 1f, this::passable).copy();
                    pathTimer = 45f;
                }
                pathTimer -= Time.delta;

                if (path != null && !path.isEmpty()){
                    if (unit.dst(enemy) <= circle) return;
                    //drop waypoints we've already reached
                    while (!path.isEmpty() && unit.within(path.first(), tilesize * 0.6f)){
                        path.remove(0);
                    }
                    if (!path.isEmpty()){
                        Tile next = path.first();
                        vec.set(next.worldx() - unit.x, next.worldy() - unit.y).setLength(unit.speed());
                        unit.movePref(vec);
                    }
                }
            }
            faceTarget();
        }

        private boolean raycastClear(float x1, float y1, float x2, float y2){
            boolean[] clear = {true};
            World.raycastEachWorld(x1, y1, x2, y2, (x, y) -> {
                Tile tile = world.tile(x, y);
                if (tile == null || !passable(tile)){
                    clear[0] = false;
                    return true;
                }
                return false;
            });
            return clear[0];
        }

        /** Air units pass over everything; ground units avoid walls and deep liquid; naval units ignore depth. */
        private boolean passable(Tile tile){
            if (!unit.isGrounded()) return true;
            return !tile.solid() && (unit.type.naval || !tile.floor().isDeep());
        }
    }

    /** A single group of units of one type to spawn for a team. */
    public static class UnitWave {
        public final UnitType type;
        public final int count;

        public UnitWave(UnitType type, int count) {
            this.type = type;
            this.count = count;
        }
    }

    /** A preset matchup between two teams. */
    public static class BattlePreset {
        public final String name;
        public final Seq<UnitWave> blue = new Seq<>();
        public final Seq<UnitWave> crux = new Seq<>();

        public BattlePreset(String name) {
            this.name = name;
        }

        public BattlePreset blue(UnitWave... waves) {
            blue.addAll(waves);
            return this;
        }

        public BattlePreset crux(UnitWave... waves) {
            crux.addAll(waves);
            return this;
        }
    }
}
