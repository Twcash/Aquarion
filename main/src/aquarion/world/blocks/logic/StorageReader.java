package aquarion.world.blocks.logic;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
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
            build.threshold = value & 0xFFFF;
            build.above = ((value >> 16) & 1) == 1;
        });
    }

    @Override
    public void load(){
        super.load();
        onRegion = Core.atlas.find(name + "-on");
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        Draw.rect(region, plan.drawx(), plan.drawy(), plan.rotation * 90);
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
            Draw.rect(region, x, y, rotation * 90);
            if(enabled) Draw.rect(onRegion, x, y, rotation * 90);
        }

        @Override
        public void updateTile(){
            Building target = back();
            if(target != null && target.items != null && filterItem != null){
                int cap = target.block.itemCapacity;
                int count = target.items.get(filterItem);
                int required = cap * threshold / 100;
                enabled = above ? count >= required : count < required;
            }

            if(front() != null){
                if(front() instanceof BinaryChannel.BinaryChannelBuild y){
                    if(front().back() != null && front().back() == this) y.active = enabled;
                }else if(!(front() instanceof BinarySplitter.BinarySplitterBuild || front() instanceof toggler.togglerBuild)){
                    front().enabled = enabled;
                }
            }
        }

        @Override
        public void buildConfiguration(Table table){
            Label pctLabel = new Label(threshold + "%");
            TextButton modeButton = new TextButton(
                above ? Core.bundle.get("setting.storagereader.above") : Core.bundle.get("setting.storagereader.below"),
                Styles.cleart
            );
            modeButton.clicked(() -> {
                above = !above;
                modeButton.setText(above ? Core.bundle.get("setting.storagereader.above") : Core.bundle.get("setting.storagereader.below"));
            });

            Slider slider = new Slider(0, 100, 5, false);
            slider.setValue(threshold);
            slider.changed(() -> {
                threshold = (int)slider.getValue();
                pctLabel.setText(threshold + "%");
            });

            table.background(Tex.pane);
            table.margin(8);
            ItemSelection.buildTable(StorageReader.this, table, Vars.content.items(), () -> filterItem, item -> {
                filterItem = item;
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
            return filterItem;
        }
    }
}
