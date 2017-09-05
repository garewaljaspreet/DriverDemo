package com.example.navde.demodriver;

import android.util.Log;

import com.example.navde.demodriver.models.DirectionResults;
import com.example.navde.demodriver.network.NetworkService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by navdeepg on 2017-08-24.
 */

public class RoadsPresenter implements RoadsPresenterInteractor{
    MapsActivity view;
    private NetworkService service;
    public RoadsPresenter(MapsActivity view, NetworkService service){
        this.view = view;
        this.service = service;
    }

    @Override
    public void getRoute(String URL, final int route) {
        Call<BeansMain> call = service.getAPI().getPath(URL,
                true,"AIzaSyAiZW5l6Si2-SZ_L_bfk_4o6sJqXaQuH6o");
        call.enqueue(new Callback<BeansMain>() {
            @Override
            public void onResponse(Call<BeansMain> call, Response<BeansMain> response) {
            }

            @Override
            public void onFailure(Call<BeansMain> call, Throwable t) {
                Log.e("Error","error");
            }
        });
    }

    @Override
    public void getDirection(String start, String end) {

        Call<DirectionResults> call = service.getAPI().getDirection(start,end,"AIzaSyCSnJ3DYh4SyUjHle6iHOea3GMpZnCcMjM");
        call.enqueue(new Callback<DirectionResults>() {
            @Override
            public void onResponse(Call<DirectionResults> call, Response<DirectionResults> response) {
                Log.e("Success","oyeeee"+response.body().toString());
                view.onDirectionResult(response.body());
            }

            @Override
            public void onFailure(Call<DirectionResults> call, Throwable t) {
                Log.e("Error","error");
            }
        });
    }
}
