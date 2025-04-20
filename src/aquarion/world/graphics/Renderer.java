package aquarion.world.graphics;

import java.util.HashMap;
import java.util.Map;

public class Renderer {
    public static final Renderer instance = new Renderer();

    private final Map<Object, Runnable> envRenderers = new HashMap<>();

    private Renderer() {}

    public void addEnvRenderer(Object env, Runnable renderer) {
        envRenderers.put(env, renderer);
    }

    public Map<Object, Runnable> getEnvRenderers() {
        return envRenderers;
    }
}