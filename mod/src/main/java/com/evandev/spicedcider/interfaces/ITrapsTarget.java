package com.evandev.spicedcider.interfaces;

public interface ITrapsTarget {
    void cider$setTargetTrapped(boolean trapped, boolean notifyOthers);

    boolean cider$isTargetTrapped();

    void cider$setTargetTrappedCounter(int value);
}