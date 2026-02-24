package aquarion.entities.comp;

import aquarion.annotations.Annotations;

@Annotations.EntityDef(value = NeoplasiaUpdaterc.class, serialize = false, genio = false)
@Annotations.EntityComponent
public class NeoplasiaUpdaterComp {
    public transient NeoplasmManager = manager;
    @Override
    public void update(){
        manager.update;
    }
}
