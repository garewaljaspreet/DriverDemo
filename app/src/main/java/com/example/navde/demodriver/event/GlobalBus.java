package com.example.navde.demodriver.event;

/**
 * Created by navdeepg on 3/2/2017.
 */

import org.greenrobot.eventbus.EventBus;

public class GlobalBus {
    private static EventBus sBus;
    public static EventBus getBus() {
        if (sBus == null)
            sBus = EventBus.getDefault();
        return sBus;
    }
}

