package com.example.navde.demodriver.models;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by navdeepg on 2/11/2016.
 */
public class BeansPickAddress implements Parcelable{

    public String getStreet1() {
        return street1;
    }

    public void setStreet1(String street1) {
        this.street1 = street1;
    }

    String street1;

    public String getStreet2() {
        return street2;
    }

    public void setStreet2(String street2) {
        this.street2 = street2;
    }

    String street2;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    String city;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    String country;

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    String postalCode;

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    String latitude;

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    String longitude;

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    String region;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String id;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(street1);
        dest.writeString(street2);
        dest.writeString(city);
        dest.writeString(country);
        dest.writeString(postalCode);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(region);
        dest.writeString(name);
        dest.writeString(id);

    }
    public BeansPickAddress() {
        // Normal actions performed by class, since this is still a normal object!
    }

    public BeansPickAddress(Parcel source) {
        street1 = source.readString();
        street2 = source.readString();
        city = source.readString();
        country = source.readString();
        postalCode = source.readString();
        latitude = source.readString();
        longitude = source.readString();
        region = source.readString();
        name = source.readString();
        id = source.readString();
    }



    public static final Creator<BeansPickAddress> CREATOR =
            new Creator<BeansPickAddress>(){

                @Override
                public BeansPickAddress createFromParcel(Parcel source) {
                    return new BeansPickAddress(source);
                }

                @Override
                public BeansPickAddress[] newArray(int size) {
                    return new BeansPickAddress[size];
                }
            };
}
