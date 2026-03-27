package aquarion.world.content;

import aquarion.content.blocks.CrafterBlocks;
import aquarion.content.blocks.LiquidBlocks;
import aquarion.content.blocks.PowerBlocks;
import aquarion.content.blocks.TurretBlocks;
import aquarion.content.AquaSectorPresets;
import aquarion.ui.WorldToasts;
import aquarion.world.blocks.power.PowerOutlet;
import aquarion.world.blocks.power.PowerPylon;
import aquarion.world.type.GroundDrill;
import arc.Core;
import arc.Events;
import arc.func.Boolp;
import arc.struct.ObjectSet;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Structs;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.ui.fragments.HintsFragment;
import mindustry.world.Block;
import mindustry.world.blocks.power.SolarGenerator;

import static mindustry.Vars.*;

public enum AquaHints implements HintsFragment.Hint {
    power(
            () -> false,
            () -> Vars.control.input.block instanceof PowerOutlet || Vars.control.input.block instanceof PowerPylon
    ),
    boosting(
            () -> false,
            () -> Vars.control.input.block instanceof GroundDrill
    ),
    solar(
            () -> false,
            () -> Vars.control.input.block instanceof SolarGenerator
    ),
    outposts(
            () -> false,
            () -> state.rules.sector == AquaSectorPresets.twinPass.sector
    ),
    ammo(
            () -> false,
            () -> control.input.block == TurretBlocks.pelt
    ),
    siphon(
            () -> false,
            () -> control.input.block == (LiquidBlocks.siphon)
    );
    final Boolp complete;
    Boolp shown = () -> true;
    AquaHints[] requirements;

    int visibility = visibleAll;
    boolean cached, finished;

    AquaHints(Boolp complete) {
        this.complete = complete;
    }

    AquaHints(Boolp complete, Boolp shown) {
        this(complete);
        this.shown = shown;
    }

    AquaHints(Boolp complete, Boolp shown, AquaHints... requirements) {
        this(complete, shown);
        this.requirements = requirements;
    }

    @Override
    public boolean complete() {
        return complete.get();
    }

    @Override
    public void finish() {
        Core.settings.put("aquarion-" + name() + "-hint-done", finished = true);
    }

    @Override
    public boolean finished() {
        if (!cached) {
            cached = true;
            finished = Core.settings.getBool("aquarion-" + name() + "-hint-done", false);
        }
        return finished;
    }

    public static void addHints() {
        Vars.ui.hints.hints.add(Seq.with(AquaHints.values()).removeAll(
                hint -> Core.settings.getBool("aquarion-" + hint.name() + "-hint-done", false)
        ));
    }

    @Override
    public int order() {
        return ordinal();
    }

    public static void reset() {
        for (AquaHints hint : values()) {
            Core.settings.put("aquarion-" + hint.name() + "-hint-done", hint.finished = false);
        }
        addHints();
    }

    @Override
    public boolean show() {
        return shown.get() && (requirements == null || (requirements.length == 0 || !Structs.contains(requirements, d -> !d.finished())));
    }

    @Override
    public String text() {
        return Core.bundle.get("hint." + "aquarion-" + name(), "Missing bundle for hint: hint." + "aquarion-" + name());
    }

    @Override
    public boolean valid() {
        return (Vars.mobile && (visibility & visibleMobile) != 0) || (!Vars.mobile && (visibility & visibleDesktop) != 0);
    }
}