package com.cybermed.cdoc_patient.login.viewmodel;

public class SignInUpLiveAction <T> {
    private T mLiveActionValue;
    private SignInUpEvent mLiveActionEvent;

    public SignInUpLiveAction(T liveActionValue, SignInUpEvent liveEvent){
        this.mLiveActionValue = liveActionValue;
        this.mLiveActionEvent = liveEvent;
    }

    public SignInUpLiveAction(SignInUpEvent liveEvent){
        this.mLiveActionEvent = liveEvent;
    }

    public T getLiveActionValue() {
        return mLiveActionValue;
    }

    public SignInUpEvent getLiveActionEvent() {
        return mLiveActionEvent;
    }

}
