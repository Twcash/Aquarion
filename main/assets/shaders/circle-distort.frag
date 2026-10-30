varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform vec2 u_camera;
uniform vec2 u_worldSize;
uniform vec2 u_center;
uniform float u_radius;
uniform float u_strength;
uniform float u_time;
uniform float u_smoke;

float hash(vec2 p){
    // Wrap lattice corners, including the neighbor across each tile edge.
    p = mod(p, 32.0);
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);
}

float noise(vec2 p){
    vec2 cell = floor(p), f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    return mix(mix(hash(cell), hash(cell + vec2(1.0, 0.0)), f.x),
        mix(hash(cell + vec2(0.0, 1.0)), hash(cell + vec2(1.0)), f.x), f.y);
}

float caustic(vec2 p){
    // Integer harmonics keep every octave periodic over the same 32-cell tile.
    return noise(p) * 0.6 + noise(p * 2.0) * 0.28 + noise(p * 4.0) * 0.12;
}

void main(){
    vec2 world = u_camera + v_texCoords * u_worldSize;
    vec2 delta = world - u_center;
    float distance = length(delta);
    if(u_smoke > 0.5){
        vec2 p = world / 38.0 + vec2(u_time * 0.006, -u_time * 0.003);
        float field = caustic(p);
        float mask = (1.0 - smoothstep(u_radius * 0.4, u_radius, distance)) * u_strength;
        vec2 flow = vec2(caustic(p + vec2(5.2, 1.3)), caustic(p + vec2(-1.7, 9.2))) - 0.5;
        vec2 warped = clamp(v_texCoords + flow * 42.0 * mask / u_worldSize, vec2(0.001), vec2(0.999));
        vec4 smoke = texture2D(u_texture, warped);
        // Subtle moving ridges break up the flat puff silhouettes without adding fog.
        smoke.rgb *= 1.0 + (field - 0.5) * 0.28 * mask;
        gl_FragColor = smoke;
        return;
    }
    float band = (distance - u_radius) / min(60.0, 14.0 + u_radius * 0.08);
    float envelope = exp(-band * band * 2.0);
    vec2 direction = delta / max(distance, 0.001);
    float displacement = sin(band * 3.14159265) * envelope * 24.0 * u_strength;
    vec2 uv = clamp(v_texCoords + direction * displacement / u_worldSize, vec2(0.001), vec2(0.999));
    vec4 scene = texture2D(u_texture, uv);
    scene.rgb += vec3(0.035, 0.028, 0.015) * envelope * u_strength;
    gl_FragColor = scene;
}
