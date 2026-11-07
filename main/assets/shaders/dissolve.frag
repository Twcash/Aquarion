#define HIGHP

#define NSCALE 180.0 / 2.0

uniform sampler2D u_texture;
uniform sampler2D u_noise;

uniform vec2 u_texsize;
uniform vec2 u_uv;
uniform vec2 u_uv2;
uniform float u_progress;
uniform vec4 u_edgeColor;

varying vec4 v_color;
varying vec2 v_texCoords;

void main(){
    vec4 color = texture2D(u_texture, v_texCoords);

    if(color.a < 0.004){
        discard;
    }

    color *= v_color;

    //pixel offset of this fragment inside the sampled region
    vec2 regionSize = max(abs(u_uv2 - u_uv) * u_texsize, vec2(1.0));
    vec2 coords = clamp((v_texCoords - u_uv) * u_texsize, vec2(0.0), regionSize);

    float noise = (texture2D(u_noise, coords / NSCALE * vec2(-0.9, 0.8)).r
                 + texture2D(u_noise, coords / NSCALE * vec2(-0.8, -1.0)).r) / 2.0;

    float p = clamp(u_progress, 0.0, 1.0);
    float edge = 0.09;

    //overshoot the cut so the sprite is fully erased at p == 1 and fully intact at p == 0
    float cut = p * (1.0 + edge * 2.0) - edge;

    float alpha = smoothstep(cut - edge, cut + edge, noise);
    float glow = alpha * (1.0 - smoothstep(cut + edge * 0.5, cut + edge * 2.5, noise));

    color.rgb = mix(color.rgb * 0.8, u_edgeColor.rgb, glow * u_edgeColor.a);
    color.a *= alpha;

    gl_FragColor = color;
}
