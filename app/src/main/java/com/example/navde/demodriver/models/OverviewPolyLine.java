package com.example.navde.demodriver.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by navdeepg on 2017-08-31.
 */

public class OverviewPolyLine {

    @SerializedName("points")
    public String points;

    public String getPoints() {
        return points;
    }
}