#define HIGHP

uniform sampler2D u_texture;

uniform vec2 u_campos;
uniform vec2 u_resolution;
uniform float u_time;

varying vec2 v_texCoords;

const float mscl = 40.0;
const float mth =2.0;

void main(){

	vec2 c = v_texCoords;
	vec2 v = vec2(1.0/u_resolution.x, 1.0/u_resolution.y);
	vec2 coords = vec2(c.x / v.x + u_campos.x, c.y / v.y + u_campos.y);

	float stime = u_time/6.0;
	float colora = texture2D(u_texture, c).a;
vec4 sampled = texture2D(u_texture, c + vec2(
    sin(stime / 4.0 + coords.y / 2.0) * v.x*1.1*colora,
    sin(stime / 4.0 + coords.x / 2.0) * v.y*1.1*colora 
));
    vec3 color = sampled.rgb * vec3(0.9, 0.9, 1);

	gl_FragColor = vec4(color.rgb, min(sampled.a * 100.0, 1.0));
}

