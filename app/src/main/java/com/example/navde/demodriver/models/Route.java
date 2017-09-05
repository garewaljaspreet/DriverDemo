package com.example.navde.demodriver.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by navdeepg on 2017-08-31.
 */

public class Route {
    @SerializedName("overview_polyline")
    private OverviewPolyLine overviewPolyLine;

    private List<Legs> legs;

    public OverviewPolyLine getOverviewPolyLine() {
        return overviewPolyLine;
    }

    public List<Legs> getLegs() {
        return legs;
    }
}
