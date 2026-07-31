uniform sampler2D u_texture;
uniform vec4 u_ambient;

varying vec2 v_texCoords;

void main(){

    vec4 light = texture2D(u_texture, v_texCoords);

    float intensity = light.a;

    float edge = 0.025;

    float band1 = smoothstep(
        0.20 - edge,
        0.20 + edge,
        intensity
    );

    float band2 = smoothstep(
        0.45 - edge,
        0.45 + edge,
        intensity
    );

    float band3 = smoothstep(
        0.70 - edge,
        0.70 + edge,
        intensity
    );

    float band =
        band1 * 0.33 +
        band2 * 0.33 +
        band3 * 0.34;

    band = clamp(band, 0.0, 1.0);

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

    lightColor = clamp(lightColor, 0.0, 1.0);
    vec3 ambient = u_ambient.rgb;
    vec3 finalColor = mix(
        ambient,
        lightColor,
        band
    );

    float fullLight = smoothstep(
        0.75,
        0.95,
        intensity
    );

    finalColor = mix(
        finalColor,
        light.rgb,
        fullLight
    );

    float alpha = u_ambient.a - intensity;

    gl_FragColor = clamp(
        vec4(finalColor, alpha),
        0.0,
        1.0
    );
}