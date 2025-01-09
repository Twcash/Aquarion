#define HIGHP

#define NSCALE 180.0 / 2.0
#define PINK vec3(255, 105, 180) / 255.0   // Hot pink

uniform sampler2D u_texture;
uniform sampler2D u_noise;

uniform vec2 u_campos;
uniform vec2 u_resolution;
uniform float u_time;

varying vec2 v_texCoords;

void main(){
    vec2 c = v_texCoords.xy;
    vec2 coords = vec2(c.x * u_resolution.x + u_campos.x, c.y * u_resolution.y + u_campos.y);

    float btime = u_time / 8000.0;

    // Single noise layer for soft cloud-like movement
    float noise = (texture2D(u_noise, (coords) / NSCALE + vec2(btime) * vec2(-0.5, 0.6)).r + 
                   texture2D(u_noise, (coords) / NSCALE + vec2(btime * 1.2) * vec2(-0.6, -1.2)).r) / 2.0;

    vec4 color = texture2D(u_texture, c);

    // Apply the pink color based on noise value
    if(noise > 0.52 && noise < 0.56){
        color.rgb = PINK; // Hot pink areas
    }else if (noise > 0.47 && noise < 0.6){
        color.rgb = PINK; // Light pink areas
    }

    gl_FragColor = color;
}