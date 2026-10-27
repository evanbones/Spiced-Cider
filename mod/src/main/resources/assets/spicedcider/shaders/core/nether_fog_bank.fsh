#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;
uniform sampler2D DepthSampler;
uniform mat4 ProjMat;
uniform vec2 ScreenSize;
uniform float FogStart;
uniform float FogEnd;
uniform float FadeDistance;

in float vertexDistance;
in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

float linearDepth(float depth) {
    return ProjMat[3][2] / ((depth * 2.0 - 1.0) + ProjMat[2][2]);
}

void main() {
    float coverage = texture(Sampler0, texCoord0).a * vertexColor.a;
    float scene = texture(DepthSampler, gl_FragCoord.xy / ScreenSize).r;
    float gap = scene >= 1.0 ? FadeDistance : linearDepth(scene) - linearDepth(gl_FragCoord.z);
    coverage *= clamp(gap / FadeDistance, 0.0, 1.0);
    coverage *= linear_fog_fade(vertexDistance, FogStart, FogEnd);
    if (coverage <= 0.001) discard;
    fragColor = vec4(vertexColor.rgb * coverage, 1.0);
}
