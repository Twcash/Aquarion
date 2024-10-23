package aquarion.world.blocks.distribution;

import arc.Core;
import arc.func.Boolf;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.struct.Seq;
import arc.util.Nullable;
import ent.anno.Annotations;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.gen.Teamc;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.Edges;
import mindustry.world.blocks.distribution.Conveyor;
import mindustry.world.blocks.distribution.Duct;
import mindustry.graphics.*;
import mindustry.world.blocks.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.world.blocks.distribution.Junction;

import static mindustry.Vars.*;

public class SealedConveyor extends Duct implements Autotiler{

    public float stopSpeed;
    public float visualSpeed = 1f;
    public int capacity = 3;
    public TextureRegion capRegion;

    public SealedConveyor(String name) {
        super(name);

    }

    public TextureRegion[][] regions = new TextureRegion[7][4];
    public TextureRegion[][] glowRegions = new TextureRegion[7][4];
    public @Nullable Block junctionReplacement;
    @Override
    public void load(){
        super.load();
        capRegion = Core.atlas.find(name + "-cap");
        itemCapacity = 2;
        for(int i = 0; i < 7; i++){
            for(int j = 0; j < 4; j++){
                regions[i][j] = Core.atlas.find(name + "-" + i + "-" + j);
                glowRegions[i][j] = Core.atlas.find(name + "-" + i + "-" + j);
            }
        }
    }
    @Annotations.Replace
    @Override
    public Block getReplacement(BuildPlan req, Seq<BuildPlan> plans){
        if(junctionReplacement == null) return this;

        Boolf<Point2> cont = p -> plans.contains(o -> o.x == req.x + p.x && o.y == req.y + p.y && (req.block instanceof SealedConveyor || req.block instanceof Junction));
        return cont.get(Geometry.d4(req.rotation)) &&
                cont.get(Geometry.d4(req.rotation - 2)) &&
                req.tile() != null &&
                req.tile().block() instanceof SealedConveyor &&
                Mathf.mod(req.build().rotation - req.rotation, 2) == 1 ? junctionReplacement : this;
    }
    @Annotations.Replace
    @Override
    public void handlePlacementLine(Seq<BuildPlan> plans){
    }
    public class SealedConveyorBuild extends Building {
        public boolean backCapped = false;
        public boolean capped = false;
        //next entity
        public @Nullable Building next;
        public @Nullable Conveyor.ConveyorBuild nextc;
        public float progress;
        public @Nullable Item current;
        public int recDir = 0;
        public int blendbits, xscl, yscl, blending;
        public int blendsclx = 1, blendscly = 1;

        public float clogHeat = 0f;
        protected void drawAt(float x, float y, int bits, float rotation, SliceMode slice){
            int frame = enabled && clogHeat <= 0.5f ? (int) (((Time.time * visualSpeed * .05f * timeScale * efficiency)) % 4) : 0;
            Draw.z(Layer.blockUnder + 0.1f);
            Draw.rect(regions[blendbits][frame], x, y, tilesize * blendsclx, tilesize * blendscly, rotation);
            Draw.z(Layer.blockUnder + 0.3f);
            Draw.rect(sliced(topRegions[bits], slice), x, y, rotation);
            Draw.z(Layer.blockUnder + 0.4f);
            Draw.scl(xscl, yscl);
            int r = this.rotation;
            Draw.z(Layer.blockUnder + 0.6f);
            if(capped && capRegion.found()) Draw.rect(capRegion, x, y, rotdeg());
            if(backCapped && capRegion.found()) Draw.rect(capRegion, x, y, rotdeg() + 180);
        }
        @Override
        public int removeStack(Item item, int amount){
            int removed = super.removeStack(item, amount);
            if(item == current) current = null;
            return removed;
        }
        @Override
        public boolean acceptItem(Building source, Item item){
            return current == null && items.total() == 0 &&
                    (armored ?
                            //armored acceptance
                            ((source.block.rotate && source.front() == this && source.block.hasItems && source.block.isDuct) ||
                                    Edges.getFacingEdge(source.tile(), tile).relativeTo(tile) == rotation) :
                            //standard acceptance - do not accept from front
                            !(source.block.rotate && next == source) && Edges.getFacingEdge(source.tile, tile) != null && Math.abs(Edges.getFacingEdge(source.tile, tile).relativeTo(tile.x, tile.y) - rotation) != 2
                    );
        }

        @Override
        public void handleStack(Item item, int amount, Teamc source){
            super.handleStack(item, amount, source);
            current = item;
        }

        @Override
        public void handleItem(Building source, Item item){
            current = item;
            progress = -1f;
            recDir = relativeToEdge(source.tile);
            items.add(item, 1);
            noSleep();
        }


        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();

            int[] bits = buildBlending(tile, rotation, null, true);
            blendbits = bits[0];
            xscl = bits[1];
            yscl = bits[2];
            blending = bits[4];
            next = front();
            blendsclx = bits[1];
            blendscly = bits[2];

            Building next = front(), prev = back();
            capped = next == null || next.team != team || !next.block.hasItems;
            backCapped = blendbits == 0 && (prev == null || prev.team != team || !prev.block.hasItems);
            nextc = next instanceof Conveyor.ConveyorBuild ? (Conveyor.ConveyorBuild) next : null;
        }

        @Override
        public boolean shouldAmbientSound() {
            return clogHeat <= 0.5f;
        }

        public void draw() {
            float rotation = rotdeg();
            int r = this.rotation;
            int frame = enabled && clogHeat <= 0.5f ? (int) (((Time.time * speed * .04f * timeScale * efficiency)) % 4) : 0;

            //draw extra uhh things facing this one for tiling purposes
            for(int i = 0; i < 4; i++){
                if((blending & (1 << i)) != 0){
                    int dir = r - i;
                    float rot = i == 0 ? rotation : (dir)*90;
                    drawAt(x + Geometry.d4x(dir) * tilesize*0.75f, y + Geometry.d4y(dir) * tilesize*0.75f, 0, rot, i != 0 ? SliceMode.bottom : SliceMode.top);
                }
            }

            //draw item
            if (current != null) {
                Draw.z(Layer.blockUnder + 0.2f);
                Tmp.v1.set(Geometry.d4x(recDir) * tilesize / 2f, Geometry.d4y(recDir) * tilesize / 2f)
                        .lerp(Geometry.d4x(r) * tilesize / 2f, Geometry.d4y(r) * tilesize / 2f,
                                Mathf.clamp((progress + 1f) / 2f));

                Draw.rect(current.fullIcon, x + Tmp.v1.x, y + Tmp.v1.y, itemSize, itemSize);
            }
            Draw.scl(xscl, yscl);
            drawAt(x, y, blendbits, rotation, SliceMode.none);
            Draw.reset();
        }
        @Override
        public void updateTile(){
            progress += edelta() / speed * 2f;
            if(progress != 0 && next != null){
                clogHeat = Mathf.approachDelta(clogHeat, 1f, 1f / stopSpeed);
            }

            if(current != null && next != null){
                if(progress >= (1f - 1f/speed) && moveForward(current)){
                    items.remove(current, 1);
                    current = null;
                    progress %= (1f - 1f/speed);
                    clogHeat = 0;
                }
            }else{
                progress = 0;
                clogHeat = Mathf.approachDelta(clogHeat, 1f, 1f / stopSpeed);
            }

            if(current == null && items.total() > 0){
                current = items.first();
            }
        }
    }
}