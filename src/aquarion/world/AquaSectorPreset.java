package aquarion.world;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import arc.scene.style.TextureRegionDrawable;
import mindustry.graphics.g3d.PlanetGrid;
import mindustry.type.Planet;
import mindustry.type.Sector;
import mindustry.type.SectorPreset;

import javax.swing.*;

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
