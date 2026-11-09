package aquarion.units.type;

import aquarion.world.graphics.AquaFx;
import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import mindustry.Vars;
import mindustry.entities.abilities.Ability;
import mindustry.game.Team;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.type.UnitType;

public class GerbCorpseType extends UnitType{
    private final String sprite;
    private TextureRegion corpseRegion;

    public GerbCorpseType(UnitType infantry, String sprite){
        super(infantry.name.substring("aquarion-".length()) + "-corpse");
        this.sprite = sprite;
        health = infantry.health * 2f;
        hitSize = infantry.hitSize;
        hidden = true;
        targetable = false;
        playerControllable = logicControllable = controlSelectGlobal = false;
        allowedInPayloads = isEnemy = useUnitCap = false;
        canDrown = createWreck = createScorch = physics = false;
        speed = knockbackMultiplier = fogRadius = deathShake = 0f;
        deathExplosionEffect = AquaFx.fleshyDeathSmall;

        infantry.abilities.add(new Ability(){
            { display = false; }

            @Override
            public void death(Unit unit){
                AquaFx.bloodPool.at(unit.x, unit.y, unit.rotation);
                if(Vars.net.client()) return;

                Unit corpse = create(Team.derelict);
                corpse.set(unit.x, unit.y);
                corpse.rotation = unit.rotation;
                corpse.add();
            }
        });
    }

    @Override
    public void load(){
        super.load();
        corpseRegion = Core.atlas.find("aquarion-" + sprite + "-corpse");
    }

    @Override
    public void draw(Unit unit){
        if(unit.inFogTo(Vars.player.team())) return;
        float layer = Draw.z();
        Draw.z(Layer.groundUnit - 1f);
        Draw.rect(corpseRegion, unit.x, unit.y, unit.rotation + 180f);
        Draw.z(layer);
    }
}
