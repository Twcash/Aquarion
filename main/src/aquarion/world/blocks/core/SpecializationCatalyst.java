package aquarion.world.blocks.core;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Scaling;
import arc.util.Structs;
import arc.util.Strings;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.graphics.Pal;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.type.ItemStack;
import mindustry.type.Item;
import mindustry.type.UnitType;
import mindustry.ui.Styles;
import mindustry.ui.Bar;
import mindustry.core.UI;
import mindustry.world.Block;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.consumers.ConsumeItemDynamic;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatValues;

import java.util.Arrays;

public class SpecializationCatalyst extends Block {
    public int[] capacities = {};

    public SpecializationCatalyst(String name) {
        super(name);
        update = true;
        hasItems = true;
        solid = true;
        configurable = true;
        clearOnDoubleTap = true;
        configClear((specBuild build) -> build.selectPlan(-1));
        config(Integer.class, (specBuild build, Integer i) -> {
            if(!configurable) return;

            if(build.currentPlan == i) return;
            build.selectPlan(i);
        });

        config(UnitType.class, (specBuild build, UnitType val) -> {
            if(!configurable) return;

            int next = plans.indexOf(p -> p.unit == val);
            if(build.currentPlan == next) return;
            build.selectPlan(next);
        });
        consume(new ConsumeItemDynamic((specBuild e) -> e.plan() == null ? ItemStack.empty : e.plan().requirements));
    }
    public Seq<UnitPlan> plans = new Seq<>(4);

    public static class UnitPlan{
        public UnitType unit;
        public ItemStack[] requirements;
        public float time;

        public UnitPlan(UnitType unit, float time, ItemStack[] requirements){
            this.unit = unit;
            this.time = time;
            this.requirements = requirements;
        }

        /** Matches vanilla unit research costs, using this catalyst recipe as the build cost. */
        public ItemStack[] researchRequirements(){
            Seq<ItemStack> result = new Seq<>();
            for(ItemStack stack : requirements){
                int amount = UI.roundAmount((int)(stack.amount * unit.researchCostMultiplier));
                if(amount > 0) result.add(new ItemStack(stack.item, amount));
            }
            return result.toArray(ItemStack.class);
        }

        UnitPlan(){}
    }
    @Override
    public void init(){
        initCapacities();
        super.init();
    }

    @Override
    public void afterPatch(){
        initCapacities();
        super.afterPatch();
    }
    @Override
    public void checkContentArrayCapacity(int items, int liquids){
        super.checkContentArrayCapacity(items, liquids);
        if(capacities.length != items) capacities = Arrays.copyOf(capacities, items);
    }
    @Override
    public boolean outputsItems(){
        return false;
    }
    @Override
    public void setBars(){
        super.setBars();
        addBar("progress", (specBuild build) -> new Bar(
            () -> Core.bundle.format("bar.progress", Strings.autoFixed(build.fraction() * 100f, 0)),
            () -> Pal.ammo, build::fraction));
    }
    @Override
    public void setStats(){
        super.setStats();

        stats.remove(Stat.itemCapacity);

        stats.add(Stat.output, table -> {
            table.row();

            for(var plan : plans){
                table.table(Styles.grayPanel, t -> {

                    if(plan.unit.isBanned()){
                        t.image(Icon.cancel).color(Pal.remove).size(40);
                        return;
                    }

                    if(plan.unit.unlockedNow()){
                        t.image(plan.unit.uiIcon).size(40).pad(10f).left().scaling(Scaling.fit).with(i -> StatValues.withTooltip(i, plan.unit));
                        t.table(info -> {
                            info.add(plan.unit.localizedName).left();
                            info.row();
                            info.add(Strings.autoFixed(plan.time / 60f, 1) + " " + Core.bundle.get("unit.seconds")).color(Color.lightGray);
                        }).left();

                        t.table(req -> {
                            req.right();
                            for(int i = 0; i < plan.requirements.length; i++){
                                if(i % 6 == 0){
                                    req.row();
                                }

                                ItemStack stack = plan.requirements[i];
                                req.add(StatValues.stack(stack.item, stack.amount)).pad(5);
                            }
                        }).right().grow().pad(10f);
                    }else{
                        t.image(Icon.lock).color(Pal.darkerGray).size(40);
                    }
                }).growX().pad(5);
                table.row();
            }
        });
    }
    public void initCapacities(){
        capacities = new int[Vars.content.items().size];
        itemCapacity = 10;
        for(UnitPlan plan : plans){
            for(ItemStack stack : plan.requirements){
                capacities[stack.item.id] = Math.max(capacities[stack.item.id], stack.amount * 2);
                itemCapacity = Math.max(itemCapacity, stack.amount * 2);
            }
        }

        consumeBuilder.each(c -> c.multiplier = b -> Vars.state.rules.unitCost(b.team));
    }
    public class specBuild extends Building{
        public int currentPlan = -1;
        public float progress, animationTime, warmup;
        public int pendingPlayer = -1, pendingCore = -1;

        public void cancelRespawn(){
            pendingPlayer = pendingCore = -1;
            progress = 0f;
        }

        public boolean requestRespawn(Player player, AquaCoreBlock.AquaCoreBuild core){
            if(!canSpawn()) return false;
            if(pendingPlayer == -1){
                pendingPlayer = player.id;
                pendingCore = core.pos();
                progress = 0f;
            }
            return pendingPlayer == player.id && pendingCore == core.pos() && isReady();
        }

        public boolean hasRespawnRequest(){
            if(pendingPlayer == -1) return false;
            Player player = Groups.player.getByID(pendingPlayer);
            Building core = Vars.world.build(pendingCore);
            return player != null && player.dead() && player.team() == team
                && core instanceof AquaCoreBlock.AquaCoreBuild build && core.team == team
                && ((AquaCoreBlock)core.block).allowSpawn && build.proximity.contains(this)
                && player.bestCore() == core;
        }

        public void selectPlan(int index){
            int next = index < 0 || index >= plans.size ? -1 : index;
            if(currentPlan != next){
                currentPlan = next;
                cancelRespawn();
            }
        }

        public float fraction(){
            UnitPlan plan = plan();
            return plan == null || pendingPlayer == -1 ? 0f : plan.time <= 0f ? 1f : Mathf.clamp(progress / plan.time);
        }

        public boolean isReady(){
            return pendingPlayer != -1 && plan() != null && progress >= plan().time;
        }

        /** Called once by the server after handing the constructed unit to a player. */
        public void finishSpawn(){
            if(!Vars.state.rules.infiniteResources) consume();
            cancelRespawn();
        }

        @Override
        public void updateTile(){
            if(!Vars.net.client() && pendingPlayer != -1 && !hasRespawnRequest()) cancelRespawn();
            boolean building = pendingPlayer != -1 && canSpawn() && !isReady();
            warmup = Mathf.lerpDelta(warmup, building ? 1f : 0f, 0.05f);
            animationTime += delta() * warmup * Vars.state.rules.unitBuildSpeed(team);
            if(building){
                progress = Math.min(plan().time, progress + delta() * Vars.state.rules.unitBuildSpeed(team));
            }
        }

        @Override
        public void draw(){
            super.draw();
            if(unit() != null && pendingPlayer != -1){
                Draw.draw(Layer.blockOver, () -> Drawf.construct(this, unit(), 90f, fraction(), warmup, animationTime));
            }
        }

        public @Nullable UnitPlan plan(){
            return currentPlan >= 0 && currentPlan < plans.size ? plans.get(currentPlan) : null;
        }

        public boolean canSpawn(){
            UnitPlan plan = plan();
            return enabled && plan != null && !plan.unit.isBanned() && plan.unit.unlockedNow()
                && plan.unit.supportsEnv(Vars.state.rules.env)
                && (Vars.state.rules.infiniteResources || items.has(plan.requirements, Vars.state.rules.unitCost(team)));
        }

        @Override
        public int getMaximumAccepted(Item item){
            return item.id < capacities.length ? Mathf.round(capacities[item.id] * Vars.state.rules.unitCost(team)) : 0;
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            UnitPlan plan = plan();
            return plan != null && items.get(item) < getMaximumAccepted(item)
                && Structs.contains(plan.requirements, stack -> stack.item == item);
        }

        @Override
        public byte version(){
            return 3;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.i(currentPlan);
            write.f(progress);
            write.i(pendingPlayer);
            write.i(pendingCore);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            currentPlan = revision >= 1 ? read.i() : -1;
            progress = revision >= 2 ? read.f() : 0f;
            pendingPlayer = revision >= 3 ? read.i() : -1;
            pendingCore = revision >= 3 ? read.i() : -1;
            if(pendingPlayer == -1) progress = 0f;
            if(plan() == null){
                currentPlan = -1;
                progress = 0f;
                pendingPlayer = pendingCore = -1;
            }else{
                progress = Mathf.clamp(progress, 0f, Math.max(plan().time, 0f));
            }
        }
        @Override
        public void drawSelect(){
            super.drawSelect();
            if(unit() != null){
                drawItemSelection(unit());
            }
        }
        @Override
        public void buildConfiguration(Table table){
            Seq<UnitType> units = Seq.with(plans).map(u -> u.unit).retainAll(u -> u.unlockedNow() && !u.isBanned());

            if(units.any()){
                ItemSelection.buildTable(SpecializationCatalyst.this, table, units, this::unit, unit -> configure(plans.indexOf(u -> u.unit == unit)), selectionRows, selectionColumns);
            }else{
                table.table(Styles.black3, t -> t.add("@none").color(Color.lightGray));
            }
        }
        @Override
        public void display(Table table){
            super.display(table);

            TextureRegionDrawable reg = new TextureRegionDrawable();

            table.row();
            table.table(t -> {
                t.left();
                t.image().update(i -> {
                    i.setDrawable(unit() == null ? Icon.cancel : reg.set(unit().uiIcon));
                    i.setScaling(Scaling.fit);
                    i.setColor(unit() == null ? Color.lightGray : Color.white);
                }).size(32).padBottom(-4).padRight(2);
                t.label(() -> unit() == null ? "@none" : unit().localizedName).wrap().width(230f).color(Color.lightGray);
            }).left();
        }
        @Override
        public Object config(){
            return currentPlan;
        }
        public @Nullable UnitType unit(){
            return plan() == null ? null : plan().unit;
        }
    }
}
