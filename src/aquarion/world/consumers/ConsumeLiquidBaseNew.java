package aquarion.world.consumers;


import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.consumers.Consume;

public abstract class ConsumeLiquidBaseNew extends Consume{
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