package aquarion.world.campaign;

import mindustry.game.MapObjectives.MapObjective;
import mindustry.gen.Groups;
import mindustry.world.Block;

import static mindustry.Vars.*;

/** Map-editor condition: wait until the player has built the requested blocks. */
public class BlockPlacedObjective extends MapObjective{
    public Block block;
    public int amount = 1;
    /** Optional exact map name. Empty accepts any map. */
    public String mapName = "";
    /** Optional campaign sector ID. Negative accepts any sector. */
    public int sector = -1;
    public String requiresGlobalFlag = "";
    public String[] setGlobalFlags = {};
    public boolean showText = false;

    @Override
    public boolean qualified(){
        return super.qualified()
            && (mapName.isEmpty() || state.map != null && mapName.equals(state.map.name()))
            && (sector < 0 || state.isCampaign() && state.getSector().id == sector)
            && (requiresGlobalFlag.isEmpty() || GlobalFlags.has(requiresGlobalFlag));
    }

    @Override
    public boolean update(){
        return block != null && Groups.build.count(build -> build.block == block
            && build.team == state.rules.defaultTeam && build.isValid()) >= Math.max(1, amount);
    }

    @Override
    public void done(){
        super.done();
        for(String flag : setGlobalFlags) GlobalFlags.set(flag);
    }

    @Override
    public String text(){
        return showText && block != null ? "Build " + amount + " " + block.localizedName : null;
    }
}
