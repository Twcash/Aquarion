package aquarion.world.meta;

import aquarion.world.graphs.RTGraph;
import aquarion.world.interfaces.HasRT;
import aquarion.world.meta.AquaStat;
import arc.*;
import arc.graphics.*;
import arc.math.Mathf;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.meta.*;

public class RTConfig {
 public boolean outputsRT, acceptsRT = true;
    Color color = Color.valueOf("92dd7e");
    public void addBars(Block block) {
        block.addBar("rotation-power", b -> {
            HasRT build = (HasRT) b;
            return new Bar(
                    () -> Core.bundle.get("rotation-power", "Rotation Power") + ": " + build.rTGraph().getTotalRotationPower(),
                    () -> color.cpy().lerp(color, build.rTGraph().getTotalRotationPower() / 100f),
                    () -> build.rTGraph().getTotalRotationPower() / 100f
            );
        });
    }

}