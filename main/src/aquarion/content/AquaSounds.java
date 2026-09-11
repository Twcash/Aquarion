package aquarion.content;

import arc.Core;
import arc.assets.AssetDescriptor;
import arc.assets.loaders.SoundLoader;
import arc.audio.Sound;
import mindustry.Vars;

public class AquaSounds {
    public static Sound
            fan1 = new Sound(),
            machine1 =new Sound(),
            machine2 =new Sound(),
            machine3 =new Sound(),
            machine4 = new Sound(),
            machine5 = new Sound(),
            machine6 = new Sound(),
            machine7 = new Sound(),
            machine8 = new Sound(),
            machine9 = new Sound(),
            engine1 = new Sound(),
            researchLabloop = new Sound(),
            researchLabVoid = new Sound(),
            shootMissileHuge = new Sound(),
            knightStep = new Sound(),
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
            refine = new Sound(),
            electricExplosion = new Sound();

    public static void load(){
        shootMissileHuge = loadSound("shootMissileHuge");
        electricExplosion = loadSound("electricExplosion");
        knightStep = loadSound("mechStepKnight");
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
        machine1 = loadSound("machine1");
        machine2 = loadSound("machine2");
        machine3 = loadSound("machine3");
        machine4 =  loadSound("machine4");
        machine5 =  loadSound("machine5");
        machine6 =  loadSound("machine6");
        machine7 =  loadSound("machine7");
        machine8 =  loadSound("machine8");
        machine9 =  loadSound("machine9");
        engine1 =  loadSound("engine1");
        fan1 =  loadSound("fan1");
        researchLabloop =  loadSound("ResearchLabLoop");
        researchLabVoid =  loadSound("researchLabVoid");

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