varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform float u_maxAlpha;

void main(){
    vec4 area = texture2D(u_texture, v_texCoords);
    gl_FragColor = vec4(area.rgb, min(area.a, u_maxAlpha));
}
