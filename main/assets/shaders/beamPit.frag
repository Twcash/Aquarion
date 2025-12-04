#define HIGHP


uniform sampler2D u_texture;
uniform vec2 u_campos;
uniform vec2 u_resolution;
uniform float u_time;

varying vec2 v_texCoords;

const float mscl = 40.0;
const float mth = 7.0;

float rand(vec2 co){
    return fract(sin(dot(co.xy, vec2(12.9898, 78.233))) * 43758.5453);
}

float beamPattern(vec2 uv, float spacing, float thickness, float orientation){
    float line;
    if(orientation < 0.5){
        line = step(0.5 - thickness * 0.15, abs(fract(uv.x * spacing) - 0.5));
    }else{
        line = step(0.5 - thickness * 0.15, abs(fract(uv.y * spacing) - 0.5));
    }
    return line;
}

void main(){
    vec2 c = v_texCoords;
    vec2 v = vec2(1.0/u_resolution.x, 1.0/u_resolution.y);
    vec2 coords = vec2(c.x / v.x + u_campos.x*1.01, c.y / v.y + u_campos.y*1.01);

    vec4 base = texture2D(u_texture, c);
    float mask = smoothstep(0.8, 1.0, dot(base.rgb, vec3(0.333)));

    vec3 beamColor = vec3(0x3f, 0x40, 0x49) / 255.0;
    vec3 structureColor = vec3(0.0);

    vec2 uv = coords / 10.0;

    for(int i = 0; i < 6; i++){
        vec2 seed = vec2(float(i) * 7.37, float(i) * 3.91);
        float spacing = mix(0.1, 1.0, rand(seed));
        float thickness = mix(0.1, 0.6, rand(seed + 0.12));
        float orientation = step(0.5, rand(seed + 0.28));
        vec2 offset = vec2(rand(seed + 0.7) * 10.0, rand(seed + 0.5) * 10.0);

        float depth = float(i + 1);
        vec2 layerUV = uv * mix(1.0, 0.5, depth * 0.1);
        layerUV += (u_campos * 0.001 * depth) + offset;

        float beam = beamPattern(layerUV, spacing, thickness, orientation);
        float brightness = mix(1.0, 0.0, depth * 0.19);
        vec3 layerColor = beamColor * brightness * beam;

        structureColor = max(structureColor, layerColor);
    }

    vec3 finalColor = mix(base.rgb, structureColor, mask);

    float stime = u_time / 5.0;

    vec4 sampled = texture2D(u_texture, c * v.x, 0.0);
    vec3 color = finalColor * vec3(0.9, 0.9, 1.0);

    gl_FragColor = vec4(color.rgb, min(sampled.a * 100.0, 1.0));
}