package com.example.navde.demodriver.service;


import com.example.navde.demodriver.BeansMessage;

import java.util.ArrayList;

/**
 * Created by Frederick on 5/14/15.
 */

public class DefaultChatterBoxCallback implements ChatterBoxCallback {


    @Override
    public void onMessage(BeansMessage message) {

    }

    @Override
    public void onHistorySuccess(ArrayList<BeansMessage> historyChatList) {

    }

    @Override
    public void onMessagePublished(String message) {

    }

    @Override
    public void onHeartBeat(boolean error) {

    }

    @Override
    public void onError(String e) {

    }

    @Override
    public void onDisconnect() {

    }

    @Override
    public void onConnect() {

    }

    @Override
    public void onLocationUpdate(BeansMessage locationMsg) {

    }

    @Override
    public void onLocationHistorySucess(ArrayList<BeansMessage> historyChatList) {

    }

    @Override
    public void onPresenceUpdate(String strUserId,String presence) {

    }
}
