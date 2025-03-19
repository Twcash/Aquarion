package aquarion.world.consumers;


import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.consumers.Consume;
import mindustry.world.consumers.ConsumeLiquidBase;

public abstract class ConsumeLiquidBaseNew extends ConsumeLiquidBase {
    /** amount used per frame */
    public float amount;

    public ConsumeLiquidBaseNew(float amount){
        this.amount = amount;
    }

    public ConsumeLiquidBaseNew(){}

    @Override
    public void apply(Block block){
        block.hasLiquids = true;
    }

    public abstract boolean consumes(Liquid liquid);
}