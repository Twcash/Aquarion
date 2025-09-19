package aquarion.world.blocks.heatBlocks;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.struct.IntSet;
import arc.struct.Seq;
import arc.util.Eachable;
import mindustry.Vars;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.blocks.heat.HeatBlock;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;

import java.util.Arrays;

/** block that takes het from the block behind it and moves it forward. **/
public class AquaheatMover extends Block {

    public float visualMaxHeat = 15f;

    public float visualMinHeat = -15f;

    public DrawBlock drawer = new DrawDefault();

    public AquaheatMover(String name) {
        super(name);
        update = solid = rotate = true;
        rotateDraw = false;
        size = 2;
    }

    @Override
    public void setBars() {
        super.setBars();

        //TODO show number
        addBar("temperature", (HeatMoverBuild entity) -> new Bar(() -> Core.bundle.format("bar.heatamount", (int) (entity.heat + 0.001f)), () -> Pal.techBlue.lerp(Pal.lightOrange, entity.heat / visualMaxHeat), () -> entity.heat / visualMaxHeat));
    }

    @Override
    public void load() {
        super.load();
        drawer.load(this);
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        drawer.drawPlan(this, plan, list);
    }

    @Override
    public TextureRegion[] icons() {
        return drawer.finalIcons(this);
    }

    public class HeatMoverBuild extends Building implements AquaHeatBlock, AquaHeatConsumer {
        public float heat = 0f;
        public float[] sideHeat = new float[4];
        public IntSet cameFrom = new IntSet();
        public long lastHeatUpdate = -1;
        @Override
        public void draw(){
            drawer.draw(this);
        }
        @Override
        public void drawLight(){
            super.drawLight();
            drawer.drawLight(this);
        }
        @Override
        public float[] sideHeat(){
            return sideHeat;
        }
        @Override
        public void updateTile(){
            updateHeat();
        }
        public void updateHeat(){
            if(lastHeatUpdate == Vars.state.updateId) return;

            lastHeatUpdate = Vars.state.updateId;
            heat = calculateHeat(sideHeat, cameFrom);
        }
        @Override
        public float calculateHeat(float[] sideHeat, IntSet cameFrom) {
            Arrays.fill(sideHeat, 0.0F);
            if (cameFrom != null) cameFrom.clear();

            float totalHeat = 0f;
            IntSet visited = new IntSet();
            Seq<Building> queue = new Seq<>();
            queue.add(this);

            while (!queue.isEmpty()) {
                Building current = queue.remove(0);
                if (!(current instanceof AquaHeatBlock hb)) continue;
                if (visited.contains(current.id)) continue;

                visited.add(current.id);
                float currentHeat = hb.heat();

                for (Building nearby : current.proximity) {
                    if (nearby == null || nearby.team != current.team || !(nearby instanceof HeatBlock nb)) continue;

                    int rel = current.relativeTo(nearby);
                    int back = (rel + 2) % 4;
                    boolean validDirection = !current.block.rotate || (rel == nearby.rotation || back == nearby.rotation);
                    if (!validDirection) continue;

                    // Only allow pulling from behind if the nearby is not a conductor
                    boolean canPullFromBack = !(nearby instanceof AquaheatMover.HeatMoverBuild);

                    // Prevent pulling if cameFrom contains current
                    if (cameFrom != null && nearby instanceof AquaheatMover.HeatMoverBuild hcNearby) {
                        if (hcNearby.cameFrom.contains(current.id)) continue;
                    }

                    float diff = Math.min(Math.abs(nearby.x - current.x), Math.abs(nearby.y - current.y)) / 8f;
                    int contactPoints = Math.min(
                            (int)(current.block.size / 2f + nearby.block.size / 2f - diff),
                            Math.min(current.block.size, nearby.block.size)
                    );

                    float transfer;
                    if (canPullFromBack) {
                        // Pull heat from behind if non-conductor
                        transfer = nb.heat() / contactPoints;
                    } else {
                        // Standard push from current
                        transfer = currentHeat / (float)current.block.size * contactPoints;
                    }

                    int dir = Mathf.mod(current.relativeTo(nearby), 4);
                    sideHeat[dir] += transfer;
                    totalHeat += transfer;

                    if (cameFrom != null) {
                        cameFrom.add(nearby.id);
                        if (nearby instanceof AquaheatMover.HeatMoverBuild hcNext) {
                            cameFrom.addAll(hcNext.cameFrom);
                        }
                    }

                    if (nearby instanceof AquaheatMover.HeatMoverBuild hcNext) {
                        hcNext.updateHeat();
                        queue.add(nearby);
                    }
                }
            }

            return totalHeat;
        }
        @Override
        public float temperatureRequired(){
            return visualMaxHeat;
        }
        @Override
        public float warmup(){
            return heat;
        }

        @Override
        public float heat(){
            return heat;
        }

    }
}
