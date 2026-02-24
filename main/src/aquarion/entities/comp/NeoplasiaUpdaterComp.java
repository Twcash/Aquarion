package aquarion.entities.comp;
import aquarion.gen.NeoplasmManagerc;
import aquarion.annotations.Annotations;
import aquarion.world.entities.NeoplasmManager;
import mindustry.gen.Entityc;

@Annotations.EntityDef(value = NeoplasmManagerc.class, serialize = false, genio = false)
@Annotations.EntityComponent
public class NeoplasiaUpdaterComp implements Entityc {
    public transient NeoplasmManager manager;
    @Override
    public void update(){
        manager.update();
    }
}
