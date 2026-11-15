package com.evandev.spicedcider.compat.voxy;

import org.lwjgl.opengl.GL20C;

public final class EdgeFogShaders {
    public static final String TERRAIN_UNIFORM = "u_SpicedCiderEdgeFog";
    private static final int LOD_LOCATION = 21;

    private static final String TERRAIN_DECLARATION = "uniform int u_FogShape;";
    private static final String TERRAIN_DISTANCE = "v_FragDistance = getFragDistance(u_FogShape, position);";
    private static final String TERRAIN_STRETCH = """
            v_FragDistance = getFragDistance(u_FogShape, position);
                vec2 spicedciderEdgeAxes = position.xz * position.xz / max(dot(position.xz, position.xz), 0.0001);
                v_FragDistance *= 1.0 + (position.x > 0.0 ? u_SpicedCiderEdgeFog.x : u_SpicedCiderEdgeFog.y) * spicedciderEdgeAxes.x
                        + (position.z > 0.0 ? u_SpicedCiderEdgeFog.z : u_SpicedCiderEdgeFog.w) * spicedciderEdgeAxes.y;""";

    private static final String LOD_DECLARATION = "layout(location = 41) uniform vec4 roxyTerrainFogColour;";
    private static final String LOD_DISTANCE = ": length(roxyFogPoint);";
    private static final String LOD_STRETCH = """
            : length(roxyFogPoint);
                    vec2 spicedciderEdgeAxes = roxyFogPoint.xz * roxyFogPoint.xz / max(dot(roxyFogPoint.xz, roxyFogPoint.xz), 0.0001);
                    roxyFogDistance *= 1.0 + (roxyFogPoint.x > 0.0 ? spicedciderEdgeFog.x : spicedciderEdgeFog.y) * spicedciderEdgeAxes.x
                            + (roxyFogPoint.z > 0.0 ? spicedciderEdgeFog.z : spicedciderEdgeFog.w) * spicedciderEdgeAxes.y;""";

    public static final String CLOUD_SHADOW_PROGRAM = "better_cloud_shadows:cloud_shadow";
    public static final String CLOUD_SHADOW_UNIFORM = "SpicedCiderEdgeFog";
    private static final String CLOUD_SHADOW_DECLARATION = "uniform int FogShape;";
    private static final String CLOUD_SHADOW_DISTANCE = "fog_distance(relative, FogShape)";
    private static final String CLOUD_SHADOW_FUNCTION = """
            uniform int FogShape;
            uniform vec4 SpicedCiderEdgeFog;

            float spicedciderEdgeStretch(vec3 position) {
                vec2 axes = position.xz * position.xz / max(dot(position.xz, position.xz), 0.0001);
                return 1.0 + (position.x > 0.0 ? SpicedCiderEdgeFog.x : SpicedCiderEdgeFog.y) * axes.x
                        + (position.z > 0.0 ? SpicedCiderEdgeFog.z : SpicedCiderEdgeFog.w) * axes.y;
            }""";

    private static int cloudShadowProgram = -1;
    private static int cloudShadowLocation = -1;
    private static volatile boolean terrainPatched;
    private static volatile boolean lodPatched;

    public static boolean active() {
        return terrainPatched && lodPatched;
    }

    public static String patchTerrain(String source) {
        if (source == null || !source.contains(TERRAIN_DECLARATION) || !source.contains(TERRAIN_DISTANCE)) return source;
        terrainPatched = true;
        return source
                .replace(TERRAIN_DECLARATION, TERRAIN_DECLARATION + "\nuniform vec4 " + TERRAIN_UNIFORM + ";")
                .replace(TERRAIN_DISTANCE, TERRAIN_STRETCH);
    }

    public static String patchLod(String source) {
        if (source == null || !source.contains(LOD_DECLARATION) || !source.contains(LOD_DISTANCE)) return source;
        lodPatched = true;
        return source
                .replace(LOD_DECLARATION, LOD_DECLARATION + "\nlayout(location = " + LOD_LOCATION + ") uniform vec4 spicedciderEdgeFog;")
                .replace(LOD_DISTANCE, LOD_STRETCH);
    }

    public static String patchCloudShadow(String source) {
        if (source == null || !source.contains(CLOUD_SHADOW_DECLARATION) || !source.contains(CLOUD_SHADOW_DISTANCE)) return source;
        return source
                .replace(CLOUD_SHADOW_DECLARATION, CLOUD_SHADOW_FUNCTION)
                .replace(CLOUD_SHADOW_DISTANCE, "(" + CLOUD_SHADOW_DISTANCE + " * spicedciderEdgeStretch(relative))");
    }

    public static void bindCloudShadow(int program) {
        if (program != cloudShadowProgram) {
            cloudShadowProgram = program;
            cloudShadowLocation = GL20C.glGetUniformLocation(program, CLOUD_SHADOW_UNIFORM);
        }
        int location = cloudShadowLocation;
        if (location < 0) return;
        float[] stretch = active() ? VoxyFog.edgeStretch() : new float[4];
        GL20C.glUniform4f(location, stretch[0], stretch[1], stretch[2], stretch[3]);
    }

    public static void bindLod() {
        if (!lodPatched) return;
        float[] stretch = VoxyFog.edgeStretch();
        GL20C.glUniform4f(LOD_LOCATION, stretch[0], stretch[1], stretch[2], stretch[3]);
    }
}
