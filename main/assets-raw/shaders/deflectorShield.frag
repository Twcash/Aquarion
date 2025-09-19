#define ALPHA 0.42
#define STEP 2.0

uniform sampler2D u_texture;
uniform vec2 u_texsize;
uniform vec2 u_invsize;
uniform float u_time;
uniform vec2 u_offset;

varying vec2 v_texCoords;

float smoothNoise(vec2 st){
    return (sin(st.x) * cos(st.y)) * 0.5 + 0.5;
}

void main(){
    vec2 uv = v_texCoords.xy;
    vec2 coords = uv * u_texsize + u_offset;
    vec2 v = u_invsize;

    // Sample 8 neighbors for soft outline
    float sumA = 0.0;
    sumA += texture2D(u_texture, uv + vec2( STEP,  0.0) * v).a;
    sumA += texture2D(u_texture, uv + vec2(-STEP,  0.0) * v).a;
    sumA += texture2D(u_texture, uv + vec2(0.0,  STEP) * v).a;
    sumA += texture2D(u_texture, uv + vec2(0.0, -STEP) * v).a;
    sumA += texture2D(u_texture, uv + vec2( STEP,  STEP) * v).a;
    sumA += texture2D(u_texture, uv + vec2(-STEP,  STEP) * v).a;
    sumA += texture2D(u_texture, uv + vec2( STEP, -STEP) * v).a;
    sumA += texture2D(u_texture, uv + vec2(-STEP, -STEP) * v).a;

    float centerA = texture2D(u_texture, uv).a;
    float smoothAlpha = max(centerA, sumA * 0.125);

    // Edge fading
    float dist = distance(uv, vec2(0.5));
    float edgeFade = smoothstep(0.45, 0.5, dist);

    // Techy energy
    vec2 dir1 = vec2(sin(coords.y / 18.0 + u_time / 90.0), cos(coords.x / 20.0 + u_time / 20.0));
    vec2 dir2 = vec2(cos(coords.y / 24.0 - u_time / 64.0), sin(coords.x / 30.0 + u_time / 18.0));

    float energy1 = smoothNoise(coords / 30.0 + u_time / 70.0 + dir1 * 3.0);
    float energy2 = smoothNoise(coords / 50.0 - u_time / 40.0 + dir2 * 1.0);

    float energy = mix(energy1, energy2, 0.7);
    energy = smoothstep(0.24, 0.7, energy);

    // Final fog color
    vec4 color = vec4(vec3(0.7, 0.9, 1.0) * energy, ALPHA * smoothAlpha * (1.0 - edgeFade));
    
    gl_FragColor = color;
}
