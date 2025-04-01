#define HIGHP

uniform sampler2D u_texture;  // Screen texture (background)
uniform sampler2D u_noise;    // Noise texture (distortion effect)

uniform vec2 u_campos;
uniform vec2 u_resolution;
uniform float u_time;

out vec4 FragColor;

void main() {
    // Normalized screen coordinates (0 to 1)
    vec2 uv = gl_FragCoord.xy / u_resolution;

    // Generate animated noise for distortion effect
    vec2 noiseUV = uv * 5.0 + vec2(u_time * 0.1, u_time * 0.15);
    float noise = texture(u_noise, noiseUV).r;

    // Distortion strength
    float distortionStrength = 0.01; // Adjust this for more/less distortion

    // Offset UVs for distortion
    vec2 distortedUV = uv + (noise - 0.5) * distortionStrength;

    // Sample the screen texture with distorted UVs
    vec4 distortedColor = texture(u_texture, distortedUV);

    // Output only the distorted background, making the sprite itself transparent
    FragColor = vec4(distortedColor.rgb, 1.0 - noise * 0.8); // Adjust transparency based on noise
}
