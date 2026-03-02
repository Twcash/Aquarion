uniform sampler2D u_texture;
uniform sampler2D u_noises;

#define S2 vec3(35.0, 225.0, 33.0) / 100.0
#define NSCALE 500.0

uniform vec2 u_campos;
uniform vec2 u_resolution;
uniform float u_time;

varying vec2 v_texCoords;

float hash(vec2 p){
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453123);
}

float voronoi(vec2 uv){
    vec2 i = floor(uv);
    vec2 f = fract(uv);
    float minDist = 1.0;

    for(int y = -1; y <= 1; y++){
        for(int x = -1; x <= 1; x++){
            vec2 neighbor = vec2(float(x), float(y));
            vec2 point = vec2(hash(i + neighbor), hash(i + neighbor + 1.0));
            vec2 diff = neighbor + point - f;
            float dist = length(diff);
            minDist = min(minDist, dist);
        }
    }
    return minDist;
}

void main(){
    vec2 worldPos = u_campos + v_texCoords * u_resolution;

    float stime = u_time / 5.0;

    vec2 disp = vec2(
        sin(stime / 3.0 + worldPos.y / 2.0) / u_resolution.x,
        sin(stime / 3.0 + worldPos.x / 2.0) / u_resolution.y
    );

    vec2 movingCoords = v_texCoords + disp;

    vec4 center = texture2D(u_texture, movingCoords);

    vec4 maxed = center;

    float baseAlpha = center.a > 0.0 ? 0.4 : 0.0;
    float dilatedAlpha = maxed.a > 0.0 && center.a == 0.0 ? 1.0 : maxed.a;

    float mergedAlpha = max(baseAlpha, dilatedAlpha);

    vec3 basePM   = center.rgb * baseAlpha;
    vec3 dilatePM = maxed.rgb * dilatedAlpha;
    vec3 mergedPM = max(basePM, dilatePM);

    vec3 finalColor = mergedAlpha > 0.0 ? mergedPM / mergedAlpha : vec3(0.0);

    if(center.a == 0.0 && maxed.a >= 0.0){
        finalColor *= 0.8;
        if(mergedAlpha == 0.0 && maxed.a > 0.0){
            mergedAlpha = 0.0;
        }else{
            mergedAlpha = 1.0;
        }
    }else{
        mergedAlpha = 0.4;
    }

    float btime = u_time / 5000.0;

    vec2 noiseUV1 = worldPos / NSCALE + vec2(btime) * vec2(-0.9, 0.8);
    vec2 noiseUV2 = worldPos / NSCALE * 1.1 + vec2(btime * 1.1) * vec2(0.8, -1.0);

    float noises = (
        texture2D(u_noises, noiseUV1).r +
        texture2D(u_noises, noiseUV2).r
    ) * 0.5;

    if(noises <= 0.49 && center.a > 0.0){
        mergedAlpha = 0.0;
    }
	if(center.a == 0.0){
        mergedAlpha = 0.0;
    }if(center.a == 0.0) center.a = 1.0;
    
    finalColor = S2;

    gl_FragColor = vec4(finalColor, mergedAlpha);
}