#version 150

uniform sampler2D DepthSampler;
uniform sampler2D RingSampler;
uniform mat4 InvViewProjMat;
uniform float CutDistance;

in vec2 texCoord;

out vec4 fragColor;

float ring(int index) {
    vec4 texel = texelFetch(RingSampler, ivec2(index & 255, 0), 0);
    return (texel.r * 255.0 * 256.0 + texel.g * 255.0) / 16.0;
}

void main() {
    float depth = texture(DepthSampler, texCoord).r;
    if (depth < 1.0) {
        vec4 world = InvViewProjMat * vec4(texCoord * 2.0 - 1.0, depth * 2.0 - 1.0, 1.0);
        world /= world.w;
        float angle = atan(world.z, world.x);
        if (angle < 0.0) angle += 6.28318530718;
        float slot = angle / 6.28318530718 * 256.0;
        int index = int(floor(slot));
        float edge = min(ring(index), ring(index + 1)) - 1.0;
        if (length(world.xz) >= min(CutDistance, edge)) {
            depth = 1.0;
        }
    }
    gl_FragDepth = depth;
    fragColor = vec4(0.0);
}
