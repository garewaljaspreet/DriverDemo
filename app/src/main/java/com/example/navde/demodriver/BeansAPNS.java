package com.example.navde.demodriver;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by navdeepg on 2/11/2016.
 */
public class BeansAPNS implements Parcelable {

    Double startLongitude,endLatitude,endLongitude;

    public Double getStartLongitude() {
        return startLongitude;
    }

    public void setStartLongitude(Double startLongitude) {
        this.startLongitude = startLongitude;
    }

    public Double getStartLatitude() {
        return startLatitude;
    }

    public void setStartLatitude(Double startLatitude) {
        this.startLatitude = startLatitude;
    }

    Double startLatitude;



    public Double getEndLatitude() {
        return endLatitude;
    }

    public void setEndLatitude(Double endLatitude) {
        this.endLatitude = endLatitude;
    }

    public Double getEndLongitude() {
        return endLongitude;
    }

    public void setEndLongitude(Double endLongitude) {
        this.endLongitude = endLongitude;
    }


    public String getStrStartAdd() {
        return strStartAdd;
    }

    public void setStrStartAdd(String strStartAdd) {
        this.strStartAdd = strStartAdd;
    }

    public String getStrEndAdd() {
        return strEndAdd;
    }

    public void setStrEndAdd(String strEndAdd) {
        this.strEndAdd = strEndAdd;
    }

    String strStartAdd,strEndAdd;

    @Override
    public int describeContents() {
        return 0;
    }

    public BeansAPNS() {
    }

    public BeansAPNS(Parcel source) {
        readFromParcel(source);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(strStartAdd);
        dest.writeDouble(startLatitude);
        dest.writeDouble(startLongitude);
        dest.writeString(strEndAdd);
        dest.writeDouble(endLatitude);
        dest.writeDouble(endLongitude);
    }

    public void readFromParcel(Parcel source) {
        strStartAdd=source.readString();
        startLatitude=source.readDouble();
        startLongitude=source.readDouble();
        strEndAdd=source.readString();
        endLatitude=source.readDouble();
        endLongitude=source.readDouble();
    }

    public static final Creator<BeansAPNS> CREATOR =
            new Creator<BeansAPNS>() {

                @Override
                public BeansAPNS createFromParcel(Parcel source) {
                    return new BeansAPNS(source);
                }

                @Override
                public BeansAPNS[] newArray(int size) {
                    return new BeansAPNS[size];
                }
            };
}