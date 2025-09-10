package aquarion.world.blocks.core;

import arc.Events;
import mindustry.entities.Damage;
import mindustry.game.EventType;
import mindustry.gen.Bullet;
import mindustry.world.blocks.storage.CoreBlock;

public class AquaCoreBlock extends CoreBlock {
    public AquaCoreBlock(String name) {
        super(name);
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
    }
}
