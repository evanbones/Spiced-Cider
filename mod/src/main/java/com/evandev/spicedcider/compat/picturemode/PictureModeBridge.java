package com.evandev.spicedcider.compat.picturemode;

import mod.icanttellyou.picturemode.client.PictureModeClient;
import mod.icanttellyou.picturemode.client.PictureModeState;

public final class PictureModeBridge {

    public static boolean enabled() {
        PictureModeState state = PictureModeClient.getState();
        return state != null && state.isEnabled();
    }
}
