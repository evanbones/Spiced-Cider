#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D DepthSampler;
uniform mat4 InvProjMat;
uniform vec2 OutSize;
uniform float Time;
uniform float Strength;
uniform vec2 Range;

in vec2 texCoord;
out vec4 fragColor;

const float CELLS = 7.0;
const float EDGE = 0.035;

float lattice(vec2 cell) {
    vec3 p = fract(vec3(cell.xyx) * vec3(0.1031, 0.1030, 0.0973));
    p += dot(p, p.yzx + 33.33);
    return fract((p.x + p.y) * p.z);
}

float smoothNoise(vec2 p) {
    vec2 cell = floor(p);
    vec2 f = fract(p);
    vec2 u = f * f * (3.0 - 2.0 * f);
    float a = lattice(cell);
    float b = lattice(cell + vec2(1.0, 0.0));
    float c = lattice(cell + vec2(0.0, 1.0));
    float d = lattice(cell + vec2(1.0, 1.0));
    return mix(mix(a, b, u.x), mix(c, d, u.x), u.y);
}

float layered(vec2 p) {
    return smoothNoise(p) * 0.62 + smoothNoise(p * 2.07 + 11.3) * 0.28 + smoothNoise(p * 4.13 + 4.7) * 0.10;
}

float viewDistance(vec2 uv) {
    float depth = texture(DepthSampler, uv).r;
    if (depth >= 1.0) return 1.0e5;
    vec4 view = InvProjMat * vec4(uv * 2.0 - 1.0, depth * 2.0 - 1.0, 1.0);
    return length(view.xyz / view.w);
}

void main() {
    float dist = viewDistance(texCoord);
    float amount = smoothstep(Range.x, Range.y, dist) * Strength;
    if (amount <= 0.0) {
        fragColor = vec4(texture(DiffuseSampler, texCoord).rgb, 1.0);
        return;
    }

    float aspect = OutSize.x / OutSize.y;
    vec2 p = vec2(texCoord.x * aspect, texCoord.y) * CELLS;

    vec2 drift = vec2(layered(p * 0.45 + vec2(0.0, -Time * 0.22)), layered(p * 0.45 + vec2(7.9, -Time * 0.19)));
    float lateral = layered(p + drift * 1.4 + vec2(Time * 0.12, -Time * 0.85)) * 2.0 - 1.0;
    float vertical = layered(p * 1.35 + drift + vec2(-Time * 0.08, -Time * 1.1)) * 2.0 - 1.0;

    vec2 edge = smoothstep(vec2(0.0), vec2(EDGE), texCoord) * smoothstep(vec2(0.0), vec2(EDGE), 1.0 - texCoord);
    vec2 offset = vec2(lateral / aspect, vertical * 0.7) * amount * edge.x * edge.y;

    vec2 shifted = texCoord + offset;
    if (viewDistance(shifted) < dist * 0.8) shifted = texCoord;
    fragColor = vec4(texture(DiffuseSampler, shifted).rgb, 1.0);
}
