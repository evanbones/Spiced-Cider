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

    public static void bindLod() {
        if (!lodPatched) return;
        float[] stretch = VoxyFog.edgeStretch();
        GL20C.glUniform4f(LOD_LOCATION, stretch[0], stretch[1], stretch[2], stretch[3]);
    }
}
