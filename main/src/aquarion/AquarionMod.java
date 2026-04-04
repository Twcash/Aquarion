package aquarion;

import aquarion.annotations.Annotations;
import aquarion.content.*;
import aquarion.content.blocks.*;
import aquarion.content.AquaLoadouts;
import aquarion.content.AquaPlanets;
import aquarion.content.AquaSectorPresets;
import aquarion.content.TantrosTechTree;
import aquarion.content.AquaUnitTypes;
import aquarion.world.content.AquaHints;
import arc.Events;
import arc.assets.Loadable;
import mindustry.content.Blocks;
import mindustry.game.EventType;

@Annotations.LoadRegs("error")// Need this temporarily, so the class gets generated.
@Annotations.EnsureLoad
public class AquarionMod  implements Loadable{


    public static void loadContent() {
        //stuff that needs to be loaded first
        AquaStatuses.load();
        AquaLiquids.loadContent();
        AquaSounds.load();
        AquaItems.load();
        AquaAttributes.load();
        EffectBlocks.loadContent();
        AquaWeathers.load();
        //actual content needs items liquids FX ect
        EnvironmentBlocks.loadContent();
        //SwapBlockId(EnvironmentBlocks.shallowSlag, Blocks.dirt);
        PowerBlocks.loadContent();
        LiquidBlocks.loadContent();
        DefenseBlocks.loadContent();
        TurretBlocks.loadContent();
        CrafterBlocks.loadContent();
;       EnvironmentBlocks.shallowSlag.asFloor().blendGroup = Blocks.slag;
        //units and cores, keep these after blocks
        AquaUnitTypes.loadContent();
        UnitBlocks.loadContent();
        CoreBlocks.loadContent();
        //distribution blocks need to be loaded here bc of unittypes
        DistributionBlocks.loadContent();
        //keep these at the back
        AquaLoadouts.load();
        //MenuReplacer.replaceMenu(ui.menufrag);
        AquaPlanets.loadContent();
        AquaSectorPresets.load();
        TantrosTechTree.load();

    }

//    public static AquaMenuRenderer getMenuRenderer() {
//        try {
//            return Reflect.get(MenuFragment.class, ui.menufrag, "renderer");
//        } catch (Exception ex) {
//            Log.err("Failed to return renderer", ex);
//            return new AquaMenuRenderer();
//        }
//
//    }
}