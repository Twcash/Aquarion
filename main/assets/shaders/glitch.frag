#define HIGHP

uniform sampler2D u_texture;
uniform float u_time;
uniform vec2 u_resolution;

varying vec2 v_texCoords;

float rand(vec2 co) {
    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

void main() {
    vec2 uv = v_texCoords;
    vec4 color = texture2D(u_texture, uv);
	float btime = u_time/5.0;
    float pulseSize = mix(8.0, 40.0, abs(sin(btime*2.0 + uv.y*5.0)));
vec2 blockUV = floor(uv * u_resolution / pulseSize) * pulseSize / u_resolution;
color.rgb = texture2D(u_texture, blockUV).rgb;
    float hShift = (rand(vec2(floor(btime*10.0), floor(uv.y*20.0))) - 0.5) * 0.05;
uv.x += hShift;
color.rgb = texture2D(u_texture, uv).rgb;
    float driftChance = rand(floor(uv * u_resolution / 8.0) + floor(btime*5.0));
    vec2 drift = vec2(rand(uv + btime) - 0.5, rand(uv - u_time) - 0.5) * 0.008;
    if (driftChance > 0.7) {
        uv += drift;
        color.rgb = texture2D(u_texture, uv).rgb;
    }
    if (rand(vec2(floor(btime * 1.3), 0.0)) > 0.92) {
        uv += (rand(vec2(btime, 1.0)) - 0.5) * 0.08; // big jump
    } else {
        uv += (rand(vec2(floor(btime * 4.0), 2.0)) - 0.5) * 0.01; // subtle jitter
    }
    float scan = sin(uv.y * u_resolution.y * 1.5 + btime * 30.0) * 0.04;
    color.rgb -= scan * 0.15;
    vec2 center = uv - 0.5;
    float dist = dot(center, center) * 0.3;
    uv = uv + center * dist;
    float pulse = 1.0 + sin(btime * 3.0) * 0.1;
    color.rgb *= pulse;
    vec4 bloom = vec4(0.0);
    bloom += texture2D(u_texture, uv + vec2(0.002,0.0));
    bloom += texture2D(u_texture, uv + vec2(-0.002,0.0));
    bloom += texture2D(u_texture, uv + vec2(0.0,0.002));
    bloom += texture2D(u_texture, uv + vec2(0.0,-0.002));
    color.rgb = mix(color.rgb, bloom.rgb*0.25, 0.5);
    float glitchShift = (rand(vec2(floor(btime * 5.0), floor(uv.y * 30.0))) - 0.5) * 0.04;
    vec4 colR = texture2D(u_texture, uv + vec2(glitchShift, 0.0));
    vec4 colG = texture2D(u_texture, uv + vec2(-glitchShift, glitchShift * 0.5));
    vec4 colB = texture2D(u_texture, uv + vec2(0.0, -glitchShift));
    color = vec4(colR.r, colG.g, colB.b, 1.0);
    float blockSize = 16.0;
    vec2 blockCoord = floor(uv * u_resolution / blockSize);
    float blockNoise = rand(blockCoord + floor(btime * 5.0));

    if (blockNoise > 0.8) {
        vec2 lowResUV = floor(uv * u_resolution / (blockSize * 0.5)) * (blockSize * 0.5) / u_resolution;
        color = texture2D(u_texture, lowResUV);
    }

    if (blockNoise > 0.9) {
        vec2 offsetDir = vec2(rand(blockCoord), rand(blockCoord + 1.0)) - 0.5;
        offsetDir *= 0.05;
        vec4 smearColor = texture2D(u_texture, uv + offsetDir);
        color = mix(color, smearColor, 0.6);
    }
    float noise = rand(vec2(floor(uv.x * u_resolution.x / 4.0), floor(uv.y * u_resolution.y / 4.0) + btime * 50.0));
    if (noise > 0.96) {
        color.rgb = vec3(rand(uv + btime), rand(uv + btime * 1.3), rand(uv - u_time * 0.7));
    }

    float tearChance = rand(vec2(floor(btime * 10.0), 1.0));
    if (tearChance > 0.9) {
        float tearY = fract(uv.y * 10.0) > 0.5 ? 0.02 : -0.02;
        uv.y += tearY;
        color.rgb = texture2D(u_texture, uv).rgb;
    }
    float pixelChance = rand(vec2(floor(btime * 3.0), 2.0));
    if (pixelChance > 0.8) {
        float randomSize = mix(8.0, 40.0, rand(vec2(floor(btime * 10.0), uv.y * 100.0)));
        vec2 blockUV = floor(uv * u_resolution / randomSize) * randomSize / u_resolution;
        color.rgb = texture2D(u_texture, blockUV).rgb;
    }
    if (rand(uv*1000.0 + btime*50.0) > 0.995) color.rgb = vec3(1.0);
    if (rand(vec2(floor(btime*7.0), floor(uv.y*50.0))) > 0.97) color = vec4(color.g, color.b, color.r, 1.0);
    float tear = rand(vec2(floor(btime*10.0), floor(uv.y*20.0))) - 0.5;
    uv.x += tear*0.03;
    if (rand(vec2(floor(btime*20.0), floor(uv.x*50.0))) > 0.98) color.rgb = 1.0 - color.rgb;
    if (rand(vec2(floor(btime * 5.0), 3.0)) > 0.95) {
        color.rgb = vec3(rand(uv + btime * 2.0),
                         rand(uv - btime * 3.0),
                         rand(uv + btime * 5.0));
    }
    if (rand(vec2(floor(u_time*8.0), floor(uv.y*30.0))) > 0.96) {
    color.rgb += vec3(rand(uv*5.0), rand(uv*7.0), rand(uv*3.0)) * 0.5;
}
  
   float scane = sin(uv.y * u_resolution.y * 1.5 + btime * 30.0) * (0.02 + 0.03*sin(u_time*5.0));
color.rgb -= scane * 0.15;
if (rand(uv*1000.0 + btime*80.0) > 0.998) color.rgb = vec3(1.0);
if (rand(vec2(floor(u_time*5.0), floor(uv.y*50.0))) > 0.97) {
    vec2 offset = vec2(rand(uv+btime), 0.0) * 0.05;
    color.rgb = mix(color.rgb, texture2D(u_texture, uv+offset).rgb, 0.7);
}
    gl_FragColor = color;
}