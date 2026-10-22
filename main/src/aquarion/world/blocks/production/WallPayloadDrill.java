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
import mindustry.world.blocks.environment.StaticWall;
import mindustry.world.blocks.environment.TallBlock;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.PayloadBlock;
import mindustry.world.draw.DrawGlowRegion;
import mindustry.world.meta.Attribute;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatValues;

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
        addBar("drillspeed", (WallPayloadDrillBuild e) -> new Bar(
            () -> Core.bundle.format("bar.payload-drillspeed", Strings.fixed(e.productionRate(), 2)),
            () -> Pal.ammo, () -> e.heat));
    }

    @Override
    public void setStats(){
        stats.timePeriod = buildTime;
        super.setStats();
        for(var entry : attributeBlockMap){
            stats.add(Stat.output, StatValues.content(entry.value));
            stats.add(Stat.tiles, StatValues.blocks(entry.key, false, 1f, true, false));
        }
        stats.add(Stat.drillSpeed, table -> table.add(Core.bundle.format("bar.payload-drillspeed", Strings.fixed(size * 60f / buildTime, 2))));
    }

    private Tile miningTile(int tx, int ty, int rotation, int index){
        int x = tx - (size - 1) / 2, y = ty - (size - 1) / 2;
        return switch(rotation & 3){
            case 0 -> world.tile(x + size, y + index);
            case 1 -> world.tile(x + index, y + size);
            case 2 -> world.tile(x - 1, y + index);
            default -> world.tile(x + index, y - 1);
        };
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);
        ObjectMap<Attribute, Float> totals = new ObjectMap<>();
        calculateEfficiency(x, y, rotation, totals);
        Attribute selected = dominant(totals);
        Block output = selected == null ? null : attributeBlockMap.get(selected);
        float previousZ = Draw.z();
        Draw.z(previousZ - 0.01f);
        for(int i = 0; i < size; i++){
            Tile wall = miningTile(x, y, rotation, i);
            if(wall == null) continue;
            if(!(wall.block() instanceof StaticWall) || !(wall.block() instanceof TallBlock)) return;
            boolean matches = selected != null && wall.solid() && wall.block().attributes.get(selected) > 0f;
            Drawf.selected(wall, matches ? Pal.accent : Pal.remove);
        }
        Draw.z(previousZ);
        float width = drawPlaceText(Core.bundle.format("bar.payload-drillspeed",
            Strings.fixed(selected == null ? 0f : totals.get(selected, 0f) * 60f / buildTime, 2)), x, y, valid);
        if(output != null){
            float dx = x * tilesize + offset - width / 2f - 4f;
            float dy = y * tilesize + offset + size * tilesize / 2f + 5f;
            float iconSize = Vars.iconSmall / 4f;
            Draw.mixcol(Color.darkGray, 1f);
            Draw.rect(output.fullIcon, dx, dy - 1f, iconSize, iconSize);
            Draw.reset();
            Draw.rect(output.fullIcon, dx, dy, iconSize, iconSize);
        }

    }

    private Attribute dominant(ObjectMap<Attribute, Float> totals){
        Attribute best = null;
        float highest = 0f;
        for(var entry : attributeBlockMap){
            float value = totals.get(entry.key, 0f);
            if(value > highest || (value == highest && value > 0f && (best == null || entry.key.id < best.id))){
                best = entry.key;
                highest = value;
            }
        }
        return best;
    }
    @Override
    protected TextureRegion[] icons(){
        return new TextureRegion[]{region, outRegion, topRegion};
    }
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
        private final ObjectMap<Attribute, Float> attributeTotals = new ObjectMap<>();
        private Block currentRecipe;
        private float currentEfficiency;
        /**
         * Determines the recipe based on the dominant attribute in the surrounding blocks.
         */
        public Block recipe() {
            attributeTotals.clear();
            calculateEfficiency(tileX(), tileY(), rotation, attributeTotals);
            Attribute dominantAttribute = dominant(attributeTotals);
            currentEfficiency = dominantAttribute == null ? 0f : attributeTotals.get(dominantAttribute, 0f);

            currentRecipe = dominantAttribute != null ? attributeBlockMap.get(dominantAttribute) : null;
            return currentRecipe;
        }

        public float productionRate(){
            return payload == null && enabled ? currentEfficiency * efficiency * timeScale() * 60f / buildTime : 0f;
        }

        @Override public boolean shouldConsume(){ return enabled && payload == null && recipe() != null; }

        @Override public void drawSelect(){
            super.drawSelect();
            drawItemSelection(recipe());
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
            if (recipe == null){
                heat = Mathf.approachDelta(heat, 0f, 1f / 60f);
                return;
            }
            if(payload != null) efficiency = 0;
            if(payload == null)progress += edelta() * currentEfficiency;
            boolean produce = currentEfficiency > 0 && payload == null;
            if (produce && progress >= buildTime) {
                payload = new BuildPayload(recipe, team);
                recipe.placeEffect.at(x, y, (float) recipe.size / tilesize);
                progress = 0;
            }
            heat = Mathf.approachDelta(heat, Mathf.num(produce && efficiency > 0f), 1f / 60f);
            time += heat * delta();
        }


        @Override
        public void draw() {
            Draw.rect(region,x,y,0);
            Draw.rect(outRegion, x, y, rotdeg());
            var recipe = currentRecipe;
            if (recipe != null) {
                Draw.draw(Layer.blockBuilding, () -> {
                    Draw.alpha(efficiency);

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
