package aquarion.world.drawers;

import aquarion.world.blocks.turrets.AquaItemTurret;
import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.part.DrawPart;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.draw.DrawTurret;

public class AquaDrawTurret extends DrawTurret {
    public ObjectMap<UnlockableContent, DrawPart[]> ammoParts = new ObjectMap<>();
    public void setAmmoParts(Object... objects){
        for(int i = 0; i < objects.length; i+= 2){
            ammoParts.put((UnlockableContent)objects[i], ((Seq<DrawPart>)objects[i + 1]).toArray(DrawPart.class));
        }
    }
    @Override
    public void getRegionsToOutline(Block block, Seq<TextureRegion> out){
        for(var part : parts){
            part.getOutlines(out);
        }

        for(var parts : ammoParts.values()){
            for(var part : parts){
                part.getOutlines(out);
            }
        }

        if(block.region.found() && !(block.outlinedIcon > 0 && block.outlinedIcon < block.getGeneratedIcons().length && block.getGeneratedIcons()[block.outlinedIcon].equals(block.region))){
            out.add(block.region);
        }

        block.resetGeneratedIcons();
    }
    @Override
    public void draw(Building build) {
        AquaItemTurret turret = (AquaItemTurret) build.block;
        //Stupid hardcode!!!! GET RID OFFFFFFFF
        AquaItemTurret.AquaItemTurretBuild tb = ( AquaItemTurret.AquaItemTurretBuild) build;

        Draw.rect(base, build.x, build.y);
        Draw.color();

        Draw.z(shadowLayer);

        Drawf.shadow(preview, build.x + tb.recoilOffset.x - turret.elevation, build.y + tb.recoilOffset.y - turret.elevation, tb.drawrot());

        Draw.z(turretLayer);

        drawTurret(turret, tb);
        drawHeat(turret, tb);

        if (parts.size > 0) {
            if (outline.found()) {
                //draw outline under everything when parts are involved
                Draw.z(turretLayer - 0.01f);
                Draw.rect(outline, build.x + tb.recoilOffset.x, build.y + tb.recoilOffset.y, tb.drawrot());
                Draw.z(turretLayer);
            }

            float progress = tb.progress();

            //TODO no smooth reload
            var params = DrawPart.params.set(build.warmup(), 1f - progress, Mathf.clamp((float) tb.totalAmmo / turret.ammoPerShot), tb.heat, tb.curRecoil, tb.charge, tb.x + tb.recoilOffset.x, tb.y + tb.recoilOffset.y, tb.rotation);

            for (var part : parts) {
                params.setRecoil(part.recoilIndex >= 0 && tb.curRecoils != null ? tb.curRecoils[part.recoilIndex] : tb.curRecoil);

                part.draw(params);
            }
            //Hardcoding this is a bad idea and will lead to crashes. Until I make a base turret class I'll roll with this.
                if (ammoParts.size > 0 && tb.getAmmoContent() != null) {
                    var parts = ammoParts.get(tb.getAmmoContent());
                    if (parts != null) {
                        for (var part : parts) {
                            params.setRecoil(part.recoilIndex >= 0 && tb.curRecoils != null ? tb.curRecoils[part.recoilIndex] : tb.curRecoil);
                            part.draw(params);
                        }
                    }
            }
        }
    }
    @Override
    public void load(Block block){
        if(!(block instanceof Turret)) throw new ClassCastException("This drawer can only be used on turrets.");

        preview = Core.atlas.find(block.name + "-preview", block.region);
        outline = Core.atlas.find(block.name + "-outline");
        liquid = Core.atlas.find(block.name + "-liquid");
        top = Core.atlas.find(block.name + "-top");
        heat = Core.atlas.find(block.name + "-heat");
        base = Core.atlas.find(block.name + "-base");

        for(var part : parts){
            part.turretShading = true;
            part.load(block.name);
        }

        for(var parts : ammoParts.values()){
            for(var part : parts){
                part.turretShading = true;
                part.load(block.name);
            }
        }
        if(!base.found() && block.minfo.mod != null) base = Core.atlas.find(block.minfo.mod.name + "-" + basePrefix + "block-" + block.size);
        if(!base.found()) base = Core.atlas.find(basePrefix + "block-" + block.size);
    }

}
