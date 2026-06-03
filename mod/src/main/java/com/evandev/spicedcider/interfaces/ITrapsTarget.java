package com.evandev.spicedcider.interfaces;

public interface ITrapsTarget {

    void setTargetTrapped(boolean trapped, boolean notifyOthers);

    boolean isTargetTrapped();
}