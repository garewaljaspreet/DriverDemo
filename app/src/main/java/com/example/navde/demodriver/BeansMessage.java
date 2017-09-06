package com.example.navde.demodriver;

/**
 * Created by navde on 2017-09-04.
 */

public class BeansMessage {

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    String message,type;

    public Double getUserStartLat() {
        return userStartLat;
    }

    public void setUserStartLat(Double userStartLat) {
        this.userStartLat = userStartLat;
    }

    public Double getDriverLng() {
        return driverLng;
    }

    public void setDriverLng(Double driverLng) {
        this.driverLng = driverLng;
    }

    public Double getDrivarLat() {
        return drivarLat;
    }

    public void setDrivarLat(Double drivarLat) {
        this.drivarLat = drivarLat;
    }

    public Double getUserDestLat() {
        return userDestLat;
    }

    public void setUserDestLat(Double userDestLat) {
        this.userDestLat = userDestLat;
    }

    public Double getUserDestLng() {
        return userDestLng;
    }

    public void setUserDestLng(Double userDestLng) {
        this.userDestLng = userDestLng;
    }

    public Double getUserStartLng() {
        return userStartLng;
    }

    public void setUserStartLng(Double userStartLng) {
        this.userStartLng = userStartLng;
    }

    Double userStartLat,userStartLng,userDestLng,userDestLat,drivarLat,driverLng;
}
