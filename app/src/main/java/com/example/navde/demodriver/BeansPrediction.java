package com.example.navde.demodriver;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by navdeepg on 2/11/2016.
 */
public class BeansPrediction implements Parcelable {


    String strFullTxt;

    public String getStrPlaceId() {
        return strPlaceId;
    }

    public void setStrPlaceId(String strPlaceId) {
        this.strPlaceId = strPlaceId;
    }

    String strPlaceId;

    public String getStrFullTxt() {
        return strFullTxt;
    }

    public void setStrFullTxt(String strFullTxt) {
        this.strFullTxt = strFullTxt;
    }

    public String getStrPrimaryTxt() {
        return strPrimaryTxt;
    }

    public void setStrPrimaryTxt(String strPrimaryTxt) {
        this.strPrimaryTxt = strPrimaryTxt;
    }

    String strPrimaryTxt;

    @Override
    public int describeContents() {
        return 0;
    }

    public BeansPrediction() {
    }

    public BeansPrediction(Parcel source) {
        readFromParcel(source);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(strFullTxt);
        dest.writeString(strPrimaryTxt);
        dest.writeString(strPlaceId);
    }

    public void readFromParcel(Parcel source) {
        strFullTxt=source.readString();
        strPrimaryTxt=source.readString();
        strPlaceId=source.readString();
    }

    public static final Creator<BeansPrediction> CREATOR =
            new Creator<BeansPrediction>() {

                @Override
                public BeansPrediction createFromParcel(Parcel source) {
                    return new BeansPrediction(source);
                }

                @Override
                public BeansPrediction[] newArray(int size) {
                    return new BeansPrediction[size];
                }
            };
}