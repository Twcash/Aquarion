package aquarion.entities.comp;

import aquarion.annotations.Annotations;
import aquarion.gen.AquaBuilderc;
import aquarion.gen.AquaUnitc;
import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.ai.types.*;
import mindustry.async.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.ctype.*;
import mindustry.entities.*;
import mindustry.entities.abilities.*;
import mindustry.entities.units.*;
import mindustry.game.EventType.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.meta.*;
import aquarion. annotations. Annotations.*;
import static mindustry.Vars.*;
import static mindustry.logic.GlobalVars.*;
//@EntityDef(value = AquaUnitc.class, genio = true, serialize = true)
@EntityComponent(base = true)
abstract class AquaUnitComp implements Healthc, Physicsc, Hitboxc, Statusc, Teamc, Itemsc, Rotc, Unitc, Weaponsc, Drawc, Syncc, Shieldc, Displayable, Ranged, Minerc, AquaBuilderc, Senseable, Settable{

}