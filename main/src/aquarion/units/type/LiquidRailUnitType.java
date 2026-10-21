package aquarion.units.type;

import aquarion.world.units.LiquidRailUnit;
import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import mindustry.gen.Unit;

public class LiquidRailUnitType extends RailUnitType{
    public float liquidCapacity = 120f;
    public TextureRegion liquidRegion;

    public LiquidRailUnitType(String name){
        super(name);
        constructor = LiquidRailUnit::new;
        itemCapacity = 0;
        // Register the entity before saves or network packets are read.
        new LiquidRailUnit();
    }

    @Override public void load(){
        super.load();
        liquidRegion = Core.atlas.find(name + "-liquid");
    }

    @Override public void update(Unit unit){
        super.update(unit);
        if(unit instanceof LiquidRailUnit tank){
            if(tank.cargoLiquid != null) tank.displayLiquid = tank.cargoLiquid;
            float target = tank.cargoLiquid == null ? 0f : Mathf.clamp(tank.liquidAmount / liquidCapacity);
            tank.displayFill = Mathf.lerpDelta(tank.displayFill, target, 0.1f);
            if(Math.abs(tank.displayFill - target) < 0.0001f) tank.displayFill = target;
            if(tank.displayFill == 0f) tank.displayLiquid = null;
        }
    }

    @Override public void drawBody(Unit unit){
        super.drawBody(unit);
        if(unit instanceof LiquidRailUnit tank && tank.displayLiquid != null && liquidRegion.found()){
            Draw.color(tank.displayLiquid.color, tank.displayFill);
            Draw.rect(liquidRegion, unit.x, unit.y, unit.rotation - 90f);
            Draw.reset();
        }
    }
}
