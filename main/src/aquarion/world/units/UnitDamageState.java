package aquarion.world.units;

import aquarion.world.graphics.AquaFx;
import arc.graphics.Color;
import arc.math.Angles;
import arc.math.Mathf;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;

public class UnitDamageState{
    public boolean enabled = true;
    public Effect firstEffect = Fx.smoke;
    public Effect secondEffect = AquaFx.spark1;
    public Color firstColor = Color.gray;
    public Color secondColor = Pal.ammo;
    public float firstChance = 0.08f;
    public float secondChance = 0.04f;
    public float firstPower = 2f;
    public float secondPower = 3f;
    public float radiusScale = 0.35f;

    public float firstChance(float healthFraction){
        return enabled ? Mathf.clamp(firstChance * Mathf.pow(1f - Mathf.clamp(healthFraction), firstPower)) : 0f;
    }

    public float secondChance(float healthFraction){
        return enabled ? Mathf.clamp(secondChance * Mathf.pow(1f - Mathf.clamp(healthFraction), secondPower)) : 0f;
    }

    public void update(Unit unit){
        if(Vars.headless || !enabled || unit.dead || !unit.isAdded() || unit.maxHealth <= 0f || unit.health >= unit.maxHealth) return;
        float health = unit.health / unit.maxHealth;
        if(firstEffect != null && Mathf.chanceDelta(firstChance(health))) emit(firstEffect, firstColor, unit);
        if(secondEffect != null && Mathf.chanceDelta(secondChance(health))) emit(secondEffect, secondColor, unit);
    }

    private void emit(Effect effect, Color color, Unit unit){
        float angle = Mathf.random(360f), radius = Mathf.sqrt(Mathf.random()) * unit.hitSize * radiusScale;
        effect.at(unit.x + Angles.trnsx(angle, radius), unit.y + Angles.trnsy(angle, radius), unit.rotation, color);
    }
}
