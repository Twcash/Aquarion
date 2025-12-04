package aquarion.world.blocks.core;

import arc.audio.Music;
import mindustry.gen.Musics;

public interface LaunchAnimator{

    void drawLaunch();

    default void drawLaunchGlobalZ(){}

    void beginLaunch(boolean launching);

    void endLaunch();

    void updateLaunch();

    float launchDuration();

    default Music landMusic(){
        return Musics.land;
    }

    default Music launchMusic(){
        return Musics.launch;
    }

    float zoomLaunch();
}
