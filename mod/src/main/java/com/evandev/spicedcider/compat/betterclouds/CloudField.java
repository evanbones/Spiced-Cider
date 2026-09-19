package com.evandev.spicedcider.compat.betterclouds;

public record CloudField(
        double originX,
        double originZ,
        float spacing,
        float sparsity,
        float fuzziness,
        float samplingScale,
        float sizeXZ,
        float bottomSparsity,
        float cloudiness,
        float cloudHeight,
        int renderDistance,
        long signature
) {
}
