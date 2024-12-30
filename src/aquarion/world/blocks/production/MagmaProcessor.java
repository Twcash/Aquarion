package aquarion.world.blocks.production;

import mindustry.world.blocks.production.GenericCrafter;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.logic.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.liquid.Conduit.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;

public class MagmaProcessor extends GenericCrafter {

    public MagmaProcessor(String name){
        super(name);
        update = true;
        solid = true;
        hasItems = true;
        ambientSound = Sounds.machine;
        sync = true;
        ambientSoundVolume = 0.03f;
        flags = EnumSet.of(BlockFlag.factory);
        drawArrow = false;
    }
    public class MagmaProcessorBuild extends GenericCrafterBuild {
        public boolean acceptLiquid(Building source, Liquid liquid){
            if(block instanceof MagmaHarvester){
                return false;
            }
                return block.hasLiquids && block.consumesLiquid(liquid);
        }
    }
}
