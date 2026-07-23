varying vec4 v_col;
varying vec4 v_lightSpacePos;
varying vec3 v_worldPos;

uniform sampler2D u_shadowMap;
uniform vec3 u_sunPos;
uniform int u_planetCount;
uniform vec3 u_planetPos[8];
uniform float u_planetRadius[8];

float calculateShadow(vec4 lightSpacePos) {
    vec3 projCoords = lightSpacePos.xyz / lightSpacePos.w;
    projCoords = projCoords * 0.5 + 0.5;
    if (projCoords.z > 1.0 || projCoords.x < 0.0 || projCoords.x > 1.0 || projCoords.y < 0.0 || projCoords.y > 1.0) return 0.0;
    float closestDepth = texture2D(u_shadowMap, projCoords.xy).r;
    float currentDepth = projCoords.z;
    float bias = 0.005;
    return currentDepth > closestDepth + bias ? 1.0 : 0.0;
}

float raySphereIntersect(vec3 ro, vec3 rd, vec3 center, float radius) {
    vec3 oc = ro - center;
    float b = dot(oc, rd);
    float c = dot(oc, oc) - radius * radius;
    float h = b * b - c;
    if (h < 0.0) return -1.0;
    float t = -b - sqrt(h);
    return t;
}

void main(){
    float selfShadow = calculateShadow(v_lightSpacePos);

    vec3 toSun = normalize(u_sunPos - v_worldPos);
    float planetShadow = 0.0;
    for (int i = 0; i < 8; i++) {
        if (i >= u_planetCount) break;
        float t = raySphereIntersect(v_worldPos, toSun, u_planetPos[i], u_planetRadius[i]);
        if (t > 0.001) {
            planetShadow = 1.0;
            break;
        }
    }

    float shadow = max(selfShadow, planetShadow);
    vec3 color = v_col.rgb * (1.0 - shadow);
    gl_FragColor = vec4(color, v_col.a);
}
