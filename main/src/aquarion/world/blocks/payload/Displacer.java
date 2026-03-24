package aquarion.world.blocks.payload;

import aquarion.world.graphics.Renderer;
import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Eachable;
import mindustry.content.Fx;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Teams;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.ConstructBlock;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.PayloadBlock;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class Displacer extends PayloadBlock{
    public TextureRegion gantry;

    public Displacer(String name){
        super(name);
        update = true;
        solid = true;
        outputsPayload = true;
        size = 3;
        rotate = true;
    }

    @Override
    public void load(){
        super.load();
        gantry = Core.atlas.find(name + "-gantry");
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        Draw.rect(region, plan.drawx(), plan.drawy(), plan.rotation*90);
        Draw.rect(gantry, plan.drawx(), plan.drawy(), plan.rotation*90);
        if(plan.rotation == 1  || plan.rotation  == 2){
            Draw.scl(1f, -1f);
        }else{
            Draw.scl(-1f, 1f);
        }
        Draw.rect(topRegion, plan.drawx(), plan.drawy(), plan.rotation*90);
        Draw.reset();
    }
    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{region, inRegion, outRegion, gantry, topRegion};
    }
    public class DisplacerBuild extends PayloadBlockBuild{
        float extract;
        float extractTime = 40f;
        float extractProg;

        boolean retracting;
        boolean exporting;

        @Override
        public void updateTile(){
            updatePayload();
            Building f = front();
            Building b = back();
            if(payload != null) retracting = true;
            if(payload != null && exporting){

                if(f != null && f.acceptPayload(this, payload)){
                    moveOutPayload();
                    return;
                }
            }
            if(retracting){
                extractProg = Mathf.lerpDelta(extractProg, 0f, 0.05f);
                if(extractProg <= 0.01f){
                    extractProg = 0f;
                    retracting = false;
                    exporting = true;
                }
                return;
            }
            if(payload != null){
                return;
            }

            if(b == null || b.team != team || b.block.size >= size || b instanceof ConstructBlock.ConstructBuild){
                extract = 0f;
                extractProg = Mathf.lerpDelta(extractProg, 0f, 0.15f);
                return;
            }

            extract += edelta() / 10f;
            extractProg = Mathf.lerpDelta(extractProg, 1f, 0.1f);

            if(extract >= extractTime && extractProg >= 0.98f){
                extract = 0f;

                Teams.TeamData data = team.data();
                payload = new BuildPayload(b.block, team);

                int bx = (int)b.x;
                int by = (int)b.y;
                short br = (short)b.rotation;
                Block bblock = b.block;
                Object bconfig = b.config();

                Fx.placeBlock.at(b.x, b.y, 1f);
                b.kill();

                data.plans.add(new Teams.BlockPlan(bx, by, br, bblock, bconfig));

                retracting = true;
                exporting = false;
            }

        }

        @Override
        public void draw() {
            Draw.rect(region, x, y, rotdeg());

            Draw.rect(outRegion, x, y, rotdeg());
            Draw.rect(inRegion, x, y, rotdeg());


            float len = tilesize * 1.2f;
            float ox = Angles.trnsx(rotdeg(), extractProg * -len);
            float oy = Angles.trnsy(rotdeg(), extractProg * -len);

            if (payload != null) {
                if (extractProg <= 1) payload.set(x + ox, y + oy, payRotation);
                payload.draw();
            }
            Draw.z(Layer.blockOver + 0.1f);
            Draw.rect(gantry, x + ox, y + oy, rotdeg());

            if (rotation == 1 || rotation == 2) {
                Draw.scl(1f, -1f);
            } else {
                Draw.scl(-1f, 1f);
            }
            Draw.rect(topRegion, x, y, rotdeg());
            Draw.reset();
        }
    }
}
