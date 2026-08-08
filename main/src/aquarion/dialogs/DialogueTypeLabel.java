package aquarion.dialogs;

import arc.scene.event.Touchable;
import arc.scene.ui.Label;
import arc.scene.ui.Label.LabelStyle;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;

/** A wrapped label that reveals its text one character at a time, terminal style.
 *  Markup color tags are kept intact so partially revealed text never shows a broken tag.
 *  Inline speed markers adjust the reveal rate: {@code ~1.1~} sets a 1.1x multiplier,
 *  {@code ~~} resets it to 1x. */
public class DialogueTypeLabel extends Table {
    public final Label label;
    private final Seq<String> tags = new Seq<>();
    private final Seq<String> chunks = new Seq<>();
    private final Seq<Float> speeds = new Seq<>();
    private final Runnable onProgress;

    /** Base characters revealed per second (before speed markers). */
    public float speed = 48f;
    private float time;
    private int lastShown = -1;
    private boolean done = true;

    public DialogueTypeLabel(String text, LabelStyle style, Runnable onProgress){
        this.onProgress = onProgress;
        label = new Label("", style);
        label.setWrap(true);
        label.touchable = Touchable.disabled;
        add(label).growX().left();
        setText(text);
    }

    /** Splits the text into visible chunks, remembering each chunk's color tag and speed. */
    public void setText(String text){
        tags.clear();
        chunks.clear();
        speeds.clear();
        if(text != null) parseMarkup(text);
        time = 0f;
        lastShown = -1;
        done = chunks.isEmpty();
        label.setText("");
    }

    private int totalChars(){
        int n = 0;
        for(String c : chunks) n += c.length();
        return n;
    }

    private String fullText(){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < chunks.size; i++){
            sb.append(tags.get(i)).append(chunks.get(i));
        }
        return sb.toString();
    }

    /** Builds the text with the first {@code count} characters revealed. */
    private String revealedText(int count){
        StringBuilder sb = new StringBuilder();
        int remaining = count;
        for(int i = 0; i < chunks.size && remaining > 0; i++){
            String c = chunks.get(i);
            if(remaining >= c.length()){
                sb.append(tags.get(i)).append(c);
                remaining -= c.length();
            }else{
                sb.append(tags.get(i)).append(c, 0, remaining);
                remaining = 0;
            }
        }
        return sb.toString();
    }

    @Override
    public void act(float delta){
        super.act(delta);
        if(done) return;
        time += delta;
        int shown = countAtTime(time);
        if(shown >= totalChars()){
            shown = totalChars();
            done = true;
        }
        if(shown != lastShown){
            lastShown = shown;
            label.setText(done ? fullText() : revealedText(shown) + "_");
        }
        if(onProgress != null) onProgress.run();
    }

    /** How many characters are revealed after {@code t} seconds, respecting per-chunk speeds. */
    private int countAtTime(float t){
        int count = 0;
        for(int i = 0; i < chunks.size; i++){
            float dur = chunks.get(i).length() / (speed * speeds.get(i));
            if(t >= dur){
                count += chunks.get(i).length();
                t -= dur;
            }else{
                count += (int)(t * speed * speeds.get(i));
                break;
            }
        }
        return count;
    }

    /** True once the whole text has been revealed. */
    public boolean isDone(){
        return done;
    }

    private void parseMarkup(String text){
        StringBuilder visible = new StringBuilder();
        String current = "";
        float currentSpeed = 1f;
        int i = 0, n = text.length();
        while(i < n){
            char c = text.charAt(i);
            if(c == '['){
                int end = text.indexOf(']', i + 1);
                if(end == -1){
                    visible.append('[');
                    i++;
                    continue;
                }
                String tag = text.substring(i + 1, end);
                if(tag.isEmpty()){
                    flush(current, currentSpeed, visible);
                    current = "";
                }else if(tag.startsWith("[")){
                    visible.append('[');
                }else{
                    flush(current, currentSpeed, visible);
                    current = "[" + tag + "]";
                }
                i = end + 1;
            }else if(c == '~'){
                int end = text.indexOf('~', i + 1);
                if(end == -1){
                    visible.append('~');
                    i++;
                    continue;
                }
                String val = text.substring(i + 1, end);
                if(val.isEmpty()){
                    flush(current, currentSpeed, visible);
                    currentSpeed = 1f;
                }else{
                    try{
                        float f = Float.parseFloat(val);
                        flush(current, currentSpeed, visible);
                        currentSpeed = f;
                    }catch(NumberFormatException e){
                        visible.append('~').append(val).append('~');
                    }
                }
                i = end + 1;
            }else{
                visible.append(c);
                i++;
            }
        }
        flush(current, currentSpeed, visible);
    }

    private void flush(String tag, float speed, StringBuilder visible){
        if(visible.length() > 0){
            tags.add(tag);
            speeds.add(speed);
            chunks.add(visible.toString());
            visible.setLength(0);
        }
    }
}
