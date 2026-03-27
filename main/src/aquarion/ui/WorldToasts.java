package aquarion.ui;

import arc.struct.Seq;

public class WorldToasts {

    public static Seq<WorldToast> toasts = new Seq<>();

    public static void show(String text, float worldX, float worldY){
        WorldToast t = new WorldToast(text, worldX, worldY);
        toasts.add(t);
    }

    public static void clear(){
        for(WorldToast t : toasts){
            t.removeToast();
        }
        toasts.clear();
    }
}