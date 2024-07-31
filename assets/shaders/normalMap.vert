#define HIGHP

attribute vec4 a_position;
attribute vec2 a_texCoord;

varying vec2 v_texCoords;

void main() {
    v_texCoords = a_texCoord;
    gl_Position = a_position;
}