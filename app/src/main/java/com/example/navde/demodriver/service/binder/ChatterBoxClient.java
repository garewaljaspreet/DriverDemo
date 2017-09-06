package com.example.navde.demodriver.service.binder;

import android.os.Binder;
import android.util.Log;

import com.example.navde.demodriver.BeansMessage;
import com.example.navde.demodriver.service.ChatterBoxCallback;
import com.example.navde.demodriver.service.ChatterBoxService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import java.util.Arrays;

/**
 * Represents pubnub related functionality i.e subscribing, fetching and publishing data to pubnub.
 * @author navdeepg
 */
public class ChatterBoxClient extends Binder {

    private static String TAG="ChatterBoxClient";
    private ChatterBoxService chatterBoxService;
    ChatterBoxCallback listener,locationListener;//Used for callback to UI
    private SubscribeCallback subscribeCallbackChat=null;
    public ChatterBoxClient(ChatterBoxService service) {
        chatterBoxService = service;
    }

    ChatterBoxClient getService() {
        return ChatterBoxClient.this;
    }


    /**
     * Subscribe chat channel to get alert about new messages
     * @param channelName name of channel to subscribe
     */
    public  void subscribeChat(final String channelName) {
        // Subscribe to channel

        try {
            chatterBoxService.getPubNubHybrid().subscribe()
                    .channels(Arrays.asList("TestChatNewest")) // subscribe to channels
                    .execute();
        }
        catch(Exception e)
        {
            Log.e(TAG, e.toString());
        }

    }

    /**
     * Initialises the callback listener
     * @param listener reference of listener to be called
     */
    public void initChatViewListener(final ChatterBoxCallback listener)
    {
        this.listener=listener;
    }

    /**
     * Initialises the callback listener
     * @param listener reference of listener to be called
     */
    public void initListener( final ChatterBoxCallback listener)
    {
        this.listener=listener;
        try {
            if(subscribeCallbackChat==null)
            {
                subscribeCallbackChat=new SubscribeCallback() {
                    @Override
                    public void status(PubNub pubnub, PNStatus status) {
                        if (status.getCategory() == PNStatusCategory.PNUnexpectedDisconnectCategory) {
                            // internet got lost, do some magic and call reconnect when ready
                            pubnub.reconnect();
                            Log.e("SubError reconnect1", "sub connection error");
                        } else if (status.getCategory() == PNStatusCategory.PNTimeoutCategory) {
                            // do some magic and call reconnect when ready
                            pubnub.reconnect();
                            Log.e("SubError reconnect2", "sub connection error");
                        } else {
                            if (!status.isError()) {
                                // Message successfully published to specified channel.
                            }
                            // Request processing failed.
                            else {

                                Log.e("SubError", "sub connection error");
                            }

                        }
                    }

                    @Override
                    public void message(PubNub pubnub, PNMessageResult message) {

                        try {
                            ObjectMapper mapper = new ObjectMapper();
                            BeansMessage beansChatMessages = mapper.readValue(message.getMessage().toString(), BeansMessage.class);

                            publishMessageData(beansChatMessages);
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }

                    @Override
                    public void presence(PubNub pubnub, PNPresenceEventResult presence) {

                    }
                };
                chatterBoxService.getPubNubHybrid().addListener(subscribeCallbackChat);
            }


        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

    }
    //Remove listener from channel
    public void removeListener()
    {
        chatterBoxService.getPubNubHybrid().removeListener(subscribeCallbackChat);
    }



    /**
     * Publishes chat message to hybrid channel i.e to save in history of chat
     * @param channel name of channel to publish to
     * @param chatDataObject contains chat message
     */
    public void publishHybrid(final String channel, BeansMessage chatDataObject) {
        try {
            chatterBoxService.getPubNubHybrid().publish()
                    .message(chatDataObject)
                    .channel("TestChatNewest")
                    .async(new PNCallback<PNPublishResult>() {
                        @Override
                        public void onResponse(PNPublishResult result, PNStatus status) {

                           /* Uri uri = Uri.parse(unescape(status.getClientRequest().toString()));
                            String protocol = uri.getScheme();
                            String server = uri.getAuthority();
                            String path = uri.getPath();
                            Set<String> args = uri.getQueryParameterNames();
                            String msg = uri.getQueryParameter("msg");
                            Log.e("PublishSecond",msg+"@@@"+args.toString()+"   "+unescape(status.getClientRequest().toString()));*/
                            // handle publish result, status always present, result if successful
                            // status.isError to see if error happened
                        }
                    });

        } catch (Exception e) {
            Log.e(TAG, "exception while publishing message", e);
        }
    }


    /**
     * Callback to UI
     * @param beansChatMessages chat message received
     */
    public void publishMessageData(BeansMessage beansChatMessages)
    {
        listener.onMessage(beansChatMessages);
    }

}