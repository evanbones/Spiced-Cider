package com.evandev.spicedcider.client.options;

@FunctionalInterface
public interface SnapSink {

    void accept(int x, int y, int range);
}
