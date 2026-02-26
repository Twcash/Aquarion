uniform sampler2D u_texture;

#define step 2.0

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

    for(int y=-1; y<=1; y++){
        for(int x=-1; x<=1; x++){
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

    vec2 c = v_texCoords;
    vec2 texel = 1.0 / u_resolution;

    vec2 coords = vec2(
        c.x / texel.x + u_campos.x,
        c.y / texel.y + u_campos.y
    );

    float stime = u_time / 5.0;

    vec2 disp = vec2(
        sin(stime / 3.0 + coords.y / 2.0) * texel.x,
        sin(stime / 3.0 + coords.x / 2.0) * texel.y
    );

    vec2 movingCoords = c + disp;

    vec4 center = texture2D(u_texture, movingCoords);

    float vscale = 0.12;
    float vnoise = voronoi(coords * 0.05 * vscale + stime * 0.02);

    float vmask = smoothstep(0.6, 0.3, vnoise);

    float baseAlpha = center.a * vmask;

    vec4 maxed = center;

    for(int y = -1; y <= 1; y++){
        for(int x = -1; x <= 1; x++){
            vec2 offset = vec2(float(x), float(y)) * step * texel;
            vec4 s = texture2D(u_texture, movingCoords + offset);
            maxed = max(maxed, s);
        }
    }
	
    float dilatedAlpha = maxed.a;
    float mergedAlpha = max(baseAlpha, dilatedAlpha);
    if(center.a == 0.0 && mergedAlpha > 0.0){
        center.a = 1.0;
    }

    vec3 finalColor = maxed.rgb;
	if(center.a == 0.0 && mergedAlpha > 0.0){
        finalColor *= 0.1;
    }
    gl_FragColor = vec4(finalColor, center.a);
}