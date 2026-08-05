package com.evandev.spicedcider.blockgrid.storage;

import net.minecraft.world.phys.Vec3;

public interface SignOffsetHolder {
    Vec3 spicedcider$getSignOffset();

    void spicedcider$setSignOffset(Vec3 offset);
}
