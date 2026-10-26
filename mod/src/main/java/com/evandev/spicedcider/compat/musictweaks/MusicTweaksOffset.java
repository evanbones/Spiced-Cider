package com.evandev.spicedcider.compat.musictweaks;

import com.evandev.music_tweaks.client.jukebox.PendingStreamOffset;

public final class MusicTweaksOffset {
    public static void skip(float seconds) {
        PendingStreamOffset.set(seconds);
    }
}
