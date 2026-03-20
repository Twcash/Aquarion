package aquarion.world.blocks.defense;

import aquarion.world.MultiBlockLib.MultiBlock;
import aquarion.world.MultiBlockLib.MultiBlockEntity;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.struct.IntSeq;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.ai.types.CommandAI;
import mindustry.content.Fx;
import mindustry.content.UnitTypes;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.input.Placement;
import mindustry.type.UnitType;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.meta.Stat;

import java.util.concurrent.atomic.AtomicReference;

import static mindustry.Vars.*;

public class MissileBlock extends Block implements MultiBlock {
    public int[] linkValues = {};
    public Seq<Point2> linkPos = new Seq<>();
    public IntSeq linkSize = new IntSeq();
    public DrawBlock drawer = new DrawDefault();

    public boolean canMirror = false;
    public int[] rotations = {0, 1, 2, 3, 0, 1, 2, 3};
    public float range = 250;
    public UnitType spawn = UnitTypes.alpha;
    public float time = 240;
    public MissileBlock(String name) {
        super(name);

        hasItems = true;
        hasLiquids = true;
        update = true;
        rotate = true;
        rotateDraw = true;
        quickRotate = false;
        allowDiagonal = false;
    }

    @Override
    public boolean isMirror() {
        return name.endsWith("-mirror");
    }
    @Override
    public void setBars() {
        super.setBars();
        addBar("progress", (MissileBlock.MissileBuild e) -> new Bar("bar.progress", Pal.ammo, e::totalprogress));
    }
    @Override
    @Nullable
    public Block mirrorBlock(){
        if (isMirror()) return content.block(name.replace("-mirror", ""));
        else return content.block(name + "-mirror");
    }

    @Override
    public void load(){
        super.load();

        drawer.load(this);
    }

    @Override
    public void init() {
        super.init();
        addLink(linkValues);

        rotateDraw = true;
        quickRotate = false;
        allowDiagonal = false;

        hasItems = true;
        hasLiquids = true;

        if (isMirror()){
            alwaysUnlocked = true;
        }
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.remove(Stat.size);
        stats.add(Stat.size, "@x@", getMaxSize(size, 0).x, getMaxSize(size, 0).y);
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        return super.canPlaceOn(tile, team, rotation) && checkLink(tile, team, size, rotation);
    }

    @Override
    public void placeBegan(Tile tile, Block previous) {
        createPlaceholder(tile, size);
    }
    @Override
    public void changePlacementPath(Seq<Point2> points, int rotation) {
        Placement.calculateNodes(points, this, rotation, (point, other) -> {
            if (rotation % 2 == 0) {
                return Math.abs(point.x - other.x) <= getMaxSize(size, rotation).x;
            } else {
                return Math.abs(point.y - other.y) <= getMaxSize(size, rotation).y;
            }
        });
    }

    @Override
    public Seq<Point2> linkBlockPos() {
        return linkPos;
    }

    @Override
    public IntSeq linkBlockSize() {
        return linkSize;
    }

    @Override
    public void flipRotation(BuildPlan req, boolean x){
        if (canMirror) {
            if (mirrorBlock() != null){
                if (x){
                    if (req.rotation == 1) req.rotation = 3;
                    if (req.rotation == 3) req.rotation = 1;
                }else {
                    if (req.rotation == 0) req.rotation = 2;
                    if (req.rotation == 2) req.rotation = 0;
                }
                req.block = mirrorBlock();
            }else {
                req.rotation = rotations[req.rotation + (x?0:4)];
            }
        }else {
            super.flipRotation(req, x);
        }
    }
    public class MissileBuild extends Building implements MultiBlockEntity {
        public boolean linkCreated = false, linkValid = true;

        public Seq<Building> linkEntities;
        public float progress = 0;
        public Seq<Building[]> linkProximityMap;
        public Tile teamPos, statusPos;
        public float totprogress = 0;
        @Override
        public void created(){
            super.created();
            linkProximityMap = new Seq<>();
        }
        @Override
        public void draw(){
            drawer.draw(this);
        }
        public float totalprogress(){
            return totprogress/time;
        }
        @Override
        public void updateTile() {
            super.updateTile();
            if (!linkCreated) {
                linkEntities = setLinkBuild(this, block, tile, team, size, rotation);
                linkCreated = true;
                updateLinkProximity();
            }
            if (efficiency > 0) {
                progress += edelta() * Vars.state.rules.unitBuildSpeed(team) * efficiency;
                totprogress += progress;
            }

            Building targetBuilding = findEnemyBuilding(range);
            Unit targetUnit = findEnemyUnit(range);

            if ((targetBuilding != null || targetUnit != null) && progress >= time) {
                float targetX = x;
                float targetY = y;

                if (targetBuilding != null) {
                    targetX = targetBuilding.x;
                    targetY = targetBuilding.y;
                } else {
                    targetX = targetUnit.x;
                    targetY = targetUnit.y;
                }

                var b = spawn.create(team);
                b.set( x,y);
                Tmp.v6.set(targetX, targetY);
                if(b.controller() instanceof CommandAI ai){
                    ai.commandPosition(Tmp.v6);
                }
                b.rotation = this.rotdeg();
                Effect.shake(2f, 3f, this);
                Fx.producesmoke.at(this);
                b.add();
                kill();
            }
        }

        Unit findEnemyUnit(float radius) {
            final Unit[] found = {null};
            Units.nearbyEnemies(team, x, y, radius, u -> {
                if (!u.dead) {
                    found[0] = u;
                }
            });
            return found[0];
        }

        // Find enemy buildings in range
        Building findEnemyBuilding(float radius) {
            AtomicReference<Building> b = new AtomicReference<>();
            indexer.findEnemyTile(team, x, y, range, build ->{
                b.set(build);
                return true;
            });
            return b.get();
        }
        @Override
        public void updateLinkProximity() {
            if (linkEntities != null) {
                linkProximityMap.clear();
                //add link entity's proximity
                for (Building link : linkEntities) {
                    for (Building linkProx : link.proximity) {
                        if (linkProx != this && !linkEntities.contains(linkProx)) {
                            if (checkValidPair(linkProx, link)) {
                                linkProximityMap.add(new Building[]{linkProx, link});
                            }
                        }
                    }
                }

                //add self entity's proximity
                for (Building prox : proximity) {
                    if (!linkEntities.contains(prox)) {
                        if (checkValidPair(prox, this)) {
                            linkProximityMap.add(new Building[]{prox, this});
                        }
                    }
                }
            }
        }

        public boolean checkValidPair(Building target, Building source) {
            for (Building[] pair : linkProximityMap) {
                Building pairTarget = pair[0];
                Building pairSource = pair[1];

                if (target == pairTarget) {
                    if (target.relativeTo(pairSource) == target.relativeTo(source)) {
                        return false;
                    }
                }
            }
            return true;
        }

        @Override
        public void onProximityUpdate() {
            super.onProximityUpdate();
            updateLinkProximity();
        }

        @Override
        public void onRemoved() {
            createPlaceholder(tile, size);
        }

        @Override
        public boolean canPickup() {
            return false;
        }

        @Override
        public void drawSelect() {
            super.drawSelect();
        }

        @Override
        public void drawTeam() {
            teamPos = world.tile(tileX() + teamOverlayPos(size, rotation).x, tileY() + teamOverlayPos(size, rotation).y);
            if (teamPos != null) {
                Draw.color(team.color);
                Draw.rect("block-border", teamPos.worldx(), teamPos.worldy());
                Draw.color();
            }
        }

        @Override
        public void drawStatus() {
            statusPos = world.tile(tileX() + statusOverlayPos(size, rotation).x, tileY() + statusOverlayPos(size, rotation).y);
            if (block.enableDrawStatus && block.consumers.length > 0) {
                float multiplier = block.size > 1 ? 1 : 0.64F;
                Draw.z(Layer.power + 1);
                Draw.color(Pal.gray);
                Fill.square(statusPos.worldx(), statusPos.worldy(), 2.5F * multiplier, 45);
                Draw.color(status().color);
                Fill.square(statusPos.worldx(), statusPos.worldy(), 1.5F * multiplier, 45);
                Draw.color();
            }
        }
        @Override
        public void write(Writes write){
            super.write(write);
            write.f(progress);
        }
        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
             progress = read.f();

        }
    }
}