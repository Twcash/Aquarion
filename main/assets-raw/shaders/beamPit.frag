#define HIGHP

varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform sampler2D u_noise;

uniform vec2 u_campos;
uniform vec2 u_resolution;
uniform float u_time;

float rand(vec2 co){
    return fract(sin(dot(co.xy, vec2(12.9898, 78.233))) * 43758.5453);
}

float beamPattern(vec2 uv, float spacing, float thickness, float orientation){
    float line;
    if(orientation < 0.5){
        line = step(0.5 - thickness * 0.5, abs(fract(uv.x * spacing) - 0.5));
    }else{
        line = step(0.5 - thickness * 0.5, abs(fract(uv.y * spacing) - 0.5));
    }
    return line;
}

void main(){
    vec4 base = texture2D(u_texture, v_texCoords);

    float mask = smoothstep(0.8, 1.0, dot(base.rgb, vec3(0.333)));

    vec3 bg = vec3(0.0);
    vec3 beamColor = vec3(0x3f, 0x40, 0x49) / 255.0; // #3f4049 gray

    float depthFactor = 1.0 - v_texCoords.y;

    vec3 color = bg;

    vec2 uv = v_texCoords * 8.0;

    for(int i = 0; i < 6; i++){
        vec2 seed = vec2(float(i) * 7.37, float(i) * 3.91);
        float spacing = mix(0.1, 1.0, rand(seed));
        float thickness = mix(0.1, 0.6, rand(seed + 0.12));
        float orientation = step(0.5, rand(seed + 0.28));
        vec2 offset = vec2(rand(seed + 0.7) * 10.0, rand(seed + 0.5) * 10.0);

        float depth = float(i + 1);
        vec2 layerUV = uv * mix(1.0, 0.5, depth * 0.1);
        layerUV += (u_campos * 0.01 * depth) + offset;

        float beam = beamPattern(layerUV, spacing, thickness, orientation);

        float brightness = mix(1.0, 0.0, depth * 0.19);
        vec3 layerColor = beamColor * brightness * beam;

        color = mix(color, layerColor, step(length(layerColor), length(color)) == 0.0 ? 1.0 : 0.0);
        color = max(color, layerColor);
    }

    vec3 finalColor = mix(base.rgb, color, mask);

    gl_FragColor = vec4(finalColor, 1.0);
}