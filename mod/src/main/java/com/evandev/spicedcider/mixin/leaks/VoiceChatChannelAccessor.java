package com.evandev.spicedcider.mixin.leaks;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import de.maxhenkel.voicechat.net.Channel;
import de.maxhenkel.voicechat.net.NetManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@IfModLoaded("voicechat")
@Mixin(value = Channel.class, remap = false)
public interface VoiceChatChannelAccessor {
    @Accessor("serverListener")
    void spicedcider$setServerListener(NetManager.ServerReceiver<?> listener);
}
