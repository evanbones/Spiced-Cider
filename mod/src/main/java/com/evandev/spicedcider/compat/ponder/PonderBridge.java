package com.evandev.spicedcider.compat.ponder;

import net.createmod.ponder.foundation.PonderIndex;

final class PonderBridge {

    static void register() {
        PonderIndex.addPlugin(new SpicedCiderPonderPlugin());
    }
}
