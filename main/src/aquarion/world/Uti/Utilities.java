package aquarion.world.Uti;

public class Utilities {
    //Divide by tick-speed without float errors.... Hopefully
    public static float stupid(float y1){
        return Math.round(y1/60f);
    };
    //For 0.5 0.25 ect. In which I rarely use and should never be an issue
    public static float stupider(float y1, float interval){
        return roundToInterval((float) (y1/60f), interval);
    };
    public static float roundToInterval(float value, float interval){
        return Math.round(value / interval) * interval;
    }
}
