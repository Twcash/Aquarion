    
#define HIGHP

uniform sampler2D u_texture;
uniform sampler2D u_noise;

uniform vec2 u_campos;
uniform vec2 u_resolution;
uniform float u_time;
out vec4 FragColor;

void main() {
    // Normalized screen coordinates (0 to 1)
    vec2 uv = gl_FragCoord.xy / u_Resolution;

    // Scale the noise texture coordinates for finer distortion
    vec2 noiseUV = uv * 5.0 + vec2(u_Time * 0.1, u_Time * 0.15);

    // Fetch the noise value
    float noise = texture(u_NoiseTexture, noiseUV).r;

    // Apply the noise as distortion
    vec2 distortedUV = uv + (noise - 0.5) * 0.02;

    // Fetch the color from the screen texture
    vec4 color = texture(u_ScreenTexture, distortedUV);

    // Output the final color
    FragColor = color;
}