package com.cybermed.cdoc_patient.common.base;

/**
 *
 * @param <T>  class
 */
public class LiveActions<T> {

    private T liveActionValue;
    private LiveUIEvent liveActionEvent;
    /**
     *
     * @param liveActionEvent Event name
     */
    public LiveActions(LiveUIEvent liveActionEvent) {
        this.liveActionEvent = liveActionEvent;
    }
    /**
     *
     * @param liveActionValue Model type val
     * @param liveActionEvent Event name
     */
    public LiveActions(T liveActionValue, LiveUIEvent liveActionEvent) {
        this(liveActionEvent);
        this.liveActionValue = liveActionValue;
    }

    public T getLiveActionValue() {
        return liveActionValue;
    }

    public LiveUIEvent getLiveActionEvent() {
        return liveActionEvent;
    }
}
