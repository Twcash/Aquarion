package aquarion.world.blocks.logic;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.struct.IntSeq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.Eachable;
import mindustry.*;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.gen.Tex;
import mindustry.type.Item;
import mindustry.ui.*;
import mindustry.world.Block;
import mindustry.world.blocks.ItemSelection;

public class StorageReader extends Block {
    public TextureRegion onRegion;
    public TextureRegion topRegion;

    public StorageReader(String name) {
        super(name);
        update = true;
        solid = true;
        configurable = true;
        hasItems = false;
        hasLiquids = false;
        rotate = true;
        drawArrow = true;

        config(Item.class, (StorageReaderBuild build, Item item) -> build.filterItem = item);
        config(Integer.class, (StorageReaderBuild build, Integer value) -> {
            build.threshold = Mathf.clamp(value & 0xFFFF, 0, 100);
            build.above = ((value >> 16) & 1) == 1;
        });
        config(IntSeq.class, (StorageReaderBuild build, IntSeq values) -> {
            if(values.size != 3) return;
            build.filterItem = values.get(0) < 0 ? null : Vars.content.item(values.get(0));
            build.threshold = Mathf.clamp(values.get(1), 0, 100);
            build.above = values.get(2) != 0;
        });
        configClear((StorageReaderBuild build) -> build.filterItem = null);
    }

    @Override
    public void load(){
        super.load();
        onRegion = Core.atlas.find(name + "-on");
        topRegion = Core.atlas.find(name + "-top");

    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        Draw.rect(region, plan.drawx(), plan.drawy(),0);
    }
    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{region};
    }

    public class StorageReaderBuild extends Building {
        public Item filterItem;
        public int threshold = 50;
        public boolean above = true;

        @Override
        public void draw(){
            super.draw();
            Draw.rect(region, x, y, 0);
            if(enabled) Draw.rect(onRegion, x, y, rotation * 90);
            Draw.rect(topRegion, x, y, 0);
        }

        @Override
        public void updateTile(){
            Building target = back();
            enabled = false;
            if(target != null && target.team == team && target.items != null && filterItem != null){
                int cap = target.block.itemCapacity;
                int count = target.items.get(filterItem);
                int required = cap * threshold / 100;
                enabled = above ? count >= required : count < required;
            }

            BinaryBlock.transmit(this, front(), enabled);
        }

        @Override
        public void buildConfiguration(Table table){
            Label pctLabel = new Label(threshold + "%");
            TextButton modeButton = new TextButton(
                above ? Core.bundle.get("setting.storagereader.above") : Core.bundle.get("setting.storagereader.below"),
                Styles.cleart
            );
            modeButton.clicked(() -> {
                configure(threshold | ((!above ? 1 : 0) << 16));
            });
            modeButton.update(() -> modeButton.setText(Core.bundle.get(
                above ? "setting.storagereader.above" : "setting.storagereader.below")));
            pctLabel.update(() -> pctLabel.setText(threshold + "%"));

            Slider slider = new Slider(0, 100, 5, false);
            slider.setValue(threshold);
            slider.changed(() -> {
                configure((int)slider.getValue() | ((above ? 1 : 0) << 16));
            });

            table.background(Tex.pane);
            table.margin(8);
            ItemSelection.buildTable(StorageReader.this, table, Vars.content.items(), () -> filterItem, item -> {
                configure(item);
            });
            table.row();
            table.add(Core.bundle.get("setting.storagereader.threshold") + ": ");
            table.row();
            table.add(slider).width(200);
            table.add(pctLabel).width(50);
            table.row();
            table.add(modeButton).size(120, 50);
        }

        @Override
        public Object config(){
            return IntSeq.with(filterItem == null ? -1 : filterItem.id, threshold, above ? 1 : 0);
        }

        @Override
        public byte version(){
            return 1;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.s(filterItem == null ? -1 : filterItem.id);
            write.i(threshold);
            write.bool(above);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            if(revision < 1) return;
            int item = read.s();
            filterItem = item < 0 ? null : Vars.content.item(item);
            threshold = Mathf.clamp(read.i(), 0, 100);
            above = read.bool();
        }
    }
}
