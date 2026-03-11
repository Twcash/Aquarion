#define HIGHP

//shades of slag
#define S3 vec3(22.0, 22.0, 22.0) / 100.0
#define S1 vec3(255.0/255.0, 88.0/255.0, 88.0/255.0)
#define S2 vec3(255.0/255.0, 166.0/255.0, 166.0/255.0)
#define S0 vec3(190.0/255.0, 35.0/255.0, 35.0/255.0)
#define NSCALE 200.0 / 2.0

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

    vec4 color = texture2D(u_texture, c);
    if(color.a < 0.55){
        color = orig;
    }

    if(noise > 0.6){
        color.rgb = S2;
    }else if(noise > 0.54){
        color.rgb = S1;
    }else if(noise > 0.42){
        color.rgb = S0;
    } else if(noise <= 0.6){
        color.rgb = S3;
        if(noise <= 0.35){
            color.rgb *= 1.2;
        }
    }

    gl_FragColor = color;
}