package com.example.navde.demodriver.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.example.navde.demodriver.Constants;
import com.example.navde.demodriver.service.binder.ChatterBoxClient;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service to create pubnub connections.
 * @author navdeepg
 */

public class ChatterBoxService extends Service {

    private final Map<String, List<ChatterBoxCallback>> listeners = new HashMap<>();
    private String UUID_SUB="customers";

    /**
     * Single instance of PubNub
     */
    private PubNub pnHybrid,pnDriver,pnCustomer;
    private boolean connected = false;
    public ChatterBoxService() {
    }

    /**
     * Initialises pubnub for hybrid keys
     * @return pubnub object
     */
    public PubNub getPubNubHybrid() {
        if ((null == pnHybrid) ) {
            PNConfiguration pnConfiguration = new PNConfiguration();
            pnConfiguration.setSubscribeKey(Constants.PUBNUB_HYBRID_SUBSCRIBE_KEY);
            pnConfiguration.setPublishKey(Constants.PUBNUB_HYBRID_PUBLISH_KEY);
            pnConfiguration.setUuid(UUID_SUB+PreferenceManager.getDefaultSharedPreferences(this).getString("CUSTOMER_ID",""));
            pnConfiguration.setPresenceTimeoutWithCustomInterval(30,12);
            pnConfiguration.setSecure(true);
            //pnConfiguration.setAuthKey(Constants.PUBNUB_AUTH_KEY);
            pnHybrid = new PubNub(pnConfiguration);

            connected = true;
        }
        return pnHybrid;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        ChatterBoxClient chatterBoxClient = new ChatterBoxClient(this);
        return chatterBoxClient;
    }

}
