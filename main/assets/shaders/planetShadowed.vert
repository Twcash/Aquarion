attribute vec4 a_position;
attribute vec3 a_normal;
attribute vec4 a_color;
attribute vec4 a_emissive;

uniform mat4 u_proj;
uniform mat4 u_trans;
uniform vec3 u_lightdir;
uniform vec3 u_camdir;
uniform vec3 u_campos;
uniform vec3 u_ambientColor;
uniform float u_emissive;
uniform mat4 u_lightSpaceMatrix;

varying vec4 v_col;
varying vec3 v_ambient;
varying vec4 v_lightSpacePos;
varying vec3 v_worldPos;
varying float v_emissive;

void main(){
    vec3 worldPos = (u_trans * a_position).xyz;
    vec3 worldNormal = normalize(mat3(u_trans) * a_normal);
    vec3 vertexEye = normalize(u_campos - worldPos);
    float facing = max(dot(worldNormal, u_lightdir), 0.0);
    vec3 lightReflect = reflect(-u_lightdir, worldNormal);
    // glTF uses opaque vertex alpha, which previously disabled every metal highlight.
    float specular = pow(max(dot(vertexEye, lightReflect), 0.0), 32.0) * facing;
    float rim = pow(1.0 - abs(dot(worldNormal, vertexEye)), 3.0);
    vec3 ambient = u_ambientColor * vec3(0.16, 0.19, 0.25);
    vec3 norc = ambient + u_ambientColor * (0.9 * facing);
    vec3 metal = a_color.rgb * norc
        + vec3(0.30, 0.38, 0.48) * specular
        + a_color.rgb * vec3(0.10, 0.18, 0.28) * rim;

    float emissive = a_emissive.a * u_emissive * (0.8 + 0.2 * min(pow(max(0.0, (1.0 - norc.r) * 1.2), 3.0), 1.0));

    v_col = vec4(mix(metal, a_emissive.rgb, emissive), 1.0);
    // Shadows remove direct sunlight, while ambient light and emission remain visible.
    v_ambient = mix(a_color.rgb * (ambient + vec3(0.10, 0.18, 0.28) * rim), a_emissive.rgb, emissive);
    v_emissive = clamp(emissive, 0.0, 1.0);
    v_lightSpacePos = u_lightSpaceMatrix * vec4(worldPos, 1.0);
    v_worldPos = worldPos;
    gl_Position = u_proj * u_trans * a_position;
}
