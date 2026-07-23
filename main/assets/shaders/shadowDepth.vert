attribute vec4 a_position;

uniform mat4 u_lightSpaceMatrix;
uniform mat4 u_trans;

varying float v_depth;

void main() {
    vec4 lightPos = u_lightSpaceMatrix * u_trans * a_position;
    gl_Position = lightPos;
    v_depth = (lightPos.z / lightPos.w + 1.0) * 0.5;
}
