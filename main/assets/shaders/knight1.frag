#define HIGHP

uniform sampler2D u_texture;

uniform vec4 u_region;
uniform vec2 u_regionSize;
uniform float u_time;

varying vec2 v_texCoords;

void main(){

	vec2 c = v_texCoords;

	vec2 regionMin = vec2(min(u_region.x, u_region.z), min(u_region.y, u_region.w));
	vec2 regionMax = vec2(max(u_region.x, u_region.z), max(u_region.y, u_region.w));
	vec2 span = max(regionMax - regionMin, vec2(0.0001));
	vec2 local = clamp((c - regionMin) / span, 0.0, 1.0);
	vec2 px = local * max(u_regionSize, vec2(1.0));

	float stime = u_time /  30.0;
	float teste = 0.5
		+ 0.3 * sin(px.x / 14.0 + stime * 1.2)
		+ 0.4 * sin(px.y / 24.0 - stime * 0.9)
		+ 0.3 * sin((px.x + px.y) / 50.0 + stime * 0.5);

	vec4 color = texture2D(u_texture, clamp(c, regionMin, regionMax));
	float bright = smoothstep(0.55, 0.95, teste);
	float dim = smoothstep(0.4, 0.1, teste);
	color.rgb *= 1.0 + bright * 0.35;
	color.a *= 1.0 - dim * 0.5;

	gl_FragColor = vec4(color.rgba);
}