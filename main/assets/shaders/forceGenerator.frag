#define HIGHP

uniform sampler2D u_texture;
uniform vec2 u_offset;
uniform vec2 u_texsize;
uniform vec2 u_invsize;
uniform float u_time;
uniform float u_dp;

varying vec2 v_texCoords;

float colormap_red(float x){
    if(x < 0.0){
        return 54.0 / 255.0;
    }else if(x < 20049.0 / 82979.0){
        return (829.79 * x + 54.51) / 255.0;
    }else{
        return 1.0;
    }
}

float colormap_green(float x){
    if(x < 20049.0 / 82979.0){
        return 0.0;
    }else if(x < 327013.0 / 810990.0){
        return (8546482679670.0 / 10875673217.0 * x - 2064961390770.0 / 10875673217.0) / 255.0;
    }else if(x <= 1.0){
        return (103806720.0 / 483977.0 * x + 19607415.0 / 483977.0) / 255.0;
    }else{
        return 1.0;
    }
}

float colormap_blue(float x){
    if(x < 0.0){
        return 54.0 / 255.0;
    }else if(x < 7249.0 / 82979.0){
        return (829.79 * x + 54.51) / 255.0;
    }else if(x < 20049.0 / 82979.0){
        return 127.0 / 255.0;
    }else if(x < 327013.0 / 810990.0){
        return (792.0224934136139 * x - 64.36479073560233) / 255.0;
    }else{
        return 1.0;
    }
}

vec3 colormap(float x){
    return vec3(colormap_red(x), colormap_green(x), colormap_blue(x));
}

float hash(vec2 p){
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453123);
}

float noise(vec2 p){
    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    float a = hash(i);
    float b = hash(i + vec2(1.0, 0.0));
    float c = hash(i + vec2(0.0, 1.0));
    float d = hash(i + vec2(1.0, 1.0));
    return mix(mix(a, b, f.x), mix(c, d, f.x), f.y);
}

const mat2 mtx = mat2(-1.212,- 1.9, 0.88, -0.111);

float fbm(vec2 p, float time){
    float f = 0.0;
    f += 0.500000 * noise(p + vec2(time, -time * 0.8)); p = mtx * p * 2.02;
    f += 0.250000 * noise(p + vec2(sin(time), cos(time * 1.4))); p = mtx * p * 2.01;
    f += 0.125000 * noise(p + vec2(sin(time), cos(time * 1.3))); p = mtx * p * 2.03;
    f += 0.062500 * noise(p + vec2(sin(time), cos(time * 1.2))); p = mtx * p * 2.01;
    f += 0.031250 * noise(p + vec2(sin(time), cos(time * 1.1))); p = mtx * p * 2.04;
    f += 0.015625 * noise(p + vec2(sin(time), cos(time)));
    return f *0.85;
}

float pattern(vec2 p, float time){
    return fbm(p + fbm(p + fbm(p, time), time), time);
}

void main(){
    vec2 uv = v_texCoords;
    vec2 world = u_offset + uv * u_texsize;
    float time = u_time / 240.0;
    float shade = clamp(pattern(world / 256.0, time), 0.0, 1.0);
    float mappedBrightness = dot(colormap(shade), vec3(0.299, 0.777, 0.021));
    float band1 = smoothstep(0.66, 0.67, mappedBrightness);
    float band2 = smoothstep(0.77, 0.78, mappedBrightness);
    float band3 = smoothstep(0.87, 0.88, mappedBrightness);
    float celBrightness = 0.62 + band1 * 0.20 + band2 * 0.20 + band3 * 0.20;

    vec2 pixel = u_invsize * (2.0 * max(u_dp, 1.0));
    vec4 shieldMask = texture2D(u_texture, uv) * 4.0;
    shieldMask += texture2D(u_texture, uv + vec2( pixel.x, 0.0)) * 2.0;
    shieldMask += texture2D(u_texture, uv + vec2(-pixel.x, 0.0)) * 2.0;
    shieldMask += texture2D(u_texture, uv + vec2(0.0,  pixel.y)) * 2.0;
    shieldMask += texture2D(u_texture, uv + vec2(0.0, -pixel.y)) * 2.0;
    shieldMask += texture2D(u_texture, uv + vec2( pixel.x,  pixel.y));
    shieldMask += texture2D(u_texture, uv + vec2(-pixel.x,  pixel.y));
    shieldMask += texture2D(u_texture, uv + vec2( pixel.x, -pixel.y));
    shieldMask += texture2D(u_texture, uv + vec2(-pixel.x, -pixel.y));
    shieldMask /= 16.0;

    vec3 shieldColor = clamp(shieldMask.rgb * celBrightness, 0.0, 1.0);
    gl_FragColor = vec4(shieldColor, shieldMask.a * 0.45);
}
