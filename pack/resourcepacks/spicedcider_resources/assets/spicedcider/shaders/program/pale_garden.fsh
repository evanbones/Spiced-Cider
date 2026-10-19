#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D InDepth;
uniform mat4 InvProjMat;
uniform vec3 FogColor;
uniform vec2 FogRange;
uniform float Strength;

in vec2 texCoord;
out vec4 fragColor;

vec3 rgbToHsv(vec3 c) {
    vec4 k = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, k.wz), vec4(c.gb, k.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));
    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}

float fogAmount(vec2 uv) {
    float depth = texture(InDepth, uv).r;
    if (depth >= 1.0) return 1.0;
    vec4 view = InvProjMat * vec4(uv * 2.0 - 1.0, depth * 2.0 - 1.0, 1.0);
    float dist = length(view.xyz / view.w);
    if (dist <= FogRange.x) return 0.0;
    return dist < FogRange.y ? smoothstep(FogRange.x, FogRange.y, dist) : 1.0;
}

void main() {
    vec4 color = texture(DiffuseSampler, texCoord);
    float fog = min(fogAmount(texCoord), 0.92);
    vec3 original = clamp((color.rgb - fog * FogColor) / (1.0 - fog), 0.0, 1.0);

    vec3 hsv = rgbToHsv(original);
    float hue = hsv.x * 360.0;
    float amber = smoothstep(6.0, 14.0, hue) * (1.0 - smoothstep(44.0, 54.0, hue));
    amber *= smoothstep(0.6, 0.72, hsv.y) * smoothstep(0.62, 0.74, hsv.z);
    amber *= 1.0 - smoothstep(0.85, 0.92, fog);

    float grey = dot(color.rgb, vec3(0.2126, 0.7152, 0.0722));
    vec3 glow = mix(original, FogColor, fog * 0.35);
    vec3 graded = mix(vec3(grey), glow, amber);
    fragColor = vec4(mix(color.rgb, graded, Strength), 1.0);
}
