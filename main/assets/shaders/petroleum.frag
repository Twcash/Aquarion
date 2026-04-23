#define HIGHP
#define NSCALE 180.0 / 2.0

uniform sampler2D u_texture;

uniform vec2 u_campos;
uniform vec2 u_resolution;
uniform float u_time;
uniform sampler2D u_noise;

varying vec2 v_texCoords;

const float scale = 80.0;
//"borrowed" fractal noise because I didn't want the vanilla noise.
float hash(vec2 p){
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);
}


void main(){

    vec2 c = v_texCoords.xy;
    vec2 px = vec2(1.0/u_resolution.x, 1.0/u_resolution.y);
    vec2 coords = vec2(c.x * u_resolution.x + u_campos.x, c.y * u_resolution.y + u_campos.y);

    float btime = u_time / 3000.0;
    float noise = (texture2D(u_noise, (coords) / NSCALE + vec2(btime) * vec2(-0.9, 0.8)).r + texture2D(u_noise, (coords) / NSCALE + vec2(btime * 1.1) * vec2(-0.8, -1.0)).r) / 2.0;
    float t = u_time * 0.02;

    vec2 flow = vec2(
        sin(coords.y +t* 0.05) * 2.0,
        cos(coords.x +t * 0.04* 0.7) * 2.0
    );

    vec2 uv = c + flow * px * 2.0;

    vec4 sampled = texture2D(u_texture, uv);

    float n1 = noise;
    float n2 = noise * 0.2;
    float blobs = mix(n1, n2, 0.6);

    vec3 oilColor = vec3(0.05, 0.02, 0.025);

	if(blobs >= 0.9){
       oilColor = vec3(0.09, 0.04, 0.07);
    }
    float sheen = sin((coords.x + coords.y) * 0.01 + t * 2.0) * 0.5 + 0.5;
    vec3 iridescence = vec3(
        tan(btime + 0.2 + 0.2 * cos(sheen * 2.283)),
        0.15 + btime*1.1 * cos(sheen * 4.0 + 6.0 + btime),
        btime*1.2 + 0.1 * cos(sheen * 2.283 + 2.0 + btime)
    );
	
    vec3 color = sampled.rgb * 0.3 + oilColor;
    if(blobs <= 0.55){
        color *= iridescence*1.1;
        color = iridescence*1.5;
    };
	if((noise >= 0.49 && noise <= 0.58)){
        color.rgb *= 1.3;
        color *= iridescence*1.5 / noise*1.3;
    }
    

    gl_FragColor = vec4(color, min(sampled.a * 50.0, 1.0));
}