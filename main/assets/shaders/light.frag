uniform sampler2D u_texture;
uniform vec4 u_ambient;

varying vec2 v_texCoords;

void main(){

    vec4 light = texture2D(u_texture, v_texCoords);

    float intensity = light.a;

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