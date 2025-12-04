#define HIGHP
uniform sampler2D u_texture;

varying vec2 v_texCoords;

void main() {
	vec2 c = v_texCoords.xy;
	vec4 color = texture2D(u_texture, c);
	if(color.a > 0.0){
		color = vec4(0.0, 0.0, 0.0, min(0.5, color.a ));
	}
	gl_FragColor = color;
}