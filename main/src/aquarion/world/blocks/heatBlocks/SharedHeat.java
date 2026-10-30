package aquarion.world.blocks.heatBlocks;

import arc.Events;
import arc.struct.IntSet;
import mindustry.game.EventType.ContentInitEvent;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.blocks.heat.*;
import mindustry.world.blocks.production.HeatCrafter;
import mindustry.world.blocks.defense.turrets.*;

import static mindustry.Vars.*;

/** Receiver-aware sharing for omnidirectional sources; vanilla sources stay unchanged. */
public final class SharedHeat{
    public interface Source extends HeatBlock{
        float heatTo(Building receiver);
    }

    private static boolean registered;
    private SharedHeat(){}

    public static boolean canReceive(Building build){
        if(!build.enabled) return false;
        if(build instanceof HeatConsumer consumer) return consumer.heatRequirement() > 0f;
        if(build.block instanceof Turret turret) return turret.heatRequirement > 0f;
        return build instanceof AquaHeatConsumer consumer && consumer.temperatureRequired() > 0f;
    }

    public static int contactPoints(Building source, Building receiver){
        float diff = Math.min(Math.abs(source.x - receiver.x), Math.abs(source.y - receiver.y)) / tilesize;
        return Math.max(0, Math.min((int)(source.block.size / 2f + receiver.block.size / 2f - diff),
            Math.min(source.block.size, receiver.block.size)));
    }

    /** Replace vanilla's contact-width contribution with the receiver's explicit share. */
    public static float adjust(Building receiver, float[] sideHeat, float received){
        for(Building source : receiver.proximity){
            if(source.team != receiver.team || !(source instanceof Source shared)) continue;
            float vanilla = shared.heat() / source.block.size * contactPoints(source, receiver);
            float difference = shared.heatTo(receiver) - vanilla;
            received += difference;
            int side = Math.floorMod(receiver.relativeTo(source), 4);
            sideHeat[side] += difference;
        }
        return received;
    }

    public static void register(){
        if(registered) return;
        registered = true;
        Events.on(ContentInitEvent.class, e -> installReceivers());
    }

    /** Wrap only the engine's exact standard builds, preserving custom subclass behavior. */
    public static void installReceivers(){
        for(Block block : content.blocks()){
            if(block.buildType == null) continue;
            if(!(block instanceof HeatConductor || block instanceof HeatCrafter || block instanceof Turret turret && turret.heatRequirement > 0f)) continue;
            Class<?> type = block.buildType.get().getClass();
            if(block instanceof HeatConductor conductor && type == HeatConductor.HeatConductorBuild.class){
                block.buildType = () -> conductor.new HeatConductorBuild(){
                    @Override public float calculateHeat(float[] sides, IntSet cameFrom){
                        return adjust(this, sides, super.calculateHeat(sides, cameFrom));
                    }
                };
            }else if(block instanceof HeatCrafter crafter && type == HeatCrafter.HeatCrafterBuild.class){
                block.buildType = () -> crafter.new HeatCrafterBuild(){
                    @Override public float calculateHeat(float[] sides, IntSet cameFrom){
                        return adjust(this, sides, super.calculateHeat(sides, cameFrom));
                    }
                };
            }else if(block instanceof PowerTurret turret && type == PowerTurret.PowerTurretBuild.class){
                block.buildType = () -> turret.new PowerTurretBuild(){
                    @Override public float calculateHeat(float[] sides, IntSet cameFrom){
                        return adjust(this, sides, super.calculateHeat(sides, cameFrom));
                    }
                };
            }else if(block instanceof ItemTurret turret && type == ItemTurret.ItemTurretBuild.class){
                block.buildType = () -> turret.new ItemTurretBuild(){
                    @Override public float calculateHeat(float[] sides, IntSet cameFrom){
                        return adjust(this, sides, super.calculateHeat(sides, cameFrom));
                    }
                };
            }else if(block instanceof LiquidTurret turret && type == LiquidTurret.LiquidTurretBuild.class){
                block.buildType = () -> turret.new LiquidTurretBuild(){
                    @Override public float calculateHeat(float[] sides, IntSet cameFrom){
                        return adjust(this, sides, super.calculateHeat(sides, cameFrom));
                    }
                };
            }else if(block instanceof LaserTurret turret && type == LaserTurret.LaserTurretBuild.class){
                block.buildType = () -> turret.new LaserTurretBuild(){
                    @Override public float calculateHeat(float[] sides, IntSet cameFrom){
                        return adjust(this, sides, super.calculateHeat(sides, cameFrom));
                    }
                };
            }else if(block instanceof ContinuousLiquidTurret turret && type == ContinuousLiquidTurret.ContinuousLiquidTurretBuild.class){
                block.buildType = () -> turret.new ContinuousLiquidTurretBuild(){
                    @Override public float calculateHeat(float[] sides, IntSet cameFrom){
                        return adjust(this, sides, super.calculateHeat(sides, cameFrom));
                    }
                };
            }else if(block instanceof ContinuousTurret turret && type == ContinuousTurret.ContinuousTurretBuild.class){
                block.buildType = () -> turret.new ContinuousTurretBuild(){
                    @Override public float calculateHeat(float[] sides, IntSet cameFrom){
                        return adjust(this, sides, super.calculateHeat(sides, cameFrom));
                    }
                };
            }
        }
    }
}
