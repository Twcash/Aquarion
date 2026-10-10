package aquarion.world.blocks.effect;

import aquarion.annotations.Annotations;
import arc.audio.Sound;
import arc.func.Cons;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.Rand;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import aquarion.world.graphics.AquaFx;
import aquarion.world.blocks.AlternativeBuildCosts;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.ctype.UnlockableContent;
import mindustry.graphics.Layer;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.world.Block;

import static aquarion.content.AquaItems.nickel;

public class ResearchVoider extends Block {
    public float processRate = 1f;
    public Sound researchSound;
    public float processTime = 600;
    public @Annotations.Load("circle-shadow") TextureRegion softGlowRegion;
    Rand rand = new Rand();

    public ResearchVoider(String name) {
        super(name);
        hasItems = true;
        acceptsItems = true;
        update = true;
        hasPower = true;
        solid = true;
    }

    @Override
    public void setStats(){
        super.setStats();
        AlternativeBuildCosts.applyStats(this);
    }

    @Override
    public void getDependencies(Cons<UnlockableContent> cons){
        super.getDependencies(content -> {
            if(content instanceof Item item && AlternativeBuildCosts.isAlternativeItem(this, item)) return;
            cons.get(content);
        });
    }

    public class ResearchVoiderBuild extends Building implements AlternativeBuildCosts.CostChoiceReceiver {
        public float processProg = 0f;
        public float warmup = 0f;
        private ItemStack constructionCost;

        @Override
        public void setBuildCostStack(ItemStack stack){
            constructionCost = stack == null ? null : stack.copy();
        }

        @Override
        public ItemStack buildCostStack(){
            return constructionCost == null ? null : constructionCost.copy();
        }

        public void processBatch() {
            if (items.empty()) return;
            mindustry.type.Sector sector = Vars.state.getSector();
            if (sector != null) {
                items.each((item, amount) -> {
                    sector.info.handleItemExport(item, amount);
                    ResearchServer.addResearch(sector.id, item, amount);
                });
            };
            AquaFx.vaporizeItem.at(x, y, 0, items.first());
            items.clear();
            processProg = 0f;
        }

        @Override
        public void updateTile() {
            if (efficiency <= 0f || items.empty()) {
                warmup = Mathf.approachDelta(warmup, 0f, 0.02f);
                return;
            }

            warmup = Mathf.approachDelta(warmup, 1f, 0.02f);
            processProg += edelta() * processRate;
            if (processProg >= processTime) {
                processBatch();
                researchSound.at(this);
            }
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return items.get(item) < block.itemCapacity;
        }

        @Override
        public byte version() {
            return 5;
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(processProg);
            write.f(warmup);
            write.s((short)(constructionCost == null ? -1 : constructionCost.item.id));
            write.i(constructionCost == null ? -1 : constructionCost.amount);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            if (revision >= 1) {
                processProg = read.f();
                warmup = read.f();
            }
            if (revision == 2) {
                read.l(); // skip old lastSavedTime for backwards compat
            }
            if(revision >= 4){
                short itemId = read.s();
                if(revision >= 5){
                    int amount = read.i();
                    constructionCost = itemId < 0 || amount < 0 ? null : new ItemStack(Vars.content.item(itemId), amount);
                }else{
                    constructionCost = AlternativeBuildCosts.stackFor(block, itemId < 0 ? null : Vars.content.item(itemId));
                }
            }else{
                // Older Translation Laboratories required nickel directly.
                constructionCost = AlternativeBuildCosts.stackFor(block, nickel);
                if(constructionCost == null) constructionCost = new ItemStack(nickel, 900);
            }
        }

        @Override
        public void draw() {
            Draw.rect(region, x, y, rotdeg());

            Draw.z(Layer.blockOver);
            Item item = items.first();
            if (item == null) return;
            float p = processProg / processTime;
            Draw.alpha(1);
            for (int i = 0; i < 38; i++) {
                rand.setSeed(this.id + i);
                float fin = (rand.random(2f) + 1) % 1f;
                float fout = 1f - fin;
                float angle = rand.random(360f) + (Time.time / 12) % 360f;
                float len = 8 * Interp.pow2Out.apply(fout);
                Draw.color(Color.black);
                Fill.circle(
                        x + Angles.trnsx(angle, len),
                        y + Angles.trnsy(angle, len),
                        8 * Interp.pow2Out.apply(fin) * warmup()
                );
                Draw.color(Color.white);
                Fill.circle(
                        x + Angles.trnsx(angle, len),
                        y + Angles.trnsy(angle, len),
                        6 * Interp.pow2Out.apply(fin) * warmup()
                );
            }
            Draw.alpha(1);
            Draw.color(Color.black);
            Fill.circle(x, y, 8 * Interp.pow2Out.apply(p) + Mathf.absin(Time.time / 2.0f, 10, 2.5f));
            float flareLen = 24f * Interp.pow2Out.apply(p) * warmup;
            float flareWidth = 4f * Interp.pow2Out.apply(p) * warmup;
            Draw.color(Color.black);
            Draw.alpha(1);
            Fill.tri(x, y, x + flareLen + Mathf.absin(Time.time / 2.0f, 10, 2.5f), y, x, y + flareWidth);
            Fill.tri(x, y, x - flareLen + Mathf.absin(Time.time / 2.0f, 10, 2.5f), y, x, y + flareWidth);
            Fill.tri(x, y, x + flareLen + Mathf.absin(Time.time / 2.0f, 10, 2.5f), y, x, y - flareWidth);
            Fill.tri(x, y, x - flareLen + Mathf.absin(Time.time / 2.0f, 10, 2.5f), y, x, y - flareWidth);
            Draw.color(Color.white);
            float innerLen = flareLen * 0.65f;
            float innerWidth = flareWidth * 0.5f;
            Fill.tri(x, y, x + innerLen + Mathf.absin(Time.time / 2.0f, 10, 2.5f), y, x, y + innerWidth);
            Fill.tri(x, y, x - innerLen + Mathf.absin(Time.time / 2.0f, 10, 2.5f), y, x, y + innerWidth);
            Fill.tri(x, y, x + innerLen + Mathf.absin(Time.time / 2.0f, 10, 2.5f), y, x, y - innerWidth);
            Fill.tri(x, y, x - innerLen + Mathf.absin(Time.time / 2.0f, 10, 2.5f), y, x, y - innerWidth);
            Draw.alpha(p / 3);
            Draw.color(Color.white);
            Fill.circle(x, y, 6 * Interp.pow2Out.apply(p) + Mathf.absin(Time.time, 10, 2f));
            Draw.alpha(p * 5);
            Draw.rect(softGlowRegion, x, y, 7 * Interp.pow2Out.apply(p) * 8, 7 * Interp.pow2Out.apply(p) * 8, edelta());
            Fill.circle(x, y, 5 * Interp.pow2Out.apply(p) + Mathf.absin(Time.time, 10, 3f));
            if (Mathf.chanceDelta(0.05f)) {
                AquaFx.translatorCharge.at(x, y, 0, warmup * efficiency);
            }
        }
    }
}
