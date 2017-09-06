package com.example.navde.demodriver.event;

import android.os.Bundle;

/**
 * Created by navdeepg on 3/2/2017.
 */

public class ViewUpdate {
    private int eventType;
    private Bundle bundle;

    public ViewUpdate(int eventType, Bundle infoBundle)
    {
        this.bundle=infoBundle;
        this.eventType=eventType;
    }
    public int getEventType() {
        return eventType;
    }
    public Bundle getInfoBundle() {
        return bundle;
    }
}
