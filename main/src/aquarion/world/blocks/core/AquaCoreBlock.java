package aquarion.world.blocks.core;

import arc.Events;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.entities.units.WeaponMount;
import mindustry.game.EventType;
import mindustry.gen.*;
import mindustry.type.Item;
import mindustry.type.UnitType;
import mindustry.world.blocks.storage.CoreBlock;

import static mindustry.Vars.net;
import static mindustry.Vars.state;

public class AquaCoreBlock extends CoreBlock {

    public Seq<UnitType> unitTypes = new Seq<>();

    public AquaCoreBlock(String name) {
        super(name);
        this.<Integer, AquaCoreBuild>config(Integer.class, (build, playerId)->{
            Player player = Groups.player.getByID(playerId);
            if(player != null ){
                build.playerSpawn(player);
            }
        });
    }

    @Override
    public void init() {
        super.init();
        if(unitTypes.isEmpty() && unitType != null){
            unitTypes.add(unitType);
        }
    }

    public class AquaCoreBuild extends CoreBuild{
        @Override
        public boolean collision(Bullet other){
            boolean wasDead = health <= 0;

            float damage = other.type.buildingDamage(other);
            if(!other.type.pierceArmor){
                damage = Damage.applyArmor(damage, block.armor);
            }

            damage(other, other.team, damage);

            if(health <= 0 && !wasDead){
                Events.fire(new EventType.BuildingBulletDestroyEvent(self(), other));
            }
            if(other.type().pierceBuilding){
                other.collided.add(this.id);
                other.collided.add(this.id);
                other.collided.add(this.id);
                other.collided.add(this.id);
                other.collided.add(this.id);
                other.collided.add(this.id);
                other.collided.add(this.id);
                other.collided.add(this.id);
                other.collided.add(this.id);
                other.collided.add(this.id);
            }
            return true;
        }

        @Override
        public void requestSpawn(Player player) {
            //do not try to respawn in unsupported environments at all
            if(!selectPlayerUnit().supportsEnv(state.rules.env) || !allowSpawn) return;


            configure(player.id);
        }

        public UnitType selectPlayerUnit(){
            for(UnitType type: unitTypes){
                if(type.supportsEnv(state.rules.env)){
                    return type;
                }
            }

            return unitType;
        }

        public void playerSpawn(Player player){
            UnitType spawnType = selectPlayerUnit();
            if(this.wasVisible){
                Fx.spawn.at(this);
            }

            player.set(this);

            if(!net.client()){
                Unit unit = spawnType.create(tile.team());
                //reset reload so that the player can't shoot immediately
                for(WeaponMount mount : unit.mounts){
                    mount.reload = mount.weapon.reload;
                }
                unit.set(this);
                unit.rotation(90f);
                unit.impulse(0f, 3f);
                unit.spawnedByCore(true);
                unit.controller(player);
                unit.add();
            }

            if(state.isCampaign() && player == Vars.player){
                spawnType.unlock();
            }
        }
        @Override
        public boolean acceptItem(Building source, Item item){
            return items.get(item) < itemCapacity && item.buildable;
        }
    }
}
