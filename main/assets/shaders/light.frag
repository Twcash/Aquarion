uniform sampler2D u_texture;
uniform vec4 u_ambient;
uniform vec2 u_texelsize;

varying vec2 v_texCoords;

const float ditherAmount = 2.0;
const float solidRange = 0.22;

float bayer4(vec2 pos){
    float x = mod(pos.x, 4.0);
    float y = mod(pos.y, 4.0);
    float v;
    if (y < 1.0)      v = x < 1.0 ? 0.0 : x < 2.0 ? 8.0 : x < 3.0 ? 2.0 : 10.0;
    else if (y < 2.0) v = x < 1.0 ? 12.0 : x < 2.0 ? 4.0 : x < 3.0 ? 14.0 : 6.0;
    else if (y < 3.0) v = x < 1.0 ? 3.0 : x < 2.0 ? 11.0 : x < 3.0 ? 1.0 : 9.0;
    else              v = x < 1.0 ? 15.0 : x < 2.0 ? 7.0 : x < 3.0 ? 13.0 : 5.0;
    return (v / 16.0) - 0.5;
}

const int blurRadius = 4;
const float blurStrength = 1.0;

vec4 blurLight(vec2 uv){
    vec4 sum = vec4(0.0);
    float total = 0.0;
    for(int y = -blurRadius; y <= blurRadius; y++){
        for(int x = -blurRadius; x <= blurRadius; x++){
            float d = float(x * x + y * y);
            float w = exp(-d * blurStrength);
            sum += texture2D(u_texture, uv + vec2(float(x), float(y)) * u_texelsize) * w;
            total += w;
        }
    }
    return sum / total;
}

void main(){

    vec4 light = blurLight(v_texCoords);

    float baseIntensity = light.a;

    float nearestEdge = min(
        min(abs(baseIntensity - 0.10), abs(baseIntensity - 0.35)),
        min(abs(baseIntensity - 0.60), abs(baseIntensity - 0.82))
    );

    float solid = clamp(nearestEdge / solidRange, 0.0, 1.0);

    float dither = bayer4(gl_FragCoord.xy) * ditherAmount * 0.25;

    float intensity = baseIntensity + dither * (1.0 - solid);

    float edge = 0.01;

    float band1 = smoothstep(
        0.10 - edge,
        0.10 + edge,
        intensity
    );

    float band2 = smoothstep(
        0.35 - edge,
        0.35 + edge,
        intensity
    );

    float band3 = smoothstep(
        0.60 - edge,
        0.60 + edge,
        intensity
    );

    float band4 = smoothstep(
        0.82 - edge,
        0.82 + edge,
        intensity
    );

    vec3 lightColor = light.rgb;

    float lightLum = dot(
        lightColor,
        vec3(0.299, 0.587, 0.114)
    );

    lightColor = mix(
        vec3(lightLum),
        lightColor,
        1.35
    );

    lightColor = clamp(
        lightColor,
        0.0,
        1.0
    );

    vec3 ambient = u_ambient.rgb;

    vec3 shadowTint = vec3(
        0.70,
        0.78,
        0.90
    );

    vec3 shadowColor =
        ambient * shadowTint * 0.55;

    float colorBoost = smoothstep(
        0.35,
        0.80,
        intensity
    );

    vec3 vividLight = mix(
        lightColor,
        mix(
            vec3(lightLum),
            lightColor,
            1.9
        ),
        colorBoost
    );

    vividLight = clamp(
        vividLight,
        0.0,
        1.0
    );

    vec3 midColor = mix(
        lightColor * 0.75,
        vividLight,
        0.45
    );

    vec3 brightColor =
        vividLight;

    vec3 highlightBase = mix(
        vec3(lightLum),
        light.rgb,
        2.4
    );

    highlightBase = clamp(
        highlightBase,
        0.0,
        1.0
    );

    vec3 highlightColor = mix(
        vividLight,
        highlightBase,
        0.75
    );

    vec3 finalColor;

    if (intensity < 0.10) {

        finalColor = shadowColor;

    }
    else if (intensity < 0.35) {

        finalColor = mix(
            shadowColor,
            midColor,
            band1
        );

    }
    else if (intensity < 0.60) {

        finalColor = mix(
            midColor,
            brightColor,
            band2
        );

    }
    else if (intensity < 0.82) {

        finalColor = mix(
            brightColor,
            highlightColor,
            band3
        );

    }
    else {

        finalColor = highlightColor;

    }

    float highlight = smoothstep(
        0.94,
        0.985,
        intensity
    );

    finalColor = mix(
        finalColor,
        highlightColor,
        highlight
    );

    float alpha =
        u_ambient.a - intensity;

    gl_FragColor = clamp(
        vec4(finalColor, alpha),
        0.0,
        1.0
    );
}