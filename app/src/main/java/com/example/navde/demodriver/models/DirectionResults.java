package com.example.navde.demodriver.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by navdeepg on 2017-08-31.
 */

public class DirectionResults {
    @SerializedName("routes")
    private List<Route> routes;

    public List<Route> getRoutes() {
        return routes;
    }}
