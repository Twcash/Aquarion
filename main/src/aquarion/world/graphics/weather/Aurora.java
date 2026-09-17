package aquarion.world.graphics.weather;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.scene.ui.layout.Scl;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.gen.WeatherState;
import mindustry.graphics.Layer;
import mindustry.type.weather.ParticleWeather;

/** Weather tied to the world's current lighting: invisible during the day, brighter the darker the world is.
 *  Renders aurora-borealis-style light curtains across the sky. Fades out when zooming in. */
public class Aurora extends ParticleWeather{
    /** Extra brightness multiplier applied on top of the darkness factor. */
    public float brightness = 1f;
    /** Minimum visibility required for the aurora to draw at all. */
    public float minDarkness = 0.01f;

    /** Amount of curtain layers drawn. */
    public int curtains = 3;
    /** Base alpha of the lowest curtain; higher curtains fade more. */
    public float curtainAlpha = 0.3f;
    /** Bottom-edge color of each curtain layer. Fades to transparent at the top. */
    public Color[] curtainColors = {
        Color.valueOf("64ff96"), //green
        Color.valueOf("48fff0"), //teal
        Color.valueOf("8a6bff")  //violet
    };

    /** Zoom levels (display scale) at which the aurora is fully visible / fully faded. */
    public float zoomVisible = 1f, zoomGone = 1.9f;

    public Aurora(String name){
        super(name);
    }

    /** Current world darkness: 0 in full daylight, ~1 at night. */
    public float darkness(){
        return Vars.state.rules.lighting ? Vars.state.rules.ambientLight.a : 0f;
    }

    /** 1 at default zoom and further out, 0 when zoomed in past the limit. */
    public float zoomFade(){
        float scale = Vars.renderer.getDisplayScale();
        float def = Scl.scl(4f);
        return Mathf.clamp((zoomGone * def - scale) / ((zoomGone - zoomVisible) * def));
    }

    @Override
    public void drawOver(WeatherState state){
        float dark = Mathf.clamp(darkness() * brightness) * zoomFade();
        if(dark <= minDarkness) return;

        //render above the light layer: the aurora is a light source, so the night's own darkness must not dim it
        Draw.draw(Layer.light + 0.01f, () -> {
            //scale every alpha contribution by darkness, then restore the fields
            float om = opacityMultiplier, mn = minAlpha, mx = maxAlpha;
            opacityMultiplier *= dark;
            minAlpha *= dark;
            maxAlpha *= dark;
            super.drawOver(state);
            opacityMultiplier = om;
            minAlpha = mn;
            maxAlpha = mx;

            drawCurtains(state, dark);
        });
    }

    /** Draws slowly waving aurora curtains across the camera view. */
    void drawCurtains(WeatherState state, float dark){
        float camX = Core.camera.position.x, camY = Core.camera.position.y;
        float camW = Core.camera.width, camH = Core.camera.height;
        float left = camX - camW / 2f;

        float baseAlpha = state.opacity * opacityMultiplier * dark * Vars.renderer.weatherAlpha * curtainAlpha;
        if(baseAlpha <= 0.001f) return;

        float t = Time.time;
        int slices = 64;
        float step = camW / slices;

        Draw.blend(Blending.additive);

        for(int l = 0; l < curtains; l++){
            Color col = curtainColors[l % curtainColors.length];
            float layerAlpha = baseAlpha * (1f - l * 0.3f);
            if(layerAlpha <= 0.001f) continue;

            //each curtain sits higher up and drifts at its own pace
            float baseY = camY + camH * (0.02f + 0.13f * l);
            float phase = l * 40f;
            float drift = t * (2.0f + l * 0.6f);
            //gentle breathing of the curtain height
            float height = camH * (0.22f + 0.07f * l) * (0.92f + 0.08f * Mathf.sin(t * 1.5f + phase, 110f, 1f));

            //halo: full height, soft
            curtainPass(left, step, slices, baseY, height, drift, phase, t, col, layerAlpha * 0.4f, 0f);
            //core: lower half, brighter and whiter
            curtainPass(left, step, slices, baseY, height * 0.45f, drift, phase, t, col, layerAlpha, 0.35f);
            //edge: sharp bright bottom border
            curtainPass(left, step, slices, baseY, height * 0.1f, drift, phase, t, col, layerAlpha * 1.7f, 0.7f);
        }

        Draw.blend();
        Draw.reset();
    }

    /** Draws one curtain pass as a strip of vertical gradient quads with a waving bottom edge. */
    void curtainPass(float left, float step, int slices, float baseY, float height, float drift, float phase, float t, Color col, float alpha, float whiten){
        if(alpha <= 0.001f) return;

        for(int i = 0; i < slices; i++){
            float x1 = left + i * step;
            float x2 = x1 + step + 1f; //tiny overlap hides seams

            float y1 = baseY
                + Mathf.sin(x1 * 0.9f + drift + phase, 170f, Core.camera.height * 0.05f)
                + Mathf.sin(x1 * 0.35f - drift * 0.7f + phase, 420f, Core.camera.height * 0.08f);
            float y2 = baseY
                + Mathf.sin(x2 * 0.9f + drift + phase, 170f, Core.camera.height * 0.05f)
                + Mathf.sin(x2 * 0.35f - drift * 0.7f + phase, 420f, Core.camera.height * 0.08f);

            //brightness ripple running along the curtain
            float ripple1 = 0.65f + 0.35f * Mathf.sin(x1 * 0.5f + t * 2.4f + phase, 260f, 1f);
            float ripple2 = 0.65f + 0.35f * Mathf.sin(x2 * 0.5f + t * 2.4f + phase, 260f, 1f);

            Tmp.c1.set(col).lerp(Color.white, whiten);
            Tmp.c2.set(Tmp.c1);
            float cb1 = Tmp.c1.a(alpha * ripple1).toFloatBits();
            float cb2 = Tmp.c2.a(alpha * ripple2).toFloatBits();
            float ct = Tmp.c3.set(Tmp.c1).a(0f).toFloatBits();

            Fill.quad(Core.atlas.white(),
                x1, y1, cb1,
                x1, y1 + height, ct,
                x2, y2 + height, ct,
                x2, y2, cb2);
        }
    }
}
