package aquarion.world.blocks.heatBlocks;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.*;
import arc.util.Tmp;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.blocks.heat.*;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.meta.*;

public class AquaHeatCrafter extends GenericCrafter {
    /**
     * Base heat requirement for 100% efficiency. if put into negatives it will invert
     */
    public float heatRequirement = 10f;
    /**
     * After heat meets this requirement, excess heat will be scaled by this number.
     */
    public float overheatScale = 0.8f;
    /**
     * Maximum possible efficiency after overheat.
     */
    public float maxEfficiency = 4f;
    /**
     * if you go below the heat req eff goes up
     */
    public boolean flipHeatScale = false;
    /** the efficiency this machine works at without any heat **/
    public float baseEfficiency = 0f;

    public AquaHeatCrafter(String name) {
        super(name);
    }

    @Override
    public void setBars() {
        super.setBars();

        addBar("heat", (AquaHeatCrafterBuild entity) ->
                new Bar(() ->
                        Core.bundle.format("bar.heatpercent",
                                (int)(entity.heat + 0.01f),
                                (int)(entity.efficiencyScale() * 100 + 0.01f)),
                        () -> {
                            float max = heatRequirement * 5f;
                            float heat = entity.heat;

                            if(heat < 0f){
                                float t = Mathf.clamp(1f + heat / max);
                                return Tmp.c1.set(Color.black).lerp(Pal.techBlue, t);
                            }else{
                                float t = Mathf.clamp(heat / max);
                                return Tmp.c1.set(Pal.lightOrange).lerp(Color.white, t);
                            }
                        },
                        () -> Mathf.clamp(Math.abs(entity.heat) / Math.abs(heatRequirement))
                )
        );
    }

    @Override
    public void setStats() {
        super.setStats();

        stats.add(Stat.input, heatRequirement, StatUnit.heatUnits);
        stats.add(Stat.maxEfficiency, (int) (maxEfficiency * 100f), StatUnit.percent);
    }

    public class AquaHeatCrafterBuild extends GenericCrafterBuild implements HeatConsumer {
        //TODO sideHeat could be smooth
        public float[] sideHeat = new float[4];
        public float heat = 0f;

        @Override
        public void updateTile() {
            heat = calculateHeat(sideHeat);

            super.updateTile();
        }

        @Override
        public float heatRequirement() {
            return heatRequirement;
        }

        @Override
        public float[] sideHeat() {
            return sideHeat;
        }

        @Override
        public float warmupTarget() {
            return Mathf.clamp(heat / heatRequirement);
        }

        @Override
        public float efficiencyScale() {
            float eff = 0;
            if (flipHeatScale) {
                float over = Math.max(heat - heatRequirement, 0f);
                eff = -Math.min((heat / -heatRequirement) + over / -heatRequirement * overheatScale, maxEfficiency);
                if (eff > 1) eff = Math.min(((eff - 1) * overheatScale) + 1, maxEfficiency);
                eff = Math.max(eff, 0);
            } else {
                float over = Math.max(heat - heatRequirement, 0f);
                eff = Math.min(Mathf.clamp(heat / heatRequirement) + over / heatRequirement * overheatScale, maxEfficiency) + baseEfficiency;
                eff = Math.max(eff, 0);
            }
            return  eff;
        }
    }
}
