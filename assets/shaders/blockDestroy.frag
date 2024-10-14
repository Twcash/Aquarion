#define HIGHP

uniform sampler2D u_texture;
uniform vec2 u_texsize;
uniform vec2 u_uv; // UV coordinates for the starting point of the mask
uniform vec2 u_uv2; // UV coordinates for the ending point of the mask
uniform float u_progress; // Progress of the mask effect (0 to 1)
uniform float u_time;

varying vec4 v_color;
varying vec2 v_texCoords;

void main() {
    vec2 t = v_texCoords.xy;
    vec4 color = texture2D(u_texture, t);

    // Calculate the center of the triangle
    vec2 center = (u_uv + u_uv2) / 2.0;

    // Determine if the current fragment is within the triangular area
    bool inTriangle = (t.x >= u_uv.x && t.x <= center.x) && (t.y >= u_uv.y);

    // Calculate the scaling factor based on u_progress for shrinking
    float scaleFactor = mix(1.0, 0.0, u_progress); // Scale from full size to 0

    // Render based on the triangular area
    if (inTriangle && color.a > 0.1) {
        gl_FragColor = v_color * scaleFactor; // Apply scaling while rendering
    } else if (color.a > 0.1) {
        gl_FragColor = color * scaleFactor; // Apply scaling for visible parts
    } else {
        gl_FragColor = vec4(0.0); // Transparent if the fragment is fully masked
    }
}