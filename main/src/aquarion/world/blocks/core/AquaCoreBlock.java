package aquarion.world.blocks.core;

import arc.Events;
import arc.struct.Seq;
import aquarion.world.items.AquaItemEntity;
import arc.util.io.Reads;
import arc.util.io.Writes;
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

    public class AquaCoreBuild extends CoreBuild implements AquaItemEntity.Container{
        private final Seq<AquaItemEntity> trackedItems = new Seq<>();

        @Override
        public Seq<AquaItemEntity> aquaItemEntities(){
            return trackedItems;
        }

        @Override
        public void handleItem(Building source, Item item){
            int before = items.get(item);
            super.handleItem(source, item);
            if(items.get(item) > before) AquaItemEntity.addIfTracked(trackedItems, AquaItemEntity.capture(source, item));
        }

        @Override
        public void updateTile(){
            AquaItemEntity.update(trackedItems, items, edelta());
            super.updateTile();
        }

        @Override
        public byte version(){
            return (byte)(super.version() + 1);
        }

        @Override
        public void write(Writes write){
            super.write(write);
            AquaItemEntity.write(write, trackedItems);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            if(revision > super.version()) AquaItemEntity.read(read, trackedItems);
            AquaItemEntity.reconcile(items, trackedItems);
        }

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
            if(player == null || !allowSpawn) return;
            if(net.client()){
                configure(player.id);
            }else{
                playerSpawn(player);
            }
        }

        public UnitType selectPlayerUnit(){
            SpecializationCatalyst.specBuild catalyst = getCatalyst();
            return catalyst == null ? defaultPlayerUnit() : catalyst.unit();
        }

        public UnitType defaultPlayerUnit(){
            for(UnitType type: unitTypes){
                if(type.supportsEnv(state.rules.env)){
                    return type;
                }
            }

            return unitType;
        }
        public UnitType getUnit(){
            SpecializationCatalyst.specBuild catalyst = getCatalyst();
            return catalyst == null ? null : catalyst.unit();
        }

        public SpecializationCatalyst.specBuild getCatalyst(){
            SpecializationCatalyst.specBuild selected = null;
            for(Building other : proximity){
                if(other instanceof SpecializationCatalyst.specBuild catalyst && other.team == team && catalyst.canSpawn()
                    && (selected == null || catalyst.pos() < selected.pos())){
                    selected = catalyst;
                }
            }
            return selected;
        }
        public void playerSpawn(Player player){
            // Configuration is forwarded to clients too; only the server creates and pays for units.
            if(net.client() || player == null || player.team() != team || !player.dead() || !allowSpawn) return;
            SpecializationCatalyst.specBuild catalyst = getCatalyst();
            if(catalyst != null && !catalyst.requestRespawn(player, this)){
                player.set(this);
                return;
            }
            UnitType spawnType = catalyst == null ? defaultPlayerUnit() : catalyst.unit();
            if(spawnType == null || !spawnType.supportsEnv(state.rules.env)) return;
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
                if(catalyst != null) catalyst.finishSpawn();
            }

            if(state.isCampaign() && player == Vars.player){
                spawnType.unlock();
            }
        }
        @Override
        public boolean acceptItem(Building source, Item item){
            return items.get(item) < storageCapacity&& item.buildable;
        }
    }
}
