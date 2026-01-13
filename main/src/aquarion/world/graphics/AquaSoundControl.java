package aquarion.world.graphics;

import arc.math.Mathf;
import mindustry.Vars;
import mindustry.audio.SoundControl;

import static mindustry.Vars.player;
import static mindustry.Vars.state;

public class AquaSoundControl extends SoundControl {

    @Override
    public void playRandom(){
        if(state.boss() != null){
            playOnce(bossMusic.random(lastRandomPlayed));
        }else if(darkQuestionMark()){
            playOnce(darkMusic.random(lastRandomPlayed));
        }else{
            playOnce(ambientMusic.random(lastRandomPlayed));
        }
    }

    protected boolean darkQuestionMark(){

        if(player.team().data().hasCore() && player.team().data().core().healthf() < 0.85f && state.wave > 10){
            //core damaged -> dark
            return true;
        }

        //it may be dark based on wave
        if(Mathf.chance((float)(Math.log10((state.wave - 17f)/19f) + 1) / 4f)){
            return true;
        }

        //dark based on enemies
        return Mathf.chance(state.enemies / 120f);
    }
}
