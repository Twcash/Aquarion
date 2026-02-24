package aquarion.entities.comp;
import aquarion.gen.NeoplasmManagerc;
import aquarion.annotations.Annotations;
import aquarion.world.entities.NeoplasmManager;

@Annotations.EntityDef(value = NeoplasmManagerc.class, serialize = false, genio = false)
@Annotations.EntityComponent
public class NeoplasiaUpdaterComp {
    public transient NeoplasmManager manager;
    @Override
    public void update(){
        manager.update();
    }
}
