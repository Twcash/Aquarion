package aquarion.content.blocks;

import mindustry.content.Blocks;
import mindustry.content.Liquids;
import mindustry.content.StatusEffects;
import mindustry.entities.bullet.LiquidBulletType;
import mindustry.world.blocks.defense.turrets.LiquidTurret;
import mindustry.entities.bullet.BulletType;
import mindustry.graphics.Layer;

import static aquarion.content.AquaLiquids.*;
import static aquarion.content.AquaStatuses.*;

public class vannilaturret {
    public static void loadContent() {

        LiquidTurret waveBlock = (LiquidTurret) Blocks.wave;

        waveBlock.ammoTypes.put(Liquids.water, new LiquidBulletType(Liquids.water) {{
            lifetime = 49f;
            speed = 4f;
            knockback = 1.7f;
            puddleSize = 8f;
            orbSize = 4f;
            drag = 0.001f;
            ammoMultiplier = 0.4f;
            statusDuration = 60f * 4f;
            damage = 0.2f;
            layer = Layer.bullet - 2f;
        }});
        waveBlock.ammoTypes.put(clearwater, new LiquidBulletType(clearwater) {{
            lifetime = 49f;
            speed = 7f;
            knockback = 1.7f;
            puddleSize = 8f;
            orbSize = 4f;
            drag = 0.001f;
            ammoMultiplier = 0.4f;
            statusDuration = 60f * 4f;
            damage = 0.2f;
            layer = Layer.bullet - 2f;
        }});
        waveBlock.ammoTypes.put(Liquids.slag, new LiquidBulletType(Liquids.slag) {{
            lifetime = 49f;
            speed = 4f;
            knockback = 1.3f;
            puddleSize = 8f;
            orbSize = 4f;
            damage = 4.75f;
            drag = 0.001f;
            ammoMultiplier = 0.4f;
            statusDuration = 60f * 4f;
        }});
        waveBlock.ammoTypes.put(Liquids.cryofluid, new LiquidBulletType(Liquids.cryofluid) {{
            lifetime = 49f;
            speed = 4f;
            knockback = 1.3f;
            puddleSize = 8f;
            orbSize = 4f;
            drag = 0.001f;
            ammoMultiplier = 0.4f;
            statusDuration = 60f * 4f;
            damage = 0.2f;
        }});
        waveBlock.ammoTypes.put(Liquids.oil, new LiquidBulletType(Liquids.oil) {{
            lifetime = 49f;
            speed = 4f;
            knockback = 1.3f;
            puddleSize = 8f;
            orbSize = 4f;
            drag = 0.001f;
            ammoMultiplier = 0.4f;
            statusDuration = 60f * 4f;
            damage = 0.2f;
            layer = Layer.bullet - 2f;
        }});
        waveBlock.ammoTypes.put(petroleum, new LiquidBulletType(petroleum) {{
            lifetime = 52f;
            speed = 3.5f;
            knockback = 1.5f;
            puddleSize = 9f;
            orbSize = 4f;
            drag = 0.001f;
            status = StatusEffects.tarred;
            ammoMultiplier = 0.4f;
            statusDuration = 60f * 8f;
            damage = 0.8f;
            layer = Layer.bullet - 2f;
        }});
        waveBlock.ammoTypes.put(magma, new LiquidBulletType(magma) {{
            lifetime = 49f;
            speed = 4f;
            knockback = 0.5f;
            puddleSize = 9f;
            orbSize = 4f;
            drag = 0.001f;
            status = StatusEffects.melting;
            ammoMultiplier = 0.8f;
            statusDuration = 60f * 5f;
            damage = 5f;
            layer = Layer.bullet - 2f;
        }});
        waveBlock.ammoTypes.put(haze, new LiquidBulletType(haze) {{
            lifetime = 49f;
            speed = 7f;
            knockback = 0.5f;
            puddleSize = 9f;
            reloadMultiplier = 5f;
            orbSize = 4f;
            drag = 0.001f;
            status = StatusEffects.burning;
            ammoMultiplier = 0.8f;
            statusDuration = 60f * 2f;
            damage = 1f;
            layer = Layer.bullet - 2f;
        }});

        LiquidTurret tsunamiBlock = (LiquidTurret) Blocks.tsunami;

        tsunamiBlock.ammoTypes.put(Liquids.water, new LiquidBulletType(Liquids.water) {{
            lifetime = 49f;
            speed = 6f;
            knockback = 2.8f;
            puddleSize = 14f;
            orbSize = 5.5f;
            drag = 0.001f;
            ammoMultiplier = 0.4f;
            statusDuration = 60f * 4f;
            damage = 0.5f;
            layer = Layer.bullet - 2f;
        }});
        tsunamiBlock.ammoTypes.put(clearwater, new LiquidBulletType(clearwater) {{
            lifetime = 49f;
            speed = 9f;
            knockback = 2.8f;
            puddleSize = 14f;
            orbSize = 5.5f;
            drag = 0.001f;
            ammoMultiplier = 0.4f;
            statusDuration = 60f * 4f;
            damage = 0.5f;
            layer = Layer.bullet - 2f;
        }});
        tsunamiBlock.ammoTypes.put(Liquids.slag, new LiquidBulletType(Liquids.slag) {{
            lifetime = 49f;
            speed = 6f;
            knockback = 2.2f;
            puddleSize = 14f;
            orbSize = 5.5f;
            damage = 9.5f;
            drag = 0.001f;
            ammoMultiplier = 0.4f;
            statusDuration = 60f * 4f;
        }});
        tsunamiBlock.ammoTypes.put(Liquids.cryofluid, new LiquidBulletType(Liquids.cryofluid) {{
            lifetime = 49f;
            speed = 6f;
            knockback = 2.2f;
            puddleSize = 14f;
            orbSize = 5.5f;
            drag = 0.001f;
            ammoMultiplier = 0.4f;
            statusDuration = 60f * 4f;
            damage = 0.5f;
        }});
        tsunamiBlock.ammoTypes.put(Liquids.oil, new LiquidBulletType(Liquids.oil) {{
            lifetime = 49f;
            speed = 6f;
            knockback = 2.2f;
            puddleSize = 14f;
            orbSize = 5.5f;
            drag = 0.001f;
            ammoMultiplier = 0.4f;
            statusDuration = 60f * 4f;
            damage = 0.5f;
            layer = Layer.bullet - 2f;
        }});
        tsunamiBlock.ammoTypes.put(petroleum, new LiquidBulletType(petroleum) {{
            lifetime = 52f;
            speed = 5.5f;
            knockback = 2.5f;
            puddleSize = 16f;
            orbSize = 5.5f;
            drag = 0.001f;
            status = StatusEffects.tarred;
            ammoMultiplier = 0.4f;
            statusDuration = 60f * 8f;
            damage = 1.8f;
            layer = Layer.bullet - 2f;
        }});
        tsunamiBlock.ammoTypes.put(magma, new LiquidBulletType(magma) {{
            lifetime = 49f;
            speed = 6f;
            knockback = 1.2f;
            puddleSize = 16f;
            orbSize = 5.5f;
            drag = 0.001f;
            status = StatusEffects.melting;
            ammoMultiplier = 0.8f;
            statusDuration = 60f * 5f;
            damage = 11f;
            layer = Layer.bullet - 2f;
        }});
        tsunamiBlock.ammoTypes.put(haze, new LiquidBulletType(haze) {{
            lifetime = 49f;
            speed = 9f;
            knockback = 1.2f;
            puddleSize = 16f;
            reloadMultiplier = 5f;
            orbSize = 5.5f;
            drag = 0.001f;
            status = StatusEffects.burning;
            ammoMultiplier = 0.8f;
            statusDuration = 60f * 2f;
            damage = 3.5f;
            layer = Layer.bullet - 2f;
        }});
    }
}