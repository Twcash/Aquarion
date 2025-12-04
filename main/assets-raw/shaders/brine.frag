#define HIGHP

#define NSCALE 450.0
#define S1 vec3(	239, 239, 233) / 255.0
#define S2 vec3(	242, 243, 240) / 255.0

#define S3 vec3(207, 215, 187) / 255.0
#define S4 vec3(217, 226, 184) / 255.0

#define S5 vec3(	181, 192, 153) / 255.0
#define S6 vec3(	160, 170, 122) / 255.0

uniform sampler2D u_texture;
uniform sampler2D u_noise;

uniform vec2 u_campos;
uniform vec2 u_resolution;
uniform float u_time;

varying vec2 v_texCoords;

void main(){
    vec2 c = v_texCoords.xy;
    vec2 coords = vec2(c.x * u_resolution.x + u_campos.x, c.y * u_resolution.y + u_campos.y);

    float btime = u_time / 2400.0;

    // First noise layer with original scale
    float noise1 = (texture2D(u_noise, (coords) /( NSCALE*1.3) + vec2(btime) * vec2(-0.9, 0.8)).r + 
                    texture2D(u_noise, (coords) /( NSCALE*1.3) + vec2(btime * 1.1) * vec2(-0.8, -1.0)).r) / 2.0;

    // Second noise layer, scaled differently
    float noise2 = (texture2D(u_noise, (coords*1.1) / (NSCALE * 1.2) + vec2(btime) * vec2(1.0, -0.5)).r + 
                    texture2D(u_noise, (coords*1.1) / (NSCALE * 1.2) + vec2(btime * 1.2) * vec2(0.6, 1.0)).r) / 2.0;

    // Third noise layer, even smaller scale
    float noise3 = (texture2D(u_noise, (coords*1.2) / (NSCALE * 1.0) + vec2(btime) * vec2(-0.7, 0.9)).r + 
                    texture2D(u_noise, (coords*1.2) / (NSCALE * 1.0) + vec2(btime * -0.9) * vec2(0.4, -0.8)).r) / 2.0;

    vec4 color = texture2D(u_texture, c);

    if(noise1 > 0.54 && noise1 < 0.57){
        color.rgb = mix(color.rgb, S2, noise1); 
    }else if (noise1 > 0.49 && noise1 < 0.62){
        color.rgb = mix(color.rgb, S1, noise1); 
    }
    if(noise2 > 0.54 && noise2 < 0.57){
        color.rgb = mix(color.rgb, S4, noise2); 
    }else if (noise2 > 0.49 && noise2 < 0.62){
        color.rgb = mix(color.rgb, S3, noise2); 
    }

	gl_FragColor = color;
}