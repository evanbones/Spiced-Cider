package com.evandev.spicedcider.client.music;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class DeathSoundInstance extends AbstractTickableSoundInstance {
    private final int maxTicks;
    private int tickCount = 0;

    public DeathSoundInstance(SoundEvent sound, int durationInTicks) {
        super(sound, SoundSource.RECORDS, SoundInstance.createUnseededRandom());
        this.looping = false;

        this.relative = true;
        this.delay = 0;
        this.volume = 1.0f;

        this.maxTicks = durationInTicks;
    }

    @Override
    public void tick() {
        if (this.tickCount++ >= this.maxTicks) {
            this.stop();
        }
    }
}