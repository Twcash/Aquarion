uniform sampler2D u_texture; 
uniform vec2 u_resolution;
uniform float u_time;

varying vec2 v_texCoords;

// === Helpers ===
float hash(vec2 p){
    p = fract(p * vec2(234.34, 435.345));
    p += dot(p, p + 34.23);
    return fract(p.x * p.y);
}

float noise(vec2 p){
    vec2 i = floor(p);
    vec2 f = fract(p);
    float a = hash(i);
    float b = hash(i + vec2(1.0, 0.0));
    float c = hash(i + vec2(0.0, 1.0));
    float d = hash(i + vec2(1.0, 1.0));
    vec2 u = f * f * (3.0 - 2.0 * f);
    return mix(a, b, u.x) + (c - a) * u.y * (1.0 - u.x) + (d - b) * u.x * u.y;
}

float fbm(vec2 p){
    float v = 0.0;
    float a = 0.5;
    for(int i = 0; i < 5; i++){
        v += a * noise(p);
        p *= 2.1;
        a *= 0.5;
    }
    return v;
}

void main(){
    vec2 uv = v_texCoords;
    vec4 base = texture2D(u_texture, uv);

    float m = clamp(base.a, 0.0, 1.0);
    if(m < 0.001){
        gl_FragColor = base;
        return;
    }

    float t = u_time + u_time/4.0 * 100.0;

    float n1 = fbm(uv * 30.0 + t * 1.5);
    float n2 = fbm(uv * 80.0 - t * 2.0);
    float tear = (n1 - n2) * 0.15 * m;

    float timeJump = step(0.9, fract(sin(t * 8.7) * 43758.5)) * 0.03 * m;

    float lineY = fract(uv.y * 80.0 + t * 10.0);
    float lineMask = smoothstep(0.45, 0.55, lineY);
    float tearLine = (noise(vec2(t * 20.0, uv.y * 200.0)) - 0.5) * lineMask * 0.25 * m;

    vec2 disp = vec2(tear + tearLine + timeJump, 0.0);

    float chroma = 0.015 * m;
    vec4 colR = texture2D(u_texture, uv + disp + vec2(chroma, 0.0));
    vec4 colG = texture2D(u_texture, uv + disp);
    vec4 colB = texture2D(u_texture, uv + disp - vec2(chroma, 0.0));
    vec4 color = vec4(colR.r, colG.g, colB.b, 1.0);

    float scan = sin((uv.y * u_resolution.y + t * 100.0) * 0.25) * 0.5 + 0.5;
    float scanFlicker = mix(1.0, 0.6, scan * m);
    color.rgb *= scanFlicker;

    float flash = smoothstep(0.8, 1.0, noise(uv * 50.0 + vec2(t * 5.0, -t * 3.0)));
    color.rgb += flash * 0.5 * m;

    float staticNoise = (hash(gl_FragCoord.xy + floor(t * 60.0)) - 0.5) * 0.3 * m;
    color.rgb += staticNoise;

    gl_FragColor = mix(base, color, m);
}
