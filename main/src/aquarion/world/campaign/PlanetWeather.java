package aquarion.world.campaign;

import aquarion.content.AquaPlanets;
import aquarion.content.AquaWeathers;
import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.geom.Vec3;
import arc.scene.Element;
import arc.scene.ui.TextButton;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Call;
import mindustry.type.Planet;
import mindustry.type.Sector;
import mindustry.type.Weather;

/** Campaign fronts use the saved universe clock, shared by the map and sector weather. */
public class PlanetWeather {
    private static final int count = 6;
    private static int selected = -1;
    private static Planet shown;
    private static final Vec3 point = new Vec3(), normal = new Vec3(), target = new Vec3();

    public static void register(){
        for(Planet planet : new Planet[]{AquaPlanets.fakeSerpulo, AquaPlanets.tantros2}){
            var original = planet.cloudMeshLoader;
            planet.cloudMeshLoader = () -> new mindustry.graphics.g3d.MultiMesh(original.get(),
                new aquarion.world.graphics.weather.WeatherCloudMesh(planet));
        }
        Events.run(EventType.Trigger.update, PlanetWeather::updateWeather);
        Events.on(EventType.ClientLoadEvent.class, event -> {
            Events.run(EventType.Trigger.update, PlanetWeather::installRenderer);
            Element path = new Element(){
                @Override public Element hit(float x, float y, boolean touchable){
                    if(!visibleMap()) return null;
                    Planet planet = Vars.ui.planet.state.planet;
                    float closest = 14f * 14f;
                    hitOwner = -1;
                    hitWeather = null;
                    for(Sector sector : planet.sectors){
                        Weather weather = effectiveWeather(planet, sector);
                        if(weather == null || visibility(sector) < 0.08f || !frontFacing(planet, sector)) continue;
                        planet.project(sector, Vars.renderer.planets.cam, point);
                        float distance = (point.x-x)*(point.x-x) + (point.y-y)*(point.y-y);
                        if(distance < closest){
                            closest = distance;
                            hitWeather = weather;
                            hitOwner = owner(planet, sector, weather);
                        }
                    }
                    return hitOwner < 0 ? null : this;
                }
                @Override public void draw(){
                    if(!visibleMap() || selected < 0 || shown != Vars.ui.planet.state.planet) return;
                    Planet planet = shown;
                    Vec3 last = new Vec3(), screen = new Vec3(), surface = new Vec3();
                    boolean lastVisible = false;
                    Draw.color(mindustry.graphics.Pal.accent);
                    Lines.stroke(2f);
                    for(int sample = 0; sample <= 96; sample++){
                        int leg = Math.min(sample / 12, 7);
                        float fraction = sample == 96 ? 1f : sample % 12 / 12f;
                        surface.set(center(planet, selected, leg * 120).tile.v).slerp(center(planet, selected, (leg+1)*120).tile.v, fraction);
                        normal.set(surface).rotate(Vec3.Y, -planet.getRotation());
                        target.set(Vars.renderer.planets.cam.position).sub(planet.position);
                        boolean facing = normal.dot(target) > planet.radius * 1.01f;
                        surface.setLength(planet.radius * 1.01f).rotate(Vec3.Y, -planet.getRotation()).add(planet.position);
                        Vars.renderer.planets.cam.project(screen.set(surface));
                        Draw.alpha(visibility(center(planet, selected, leg * 120)));
                        if(facing && lastVisible) Lines.line(last.x, last.y, screen.x, screen.y);
                        last.set(screen);
                        lastVisible = facing;
                    }
                    Draw.reset();
                }
            };
            path.setFillParent(true);
            path.clicked(() -> {
                if(hitOwner < 0) return;
                Planet planet = Vars.ui.planet.state.planet;
                selected = shown == planet && selected == hitOwner ? -1 : hitOwner;
                shown = planet;
            });
            path.addListener(new arc.scene.ui.Tooltip(table -> {
                if(hitWeather != null) table.add(hitWeather.localizedName);
            }));
            path.update(() -> { if(visibleMap()) path.toFront(); });
            Core.scene.add(path);
        });
    }

    private static int hitOwner = -1;
    private static Weather hitWeather;

    private static int owner(Planet planet, Sector sector, Weather weather){
        for(int i = 0; i < count; i++) if(weatherAt(planet, i, sector) == weather) return i;
        return -1;
    }
    private static void installRenderer(){
        if(!Vars.ui.planet.isShown()) return;
        var state = Vars.ui.planet.state;
        if(supported(state.planet)) updateVisibility(state.planet);
        if(state.renderer == null || state.renderer instanceof WeatherRenderer) return;
        state.renderer = new WeatherRenderer(state.renderer);
    }

    private static class WeatherRenderer implements mindustry.graphics.g3d.PlanetRenderer.PlanetInterfaceRenderer{
        private final mindustry.graphics.g3d.PlanetRenderer.PlanetInterfaceRenderer delegate;
        WeatherRenderer(mindustry.graphics.g3d.PlanetRenderer.PlanetInterfaceRenderer delegate){ this.delegate = delegate; }

        @Override public void renderSectors(Planet planet){
            delegate.renderSectors(planet);
            if(!supported(planet) || !visibleMap()) return;
            // Use the same footprint as gameplay, including locked sectors.
            for(Sector sector : planet.sectors){
                for(int i = 0; i < count; i++){
                    if(weatherAt(planet, i, sector) != null){
                        Vars.renderer.planets.drawBorders(sector, Color.valueOf("5aaaff"), Vars.ui.planet.state.uiAlpha * visibility(sector));
                        break;
                    }
                }
            }
            if(selected >= 0 && shown == planet){
                for(int step = 1; step <= 8; step++){
                    Sector next = center(planet, selected, step * 120);
                    Vars.renderer.planets.drawBorders(next, mindustry.graphics.Pal.accent, Vars.ui.planet.state.uiAlpha * (1f - step * 0.07f) * visibility(next));
                }
            }
        }

        @Override public void renderOverProjections(Planet planet){ delegate.renderOverProjections(planet); }

        @Override public void renderProjections(Planet planet){
            delegate.renderProjections(planet);
            if(!supported(planet) || !visibleMap()) return;
            for(Sector sector : planet.sectors){
                Weather weather = effectiveWeather(planet, sector);
                if(weather == null || visibility(sector) <= 0.01f) continue;
                int owner = 0;
                for(int i = 0; i < count; i++) if(weatherAt(planet, i, sector) == weather){ owner = i; break; }
                final int index = owner;
                final Weather visibleWeather = weather;
                Vars.renderer.planets.drawPlane(sector, () -> {
                    String sprite = visibleWeather == mindustry.content.Weathers.rain ? "aquarion-rain" : visibleWeather.name;
                    if(Core.atlas.has(sprite)){
                        var region = Core.atlas.find(sprite);
                        Draw.color(Color.white);
                        Draw.alpha(visibility(sector));
                        float size = sector == center(planet, index, 0) ? 14f : 8f;
                        float scale = size / Math.max(region.width, region.height);
                        Draw.rect(region, 0f, 0f, region.width * scale, region.height * scale);
                        Draw.reset();
                    }else drawWeatherIcon(planet, index, visibility(sector));
                });
            }
            Draw.reset();
        }
    }

    private static void drawWeatherIcon(Planet planet, int index, float alpha){
        float x = 0f, y = 0f;
                        Weather weather = type(planet, index);
                        String sprite = weather.name;
                        if(Core.atlas.has(sprite)){
                            var region = Core.atlas.find(sprite);
                            Draw.color(Color.white);
                        Draw.alpha(alpha);
                            float size = 16f;
                            float scale = size / Math.max(region.width, region.height);
                            Draw.rect(region, x, y, region.width * scale, region.height * scale);
                            Draw.reset();
                            return;
                        }
                        Draw.color(Color.sky);
                        Draw.alpha(alpha);
                        Lines.stroke(2f);
                        int kind = planet == AquaPlanets.tantros2 ? 3 : index % 3;
                        if(kind == 0){
                            for(int a = 0; a < 3; a++) Lines.lineAngleCenter(x, y, a * 60f, 20f);
                        }else if(kind == 3){
                            for(int a = -1; a <= 1; a++) Lines.line(x - 10f, y + a*5f, x + 10f, y + a*5f + 3f);
                        }else{
                            Lines.arc(x, y + 3f, 9f, 0.5f, 0f);
                            for(int a = -1; a <= 1; a++) Lines.line(x + a*6f, y - 2f, x + a*6f - 3f, y - 9f);
                            if(kind == 2) Lines.line(x, y + 10f, x + 4f, y - 3f);
                        }
                        Draw.reset();
    }

    private static Planet visibilityPlanet;
    private static long capturedSignature = Long.MIN_VALUE;
    private static final arc.struct.ObjectIntMap<Sector> capturedDistance = new arc.struct.ObjectIntMap<>();

    private static void updateVisibility(Planet planet){
        long signature = 1;
        for(Sector sector : planet.sectors) if(sector.isCaptured()) signature = signature * 31 + sector.id + 1;
        if(visibilityPlanet == planet && capturedSignature == signature) return;
        visibilityPlanet = planet;
        capturedSignature = signature;
        capturedDistance.clear();
        arc.struct.Seq<Sector> edge = new arc.struct.Seq<>();
        for(Sector sector : planet.sectors) if(sector.isCaptured()){
            capturedDistance.put(sector, 0);
            edge.add(sector);
        }
        // Before the first capture, use the campaign landing sector as the observation point.
        if(edge.isEmpty() && planet.sectors.size > 0){
            Sector start = planet.sectors.get(arc.math.Mathf.clamp(planet.startSector, 0, planet.sectors.size - 1));
            capturedDistance.put(start, 0);
            edge.add(start);
        }
        for(int distance = 1; distance <= 10 && !edge.isEmpty(); distance++){
            arc.struct.Seq<Sector> nextEdge = new arc.struct.Seq<>();
            for(Sector sector : edge) for(Sector next : sector.near()){
                if(!capturedDistance.containsKey(next)){
                    capturedDistance.put(next, distance);
                    nextEdge.add(next);
                }
            }
            edge = nextEdge;
        }
    }

    public static float visibility(Sector sector){
        if(visibilityPlanet != sector.planet) updateVisibility(sector.planet);
        float fade = arc.math.Mathf.clamp((capturedDistance.get(sector, 11) - 2f) / 8f);
        return 1f - fade * fade * (3f - 2f * fade);
    }
    public static long cloudRevision(Planet planet){
        updateVisibility(planet);
        return capturedSignature * 31 + Vars.universe.seconds() / 120;
    }
    private static boolean supported(Planet planet){
        return planet == AquaPlanets.fakeSerpulo || planet == AquaPlanets.tantros2;
    }

    private static boolean visibleMap(){
        return Vars.ui.planet.isShown() && supported(Vars.ui.planet.state.planet) && Vars.ui.planet.state.zoom < 1.9f;
    }

    private static boolean frontFacing(Planet planet, Sector sector){
        normal.set(sector.tile.v).rotate(Vec3.Y, -planet.getRotation());
        target.set(Vars.renderer.planets.cam.position).sub(planet.position);
        return normal.dot(target) > planet.radius;
    }

    private static Weather type(Planet planet, int index){
        if(planet == AquaPlanets.tantros2) return index % 2 == 0 ? AquaWeathers.currents : AquaWeathers.sedimentDisturance;
        return index % 3 == 0 ? AquaWeathers.blizzard : index % 3 == 1 ? AquaWeathers.monsoon : AquaWeathers.thunderstorm;
    }

    private static Sector center(Planet planet, int index, int ahead){
        int tick = Math.max(0, (Vars.universe.seconds() + ahead) / 120);
        String key = planet.name + ":" + index;
        arc.struct.Seq<Sector> route = routes.get(key);
        if(route == null){
            route = new arc.struct.Seq<>();
            route.add(planet.sectors.get(index * planet.sectors.size / count));
            routes.put(key, route);
        }
        while(route.size <= tick){
            Sector current = route.peek();
            Vec3 forward = route.size > 1 ? current.tile.v.cpy().sub(route.get(route.size-2).tile.v).nor()
                : new Vec3(0.6f, 0.3f + index * 0.1f, -0.7f).nor();
            // A slowly changing pressure gradient steers each front while retaining momentum.
            float phase = route.size * 0.37f + index * 1.9f;
            Vec3 pressure = new Vec3((float)Math.sin(phase), (float)Math.cos(phase * 0.71f), (float)Math.sin(phase * 0.53f));
            Vec3 heading = forward.scl(0.7f).add(pressure.scl(0.8f)).nor();
            Sector best = null;
            float bestScore = -Float.MAX_VALUE;
            for(Sector next : current.near()){
                if(route.size > 1 && next == route.get(route.size-2)) continue;
                float score = next.tile.v.cpy().sub(current.tile.v).nor().dot(heading);
                for(int back = Math.max(0, route.size - 8); back < route.size; back++){
                    if(route.get(back) == next) score -= 2f;
                }
                if(score > bestScore){ bestScore = score; best = next; }
            }
            route.add(best == null ? current : best);
        }
        return route.get(tick);
    }

    private static final arc.struct.ObjectMap<String, arc.struct.Seq<Sector>> routes = new arc.struct.ObjectMap<>();

    private static final arc.struct.ObjectMap<String, arc.struct.ObjectMap<Sector, Weather>> footprints = new arc.struct.ObjectMap<>();
    private static final arc.struct.ObjectMap<String, Sector> footprintCenters = new arc.struct.ObjectMap<>();

    private static Weather weatherAt(Planet planet, int index, Sector sector){
        String key = planet.name + ":" + index;
        Sector center = center(planet, index, 0);
        if(footprintCenters.get(key) != center){
            int radius = index % 3 == 1 ? 2 : 1;
            Weather weather = type(planet, index);
            arc.struct.ObjectMap<Sector, Weather> footprint = new arc.struct.ObjectMap<>();
            arc.struct.Seq<Sector> edge = arc.struct.Seq.with(center);
            footprint.put(center, weather);
            int limit = radius + (weather == AquaWeathers.monsoon ? 1 : 0);
            for(int depth = 1; depth <= limit; depth++){
                arc.struct.Seq<Sector> nextEdge = new arc.struct.Seq<>();
                for(Sector tile : edge) for(Sector next : tile.near()){
                    if(!footprint.containsKey(next)){
                        footprint.put(next, depth <= radius ? weather : mindustry.content.Weathers.rain);
                        nextEdge.add(next);
                    }
                }
                edge = nextEdge;
            }
            // Shape the front in its direction of travel, with an uneven leading rain band.
            Vec3 heading = center(planet, index, 120).tile.v.cpy().sub(center.tile.v);
            heading.sub(center.tile.v.cpy().scl(heading.dot(center.tile.v))).nor();
            Vec3 side = center.tile.v.cpy().crs(heading).nor();
            float spacing = center.tile.v.dst(center.near().first().tile.v);
            arc.struct.ObjectMap<Sector, Weather> shaped = new arc.struct.ObjectMap<>();
            for(Sector tile : footprint.keys()){
                Vec3 offset = tile.tile.v.cpy().sub(center.tile.v);
                float along = offset.dot(heading) / spacing;
                float across = offset.dot(side) / spacing;
                float bend = 0.55f * (float)Math.sin(along * 1.3f + index);
                float cross = across - bend;
                float core = along*along / 4.5f + cross*cross / (1.1f + index%2 * 0.5f);
                boolean heavy = core < 1f + 0.18f*(float)Math.sin(tile.id * 1.7f + index);
                boolean rain = weather == AquaWeathers.monsoon && along > -0.6f && along < 3.2f
                    && Math.abs(cross) < 1.8f + 0.45f*(float)Math.sin(along * 1.8f + index);
                if(tile == center || heavy) shaped.put(tile, weather);
                else if(rain) shaped.put(tile, mindustry.content.Weathers.rain);
            }
            footprint = shaped;
            footprintCenters.put(key, center);
            footprints.put(key, footprint);
        }
        return footprints.get(key).get(sector);
    }
    public static Weather effectiveWeather(Planet planet, Sector sector){
        Weather result = null;
        for(int i = 0; i < count; i++){
            Weather weather = weatherAt(planet, i, sector);
            if(weather == AquaWeathers.thunderstorm) return weather;
            if(weather != null && (result == null || result == mindustry.content.Weathers.rain)) result = weather;
        }
        return result;
    }

    private static void updateWeather(){
        if(!Vars.state.isGame() || !Vars.state.isCampaign() || Vars.net.client()) return;
        Sector sector = Vars.state.rules.sector;
        if(sector == null || !supported(sector.planet)) return;
        Vars.state.rules.weather.clear();
        Weather expected = effectiveWeather(sector.planet, sector);
        for(Weather weather : new Weather[]{AquaWeathers.monsoon, AquaWeathers.thunderstorm, AquaWeathers.blizzard,
            AquaWeathers.currents, AquaWeathers.sedimentDisturance, mindustry.content.Weathers.rain}){
            if(weather != expected && weather.instance() != null) weather.instance().life(Math.min(weather.instance().life(), 60f));
        }
        if(expected != null){
            if(!expected.isActive()) Call.createWeather(expected, 1f, 600f, 1f, 0f);
            if(expected.instance() != null) expected.instance().life(600f);
        }
    }
}