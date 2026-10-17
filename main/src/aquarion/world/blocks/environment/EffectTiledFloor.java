package aquarion.world.blocks.environment;

import aquarion.world.graphics.AquaFx;
import arc.Core;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.Damage;
import mindustry.game.Team;
import mindustry.gen.TiledState;
import mindustry.world.Tile;
import mindustry.world.Block;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.environment.TiledFloor;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;
import static mindustry.Vars.net;

public class EffectTiledFloor extends TiledFloor{
    public Block parent = Blocks.air;
    protected boolean[][] ventRegions;
    public Effect effect = AquaFx.geysers;
    public Color effectColor = Color.white;
    /** Ticks between effect particles while an eruption is active. */
    public float effectSpacing = 1f;
    public float minEffectSpacing = 12*60f; //minimum time between eruptions  in seconds (hence the 60f)
    public float maxEffectSpacing = 24*60f; //maximum time between eruptions  in seconds (hence the 60f)
    public float minEffectDuration = 4*60f; //minimum time the vent stays active for  in seconds (hence the 60f)
    public float maxEffectDuration = 7*60f; //maximum time the vent stays active for in seconds (hence the 60f)
    public float splashDamageRadius = 17f; //spash damage radius in world units
    public float splashDamage = 100; //damage per second per tile of vent width during eruption

    public EffectTiledFloor(String name, int variants, int maxSize){
        super(name, variants, maxSize);
    }

    @Override
    public void load(){
        super.load();
        ventRegions = new boolean[maxSize + 1][Math.max(variants, 1)];
        for(int size = 1; size <= maxSize; size++){
            for(int v = 0; v < Math.max(variants, 1); v++){
                ventRegions[size][v] = Core.atlas.has(name + "-" + size + "-" + v)
                    || Core.atlas.has(name + "-" + size);
            }
        }
    }

    protected boolean hasVent(Tile tile){
        long packed = world.tiles.getTmpFloorState(tile.array());
        int size = Mathf.clamp(TiledState.size(packed), 1, maxSize);
        int v = variant(tile.x - TiledState.x(packed), tile.y - TiledState.y(packed), variants);
        return ventRegions[size][v];
    }

    @Override
    public void drawMain(Tile tile){
        //AirBlock is a Floor too, but has no drawable variant regions.
        if(parent != Blocks.air && parent instanceof Floor floor && parent != this){
            floor.drawMain(tile);
            floorChanged(tile);
            //Incomplete patches show the parent rather than a missing vent sprite.
            if(!hasVent(tile)) return;
        }
        super.drawMain(tile);
    }

    @Override
    public boolean updateRender(Tile tile){
        return effect != Fx.none;
    }

    @Override
    public void renderUpdate(UpdateRenderState state){
        Tile tile = state.tile;
        floorChanged(tile);
        long packed = world.tiles.getTmpFloorState(tile.array());
        int size = TiledState.size(packed);
        if(size <= 0 || TiledState.x(packed) != 0 || TiledState.y(packed) != 0
            || (parent != Blocks.air && parent instanceof Floor && parent != this && !hasVent(tile))){
            state.data = 0f;
            return;
        }

        Tile center = tile.nearby((size - 1) / 2, (size - 1) / 2);
        if(center == null) return;

        float spacing = Mathf.randomSeed(tile.pos() * 13L, minEffectSpacing, maxEffectSpacing);
        float duration = Mathf.randomSeed(tile.pos() * 17L, minEffectDuration, maxEffectDuration);
        if(state.data >= 0f){
            if((state.data += Time.delta) >= spacing){
                state.data = -duration;
            }
            return;
        }

        float elapsed = duration + state.data;
        float delta = Math.min(Time.delta, -state.data);
        boolean blocked = center.block() != Blocks.air;
        float interval = Math.max(effectSpacing, 1f);
        if(!blocked && (elapsed == 0f || (int)(elapsed / interval) != (int)((elapsed + delta) / interval))){
            float offset = (size - 1) * tilesize / 2f;
            effect.at(tile.worldx() + offset, tile.worldy() + offset, size, effectColor);
        }
        if(!net.client()){
            Damage.tileDamage(Team.derelict, center.x, center.y, splashDamageRadius / tilesize,
                splashDamage * size * delta / 60f * (blocked ? 2f : 1f));
        }
        state.data = Math.min(state.data + Time.delta, 0f);
    }
}
