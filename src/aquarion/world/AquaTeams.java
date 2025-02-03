package aquarion.world;

import arc.graphics.Color;
import mindustry.game.Team;

public class AquaTeams {
    //Credits to Slotterfleet / team oct as I heavily referenced that (I also tooka big chunk for the icon loading)
    public static Team tendere, gerb, wrecks;

    public static void load() {
        tendere = newTeam(69, "tendere", Color.valueOf("d57761"));
        gerb = newTeam(70, "gerb", Color.valueOf("8381ff"));
        wrecks = newTeam(71, "wrecks", Color.valueOf("3a2a2a"));
    }
    private static Team newTeam(int id, String name, Color color) {Team team = Team.get(id);
        team.name = name;
        team.color.set(color);

        team.palette[0] = color;
        team.palette[1] = color.cpy().mul(0.75f);
        team.palette[2] = color.cpy().mul(0.5f);

        for(int i = 0; i < 3; i++){
            team.palettei[i] = team.palette[i].rgba();
        }
        return team;
    }

}