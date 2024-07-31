#define HIGHP

uniform sampler2D u_texture;
uniform sampler2D u_normalMap;
uniform vec2 u_lightDirection;
uniform float u_lightIntensity;
varying vec2 v_texCoords;

void main() {
    vec3 normal = texture2D(u_normalMap, v_texCoords).rgb;
    normal = normalize(normal * 2.0 - 1.0); // Transform normal from [0,1] range to [-1,1] range

    vec3 lightDir = normalize(vec3(u_lightDirection, 0.0));
    float diff = max(dot(normal, lightDir), 0.0);

    vec4 color = texture2D(u_texture, v_texCoords);
    vec3 diffuse = diff * u_lightIntensity * vec3(1.0, 1.0, 1.0); // Adjust light color as needed

    gl_FragColor = vec4(color.rgb * diffuse, color.a);
}