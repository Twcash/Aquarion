package aquarion.world.blocks.distribution;

import arc.Core;
import arc.scene.ui.layout.Table;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.world.Block;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.BuildPayload;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import mindustry.world.blocks.ItemSelection;

/** A rail endpoint with saved item or liquid storage. */
public class CargoStation extends RailPoint{
    public final boolean pickup;
    public final boolean liquidCargo;
    public TextureRegion liquidRegion;

    public static boolean smallPayload(Payload payload){
        return payload instanceof BuildPayload build && build.block().size <= 2;
    }

    public CargoStation(String name, boolean pickup){
        this(name, pickup, false);
    }

    public CargoStation(String name, boolean pickup, boolean liquidCargo){
        super(name);
        this.liquidCargo = liquidCargo;
        this.pickup = pickup;
        hasItems = !liquidCargo;
        hasLiquids = liquidCargo;
        acceptsPayload = outputsPayload = !liquidCargo;
        liquidCapacity = 1000f;
        itemCapacity = 1000;
        size = 3;
        range = 120f;
        rotateDraw = false;
        buildType = CargoStationBuild::new;
        if(!pickup){
            saveConfig = true;
            config(Integer.class, (CargoStationBuild build, Integer mode) -> { if(!liquidCargo) build.payloadMode = mode == 1; });
            config(Block.class, (CargoStationBuild build, Block block) -> {
                if(!liquidCargo && block.size <= 2 && block.unlockedNow() && block.isVisible()){
                    build.filterBlock = block;
                    build.payloadMode = true;
                }
            });
            config(Item.class, (CargoStationBuild build, Item item) -> { build.filterItem = item; build.payloadMode = false; });
            config(Liquid.class, (CargoStationBuild build, Liquid liquid) -> build.filterLiquid = liquid);
            configClear((CargoStationBuild build) -> { build.filterItem = null; build.filterLiquid = null; build.filterBlock = null; });
        }
    }

    @Override
    public void load(){
        super.load();
        region = Core.atlas.find(name, Core.atlas.find(liquidCargo ? "aquarion-" + (pickup ? "cargo-dock" : "cargo-depot") : "", Core.atlas.find(pickup ? "container" : "vault")));
        liquidRegion = Core.atlas.find(name + "-liquid");
    }

    public class CargoStationBuild extends RailPointBuild{
        public Item filterItem;
        public Liquid filterLiquid;
        public Block filterBlock;
        public boolean payloadMode;
        public Payload storedPayload;

        public boolean acceptsPayloadCargo(Payload payload){
            return !liquidCargo && !pickup && payloadMode && storedPayload == null && smallPayload(payload)
                && payload instanceof BuildPayload build && build.block() == filterBlock;
        }
        public boolean liquidCargo(){ return liquidCargo; }
        public float liquidRoom(){ return Math.max(0f, liquidCapacity - liquids.sum((liquid, amount) -> amount)); }
        public boolean acceptsLiquidCargo(Liquid liquid){ return liquidCargo && !pickup && liquid != null && filterLiquid == liquid && liquidRoom() > 0.001f; }

        public boolean acceptsCargo(Item item){
            return !liquidCargo && !payloadMode && !pickup && item != null && filterItem == item && room() > 0;
        }

        @Override
        public void buildConfiguration(Table table){
            if(!pickup && liquidCargo) ItemSelection.buildTable(CargoStation.this, table, Vars.content.liquids(), () -> filterLiquid, this::configure);
            else if(!pickup){
                table.button("Items", () -> configure(0));
                table.button("Payloads", () -> configure(1));
                table.row();
                Table selection = new Table();
                table.add(selection).colspan(2);
                boolean[] previous = {!payloadMode};
                selection.update(() -> {
                    if(previous[0] == payloadMode) return;
                    previous[0] = payloadMode;
                    selection.clear();
                    if(payloadMode) ItemSelection.buildTable(CargoStation.this, selection,
                        Vars.content.blocks().select(block -> block.size == 2 && block.unlockedNow() && block.isVisible()),
                        () -> filterBlock, this::configure);
                    else ItemSelection.buildTable(CargoStation.this, selection, Vars.content.items(), () -> filterItem, this::configure);
                });
            }
        }

        @Override
        public void drawSelect(){
            super.drawSelect();
            if(!pickup) drawItemSelection(liquidCargo ? filterLiquid : payloadMode ? filterBlock : filterItem);
        }

        @Override
        public Object config(){
            return pickup ? super.config() : liquidCargo ? filterLiquid : payloadMode ? (filterBlock == null ? Integer.valueOf(1) : filterBlock) : filterItem;
        }

        @Override
        public byte version(){
            return 4;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.s(filterItem == null ? -1 : filterItem.id);
            write.s(filterLiquid == null ? -1 : filterLiquid.id);
            write.bool(payloadMode);
            write.s(filterBlock == null ? -1 : filterBlock.id);
            Payload.write(storedPayload, write);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            filterItem = revision >= 2 ? Vars.content.item(read.s()) : null;
            filterLiquid = revision >= 3 ? Vars.content.liquid(read.s()) : null;
            payloadMode = revision >= 4 && read.bool();
            filterBlock = revision >= 4 ? Vars.content.block(read.s()) : null;
            storedPayload = revision >= 4 ? Payload.read(read) : null;
        }

        public boolean pickup(){
            return pickup;
        }

        @Override public Payload getPayload(){ return storedPayload; }
        @Override public Payload takePayload(){ Payload result = storedPayload; storedPayload = null; return result; }
        @Override public boolean acceptPayload(Building source, Payload payload){
            return !liquidCargo && storedPayload == null && smallPayload(payload)
                && (source == null || source.team == team) && ((BuildPayload)payload).build.team == team
                && (pickup || acceptsPayloadCargo(payload));
        }
        @Override public void handlePayload(Building source, Payload payload){
            if(acceptPayload(source, payload)){ storedPayload = payload; payload.set(x, y, rotdeg()); }
        }

        @Override protected void drawUnderTop(){
            if(liquidCargo && liquidRegion != null && liquidRegion.found()){
                Liquid liquid = liquids.current();
                if(liquid != null){
                    Draw.color(liquid.color, Mathf.clamp(liquids.get(liquid) / liquidCapacity));
                    Draw.rect(liquidRegion, x, y);
                    Draw.reset();
                }
            }
            if(storedPayload!= null){
                storedPayload.set(x,y, 0);
                if(storedPayload instanceof BuildPayload bay) {
                    bay.draw();
                } else  storedPayload.draw();
            }
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid){
            return liquidCargo && pickup && source != null && source.team == team && liquidRoom() > 0.001f;
        }

        @Override
        public void handleLiquid(Building source, Liquid liquid, float amount){
            if(liquidCargo) liquids.add(liquid, Math.min(Math.max(0f, amount), liquidRoom()));
        }

        public int room(){
            return Math.max(0, itemCapacity - items.total());
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            return !liquidCargo && pickup && source != null && source.team == team && room() > 0;
        }

        @Override
        public int acceptStack(Item item, int amount, mindustry.gen.Teamc source){
            return !liquidCargo && pickup && (source == null || source.team() == team) ? Math.min(amount, room()) : 0;
        }

        @Override
        public void updateTile(){
            super.updateTile();
            if(storedPayload != null){
                storedPayload.set(x, y, rotdeg());
                storedPayload.update(null, this);
                if(!pickup && movePayload(storedPayload)) storedPayload = null;
            }
            if(!pickup && liquidCargo){
                for(Liquid liquid : Vars.content.liquids()) if(liquids.get(liquid) > 0f) dumpLiquid(liquid);
            }else if(!pickup) dump();
        }
    }
}


