package aquarion.units;

import mindustry.type.UnitType;

public class DefunctUnitType extends UnitType {
    public DefunctUnitType(String name) {
        super(name);
    }
    public String[] spawnLines;
    public String[] hurtLines;
    public String[] deathLines;
    public String[] victorLines;
}
