package com.evandev.spicedcider.compat.voicechat;

import com.evandev.spicedcider.mixin.leaks.VoiceChatChannelAccessor;
import de.maxhenkel.voicechat.intercompatibility.CommonCompatibilityManager;
import de.maxhenkel.voicechat.net.Channel;
import de.maxhenkel.voicechat.net.NetManager;

import java.util.List;

public final class VoiceChatLeaks {
    private VoiceChatLeaks() {
    }

    public static void release() {
        NetManager net = CommonCompatibilityManager.INSTANCE.getNetManager();
        List<Channel<?>> channels = List.of(
                net.updateStateChannel, net.playerStateChannel, net.playerStatesChannel, net.removePlayerStateChannel,
                net.secretChannel, net.requestSecretChannel, net.addGroupChannel, net.removeGroupChannel,
                net.joinGroupChannel, net.createGroupChannel, net.leaveGroupChannel, net.joinedGroupChannel,
                net.addCategoryChannel, net.removeCategoryChannel);
        for (Channel<?> channel : channels) {
            ((VoiceChatChannelAccessor) channel).spicedcider$setServerListener(null);
        }
    }
}
