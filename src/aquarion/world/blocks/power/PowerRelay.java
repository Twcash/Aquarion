package aquarion.world.blocks.power;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.core.Renderer;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.Tile;
import mindustry.world.blocks.power.PowerNode;

import static mindustry.Vars.*;

public class PowerRelay extends PowerNode {
public float pulseScl = 1;
public float pulseMag = 1;
public float laserWidth = 4f;

    // Constructor
    public PowerRelay(String name) {
        super(name);
        laserRange = 8;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        Tile tile = world.tile(x, y);

        if(tile == null || !autolink) return;

        Draw.color(Pal.placing);
        Drawf.dashSquareBasic(x, y, laserRange * tilesize * 2);

        getPotentialLinks(tile, player.team(), other -> {
            Draw.color(laserColor1, Renderer.laserOpacity * 0.5f);
            drawLaser(x * tilesize + offset, y * tilesize + offset, other.x, other.y, size, other.block.size);

            Drawf.square(other.x, other.y, other.block.size * tilesize / 2f + 2f, Pal.place);
        });

        Draw.reset();
    }

    public class PowerRelayBuild extends PowerNodeBuild {

        @Override
        public boolean onConfigureBuildTapped(Building other){
            if(linkValid(this, other)){
                configure(other.pos());
                return false;
            }

            if(this == other){ //double tapped
                if(other.power.links.size == 0){ //find links
                    Seq<Point2> points = new Seq<>();
                    getPotentialLinks(tile, team, link -> {
                        if(!insulated(this, link) && points.size < maxNodes){
                            points.add(new Point2(link.tileX() - tile.x, link.tileY() - tile.y));
                        }
                    });
                    configure(points.toArray(Point2.class));
                }else{ //clear links
                    configure(new Point2[0]);
                }
                deselect();
                return false;
            }

            return true;
        }

        @Override
        public void drawSelect(){
            super.drawSelect();

            if(!drawRange) return;

            Draw.color(Pal.accent);
            Drawf.dashSquareBasic(x, y, laserRange * tilesize * 2);
            Draw.reset();
        }


        @Override
        public void drawConfigure(){
            Draw.color(Pal.accent);
            Drawf.dashSquareBasic(x, y, tile.block().size * tilesize / 2f + 1f + Mathf.absin(Time.time, 4f, 1f));

            if(drawRange){
                float fulls = laserRange * tilesize/2f;

                Drawf.dashSquareBasic(x, y, laserRange * tilesize * 2);

                for(int x = (int)(tile.x - laserRange - 2); x <= tile.x + laserRange + 2; x++){
                    for(int y = (int)(tile.y - laserRange - 2); y <= tile.y + laserRange + 2; y++){
                        Building link = world.build(x, y);

                        if(link != this && linkValid(this, link, false)){
                            boolean linked = linked(link);

                            if(linked){
                                Drawf.square(link.x, link.y, link.block.size * tilesize / 2f + 1f, Pal.place);
                            }
                        }
                    }
                }

                Draw.reset();
            }else{
                power.links.each(i -> {
                    var link = world.build(i);
                    if(link != null && linkValid(this, link, false)){
                        Drawf.square(link.x, link.y, link.block.size * tilesize / 2f + 1f, Pal.place);
                    }
                });
            }
        }

    }
}