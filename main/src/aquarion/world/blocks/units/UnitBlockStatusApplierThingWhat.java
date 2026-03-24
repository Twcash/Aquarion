package aquarion.world.blocks.units;

import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Nullable;
import arc.util.Structs;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.Effect;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.*;
import mindustry.ui.Bar;
import mindustry.ui.Styles;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.PayloadBlock;
import mindustry.world.consumers.ConsumeItemDynamic;
import mindustry.world.consumers.ConsumeLiquidsDynamic;
import mindustry.world.consumers.ConsumePayloadDynamic;

public class UnitBlockStatusApplierThingWhat extends PayloadBlock {
    public UnitBlockStatusApplierThingWhat(String name) {
        super(name);
        rotate = true;
        acceptsItems = true;
        acceptsPayload = true;
        hasLiquids = true;
        configurable = true;
        config(StatusEffect.class, (StatusApplierBuild build, StatusEffect val) -> {
            if(!configurable) return;
            int next = plans.indexOf(p -> p.effect == val);
            if(build.planIndex == next) return;
            build.planIndex = next;
            build.progress = 0;
        });
        config(Integer.class, (StatusApplierBuild build, Integer i) -> {
            if(!configurable) return;

            if(build.planIndex == i) return;
            build.planIndex = i < 0 || i >= plans.size ? -1 : i;
            build.progress = 0;
        });
        hasItems = true;
        drawArrow = true;
    }
    @Override
    public void init(){

        consume(consPayload = new ConsumePayloadDynamic((StatusApplierBuild build) -> build.plan().requirements == null ? PayloadStack.list() : build.plan().requirements.isEmpty() ? PayloadStack.list() : build.plan().requirements));
        consume(consItem = new ConsumeItemDynamic((StatusApplierBuild build) -> build.plan().itemReq != null ? build.plan().itemReq : ItemStack.empty));
        consume(new ConsumeLiquidsDynamic((StatusApplierBuild build) -> build.plan().liquidReq != null ? build.plan().liquidReq : LiquidStack.empty));

        super.init();

        initCapacities();
    }
    public int[] capacities = {};
    @Override
    public void setBars() {
        super.setBars();

        boolean planLiquids = false;
        for (int i = 0; i < plans.size; i++) {
            var req = plans.get(i).liquidReq;
            if (req != null && req.length > 0) {
                for (var stack : req) {
                    addLiquidBar(stack.liquid);
                }
                planLiquids = true;
            }
        }

        if (planLiquids) {
            removeBar("liquid");
        }

        addBar("progress", (StatusApplierBuild e) -> new Bar("bar.progress", Pal.ammo, () -> e.progress));
    }

    public void initCapacities(){
        consumeBuilder.each(c -> c.multiplier = b -> Vars.state.rules.unitCost(b.team));

        itemCapacity = 10;
        capacities = new int[Vars.content.items().size];
        for(StatusPlan plan : plans){
            if(plan.itemReq != null){
                for(ItemStack stack : plan.itemReq){
                    capacities[stack.item.id] = Math.max(capacities[stack.item.id], stack.amount * 2);
                    itemCapacity = Math.max(itemCapacity, stack.amount * 2);
                }
            }

            if(plan.liquidReq != null){
                for(LiquidStack stack : plan.liquidReq){
                    liquidFilter[stack.liquid.id] = true;
                }
            }
        }
    }
    protected @Nullable ConsumeItemDynamic consItem;
    protected @Nullable ConsumePayloadDynamic consPayload;
    public Seq<StatusPlan> plans = new Seq<>();
    public static class StatusPlan{
        public StatusEffect effect;
        public float time;
        public Effect fx;

        @Nullable public ItemStack[] itemReq;
        @Nullable public LiquidStack[] liquidReq;
        @Nullable public Seq<PayloadStack> requirements;
        public StatusPlan(StatusEffect effect, float time){
            this.effect = effect;
            this.time = time;
        }
    }

    public class StatusApplierBuild extends PayloadBlockBuild<Payload> {
        public PayloadSeq blocks = new PayloadSeq();
        public float progress = 0f;
        public int planIndex = 0;
        public StatusPlan plan(){
            return plans.get(Mathf.clamp(planIndex, 0, plans.size - 1));
        }
        @Override
        public void updateTile() {

            if((payload instanceof BuildPayload b && b.build instanceof UnitBlock.UnitBlockBuild bb)){
                if(!enabled) return;
                if(efficiency > 0f){
                    progress += edelta() * efficiency;
                    if(Mathf.chance(0.2f * progress/plan().time) && plan().fx != null){
                        plan().fx.at(this);
                    }
                }

                if(progress >= plan().time){
                    bb.effects.addUnique(plan().effect);
                    progress = 0f;
                    consume();
                    blocks.clear();

                }
                if(!bb.effects.contains(plan().effect)){
                    moveInPayload();
                } else {
                    moveOutPayload();
                }
            }else if(payload != null){
                moveOutPayload();
            }else{
                moveInPayload();
            }
        }
        @Override
        public boolean shouldConsume(){
            if(plan().requirements != null){
                if(!plan().requirements.isEmpty() &&consPayload.efficiency(this) > 0){
                    return enabled  && consPayload.efficiency(this) > 0 &&  consItem.efficiency(this) > 0;
                }
            }
            return enabled  && consItem.efficiency(this) > 0;
        }

        @Override
        public void buildConfiguration(Table table){
            Seq<StatusEffect> effects = plans.map(p -> p.effect);

            ItemSelection.buildTable(UnitBlockStatusApplierThingWhat.this, table,
                    effects,
                    () -> planIndex >= 0 && planIndex < plans.size ? plans.get(planIndex).effect : null,
                    effect -> {
                        int index = plans.indexOf(p -> p.effect == effect);
                        configure(index);
                    },
                    selectionRows,
                    selectionColumns
            );
        }
        @Override
        public void drawSelect(){
            super.drawSelect();
            if(plans.size > 1 && planIndex != -1 && planIndex < plans.size){
                drawItemSelection(plans.get(planIndex).effect);
            }
        }
        @Override
        public void display(Table table){
            super.display(table);
            table.row();
            table.label(() -> "[accent]Status:[] " + plan().effect.localizedName);
        }
        @Override
        public PayloadSeq getPayloads(){
            return blocks;
        }

        @Override
        public boolean acceptPayload(Building source, Payload payload){
            StatusPlan plan = plan();
            if((payload instanceof BuildPayload b && b.build instanceof UnitBlock.UnitBlockBuild bb && this.payload == null)){
                return true;
            }
            if( plan.requirements == null || plan.requirements.isEmpty()) return false;
            return plan.requirements.contains(p -> p.item == payload.content()) &&  plan().requirements.contains(p -> blocks.get(payload.content()) < p.amount);
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            StatusPlan plan = plan();

            if(plan.itemReq == null) return false;

            return Structs.contains(plan.itemReq, stack -> stack.item == item)
                    && items.get(item) < getMaximumAccepted(item);
        }
        @Override
        public void handlePayload(Building source, Payload payload){
            if(payload instanceof BuildPayload b && b.build instanceof UnitBlock.UnitBlockBuild){
                super.handlePayload(source, payload);
            }else{
                StatusPlan plan = plan();
                if(plan.requirements != null && plan.requirements.contains(p -> p.item == payload.content())){
                    blocks.add(payload.content(), 1);
                }
            }
        }
        @Override
        public int getMaximumAccepted(Item item){
            StatusPlan plan = plan();

            if(plan.itemReq == null) return 0;

            for(ItemStack stack : plan.itemReq){
                if(stack.item == item){
                    return stack.amount * 2;
                }
            }

            return 0;
        }
        @Override
        public void draw(){
            Draw.rect(region, x, y, 0);
            if(payload!=null){
                payload.drawShadow(0.5f);
                payload.draw();
            }
            Draw.z(Layer.blockOver + 0.1f);
            Draw.rect(topRegion, x, y, 0);
            Draw.rect(outRegion, x, y, rotdeg());
        }
        @Override
        public Object config(){
            return planIndex;
        }
        @Override
        public void write(Writes write){
            super.write(write);
            blocks.write(write);
            write.f(progress);
            write.i(planIndex);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            blocks.read(read);
            progress = read.f();
            planIndex = read.i();
        }
    }
}