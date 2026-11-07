package aquarion.world.campaign;

import arc.Core;
import arc.graphics.Colors;
import arc.struct.FloatSeq;
import mindustry.Vars;
import java.util.ArrayList;

/** Reveals text with markup, pauses, corrections, and persistent shake/speed declarations. */
class DialogueTypewriter {
    private final String source;
    private final StringBuilder visible = new StringBuilder();
    private final FloatSeq shakes = new FloatSeq();
    private float shake, speed;
    private int cursor;
    private double budget, pause;
    private final ArrayList<Integer> characterStarts = new ArrayList<>();
    private DialogueTypewriter correction;
    private String replacement;
    private int correctionStart, correctionPhase;

    DialogueTypewriter(String value){
        source = localize(value);
    }

    static String localize(String value){
        if(value == null) return "";
        if(!value.startsWith("@")) return value;
        String key = value.substring(1);
        if(Vars.state != null && Vars.state.mapLocales != null && Vars.state.mapLocales.containsProperty(key)){
            return Vars.state.mapLocales.getProperty(key);
        }
        return Core.bundle == null ? value : Core.bundle.get(key, value);
    }

    void update(float seconds, float rate){
        budget += Math.max(0f, seconds);
        while(true){
            if(pause > 0){
                double consumed = Math.min(pause, budget);
                pause -= consumed;
                budget -= consumed;
                if(pause > 0) return;
            }
            if(correction != null){
                if(correctionPhase == 1){
                    // Slow dramatic typing must not make a correction appear stuck on its draft.
                    double interval = 1d / Math.max(rate, correction.rate(rate));
                    while(!correction.characterStarts.isEmpty() && budget >= interval){
                        budget -= interval;
                        correction.visible.setLength(correction.characterStarts.remove(correction.characterStarts.size() - 1));
                        correction.shakes.truncate(correction.visible.length());
                    }
                    visible.setLength(correctionStart);
                    shakes.truncate(correctionStart);
                    if(!correction.characterStarts.isEmpty()){
                        appendCorrection();
                        return;
                    }
                    correction = child(replacement);
                    correctionPhase = 2;
                }
                correction.budget = budget;
                correction.update(0f, rate);
                budget = correction.budget;
                visible.setLength(correctionStart);
                shakes.truncate(correctionStart);
                appendCorrection();
                if(!correction.isComplete()) return;
                if(correctionPhase == 0){
                    correctionPhase = 1;
                    pause = 0.5f;
                    continue;
                }
                shake = correction.shake;
                speed = correction.speed;
                for(int start : correction.characterStarts) characterStarts.add(correctionStart + start);
                correction = null;
                continue;
            }
            if(cursor >= source.length()) return;
            char next = source.charAt(cursor);
            if(next == '{'){
                int end = closingBrace(source, cursor);
                if(end > cursor && source.substring(cursor + 1, end).matches("[0-9]+(?:\\.[0-9]+)?")){
                    float duration = Float.parseFloat(source.substring(cursor + 1, end));
                    if(Float.isFinite(duration)){
                        cursor = end + 1;
                        pause = duration;
                        continue;
                    }
                }
                if(end > cursor){
                    String edit = source.substring(cursor + 1, end);
                    if(declaration(edit.trim())){
                        cursor = end + 1;
                        continue;
                    }
                    edit = edit.trim();
                    if(edit.startsWith("typo:")) edit = edit.substring(5).trim();
                    int comma = correctionComma(edit);
                    if(comma >= 0){
                        correction = child(edit.substring(0, comma).trim());
                        replacement = edit.substring(comma + 1).trim();
                        correctionStart = visible.length();
                        correctionPhase = 0;
                        cursor = end + 1;
                        continue;
                    }
                }
            }
            boolean escapedBracket = source.startsWith("[[", cursor);
            if(next == '[' && !escapedBracket){
                int end = source.indexOf(']', cursor + 1);
                if(end >= 0){
                    String tag = source.substring(cursor + 1, end);
                    if(tag.isEmpty() || tag.matches("#[0-9a-fA-F]{6}(?:[0-9a-fA-F]{2})?") || Colors.get(tag) != null){
                        visible.append(source, cursor, end + 1);
                        while(shakes.size < visible.length()) shakes.add(0f);
                        cursor = end + 1;
                        continue;
                    }
                }
            }
            double interval = 1d / rate(rate);
            if(budget < interval) return;
            budget -= interval;
            int end = cursor + (escapedBracket ? 2 : Character.charCount(source.codePointAt(cursor)));
            characterStarts.add(visible.length());
            visible.append(source, cursor, end);
            while(shakes.size < visible.length()) shakes.add(shake);
            cursor = end;
        }
    }

    private static int closingBrace(String value, int start){
        int depth = 0;
        for(int i = start; i < value.length(); i++){
            char ch = value.charAt(i);
            if(ch == '{') depth++;
            if(ch == '}' && --depth == 0) return i;
        }
        return -1;
    }

    private static int correctionComma(String value){
        int depth = 0;
        for(int i = 0; i < value.length(); i++){
            char ch = value.charAt(i);
            if(ch == '{') depth++;
            if(ch == '}') depth--;
            if(ch == ',' && depth == 0) return i;
        }
        return -1;
    }

    private float rate(float fallback){
        return speed > 0f ? speed : fallback;
    }

    private DialogueTypewriter child(String value){
        DialogueTypewriter result = new DialogueTypewriter(value);
        result.shake = shake;
        result.speed = speed;
        return result;
    }

    private void appendCorrection(){
        visible.append(correction.visible);
        shakes.addAll(correction.shakes);
    }

    private boolean declaration(String value){
        int colon = value.indexOf(':');
        if(colon < 0) return false;
        String name = value.substring(0, colon).trim();
        String argument = value.substring(colon + 1).trim();
        if(!name.equals("shake") && !name.equals("speed") && !name.equals("pause")) return false;
        boolean reset = argument.equals("reset");
        float number;
        try{
            number = reset ? 0f : Float.parseFloat(argument);
        }catch(NumberFormatException ignored){
            return false;
        }
        if(!Float.isFinite(number) || number < 0f || (name.equals("speed") && number == 0f && !reset)) return false;
        if(name.equals("shake")) shake = number;
        if(name.equals("speed")) speed = number;
        if(name.equals("pause")) pause = number;
        return true;
    }

    float shakeAt(int index){
        return index >= 0 && index < shakes.size ? shakes.get(index) : 0f;
    }

    boolean hasShake(){
        for(int i = 0; i < shakes.size; i++){
            if(shakes.get(i) > 0f) return true;
        }
        return false;
    }

    boolean isComplete(){
        return cursor >= source.length() && pause <= 0 && correction == null;
    }

    String text(){
        return visible.toString();
    }
}
