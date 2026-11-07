package aquarion.world.blocks.payload;

import aquarion.world.blocks.AlternativeBuildCosts;
import arc.Core;
import arc.func.Cons;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Vec2;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.ButtonGroup;
import arc.scene.ui.ImageButton;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Scaling;
import arc.util.Strings;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.ctype.UnlockableContent;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.graphics.Shaders;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.meta.BuildVisibility;
import mindustry.world.Tile;
import mindustry.world.blocks.payloads.BlockProducer;
import mindustry.world.blocks.payloads.Constructor;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.consumers.ConsumeItemDynamic;
import mindustry.world.consumers.ConsumeLiquidsDynamic;
import mindustry.world.consumers.ConsumePowerDynamic;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatValues;

import static mindustry.Vars.tilesize;

/** An Aquarion payload constructor with configurable per-recipe costs and routed output. */
public class AquaConstructor extends Constructor {
    /** Per-recipe item override. */
    public ObjectMap<Block, ItemStack[]> recipeRequirements = new ObjectMap<>();
    /** Per-recipe liquid override. */
    public ObjectMap<Block, LiquidStack[]> recipeLiquids = new ObjectMap<>();
    /** Per-recipe power cost. */
    public ObjectMap<Block, Float> powerRecipeRequirements = new ObjectMap<>();

    public AquaConstructor(String name) {
        super(name);
        buildType = RouterConstructorBuild::new;
        removeConsumers(c -> c instanceof ConsumeItemDynamic);
        consume(new ConsumeItemDynamic((BlockProducerBuild e) -> {
            Block block = e.recipe();
            if(block == null) return ItemStack.empty;
            ItemStack[] src = recipeRequirements.containsKey(block)
                ? recipeRequirements.get(block)
                : block.requirements;

            ItemStack[] out = new ItemStack[src.length];
            for(int i = 0; i < src.length; i++){
                out[i] = new ItemStack(src[i].item,
                    Mathf.ceil(src[i].amount * Vars.state.rules.buildCostMultiplier));
            }
            return out;
        }));
        consume(new ConsumeLiquidsDynamic((BlockProducerBuild e) -> {
            Block block = e.recipe();
            if(block == null) return LiquidStack.empty;
            return recipeLiquids.get(block, LiquidStack.empty);
        }));
        consume(new ConsumePowerDynamic(0f, build -> {
            Block block = ((BlockProducerBuild)build).recipe();
            return block == null ? 0f : powerRecipeRequirements.get(block, 0f);
        }));
    }

    @Override
    public void getDependencies(Cons<UnlockableContent> cons){
        super.getDependencies(content -> {
            if(content instanceof Item item && AlternativeBuildCosts.isAlternativeItem(this, item)) return;
            cons.get(content);
        });
    }

    /** True when the block has a custom item or liquid cost registered. */
    public boolean hasCustomCost(Block block){
        return recipeRequirements.containsKey(block) || recipeLiquids.containsKey(block);
    }

    /** Item cost for a recipe: the override, or the block's own requirements. */
    public ItemStack[] itemCosts(Block block){
        ItemStack[] reqs = recipeRequirements.get(block);
        return reqs != null ? reqs : block.requirements;
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.output, table -> {
            table.row();
            for(Block block : filter){
                if(!canProduce(block) || (block.buildVisibility != BuildVisibility.hidden && !block.unlockedNow())) continue;

                //time it takes to build this recipe, in seconds.
                float buildSecs = block.buildTime / Math.max(buildSpeed, 0.001f) / 60f;

                table.table(Styles.grayPanel, t -> {
                    t.left();
                    t.image(block.uiIcon).size(40).pad(10f).left().scaling(Scaling.fit).with(i -> StatValues.withTooltip(i, block));
                    t.table(info -> {
                        info.add(block.localizedName).left();
                        info.row();
                    }).pad(10).left();
                }).fill().padTop(5).padBottom(5);

                //item / liquid consumption per second + power cost of the recipe.
                table.table(Styles.grayPanel, t -> {
                    t.left();
                    for(ItemStack stack : itemCosts(block)){
                        t.image(stack.item.uiIcon).size(24).pad(3).with(i -> StatValues.withTooltip(i, stack.item));
                        float rate = buildSecs > 0f ? stack.amount / buildSecs : 0f;
                        t.add("x" + stack.amount + " (" + Strings.autoFixed(rate, 1) + "/s)").left().padRight(8);
                    }
                    for(LiquidStack stack : recipeLiquids.get(block, LiquidStack.empty)){
                        t.image(stack.liquid.uiIcon).size(24).pad(3).with(i -> StatValues.withTooltip(i, stack.liquid));
                        t.add(Strings.autoFixed(stack.amount*60, 1) + "/s").left().padRight(8);
                    }
                    float power = powerRecipeRequirements.get(block, 0f);
                    if(power > 0f){
                        t.image(Icon.power).color(Pal.power).size(24).pad(3);
                        t.add(power * 60f + "").left();
                    }
                }).fill().padTop(5).padBottom(5);

                //build time.
                table.table(Styles.grayPanel, t -> {
                    t.left();
                    t.add(Stat.buildTime.localized() + ": " + Strings.autoFixed(buildSecs, 1) + "s").left();
                }).fill().padTop(5).padBottom(5);

                table.row();
            }
        });
    }

    @Override
    public boolean canProduce(Block block){
        //Explicitly configured recipes may use custom costs, while blocks placed
        //directly in the constructor filter remain valid even without an override.
        //This also permits hidden payload blocks added to the filter.
        return hasCustomCost(block) || filter.contains(block);
    }

    public class RouterConstructorBuild extends ConstructorBuild{
        // Kept in the save format for compatibility with worlds saved before output
        // was fixed to the constructor's rotation.
        private int inputDir = -1;
        /** Direction the finished block is currently being routed toward, for the
         *  -out sprite. -1 = idle (fall back to the block's facing). */
        private int outputDir = -1;
        @Override
        public void moveOutPayload(){
            if(payload == null){
                outputDir = -1;
                return;
            }
            updatePayload();

            int trns = block.size / 2 + 1;

            //The constructor's rotation is its one output direction. If that
            //receiver is missing or full, hold the payload until it can accept it.
            int d = rotation;
            Building other = nearby(Geometry.d4[d].x * trns, Geometry.d4[d].y * trns);
            if(other != null && other.acceptPayload(other, payload)){
                outputDir = d;
                sendToward(other, d);
                return;
            }

            outputDir = -1;
            payVector.approach(Vec2.ZERO, payloadSpeed * delta());
        }

        /** Slides the finished block toward the given side and hands it off when it
         *  arrives. Returns true once the payload has been transferred. */
        private boolean sendToward(Building other, int d){
            float outAng = d * 90f;
            payRotation = Angles.moveToward(payRotation, outAng, payloadRotateSpeed * delta());
            Vec2 dest = Tmp.v1.trns(outAng, size * tilesize / 2f);
            payVector.approach(dest, payloadSpeed * delta());

            if(payVector.within(dest, 0.001f)){
                payVector.clamp(-size * tilesize / 2f, -size * tilesize / 2f, size * tilesize / 2f, size * tilesize / 2f);
                if(other.acceptPayload(other, payload)){
                    other.handlePayload(this, payload);
                    payload = null;
                    return true;
                }
            }
            return false;
        }

        /** Same as BlockProducer.draw, but the -out sprite follows the current output
         *  destination instead of always pointing at the block's facing. */
        @Override
        public void draw(){
            Draw.rect(region, x, y);
            Draw.rect(outRegion, x, y, outputDir == -1 ? rotdeg() : outputDir * 90f);

            var recipe = recipe();
            if(recipe != null){
                Drawf.shadow(x, y, recipe.size * tilesize * 2f, progress / recipe.buildTime);
                Draw.draw(Layer.blockBuilding, () -> {
                    Draw.color(Pal.accent);

                    for(TextureRegion region : recipe.getGeneratedIcons()){
                        Shaders.blockbuild.region = region;
                        Shaders.blockbuild.time = time;
                        Shaders.blockbuild.progress = progress / recipe.buildTime;

                        Draw.rect(region, x, y, recipe.rotate ? rotdeg() : 0);
                        Draw.flush();
                    }

                    Draw.color();
                });
                Draw.z(Layer.blockBuilding + 1);
                Draw.color(Pal.accent, heat);

                Lines.lineAngleCenter(x + Mathf.sin(time, 10f, tilesize / 2f * recipe.size + 1f), y, 90, recipe.size * tilesize + 1f);

                Draw.reset();
            }

            drawPayload();

            Draw.z(Layer.blockBuilding + 1.1f);
            Draw.rect(topRegion, x, y);
        }

        @Override
        public byte version(){
            return 1;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.b(inputDir);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            if(revision >= 1){
                inputDir = read.b();
            }
        }

        @Override
        public void buildConfiguration(Table table){
            //Custom selection grid: ItemSelection.buildTable filters by unlockedNow(),
            //which hides unresearched blocks. This skips that gate entirely.
            Seq<Block> items = Vars.content.blocks().select(AquaConstructor.this::canProduce);
            ButtonGroup<ImageButton> group = new ButtonGroup<>();
            group.setMinCheckCount(0);
            Table cont = new Table().top();
            cont.defaults().size(40);

            int cols = selectionColumns;
            int i = 0;
            for(Block item : items){
                ImageButton button = cont.button(Tex.whiteui, Styles.clearNoneTogglei, 40f, () -> {
                    Vars.control.input.config.hideConfig();
                }).tooltip(item.localizedName).group(group).get();
                button.changed(() -> recipe = button.isChecked() ? item : null);
                button.getStyle().imageUp = new TextureRegionDrawable(item.fullIcon);
                button.update(() -> button.setChecked(recipe == item));

                if(i++ % cols == (cols - 1)){
                    cont.row();
                }
            }

            ScrollPane pane = new ScrollPane(cont, Styles.smallPane);
            pane.setScrollingDisabled(true, false);
            pane.setOverscroll(false, false);
            table.top().add(pane).maxHeight(40 * selectionRows);
        }
        /** Override acceptItem so the building only fills up to 2x the override amount
         *  (matching the consume rate * 2, the same ratio vanilla uses). Without this,
         *  the building accepts up to 2x the BLOCK's vanilla requirements - so e.g.
         *  router2 (which costs 2 fragments) lets you fill it with 4 even when the
         *  override charges 1 per cycle. */
        @Override
        public int getMaximumAccepted(Item item){
            Block block = recipe();
            if(block == null) return 0;
            ItemStack[] reqs = recipeRequirements.get(block);
            if(reqs == null) return super.getMaximumAccepted(item);
            for(ItemStack stack : reqs){
                if(stack.item == item){
                    return Mathf.ceil(stack.amount * 2f * Vars.state.rules.buildCostMultiplier);
                }
            }
            return 0;
        }

        /** ConsumeLiquidsDynamic never populates block.liquidFilter, so vanilla
         *  hasLiquid(liquid) (and therefore acceptLiquid) returns false. Override to
         *  read from the configured recipe's liquids AND any liquids registered via
         *  the block's own consumeLiquid(...) calls. */
        @Override
        public boolean acceptLiquid(Building source, Liquid liquid){
            int id = liquid.id;
            int cap = Mathf.ceil(liquidCapacity * Vars.state.rules.buildCostMultiplier);
            //1) Recipe override (highest priority - explicit per-recipe liquid cost).
            Block block = recipe();
            if(block != null){
                LiquidStack[] reqs = recipeLiquids.get(block, LiquidStack.empty);
                for(LiquidStack stack : reqs){
                    if(stack.liquid.id == id){
                        return liquids.get(liquid) < Mathf.ceil(stack.amount * 2f * Vars.state.rules.buildCostMultiplier);
                    }
                }
            }
            //2) The constructor's own consumeLiquid(...) registrations.
            if(AquaConstructor.this.liquidFilter != null && id < AquaConstructor.this.liquidFilter.length && AquaConstructor.this.liquidFilter[id]){
                return liquids.get(liquid) < cap;
            }
            return false;
        }
    }
}
