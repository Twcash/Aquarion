package aquarion.entities.comp;
import aquarion.annotations.Annotations;
import aquarion.gen.NeoplasiaUpdaterc;
import aquarion.world.entities.NeoplasiaManager;
import aquarion.world.entities.NeoplasiaManager;
import mindustry.gen.Entityc;

@Annotations.EntityDef(value = NeoplasiaUpdaterc.class, serialize = false, genio = false)
@Annotations.EntityComponent
abstract class NeoplasiaUpdaterComp implements Entityc {
    public static NeoplasiaManager manager;
    @Override
    public void update(){
        //Stupid
        if(manager !=null) manager.update();
        if(manager !=null) manager.draw();

    }
}
