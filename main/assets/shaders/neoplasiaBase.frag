uniform sampler2D u_texture;

#define step 2.0

uniform vec2 u_campos;
uniform vec2 u_resolution;
uniform float u_time;

varying vec2 v_texCoords;


void main(){

    vec2 c = v_texCoords;
    vec2 texel = 1.0 / u_resolution;
    vec2 disp = vec2(
        sin(u_time / 15.0 + (c.y / texel.y + u_campos.y) / 2.0) * texel.x,
        sin(u_time / 15.0 + (c.x / texel.x + u_campos.x) / 2.0) * texel.y
    );

    vec2 movingCoords = c + disp;

    vec4 center = texture2D(u_texture, movingCoords);

vec4 maxed = center;
for(int y = -1; y <= 1; y++){
    for(int x = -1; x <= 1; x++){
        vec2 offset = vec2(float(x), float(y)) * step * texel;
        vec4 s = texture2D(u_texture, movingCoords + offset);
        maxed = max(maxed, s);
    }
}

if(center.a <= 0.0 && maxed.a <= 0.0){
    gl_FragColor = vec4(0.0);
    return;
}

float baseAlpha = center.a > 0.0 ? 0.4 : 0.0;

// Outline alpha always 1.0
float dilatedAlpha = maxed.a > 0.0 && center.a == 0.0 ? 1.0 : maxed.a;

// Merge
float mergedAlpha = max(baseAlpha, dilatedAlpha);
vec3 basePM   = center.rgb * baseAlpha;
vec3 dilatePM = maxed.rgb * dilatedAlpha;

// Merge colors
vec3 mergedPM = max(basePM, dilatePM);

// Unpremultiply safely
vec3 finalColor = mergedAlpha > 0.0 ? mergedPM / mergedAlpha : vec3(0.0);

// Keep your existing outline darkening if needed
if(center.a == 0.0 && maxed.a >= 0.0){
    finalColor.rgb *= 0.8;
    maxed.a = 1.0;

    if(mergedAlpha == 0.0 && maxed.a > 0.0){
        mergedAlpha = 0.0;
    } else {
        mergedAlpha = 1.0;
    }
} else {
    mergedAlpha = 0.4;
}

gl_FragColor = vec4(finalColor, mergedAlpha);
}
