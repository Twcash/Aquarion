package aquarion.entities.comp;

import aquarion.annotations.Annotations.EntityComponent;
import aquarion.annotations.Annotations.Replace;
import aquarion.annotations.Annotations.Wrap;
import mindustry.gen.Bullet;
import mindustry.gen.Hitboxc;
import mindustry.gen.Unitc;

@EntityComponent
abstract class GerbCorpseComp implements Unitc{
    @Wrap(value = "update()", block = Unitc.class)
    public boolean updateAsLivingUnit(){
        // Corpses have no controller or environmental upkeep, even on captured sectors.
        return false;
    }

    @Replace(1)
    @Override
    public boolean checkTarget(boolean air, boolean ground){
        return air || ground;
    }

    @Override
    public void collision(Hitboxc other, float x, float y){
        if(!(other instanceof Bullet bullet) || bullet.damage <= 0f) return;
        if(bullet.type.collides && !bullet.type.pierce && !bullet.type.sticky) return;

        // The default path already stops ordinary bullets. Stop piercing rounds
        // and prevent line attacks from damaging targets further along the line.
        bullet.type.hitEntity(bullet, self(), health());
        bullet.damage = 0f;
        if(bullet.type.collides && bullet.isAdded()){
            bullet.hit = true;
            bullet.remove();
        }
    }
}
