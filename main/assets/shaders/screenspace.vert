attribute vec4 a_position;
varying vec2 v_texCoords;

void main() {
	v_texCoords = a_position.xy;
	gl_Position = a_position;
}