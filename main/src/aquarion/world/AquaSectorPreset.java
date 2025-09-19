package aquarion.world;

import arc.Core;
import arc.scene.style.TextureRegionDrawable;
import mindustry.type.Planet;
import mindustry.type.SectorPreset;

public class AquaSectorPreset extends SectorPreset {

    public TextureRegionDrawable icon = null;

    public AquaSectorPreset(String name, Planet planet, int sector) {
        super(name, planet, sector);
    }

    @Override
    public void load() {
        super.load();
        icon = Core.atlas.getDrawable(name);
    }
    @Override
    public void loadIcon(){
        uiIcon = fullIcon =  Core.atlas.find(name);
    }
}
