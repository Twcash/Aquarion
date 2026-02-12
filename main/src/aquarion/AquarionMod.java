package aquarion;

import aquarion.annotations.Annotations;
import aquarion.content.*;
import aquarion.content.blocks.*;
import aquarion.content.AquaLoadouts;
import aquarion.content.AquaPlanets;
import aquarion.content.AquaSectorPresets;
import aquarion.content.TantrosTechTree;
import aquarion.content.AquaUnitTypes;
import aquarion.content.WreckUnits;
import aquarion.world.content.AquaHints;
import aquarion.world.graphics.AquaFx;
import arc.Events;
import arc.assets.Loadable;
import mindustry.Vars;
import mindustry.entities.Effect;
import mindustry.game.EventType;
import mindustry.world.Block;

import java.util.Objects;

@Annotations.LoadRegs("error")// Need this temporarily, so the class gets generated.
@Annotations.EnsureLoad
public class AquarionMod  implements Loadable{
    public static AquaHints hints = new AquaHints();
    public static void loadContent() {
        Events.on(EventType.ClientLoadEvent.class, e -> {
            clientLoaded();
        });
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
        PowerBlocks.loadContent();
        LiquidBlocks.loadContent();
        DefenseBlocks.loadContent();
        TurretBlocks.loadContent();
        CrafterBlocks.loadContent();

        //units and cores, keep these after blocks
        WreckUnits.loadContent();
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
    public static void clientLoaded(){
        hints.load();
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