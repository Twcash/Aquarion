varying vec2 v_texCoords;

uniform sampler2D u_texture0; // diffuse map
uniform sampler2D u_lightmap; // light map
uniform sampler2D u_darkmap;  // dark map
uniform vec4 u_color;         // ambient color

void main() {
	vec4 diffuseColor = texture2D(u_texture0, v_texCoords);
	vec4 light = texture2D(u_lightmap, v_texCoords);
	vec4 dark = texture2D(u_darkmap, v_texCoords);

	vec3 ambient = u_color.rgb * u_color.a * (1.0 - dark.a);
	vec3 intensity = ambient + light.rgb;
	vec3 finalColor = diffuseColor.rgb * intensity;

	gl_FragColor = vec4(finalColor, diffuseColor.a);
}
