package com.example.navde.demodriver.service;


import com.example.navde.demodriver.BeansMessage;

import java.util.ArrayList;

/**
 * Provides a callback interface for the ChatterBoxService
 * ChatterBox service interacts with channels across PubNub to
 * send and receive realtime instant messaging. This interface
 * is invoked when significant events occur.
 *
 * @author navdeepg
 */
public interface ChatterBoxCallback {

    void onMessage(BeansMessage message);

    void onMessagePublished(String message);

    void onHistorySuccess(ArrayList<BeansMessage> historyChatList);

   // void onPresence(ChatterBoxPresenceMessage pmessage);

    void onHeartBeat(boolean error);

    void onError(String e);

  //  void onPrivateChatRequest(ChatterBoxPrivateChatRequest request);

    void onDisconnect();

    void onConnect();

    void onLocationUpdate(BeansMessage locationMsg);

    void onLocationHistorySucess(ArrayList<BeansMessage> historyChatList);

    void onPresenceUpdate(String strUserID, String presence);


}
