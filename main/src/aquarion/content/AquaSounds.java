package aquarion.content;

import arc.Core;
import arc.assets.AssetDescriptor;
import arc.assets.loaders.SoundLoader;
import arc.audio.Sound;
import mindustry.Vars;

public class AquaSounds {
    public static Sound
            shootGrace = new Sound(),
            engage = new Sound(),
            advance = new Sound(),
            hold = new Sound(),
            rally = new Sound(),
            retreat = new Sound(),
            monsoon = new Sound(),
            thunder = new Sound(),
            //underwater1 = new Sound(),
            vectorShot = new Sound(),
            start = new Sound(),
            start2 = new Sound(),
            //start3 = new Sound(),
            start4 = new Sound(),
            start5 = new Sound(),
            waterRumble = new Sound(),
                    shootAftershock = new Sound(),
            waterAir = new Sound(),
            //engine = new Sound(),
            //electrolysis = new Sound(),
            //turbulent = new Sound(),
            //turbulent2 = new Sound(),
            waterHum = new Sound(),
            //waterHum2 = new Sound(),
            derrick = new Sound(),
            //compressDrill = new Sound(),
            //compressDrillImpact = new Sound(),
            refine = new Sound();

    public static void load(){
        shootGrace = loadSound("shootGrace");
        engage = loadSound("engage");
        advance = loadSound("advance");
        hold = loadSound("hold");
        rally = loadSound("rally");
        retreat = loadSound("retreat");
        monsoon = loadSound("monsoon");
        //underwater1 = loadSound("underwater");
        vectorShot = loadSound("vectorShot");
        start = loadSound("start");
        start2 = loadSound("start2");
        shootAftershock = loadSound("shootAftershock");
        //start3 = loadSound("start3");
        start4 = loadSound("start4");
        start5 = loadSound("start5");
        derrick = loadSound("derrick");
        thunder = loadSound("thunder");

        //waterRumble = loadSound("waterRumble");
        //waterAir = loadSound("waterAir");
        //engine = loadSound("engine");
        //electrolysis = loadSound("electrolysis");
        //turbulent = loadSound("turbulent");
        //turbulent2 = loadSound("turbulent2");
        waterHum = loadSound("waterHum");
        //waterHum2 = loadSound("waterHum2");
        //compressDrill = loadSound("compressDrill");
        //compressDrillImpact = loadSound("compressDrillImpact");
        refine = loadSound("refine");
    }

    private static Sound loadSound(String soundName){
        if(!Vars.headless) {
            String name = "sounds/" + soundName;
            String path = Vars.tree.get(name + ".ogg").exists() ? name + ".ogg" : name + ".mp3";

            Sound sound = new Sound();

            AssetDescriptor<?> desc = Core.assets.load(path, Sound.class, new SoundLoader.SoundParameter(sound));
            desc.errored = Throwable::printStackTrace;

            return sound;

        }else{
            return new Sound();
        }
    }
}