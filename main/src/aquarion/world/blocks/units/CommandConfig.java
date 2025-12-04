package aquarion.world.blocks.units;

import arc.math.geom.Vec2;
import mindustry.ai.UnitCommand;
//Class to save command Positions after a unitBlocks destruction
public class CommandConfig {
    public UnitCommand command;
    public Vec2 commandPos;

    public CommandConfig(UnitCommand command, Vec2 commandPos){
        this.command = command;
        this.commandPos = commandPos == null ? null : new Vec2(commandPos);
    }
}