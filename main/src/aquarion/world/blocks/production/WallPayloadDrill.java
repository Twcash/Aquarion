package aquarion.world.blocks.production;

import aquarion.annotations.Annotations;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.struct.ObjectMap;
import arc.util.Eachable;
import arc.util.Nullable;
import arc.util.Strings;
import mindustry.Vars;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.graphics.Shaders;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.PayloadBlock;
import mindustry.world.draw.DrawGlowRegion;
import mindustry.world.meta.Attribute;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class WallPayloadDrill extends PayloadBlock {
    public @Annotations.Load("@-spinny")TextureRegion spinnyRegion;
    public ObjectMap<Attribute, Block> attributeBlockMap = new ObjectMap<>();
    public float buildSpeed = 0.4f;
    public @Annotations.Load("@-side1") TextureRegion side1;
    public @Annotations.Load("@-side2") TextureRegion side2;

    public float buildTime = 300;
    public DrawGlowRegion glow = new DrawGlowRegion() {{
        suffix = "aquarion-wall-excavator-glow";
        alpha = 0.65f;
        color = Color.valueOf("e68569");
        glowIntensity = 0.3f;
        glowScale = 6f;
    }};
    public WallPayloadDrill(String name) {
        super(name);
        size = 3;
        update = true;
        outputsPayload = true;
        solid = true;
        rotate = true;
        regionRotated1 = 1;
    }

    @Override
    public void setBars() {
        super.setBars();
                addBar("progress", (WallPayloadDrillBuild e) -> new Bar(
                        () -> Core.bundle.format("bar.progress", Strings.autoFixed(e.totalProgress * 100, 1)),
                        () -> Pal.ammo,
                        e::totalProgress
                ));
    }
//    @Override
//    protected TextureRegion[] icons(){
//        return new TextureRegion[]{region, outRegion, topRegion, side1};
//    }
    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        Draw.rect(region, plan.drawx(), plan.drawy());
        Draw.rect(outRegion, plan.drawx(), plan.drawy(), plan.rotation * 90);
        Draw.rect(topRegion, plan.drawx(), plan.drawy());
        Draw.rect(side1, plan.drawx(), plan.drawy(),plan.rotation * 90);
    }

    public float calculateEfficiency(int tx, int ty, int rotation, ObjectMap<Attribute, Float> attributeTotals) {
        float efficiency = 0f;
        int cornerX = tx - (size - 1) / 2, cornerY = ty - (size - 1) / 2;

        for (int i = 0; i < size; i++) {
            int rx = 0, ry = 0;

            switch (rotation) {
                case 0 -> {
                    rx = cornerX + size;
                    ry = cornerY + i;
                }
                case 1 -> {
                    rx = cornerX + i;
                    ry = cornerY + size;
                }
                case 2 -> {
                    rx = cornerX - 1;
                    ry = cornerY + i;
                }
                case 3 -> {
                    rx = cornerX + i;
                    ry = cornerY - 1;
                }
            }

            Tile other = world.tile(rx, ry);
            if (other != null && other.solid()) {
                for (Attribute attribute : attributeBlockMap.keys()) {
                    float value = other.block().attributes.get(attribute);
                    if (value > 0) {
                        efficiency += value;
                        attributeTotals.put(attribute, attributeTotals.get(attribute, 0f) + value);
                    }
                }
            }
        }
        return efficiency;
    }

    public class WallPayloadDrillBuild extends PayloadBlockBuild<BuildPayload> {
        public float progress = 0f;
        public @Nullable Building next;
        public float heat = 0f;
        public float time = 0f;
        public float totalProgress;
        /**
         * Determines the recipe based on the dominant attribute in the surrounding blocks.
         */
        public Block recipe() {
            ObjectMap<Attribute, Float> attributeTotals = new ObjectMap<>();
            calculateEfficiency(tileX(), tileY(), rotation, attributeTotals);

            Attribute dominantAttribute = null;
            float highestTotal = 0;

            for (ObjectMap.Entry<Attribute, Float> entry : attributeTotals) {
                if (entry.value > highestTotal) {
                    highestTotal = entry.value;
                    dominantAttribute = entry.key;
                }
            }

            return dominantAttribute != null ? attributeBlockMap.get(dominantAttribute) : null;
        }

        @Override
        public void onProximityUpdate() {
            super.onProximityUpdate();

            // Calculate the position behind the drill based on its rotation
            int backRotation = (rotation + 2) % 4; // This gives the direction behind the drill
            int backX = tileX() + Geometry.d4(backRotation).x * (size / 2 + 1);
            int backY = tileY() + Geometry.d4(backRotation).y * (size / 2 + 1);

            // Set 'next' to the building directly behind the drill if there is one
            Tile backTile = world.tile(backX, backY);
            if (backTile != null && backTile.build != null) {
                next = backTile.build;
            } else {
                next = null;
            }
        }

        @Override
        public void updateTile() {
            if (payload != null && next != null && next.acceptPayload(this, payload)) {
                next.handlePayload(this, payload);
                payload = null;
            } else if (payload != null) {
                dumpPayload();
            }
            totalProgress = progress / buildTime;
            super.updateTile();

            Block recipe = recipe();
            if (recipe == null) return;

            ObjectMap<Attribute, Float> attributeTotals = new ObjectMap<>();
            float efficiency = calculateEfficiency(tileX(), tileY(), rotation, attributeTotals);

            progress +=   edelta();
            boolean produce = efficiency > 0 && payload == null;
            if (produce && progress >= buildTime) {
                payload = new BuildPayload(recipe, team);
                recipe.placeEffect.at(x, y, (float) recipe.size / tilesize);
                progress = 0;
            }
            heat = Mathf.lerpDelta(heat, 1f, 0.15f);
            time += heat * delta();
        }


        @Override
        public void draw() {
            Draw.rect(region,x,y,0);
            Draw.rect(outRegion, x, y, rotdeg());
            var recipe = recipe();
            if (recipe != null) {
                Draw.draw(Layer.blockBuilding, () -> {
                    Draw.color(Pal.accent);

                    for (TextureRegion region : recipe.getGeneratedIcons()) {
                        if (region != null) {
                            Shaders.blockbuild.region = region;
                            Shaders.blockbuild.time = time;
                            Shaders.blockbuild.progress = progress/buildTime;

                            Draw.rect(region, x, y, recipe.rotate ? rotdeg() : 0);
                            Draw.flush();
                        }
                    }

                    Draw.color();
                });
                Draw.z(Layer.blockBuilding + 1);
                Draw.color(Pal.accent, heat);

                Lines.lineAngleCenter(x + Mathf.sin(time, 10f, Vars.tilesize / 2f * recipe.size + 1f), y, 90, recipe.size * Vars.tilesize + 1f);

                Draw.reset();
            }

            if (payload != null) {
                drawPayload();
            }

            Draw.z(Layer.blockBuilding + 1.1f);
            Draw.rect(topRegion, x, y);

            Draw.rect(rotation > 1 ? side2 : side1,x, y, rotdeg());
            glow.draw(this);
        }
    }
}