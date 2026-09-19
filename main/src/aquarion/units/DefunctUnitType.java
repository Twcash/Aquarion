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
    public String[] idleLines;
    public float idleMinInterval = 900f, idleMaxInterval = 2700f;
    public String[] nearLines;
    public float nearRange = 80f, nearCooldown = 600f;
    public String[] specialLines;
    public String specialSector;
}
