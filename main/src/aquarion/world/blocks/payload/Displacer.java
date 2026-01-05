package aquarion.world.blocks.payload;

import aquarion.world.graphics.Renderer;
import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import mindustry.content.Fx;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Teams;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.blocks.ConstructBlock;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.PayloadBlock;

import static mindustry.Vars.tilesize;

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

    public class DisplacerBuild extends PayloadBlockBuild{
        float extract;
        float extractTime = 40f;
        float extractProg;

        boolean retracting;
        boolean exporting;

        @Override
        public void updateTile(){
            updatePayload();

            // EXPORT: only allowed after full retract
            if(payload != null && exporting){
                Building f = front();
                if(f != null && f.acceptPayload(this, payload)){
                    moveOutPayload();
                    return;
                }
            }

            // RETRACT after pickup
            if(retracting){
                extractProg = Mathf.lerpDelta(extractProg, 0f, 0.15f);
                if(extractProg <= 0.01f){
                    extractProg = 0f;
                    retracting = false;
                    exporting = true;
                }
                return;
            }

            // Hold payload until export phase
            if(payload != null){
                return;
            }

            Building b = back();
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
        public void draw(){
            Draw.rect(region, x, y, rotdeg());

            for(int i = 0; i < 4; i++){
                if(blends(i) && i != rotation){
                    Draw.rect(inRegion, x, y, (i * 90f) - 180f);
                }
            }

            if(payload != null && exporting){
                Building f = front();
                if(f != null && f.acceptPayload(this, payload)){
                    Draw.rect(outRegion, x, y, rotdeg());
                }
            }

            Draw.z(Renderer.Layer.blockOver + 0.01f);

            float len = tilesize * 1.2f;
            float ox = Angles.trnsx(rotdeg(), extractProg * len);
            float oy = Angles.trnsy(rotdeg(), extractProg * len);

            Draw.rect(gantry, x + ox, y + oy, rotdeg());

            if(payload != null){
                payload.set(x + ox, y + oy, payRotation);
                payload.draw();
            }

            Draw.rect(topRegion, x, y, rotdeg());
            Draw.reset();
        }

        @Override
        public boolean acceptPayload(Building source, Payload payload){
            return false;
        }
    }
}
