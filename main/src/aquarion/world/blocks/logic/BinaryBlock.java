package aquarion.world.blocks.logic;

import aquarion.world.blocks.logic.Toggler.TogglerBuild;
import aquarion.world.type.AquaBlock;
import arc.struct.ObjectSet;
import arc.struct.Seq;
import mindustry.gen.Building;

/** Shared signal rules. Blocks define their input and output directions. */
public class BinaryBlock extends AquaBlock {
    public BinaryBlock(String name) {
        super(name);
    }
    /** Source blocks share this path when driving ordinary buildings. Binary receivers read their input. */
    public static void transmit(Building source, Building target, boolean active){
        if(target == null || target.team != source.team || target instanceof BinaryBlockBuild
            || target instanceof TogglerBuild || target instanceof StorageReader.StorageReaderBuild) return;
        target.enabled = active;
    }

    public class BinaryBlockBuild extends Building {
        public boolean active;
        public float warmup;
        private final Seq<BinaryBlockBuild> inputs = new Seq<>();
        private final ObjectSet<BinaryBlockBuild> visitedInputs = new ObjectSet<>();

        public boolean outputsTo(Building target){
            return front() == target;
        }

        public boolean acceptsInput(Building source){
            return source == back();
        }

        /** Follow wiring to a live source so bidirectional splitters cannot keep each other latched on. */
        public boolean inputActive(){
            inputs.clear();
            visitedInputs.clear();
            inputs.add(this);
            visitedInputs.add(this);
            for(int i = 0; i < inputs.size; i++){
                BinaryBlockBuild receiver = inputs.get(i);
                for(Building source : receiver.proximity){
                    if(source.team != receiver.team || !receiver.acceptsInput(source)) continue;
                    if(source instanceof BinaryBlockBuild binary){
                        if(binary.outputsTo(receiver) && visitedInputs.add(binary)) inputs.add(binary);
                    }else if(receiver.signalFrom(source)){
                        return true;
                    }
                }
            }
            return false;
        }

        protected boolean signalFrom(Building source){
            if(source == null || source.team != team) return false;
            if(source instanceof BinaryBlockBuild binary) return binary.outputsTo(this) && binary.active;
            if(source instanceof TogglerBuild || source instanceof StorageReader.StorageReaderBuild){
                return source.front() == this && source.enabled;
            }
            return false;
        }

        @Override
        public void updateTile(){
            active = inputActive();
            warmup = arc.math.Mathf.approachDelta(warmup, active ? 1f : 0f, active ? 0.05f : 0.01f);
        }

        @Override
        public float warmup(){
            return warmup;
        }
    }
}
