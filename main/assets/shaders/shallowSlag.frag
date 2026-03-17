#define HIGHP

//shades of slag
#define S0 vec3(100.0, 50.0, 25.0) / 100.0
#define S2 vec3(100.0, 93.0, 49.0) / 100.0
#define S1 vec3(100.0, 60.0, 25.0) / 100.0
#define S3 vec3(22.0, 22.0, 22.0) / 100.0
#define NSCALE 150.0 / 2.0

uniform sampler2D u_texture;
uniform sampler2D u_noise;

uniform vec2 u_campos;
uniform vec2 u_resolution;
uniform float u_time;

varying vec2 v_texCoords;

void main(){
    vec2 coords = v_texCoords * u_resolution + u_campos;

    float btime = u_time / 5000.0;
    vec4 orig = texture2D(u_texture, v_texCoords);
    float noise = (texture2D(u_noise, (coords) / NSCALE + vec2(btime) * vec2(-0.9, 0.8)).r + texture2D(u_noise, (coords) / NSCALE + vec2(btime * 1.1) * vec2(0.8, -1.0)).r) / 2.0;

    //TODO: pack noise texture
    vec2 c = v_texCoords + (vec2(
    texture2D(u_noise, (coords) / 170.0 + vec2(btime) * vec2(-0.9, 0.8)).r,
    texture2D(u_noise, (coords) / 170.0 + vec2(btime * 1.1) * vec2(0.8, -1.0)).r
    ) - vec2(0.5)) * 8.0 / u_resolution;
    float wave = abs(sin(coords.x * 1.1 + coords.y) + 0.1 * sin(2.5 * coords.x  + coords.y) + 0.15 * sin(3.0 * coords.x + coords.y)) / 120.0;
    float noises = wave + (texture2D(u_noise, (coords) / NSCALE + vec2(btime) * vec2(-0.2, -0.2)).r + texture2D(u_noise, (coords) /  NSCALE + vec2(btime * 1.2) * vec2(0.8, 0.8)).r) / 2.0;

    vec4 color = texture2D(u_texture, c);
    if(color.a < 0.55){
        color = orig;
    }

    if(noise > 0.6){
        color.rgb = S2;
    }else if(noise > 0.64){
        color.rgb = S1;
    }else if(noise > 0.48){
        color.rgb = S0;
    } else if(noise <= 0.6){
        color.rgb = S3;
        if( noises  >= 0.35 && !(noise <= 0.55)){
            color.rgb *= S2 * 1.5;
        } else if(noises  >= 0.55 && !(noise >= 0.55) && !(noise <= 0.4)){
            color.rgb *= 0.8;
        }
        if(noise <= 0.4){
            color.rgb *= 1.2;
            if( noises  >= 0.45){
            color.rgb *= 1.2;
        }
        }
    }

    gl_FragColor = color;
}