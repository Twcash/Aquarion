package aquarion.world.blocks;

import arc.struct.ObjectMap;
import arc.Core;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.blocks.ConstructBlock;
import mindustry.world.blocks.storage.CoreBlock.CoreBuild;
import mindustry.world.modules.ItemModule;
import mindustry.ui.fragments.PlacementFragment;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatValues;
import mindustry.game.Objectives.Objective;

import java.lang.reflect.Field;
import java.util.Arrays;

/** Mod-side alternate item costs for block construction. One complete option is selected per build. */
public class AlternativeBuildCosts {
    private static final ObjectMap<Block, ItemStack[]> costs = new ObjectMap<>();
    private static final ObjectMap<Block, ItemStack[]> fixedCosts = new ObjectMap<>();
    private static final ObjectMap<Block, ItemStack> activeCost = new ObjectMap<>();
    private static boolean installed;
    private static Field placementDisplayState;
    private static boolean placementFieldChecked;

    private AlternativeBuildCosts(){
    }

    /** Registers mutually exclusive material choices for a building. The first entry is preferred. */
    public static void register(Block block, ItemStack... alternatives){
        if(block == null || alternatives == null || alternatives.length < 2) throw new IllegalArgumentException("At least two alternate build costs are required.");
        ItemStack[] fixed = block.requirements.clone();
        for(ItemStack alternative : alternatives){
            if(alternative == null || alternative.item == null || alternative.amount < 0) throw new IllegalArgumentException("Alternate build costs must contain valid item stacks.");
            for(ItemStack required : fixed){
                if(required.item == alternative.item) throw new IllegalArgumentException("Alternate material cannot also be a mandatory material for " + block.name + ".");
            }
        }
        ItemStack[] options = alternatives.clone();
        ItemStack active = options[0].copy();
        block.requirements = Arrays.copyOf(fixed, fixed.length + 1);
        block.requirements[fixed.length] = active;
        costs.put(block, options);
        fixedCosts.put(block, fixed);
        activeCost.put(block, active);
    }

    public static ItemStack[] get(Block block){
        return costs.get(block);
    }

    /** Requirements before the single native slot used to display/pay an alternate cost. */
    public static ItemStack[] fixed(Block block){
        return fixedCosts.get(block);
    }

    /** Returns a defensive copy of a registered option for restoring construction history from saves. */
    public static ItemStack stackFor(Block block, Item item){
        if(item == null) return null;
        ItemStack[] alternatives = get(block);
        if(alternatives != null){
            for(ItemStack alternative : alternatives){
                if(alternative.item == item) return alternative.copy();
            }
        }
        return null;
    }

    /** Generates normal research costs without making the first alternate item mandatory. */
    public static ItemStack[] researchRequirements(Block block){
        ItemStack[] requirements = block.researchRequirements();
        ItemStack[] alternatives = get(block);
        if(alternatives == null) return requirements;

        return Arrays.stream(requirements)
            .filter(stack -> !isAlternativeItem(block, stack.item))
            .toArray(ItemStack[]::new);
    }

    /** Whether this item is supplied only as one of a block's mutually exclusive build-cost choices. */
    public static boolean isAlternativeItem(Block block, Item item){
        ItemStack[] alternatives = get(block);
        if(alternatives != null){
            for(ItemStack alternative : alternatives) if(alternative.item == item) return true;
        }
        return false;
    }

    /** Objective completed by researching any one of the registered alternate materials. */
    public static Objective researchObjective(Block block){
        ItemStack[] alternatives = get(block);
        if(alternatives == null || alternatives.length == 0) throw new IllegalArgumentException("Block has no alternate build costs: " + block.name);
        ItemStack[] options = alternatives.clone();

        return new Objective(){
            @Override
            public boolean complete(){
                for(ItemStack option : options) if(option.item.unlockedHost()) return true;
                return false;
            }

            @Override
            public String display(){
                StringBuilder text = new StringBuilder(Core.bundle.get("requirement.alternative-build-cost"));
                for(int i = 0; i < options.length; i++){
                    if(i > 0) text.append(" ").append(Core.bundle.get("stat.alternativeCost")).append(" ");
                    Item item = options[i].item;
                    text.append(item.emoji()).append(" ").append(item.localizedName);
                }
                return text.toString();
            }
        };
    }

    /** Replaces the normal build-cost stat with one row that makes the alternative group explicit. */
    public static void applyStats(Block block){
        ItemStack[] alternatives = get(block);
        if(alternatives == null || alternatives.length < 2) return;

        ItemStack[] fixed = fixed(block);
        block.stats.replace(Stat.buildCost, table -> {
            boolean hasFixed = fixed != null && fixed.length > 0;
            if(hasFixed){
                StatValues.items(false, fixed).display(table);
                table.add(" [lightgray]+[] (");
            }

            for(int i = 0; i < alternatives.length; i++){
                if(i > 0) table.add(" " + Core.bundle.get("stat.alternativeCost") + " ");
                ItemStack alternative = alternatives[i];
                table.add(StatValues.displayItem(alternative.item, alternative.amount, false)).padRight(5);
            }

            if(hasFixed) table.add(")");
        });
    }

    /** Updates the native requirement slot so Mindustry's build menu shows the selected option. */
    public static void updatePreviews(){
        if(Vars.headless) return;
        ItemModule inventory = Vars.state != null && Vars.state.isGame() && Vars.player != null && Vars.player.core() != null
            ? Vars.player.core().items : null;
        costs.each((block, options) -> {
            ItemStack choice = inventory == null ? options[0] : selectBest(options, inventory);
            setActive(block, choice);
        });
    }

    private static void setActive(Block block, ItemStack choice){
        ItemStack slot = activeCost.get(block);
        if(slot != null && choice != null && (slot.item != choice.item || slot.amount != choice.amount)){
            slot.set(choice.item, choice.amount);
            invalidatePlacementDisplay();
        }
    }

    private static void invalidatePlacementDisplay(){
        if(Vars.headless || Vars.ui == null || Vars.ui.hudfrag == null) return;
        try{
            if(!placementFieldChecked){
                placementDisplayState = PlacementFragment.class.getDeclaredField("lastDisplayState");
                placementDisplayState.setAccessible(true);
                placementFieldChecked = true;
            }
            placementDisplayState.set(Vars.ui.hudfrag.blockfrag, null);
        }catch(ReflectiveOperationException | RuntimeException ignored){
            // The build card still refreshes normally when the hovered or selected block changes.
        }
    }

    /** Returns the currently preferred option for UI previews and construction. */
    public static ItemStack preview(Block block, ItemModule inventory){
        ItemStack[] choices = get(block);
        if(choices == null || choices.length == 0) return null;
        return inventory == null ? choices[0] : selectBest(choices, inventory);
    }

    private static ItemStack selectBest(ItemStack[] choices, ItemModule inventory){
        ItemStack best = choices[0];
        float bestFraction = -1f;
        for(ItemStack choice : choices){
            int required = Math.round(choice.amount * Vars.state.rules.buildCostMultiplier);
            if(inventory.has(choice.item, required)) return choice;
            float fraction = required <= 0 ? 1f : inventory.get(choice.item) / (float)required;
            if(fraction > bestFraction){
                best = choice;
                bestFraction = fraction;
            }
        }
        return best;
    }

    /** Replaces vanilla construction stubs with a build type that applies registered alternate costs. */
    public static void install(){
        if(installed) return;
        installed = true;
        for(int size = 1; size <= Vars.maxBlockSize; size++){
            ConstructBlock construct = ConstructBlock.get(size);
            construct.buildType = () -> new AlternateConstructBuild(construct);
        }
    }

    /** Implemented by finished buildings that need to remember the material used for deconstruction refunds. */
    public interface CostChoiceReceiver {
        void setBuildCostStack(ItemStack stack);
        ItemStack buildCostStack();
    }

    public static class AlternateConstructBuild extends ConstructBlock.ConstructBuild {
        private ItemStack selectedCost;
        private int alternateItemsPaid;
        private int alternateItemsRefunded;
        private int alternateRefundTotal = -1;
        private int initializedCostAmount = -1;
        private boolean deconstructionPrepared;
        private Block constructionPrevious;
        private Block deconstructionPrevious;

        public AlternateConstructBuild(ConstructBlock parent){
            parent.super();
        }

        @Override
        public void setConstruct(Block previous, Block block){
            constructionPrevious = previous;
            if(block == null){
                super.setConstruct(previous, block);
                return;
            }
            ItemStack[] choices = get(block);
            selectedCost = choices == null || choices.length == 0 ? null : choices[0].copy();
            ItemStack choice = selectedCost;
            if(choice != null) setActive(block, choice);
            super.setConstruct(previous, block);
            initializedCostAmount = choice == null ? -1 : choice.amount;
            deconstructionPrepared = false;
            alternateItemsPaid = 0;
            alternateItemsRefunded = 0;
            alternateRefundTotal = -1;
        }

        @Override
        public void setDeconstruct(Block previous){
            deconstructionPrevious = previous;
            super.setDeconstruct(previous);
            selectedCost = null;
            alternateItemsPaid = 0;
            alternateItemsRefunded = 0;
            alternateRefundTotal = -1;
            deconstructionPrepared = false;
        }

        @Override
        public void construct(Unit builder, Building core, float amount, Object config){
            ItemStack[] choices = get(current);
            if(choices == null || choices.length == 0){
                super.construct(builder, core, amount, config);
                return;
            }

            if(core == null || team.rules().infiniteResources || Vars.state.rules.infiniteResources){
                if(selectedCost == null) selectedCost = choices[0].copy();
                ItemStack choice = choiceFor(current, selectedCost.item);
                prepareConstructionCost(choice);
                super.construct(builder, core, amount, config);
                storeChoiceIfCompleted();
                return;
            }

            ItemStack choice = select(current, choices, core.items);
            if(choice == null) return;
            selectedCost = choice.copy();
            prepareConstructionCost(choice);
            super.construct(builder, core, amount, config);
            storeChoiceIfCompleted();
        }

        @Override
        public void deconstruct(Unit builder, CoreBuild core, float amount){
            ItemStack[] choices = get(current);
            ItemStack choice = selectedCost;
            if(choice == null && prevBuild != null && prevBuild.size > 0){
                Building previousBuild = prevBuild.first();
                if(previousBuild instanceof CostChoiceReceiver receiver){
                    ItemStack stored = receiver.buildCostStack();
                    if(stored != null) choice = stored.copy();
                }else if(previousBuild instanceof AlternateConstructBuild previousConstruction){
                    if(previousConstruction.selectedCost != null) choice = previousConstruction.selectedCost.copy();
                }
                selectedCost = choice == null ? null : choice.copy();
            }
            if(choices == null || choices.length == 0){
                super.deconstruct(builder, core, amount);
                return;
            }

            ItemStack selected = choice == null ? choices[0] : choiceFor(current, choice.item);
            if(choice != null && selected != null) selected.set(choice.item, choice.amount);
            if(selected == null){
                super.deconstruct(builder, core, amount);
                return;
            }
            setActive(current, selected);
            if(!deconstructionPrepared){
                super.setDeconstruct(deconstructionPrevious == null ? current : deconstructionPrevious);
                deconstructionPrepared = true;
            }
            super.deconstruct(builder, core, amount);
        }

        @Override
        public byte version(){
            return 4;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.s((short)(selectedCost == null ? -1 : selectedCost.item.id));
            write.i(alternateItemsPaid);
            write.i(alternateItemsRefunded);
            write.i(alternateRefundTotal);
            write.i(selectedCost == null ? -1 : selectedCost.amount);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            if(revision >= 2){
                short id = read.s();
                Item item = id < 0 ? null : Vars.content.item(id);
                selectedCost = stackFor(current, item);
                alternateItemsPaid = read.i();
                alternateItemsRefunded = read.i();
            }
            if(revision >= 3) alternateRefundTotal = read.i();
            if(revision >= 4){
                int amount = read.i();
                if(selectedCost != null && amount >= 0) selectedCost.amount = amount;
            }
        }

        private ItemStack select(Block block, ItemStack[] choices, ItemModule inventory){
            if(progress > 0f && selectedCost != null){
                for(ItemStack choice : choices) if(choice.item == selectedCost.item) return choice;
            }
            return selectBest(choices, inventory);
        }

        private ItemStack choiceFor(Block block, Item item){
            ItemStack choice = stackFor(block, item);
            ItemStack[] choices = get(block);
            return choice != null ? choice : choices == null || choices.length == 0 ? null : choices[0].copy();
        }

        /** Keeps vanilla's private construction accounting arrays in sync with the chosen stack. */
        private void prepareConstructionCost(ItemStack choice){
            if(choice == null || current == null) return;
            setActive(current, choice);
            if(progress <= 0f && initializedCostAmount != choice.amount){
                super.setConstruct(constructionPrevious, current);
                initializedCostAmount = choice.amount;
            }
        }

        private void storeChoiceIfCompleted(){
            if(progress >= 1f && tile.build != this && tile.build instanceof CostChoiceReceiver receiver){
                if(selectedCost != null) receiver.setBuildCostStack(selectedCost.copy());
            }
        }
    }
}
