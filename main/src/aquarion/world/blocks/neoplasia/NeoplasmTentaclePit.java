package aquarion.world.blocks.neoplasia;

import aquarion.world.entities.bullet.TentacleBulletType;
import aquarion.world.graphics.Renderer;
import arc.Events;
import arc.graphics.g2d.Draw;
import arc.math.Interp;
import arc.math.Mathf;
import mindustry.entities.Units;
import mindustry.game.EventType.BuildDamageEvent;
import mindustry.gen.Building;
import mindustry.gen.Teamc;
import mindustry.type.ItemStack;

import static mindustry.Vars.*;

/** A colony defense grown in response to nearby damage, using stored colony items. */
public class NeoplasmTentaclePit extends GenericNeoplasiaBlock{
    public float alertRange = 64f, alertDuration = 600f, spacing = 48f;
    public float reloadTime = 90f;
    /** Ticks of peace before the pit returns to mass and recovers its construction items. */
    public float retractDelay = 30f * 60f;
    public TentacleBulletType shootType;
    public NeoplasmTentaclePit largeVariant;
    /** Only the difference between small and large construction costs is paid on upgrade. */
    public ItemStack[] upgradeItemCost;

    static{
        Events.on(BuildDamageEvent.class, event -> {
            if(event.build != null && (event.source == null || event.source.team != event.build.team)) alertDamage(event.build);
        });
    }

    public NeoplasmTentaclePit(String name){
        super(name);
        bursts = 0;
        damage = 0f;
        shouldEmptyUpgrade = shouldEmpty2Upgrade = false;
        oreGrowBonus = 0f;
        buildType = TentaclePitBuild::new;
    }

    /** Record the alarm only; replacing buildings inside damage callbacks is unsafe. */
    public static void alertDamage(Building damaged){
        NeoplasmTentaclePit pit = tentaclePitBlock;
        if(pit == null || net.client() || world.isGenerating() || damaged.tile == null) return;
        Units.nearbyBuildings(damaged.x, damaged.y, pit.alertRange, other -> {
            if(other.team != damaged.team || !(other instanceof NeoplasiaBuild tissue)) return;
            if(!(other.block instanceof NeoplasmTentaclePit) && !tissue.block().veinCandidate) return;
            tissue.defenseTime = pit.alertDuration;
            if(tissue instanceof NeoplasmTentaclePit.TentaclePitBuild defense) defense.idleTime = 0f;
        });
    }

    @Override
    public boolean canUpgradeToThis(NeoplasiaBuild build){
        boolean[] nearby = {false};
        Units.nearbyBuildings(build.x, build.y, spacing, other -> {
            if(other != build && other.team == build.team && other.block instanceof NeoplasmTentaclePit) nearby[0] = true;
        });
        return !nearby[0];
    }

    public class TentaclePitBuild extends NeoplasiaBuild{
        float reload;
        float idleTime;

        @Override
        void tryUpgrades(){
            tryGrowTentaclePit();
        }

        @Override
        public void updateTile(){
            super.updateTile();
            if(net.client() || !NeoplasiaGraph.isCurrent(this) || !NeoplasiaGraph.isConnected(this) || shootType == null) return;
            idleTime += delta();
            reload = Math.min(reloadTime, reload + delta());
            if(reload < reloadTime || spawnTime < spawnDuration) return;
            Teamc target = findTarget();
            if(target == null){
                if(idleTime >= retractDelay) retract();
                return;
            }
            idleTime = 0f;
            shootType.create(this, team, x, y, angleTo(target));
            reload = 0f;
        }

        boolean retract(){
            if(base == null || net.client() || !NeoplasiaGraph.isCurrent(this)) return false;
            if(!upgradeTo(base)) return false;
            if(itemCost != null) for(ItemStack stack : itemCost) tile.build.items.add(stack.item, stack.amount);
            return true;
        }

        Teamc findTarget(){
            return Units.closestTarget(team, x, y, shootType.range,
                unit -> !unit.dead() && unit.hittable() && unit.checkTarget(shootType.collidesAir, shootType.collidesGround),
                building -> shootType.collidesGround && shootType.collidesTiles && !building.dead());
        }

        @Override
        public void draw(){
            super.draw();
            Draw.z(Renderer.Layer.blockOver + 2f);
            Draw.color();
            float scale = Interp.smooth.apply(Mathf.clamp(spawnTime / spawnDuration));
            Draw.rect(region, x, y, region.width * region.scl() * scale, region.height * region.scl() * scale);
            Draw.reset();
        }
    }
}
