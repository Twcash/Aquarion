package aquarion.world.campaign;

import arc.Events;
import arc.func.Boolp;
import arc.struct.ObjectSet;
import arc.struct.Seq;
import mindustry.game.EventType.BlockBuildEndEvent;
import mindustry.game.EventType.ResetEvent;
import mindustry.game.EventType.Trigger;
import mindustry.game.EventType.WorldLoadEvent;
import mindustry.world.Block;
import mindustry.world.blocks.ConstructBlock.ConstructBuild;

import static mindustry.Vars.*;

public class GlobalFlagTriggers{
    public static  Seq<FlagTrigger> all = new Seq<>();
    public static  ObjectSet<String> events = new ObjectSet<>();
    public static  ObjectSet<Block> placedBlocks = new ObjectSet<>();
    public static ObjectSet<Block> deconstructedBlocks = new ObjectSet<>();
    private static boolean registered;

    private GlobalFlagTriggers(){}

    public static void register(){
        if(registered) return;
        registered = true;
        Events.on(ResetEvent.class, event -> reset());
        Events.on(WorldLoadEvent.class, event -> reset());
        Events.on(BlockBuildEndEvent.class, event -> {
            if(!state.isCampaign() || event.team != state.rules.defaultTeam) return;
            if(event.breaking){
                events.add("break");
                // The completion event still points at the construction placeholder.
                Block removed = event.tile.build instanceof ConstructBuild build ? build.current : event.tile.block();
                if(removed != null) deconstructedBlocks.add(removed);
            }
            else placedBlocks.add(event.tile.block());
        });
        Events.run(Trigger.update, GlobalFlagTriggers::update);
    }

    public static void reset(){
        events.clear();
        placedBlocks.clear();
        deconstructedBlocks.clear();
    }

    /** Record an arbitrary gameplay event for predicates to inspect. */
    public static void record(String event){
        if(state != null && state.isCampaign() && event != null) events.add(event);
    }

    public static void add(FlagTrigger trigger){
        register();
        if(trigger == null || trigger.name() == null || trigger.name().isEmpty())
            throw new IllegalArgumentException("Global flag trigger requires a name");
        if(all.contains(other -> other.name().equals(trigger.name())))
            throw new IllegalArgumentException("Duplicate global flag trigger: " + trigger.name());
        all.add(trigger);
    }

    /** Register a predicate inline; enum constants may instead implement FlagTrigger. */
    public static Condition when(String flag, Boolp condition){
        Condition trigger = new Condition(flag, condition);
        add(trigger);
        return trigger;
    }

    public static void update(){
        if(state == null || !state.isGame() || !state.isCampaign() || state.isPaused() || net.client()) return;
        for(FlagTrigger trigger : all){
            if(!trigger.finished() && trigger.valid() && trigger.complete()) trigger.finish();
        }
    }

    /** Mirrors the hint completion pattern while storing completion in campaign saves. */
    public interface FlagTrigger{
        String name();
        boolean complete();
        default boolean valid(){ return true; }
        default boolean finished(){ return GlobalFlags.has(name()); }
        default void finish(){ GlobalFlags.set(name()); }
    }

    public static class Condition implements FlagTrigger{
        public final String name;
        public final Boolp condition;
        public Boolp valid = () -> true;
        public String[] dependencies = {};

        public Condition(String name, Boolp condition){
            this.name = name;
            this.condition = condition;
        }

        public Condition requires(String... flags){
            dependencies = flags;
            return this;
        }

        @Override
        public String name(){ return name; }

        @Override
        public boolean complete(){ return condition.get(); }

        @Override
        public boolean valid(){
            if(!valid.get()) return false;
            for(String flag : dependencies) if(!GlobalFlags.has(flag)) return false;
            return true;
        }
    }
}
