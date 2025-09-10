package aquarion.world.content;

import aquarion.blocks.AquaCrafters;
import aquarion.blocks.AquaLiquid;
import aquarion.blocks.AquaPower;
import aquarion.world.blocks.power.PowerPylon;
import arc.Core;
import arc.Events;
import arc.func.Boolp;
import arc.struct.ObjectSet;
import arc.util.Nullable;
import arc.util.Structs;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.ui.fragments.HintsFragment;
import mindustry.world.Block;

import static mindustry.Vars.player;
import static mindustry.Vars.ui;

public class AquaHints {
    static ObjectSet<String> events = new ObjectSet<>();
    static ObjectSet<Block> placedBlocks = new ObjectSet<>();
     public void load() {
            ui.hints.hints.addAll(AquaHint.values());
            for (var h : AquaHint.values()) {
                // remove already finished hints
                if (h.finished()) ui.hints.hints.remove(h);
            }

            Events.on(EventType.BlockBuildEndEvent.class, e -> {
                if (!e.breaking && e.unit == player.unit()) {
                    placedBlocks.add(e.tile.block());
                }
            });

        }

        public enum AquaHint implements HintsFragment.Hint {
            power(
                    () -> placedBlocks.contains(AquaPower.pylon) || placedBlocks.contains(AquaPower.outlet),
                    () -> false
            ),
            boosting(
                    () -> placedBlocks.contains(AquaCrafters.harvester),
                    () -> false
            ),
            solar(
                    () -> placedBlocks.contains(AquaPower.solarGenerator),
                    () -> false
            ),
            siphon(
                    () -> placedBlocks.contains(AquaLiquid.siphon),
                    () -> false
            );
            @Nullable
            String text;
            int visibility = visibleAll;
            HintsFragment.Hint[] dependencies = {};
            boolean finished, cached;
            Boolp complete, shown = () -> true;

            AquaHint(Boolp complete) {
                this.complete = complete;
            }

            AquaHint(int visibility, Boolp complete) {
                this(complete);
                this.visibility = visibility;
            }

            AquaHint(Boolp shown, Boolp complete) {
                this(complete);
                this.shown = shown;
            }

            AquaHint(int visibility, Boolp shown, Boolp complete) {
                this(complete);
                this.shown = shown;
                this.visibility = visibility;
            }

            @Override
            public boolean finished() {
                if (!cached) {
                    cached = true;
                    finished = Core.settings.getBool("aquarion-" + name() + "-hint-done", false);
                }
                return finished;
            }

            @Override
            public void finish() {
                Core.settings.put("aquarion-" + name() + "-hint-done", finished = true);
            }

            @Override
            public String text() {
                if (text == null) {
                    text = Vars.mobile && Core.bundle.has("aquarion.aquarion-" + name() + ".mobile") ? Core.bundle.get("hint.aquarion-" + name() + ".mobile") : Core.bundle.get("hint.aquarion-" + name());
                    if (!Vars.mobile) text = text.replace("tap", "click").replace("Tap", "Click");
                }
                return text;
            }

            @Override
            public boolean complete() {
                return complete.get();
            }

            @Override
            public boolean show() {
                return shown.get() && (dependencies.length == 0 || !Structs.contains(dependencies, d -> !d.finished()));
            }

            @Override
            public int order() {
                return ordinal();
            }

            @Override
            public boolean valid() {
                return (Vars.mobile && (visibility & visibleMobile) != 0) || (!Vars.mobile && (visibility & visibleDesktop) != 0);
            }
            }
}
