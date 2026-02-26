uniform sampler2D u_texture;
#define step 2.0
uniform vec2 u_campos;
uniform vec2 u_resolution;
uniform float u_time;

varying vec2 v_texCoords;

const float mscl = 40.0;
const float mth = 7.0;

void main(){

	vec2 c = v_texCoords;
    vec2 T = v_texCoords.xy;

	vec2 v = vec2(1.0/u_resolution.x, 1.0/u_resolution.y);
        vec4 maxed = max(max(max(texture2D(u_texture, T + vec2(0, step) * v), texture2D(u_texture, T + vec2(0, -step) * v)), texture2D(u_texture, T + vec2(step, 0) * v)), texture2D(u_texture, T + vec2(-step, 0) * v));

	vec2 coords = vec2(c.x / v.x + u_campos.x, c.y / v.y + u_campos.y);
	float stime = u_time / 5.0;

    vec4 sampled = texture2D(u_texture, c + vec2(sin(stime/3.0 + coords.y/2.0) * v.x,sin(stime/3.0 + coords.x/2.0) * v.y));
    vec3 color = sampled.rgb * vec3(0.9, 0.9, 1);
  	if(sampled.a > 0.0){
        sampled.a = 1.0;
    }
 	if(texture2D(u_texture, T).a < 0.9 && maxed.a > 0.9){

        gl_FragColor = vec4(maxed.rgb, maxed.a * 100.0);
    }
	gl_FragColor = vec4(color.rgb, min(sampled.a * 100.0, 1.0));
}
