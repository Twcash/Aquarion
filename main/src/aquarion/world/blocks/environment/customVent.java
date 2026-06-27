package aquarion.world.blocks.environment;

import arc.math.geom.Point2;
import arc.util.Time;
import mindustry.content.Blocks;
import mindustry.world.blocks.environment.SteamVent;
import arc.math.Mathf;

import static mindustry.Vars.tilesize;

public class customVent extends SteamVent {
    public customVent(String name) {
        super(name);
    }

    public float minEffectSpacing = 12*60f; //minimum time between eruptions  in seconds (hence the 60f)
    public float maxEffectSpacing = 24*60f; //maximum time between eruptions  in seconds (hence the 60f)
    public float minEffectDuration = 4*60f; //minimum time the vent stays active for  in seconds (hence the 60f)
    public float maxEffectDuration = 7*60f; //maximum time the vent stays active for in seconds (hence the 60f)


    @Override
    public void renderUpdate(UpdateRenderState state){
        if(state.tile.nearby(-1, -1) != null && state.tile.nearby(-1, -1).block() == Blocks.air && ((state.data += Time.delta) >= Mathf.random(minEffectSpacing, maxEffectSpacing)) || state.data < 0) { /* vent goes off after a random time between min and max spacing */
            if(state.data < 0) { /* if state.data is smaller than 0 do the effect stuff, else set it to random negative number between min and max duration */
                    effect.at(state.tile.x * tilesize - tilesize, state.tile.y * tilesize - tilesize, effectColor);
                    state.data += Time.delta;
            }
            else {
                state.data = -Mathf.random(minEffectDuration, maxEffectDuration);
            }
        }
    }
}
