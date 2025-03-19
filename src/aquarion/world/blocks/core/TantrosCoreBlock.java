package aquarion.world.blocks.core;

import arc.audio.Music;
import mindustry.gen.Musics;
import mindustry.world.blocks.storage.CoreBlock;

public class TantrosCoreBlock extends CoreBlock {
    public TantrosCoreBlock(String name) {
        super(name);
    }
    public class TantrosCoreBuild extends CoreBuild implements LaunchAnimator{
        @Override
        public void drawLaunch() {
            
        }

        @Override
        public void beginLaunch(boolean launching) {

        }

        @Override
        public void endLaunch() {

        }

        @Override
        public void updateLaunch() {

        }

        @Override
        public float launchDuration(){
            return 240;
        }

        @Override
        public Music landMusic(){
            return Musics.land;
        }

        @Override
        public Music launchMusic(){
            return Musics.launch;
        }

        @Override
        public float zoomLaunch() {
            return 0;
        }
    }
}
