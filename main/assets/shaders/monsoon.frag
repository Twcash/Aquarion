#define HIGHP
#define NSCALE 2400.0 / 1.8

uniform sampler2D u_texture;
uniform sampler2D u_noise;

uniform vec2 u_campos;
uniform vec2 u_resolution;
uniform float u_time;
uniform float u_intensity;

varying vec2 v_texCoords;

void main(){
    vec2 c = v_texCoords.xy;

	vec2 coords = vec2(c.x * u_resolution.x + u_campos.x, c.y * u_resolution.y + u_campos.y);

    float btime = u_time / 900.0;
vec4 baseColor = texture2D(u_texture, coords);
    float n1 = texture2D(u_noise, coords / NSCALE + btime * vec2(-3.0, 0.8)).r;
    float n2 = texture2D(u_noise, coords / NSCALE + btime * vec2(-4.0, 1.0)).r;
    float n3 = texture2D(u_noise, coords / (NSCALE * 5.0) + btime * vec2(-8.0, 1.1)).r;
    float n4 = texture2D(u_noise, coords / (NSCALE * 2.0) + btime * vec2(-12.0, 3.1)).r;
    float combined = (n1 + n2 + n3 + n4) * 0.25;

   vec3 fogColor = mix(baseColor.rgb, vec3(0.16, 0.12, 0.2), combined * 1.8);
    gl_FragColor = vec4(fogColor, u_intensity);
}


