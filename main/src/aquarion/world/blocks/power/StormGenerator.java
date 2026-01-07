package aquarion.world.blocks.power;

import aquarion.content.AquaAttributes;
import aquarion.content.AquaWeathers;
import aquarion.world.graphics.AquaPal;
import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Eachable;
import arc.util.Nullable;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Weathers;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.WeatherState;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.type.Weather;
import mindustry.world.blocks.power.SolarGenerator;
import mindustry.world.meta.Attribute;

import static aquarion.world.graphics.Renderer.Layer.shadow;

//I AM NOT MAKING THIS MODIFIABLE. THIS IS A HARDCODED HELLSCAPE
public class StormGenerator extends SolarGenerator {

    public TextureRegion drone, rotator, droneOutline, custshad;

    public float stormPower = 2f;
    public float maxLift = 30f;

    public StormGenerator(String name) {
        super(name);
    }

    @Override
    public void load(){
        super.load();
        drone = Core.atlas.find(name + "-top");
        droneOutline = Core.atlas.find(name + "-outline");
        rotator = Core.atlas.find(name + "-fan");
        custshad = Core.atlas.find(name + "-shadow");
    }
    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        Draw.rect(region, plan.drawx(), plan.drawy());
        Draw.rect(rotator, plan.drawx(), plan.drawy());
        Draw.rect(drone, plan.drawx(), plan.drawy());
        Draw.reset();
    }
    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{region,rotator, drone};
    }
    public class StormGenBuild extends SolarGeneratorBuild {
        @Nullable public Vec2 curWind;

        float lift = 0f;
        float liftSmooth = 0f;

        float stormIntensity = 0f;
        float stormIntensitySmooth = 0f;

        float swayX = 0f, swayY = 0f;
        float swayPhase = 0f;

        float fanRot = 0f;

        @Override
        public void updateTile() {
            float base = Vars.state.rules.solarMultiplier * Mathf.maxZero(
                    Attribute.light.env() +
                            (Vars.state.rules.lighting
                                    ? 1f - Vars.state.rules.ambientLight.a
                                    : 1f)
            );
            float weatherBonus = 0f;
            float intensity = AquaAttributes.wind.env();
            stormIntensity = Mathf.clamp(intensity, 0f, 1f);
            stormIntensitySmooth = Mathf.approachDelta(
                    stormIntensitySmooth,
                    stormIntensity,
                    0.03f
            );
            weatherBonus = stormPower * intensity;
            lift = maxLift * stormIntensitySmooth;
            liftSmooth = Mathf.approachDelta(liftSmooth, lift, maxLift / 8f);

            productionEfficiency = enabled ? base + weatherBonus : 0f;
            if(weatherBonus > 0f) efficiency = 1f;

            if(stormIntensitySmooth > 0.001f){
                swayPhase += edelta() * (0.8f + stormIntensitySmooth);
            }else{
                swayPhase = Mathf.approachDelta(swayPhase, 0f, 0.05f);
            }
            //I forgor the sane way to do this
            fanRot += edelta() * 6f * stormIntensitySmooth;
        }

        @Override
        public void draw(){
            super.draw();
            float swayStrength = stormIntensitySmooth * 0.6f;

            swayX = Mathf.sin(swayPhase + id * 12f, 25f, swayStrength);
            swayY = Mathf.cos(swayPhase + id * 12f, 25f, swayStrength);

            float cx = x + swayX;
            float cy = y + liftSmooth + swayY;

            drawCable(x + 20f, y, cx + 14f, cy);
            drawCable(x - 20f, y, cx - 14f, cy);
            drawCable(x, y + 20f, cx, cy + 14f);
            drawCable(x, y - 20f, cx, cy - 14f);

            if(stormIntensitySmooth > 0.001f){
                Draw.alpha(stormIntensitySmooth);
                Draw.rect(droneOutline, cx, cy);
                Draw.reset();
            }
            Draw.z(Layer.blockOver+.2f);
            Drawf.spinSprite(rotator, cx, cy, fanRot);
            Draw.rect(drone, cx, cy);

            Draw.reset();
            Draw.z(shadow);
            Draw.rect(custshad, this.x, y);
            float shadowOffset = 5f * liftSmooth / maxLift;
            Draw.rect(drone, cx - shadowOffset, cy - shadowOffset);

            Draw.reset();
        }
        //Super lazy. I just didn't want to redeclare color and stuff
        void drawCable(float x1, float y1, float x2, float y2){
            Lines.stroke(1f);
            Draw.color(AquaPal.tantDarkestTone);
            Lines.line(x1, y1, x2, y2);
            Draw.color();
        }
    }
}
