package com.evandev.spicedcider.client.music;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class PocketJukeboxSound extends AbstractTickableSoundInstance {
    public PocketJukeboxSound(SoundEvent event) {
        super(event, SoundSource.RECORDS, SoundInstance.createUnseededRandom());
        this.relative = true;
        this.attenuation = Attenuation.NONE;
        this.looping = false;
        this.volume = 1.0F;
        this.pitch = 1.0F;
    }

    @Override
    public void tick() {
    }

    public void halt() {
        stop();
    }
}
