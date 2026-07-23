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
varying vec4 v_lightSpacePos;
varying vec3 v_worldPos;

const vec3 diffuse = vec3(0.01);

void main(){
    vec3 specular = vec3(0.0, 0.0, 0.0);

    vec3 worldPos = (u_trans * a_position).xyz;
    vec3 worldNormal = normalize(mat3(u_trans) * a_normal);
    vec3 lightReflect = normalize(reflect(worldNormal, u_lightdir));
    vec3 vertexEye = normalize(u_campos - worldPos);

    float albedo = 1.0 - a_color.a;

    float specularFactor = dot(vertexEye, lightReflect);
    if(specularFactor > 0.0){
        specular = vec3(1.0 * pow(specularFactor, 40.0)) * albedo;
    }

    vec3 norc = (u_ambientColor + specular) * (diffuse + vec3(clamp((dot(worldNormal, u_lightdir) + 1.0) / 2.0, 0.0, 1.0)));

    float emissive = a_emissive.a * u_emissive * min(pow(max(0.0, (1.0 - norc.r) * 1.2), 3.0), 1.1);

    v_col = vec4(mix(a_color.rgb, a_emissive.rgb, emissive), 1.0) * vec4(mix(norc, vec3(1.0), emissive), 1.0);
    v_lightSpacePos = u_lightSpaceMatrix * vec4(worldPos, 1.0);
    v_worldPos = worldPos;
    gl_Position = u_proj * u_trans * a_position;
}
