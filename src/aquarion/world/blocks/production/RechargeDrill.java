package aquarion.world.blocks.production;

import arc.math.Mathf;
import arc.struct.EnumSet;
import mindustry.gen.Sounds;
import mindustry.world.blocks.production.Drill;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Env;

public class RechargeDrill extends Drill {

    public float rechargeTime = 180;        // Time required to recharge
    public float drillTimeLimit = 600;      // Maximum time allowed for drilling before recharge
    public RechargeDrill(String name) {
        super(name);
        update = true;
        solid = true;
        group = BlockGroup.drills;
        hasLiquids = true;
        liquidCapacity = 5f;
        hasItems = true;
        ambientSound = Sounds.drill;
        ambientSoundVolume = 0.018f;
        envEnabled |= Env.space; // Allows drills to work in space
        flags = EnumSet.of(BlockFlag.drill);
    }

    public class RechargeDrillBuild extends DrillBuild {
        public float recharge = 0;               // Tracks recharge progress
        public float activeDrillTime = 0;        // Tracks drilling progress
        public boolean isRecharging = true;      // Flag to track recharging state

        @Override
        public void updateTile() {
            // Unconditionally dump items if available
            if (timer(timerDump, dumpTime)) {
                dump(dominantItem != null && items.has(dominantItem) ? dominantItem : null);
            }

            // Check if the drill needs to recharge
            if (isRecharging) {
                recharge += edelta();

                // Once recharge time is met, switch to drilling mode
                if (recharge >= rechargeTime) {
                    recharge = 0;
                    isRecharging = false;
                }
                return;
            }

            // Slow down drilling as activeDrillTime approaches drillTimeLimit
            float timeRemaining = drillTimeLimit - activeDrillTime;
            float efficiencyModifier = Mathf.clamp(timeRemaining / drillTimeLimit);

            // Check if drill time limit has been reached, if so start recharging
            if (activeDrillTime >= drillTimeLimit) {
                activeDrillTime = 0;
                isRecharging = true;
                return;
            }

            // Proceed with normal drill logic
            if (dominantItem == null) {
                return;
            }

            timeDrilled += warmup * delta();

            float delay = getDrillTime(dominantItem);

            if (items.total() < itemCapacity && dominantItems > 0 && efficiency > 0) {
                float speed = Mathf.lerp(1f, liquidBoostIntensity, optionalEfficiency) * efficiency * efficiencyModifier;

                lastDrillSpeed = (speed * dominantItems * warmup) / delay;
                warmup = Mathf.approachDelta(warmup, speed, warmupSpeed);
                progress += delta() * dominantItems * speed * warmup;

                // Increment active drill time
                activeDrillTime += delta() * warmup;

                if (Mathf.chanceDelta(updateEffectChance * warmup)) {
                    updateEffect.at(x + Mathf.range(size * 2f), y + Mathf.range(size * 2f));
                }
            } else {
                lastDrillSpeed = 0f;
                warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);
                return;
            }

            // Offload item if progress meets delay
            if (dominantItems > 0 && progress >= delay && items.total() < itemCapacity) {
                offload(dominantItem);

                progress %= delay;

                if (wasVisible && Mathf.chanceDelta(updateEffectChance * warmup)) {
                    drillEffect.at(x + Mathf.range(drillEffectRnd), y + Mathf.range(drillEffectRnd), dominantItem.color);
                }
            }
        }
    }
}