package com.example.navde.demodriver.event;

/**
 * Created by navdeepg on 1/9/2017.
 */
public class MessageEvent {
    String strMessage,strType;

    public MessageEvent(String strMessage, String strType)
    {
        this.strMessage=strMessage;
        this.strType=strType;
    }

    public String getMessage()
    {
        return strMessage;
    }

    public String getType()
    {
        return strType;
    }

}
