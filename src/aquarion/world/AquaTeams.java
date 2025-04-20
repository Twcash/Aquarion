package aquarion.world;

import arc.graphics.Color;
import arc.util.Nullable;
import mindustry.game.Team;
import mindustry.game.Teams;
import mindustry.type.UnitType;
import mindustry.world.Block;
import mindustry.world.blocks.storage.CoreBlock;

import static mindustry.Vars.state;
import arc.func.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.Queue;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.ai.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.blocks.storage.CoreBlock.*;

import java.util.*;

import static mindustry.Vars.*;
public class AquaTeams {
    public static Seq<Teams.TeamData> active = new Seq<>();
    //Credits to Slotterfleet / team oct as I heavily referenced that (I also tooka big chunk for the icon loading)
    public static Team tendere, gerb, wrecks;

    public static void load() {
        tendere = newTeam(69, "tendere", Color.valueOf("d57761"));
        gerb = newTeam(70, "gerb", Color.valueOf("8381ff"));
        wrecks = newTeam(71, "wrecks", Color.valueOf("3a2a2a"));
    }
    private static Team newTeam(int id, String name, Color color) {Team team = Team.get(id);
        team.name = name;
        team.color.set(color);

        team.palette[0] = color;
        team.palette[1] = color.cpy().mul(0.75f);
        team.palette[2] = color.cpy().mul(0.5f);

        for(int i = 0; i < 3; i++){
            team.palettei[i] = team.palette[i].rgba();
        }
        return team;
    }
    public static class NewBlockPlan{
        public final short x, y, rotation;
        public final Block block;
        public final Object config;
        public boolean removed;

        public NewBlockPlan(int x, int y, short rotation, Block block, Object config){
            this.x = (short)x;
            this.y = (short)y;
            this.rotation = rotation;
            this.block = block;
            this.config = config;
        }

        @Override
        public String toString(){
            return "BlockPlan{" +
                    "x=" + x +
                    ", y=" + y +
                    ", rotation=" + rotation +
                    ", block=" + block +
                    ", config=" + config +
                    '}';
        }
    }
    public static class NewTeamData{
        private static final IntSeq derelictBuffer = new IntSeq();

        public final Team team;

        /** Handles building ""bases"". */
        public @Nullable BaseBuilderAI buildAi;
        /** Handles RTS unit control. */
        public @Nullable RtsAI rtsAi;

        private boolean presentFlag;

        /** Enemies with cores or spawn points. */
        public Team[] coreEnemies = {};
        /** Planned blocks for drones. This is usually only blocks that have been broken. */
        public Queue<NewBlockPlan> plans = new Queue<>();

        /** List of live cores of this team. */
        public final Seq<CoreBuild> cores = new Seq<>();
        /** Last known live core of this team. */
        public @Nullable CoreBuild lastCore;
        /** Quadtree for all buildings of this team. Null if not active. */
        public @Nullable QuadTree<Building> buildingTree;
        /** Turrets by range. Null if not active. */
        public @Nullable QuadTree<Building> turretTree;
        /** Quadtree for units of this team. Do not access directly. */
        public @Nullable QuadTree<Unit> unitTree;
        /** Current unit cap. Do not modify externally. */
        public int unitCap;
        /** Total unit count. */
        public int unitCount;
        /** Counts for each type of unit. Do not access directly. */
        public @Nullable int[] typeCounts;
        /** Cached buildings by type. */
        public ObjectMap<Block, Seq<Building>> buildingTypes = new ObjectMap<>();
        /** Units of this team. Updated each frame. */
        public Seq<Unit> units = new Seq<>(false);
        /** Same as units, but players. */
        public Seq<Player> players = new Seq<>(false);
        /** All buildings. Updated on team change / building addition or removal. Includes even buildings that do not update(). */
        public Seq<Building> buildings = new Seq<>(false);
        /** Units of this team by type. Updated each frame. */
        public @Nullable Seq<Unit>[] unitsByType;

        public NewTeamData(Team team){
            this.team = team;
        }

        public Seq<Building> getBuildings(Block block){
            return buildingTypes.get(block, () -> new Seq<>(false));
        }

        public int getCount(Block block){
            var res = buildingTypes.get(block);
            return res == null ? 0 : res.size;
        }

        /** Destroys this team's presence on the map, killing part of its buildings and converting everything to 'derelict'. */
        public void destroyToDerelict(){

            //grab all buildings from quadtree.
            var builds = new Seq<Building>();
            if(buildingTree != null){
                buildingTree.getObjects(builds);
            }

            //no remaining blocks, cease building if applicable
            plans.clear();

            //convert all team tiles to neutral, randomly killing them
            for(var b : builds){
                if(b.block.privileged) continue;

                if(b instanceof CoreBuild){
                    b.kill();
                }else{
                    scheduleDerelict(b);
                }
            }

            finishScheduleDerelict();

            //kill all units randomly
            units.each(u -> Time.run(Mathf.random(0f, 60f * 5f), () -> {
                //ensure unit hasn't switched teams for whatever reason
                if(u.team == team){
                    u.kill();
                }
            }));
        }

        /** Make all buildings within this range derelict/explode. */
        public void timeDestroy(float x, float y, float range){
            var builds = new Seq<Building>();
            if(buildingTree != null){
                buildingTree.intersect(x - range, y - range, range * 2f, range * 2f, builds);
            }

            for(var build : builds){
                if(!build.block.privileged && build.within(x, y, range) && !cores.contains(c -> c.within(build, range))){
                    scheduleDerelict(build);
                }
            }
            finishScheduleDerelict();
        }

        private void scheduleDerelict(Building build){
            //queue block to be handled later, avoid packet spam
            derelictBuffer.add(build.pos());

            if(Mathf.chance(0.2)){
                Time.run(Mathf.random(0f, 60f * 6f), build::kill);
            }
        }

        private void finishScheduleDerelict(){
            derelictBuffer.clear();
        }

        //this is just an alias for consistency
        @Nullable
        public Seq<Unit> getUnits(UnitType type){
            return unitCache(type);
        }

        @Nullable
        public Seq<Unit> unitCache(UnitType type){
            if(unitsByType == null || unitsByType.length <= type.id || unitsByType[type.id] == null) return null;
            return unitsByType[type.id];
        }

        public void updateCount(UnitType type, int amount){
            if(type == null) return;
            unitCount = Math.max(amount + unitCount, 0);
            if(typeCounts == null || typeCounts.length <= type.id){
                typeCounts = new int[Vars.content.units().size];
            }
            typeCounts[type.id] = Math.max(amount + typeCounts[type.id], 0);
        }

        public QuadTree<Unit> tree(){
            if(unitTree == null) unitTree = new QuadTree<>(Vars.world.getQuadBounds(new Rect()));
            return unitTree;
        }

        public int countType(UnitType type){
            return typeCounts == null || typeCounts.length <= type.id ? 0 : typeCounts[type.id];
        }

        public boolean active(){
            return (team == state.rules.waveTeam && state.rules.waves) || cores.size > 0 || buildings.size > 0 || (team == Team.neoplastic && units.size > 0);
        }

        public boolean hasCore(){
            return cores.size > 0;
        }

        /** @return whether this team has any cores (standard team), or any hearts (neoplasm). */
        public boolean isAlive(){
            return hasCore();
        }

        public boolean noCores(){
            return cores.isEmpty();
        }

        @Nullable
        public CoreBlock.CoreBuild core(){
            return cores.isEmpty() ? null : cores.first();
        }

        /** @return whether this team is controlled by the AI and builds bases. */
        public boolean hasAI(){
            return team.rules().rtsAi || team.rules().buildAi;
        }
        public Seq<Teams.TeamData> getActive(){
            active.removeAll(t -> !t.active());
            return active;
        }
        @Override
        public String toString(){
            return "NewTeamData{" +
                    "cores=" + cores +
                    ", team=" + team +
                    '}';
        }
    }

}