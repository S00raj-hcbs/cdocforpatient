package com.cybermed.cdoc_patient.main;

public class MainActAction<T> {
    private T mLiveActionValue;
    private MainActEvent mLiveActionEvent;

    public MainActAction(T liveActionValue, MainActEvent liveEvent){
        this.mLiveActionValue = liveActionValue;
        this.mLiveActionEvent = liveEvent;
    }

    public MainActAction(MainActEvent liveEvent){
        this.mLiveActionEvent = liveEvent;
    }

    public T getLiveActionValue() {
        return mLiveActionValue;
    }

    public MainActEvent getLiveActionEvent() {
        return mLiveActionEvent;
    }

}
