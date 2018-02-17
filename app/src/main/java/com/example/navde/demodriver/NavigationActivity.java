package com.example.navde.demodriver;

/**
 * Created by navdeepg on 2017-08-29.
 */

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.services.android.location.LostLocationEngine;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationView;
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions;
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback;
import com.mapbox.services.android.navigation.ui.v5.instruction.InstructionView;
import com.mapbox.services.android.navigation.ui.v5.listeners.NavigationListener;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationUnitType;
import com.mapbox.services.android.navigation.v5.routeprogress.ProgressChangeListener;
import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress;
import com.mapbox.services.android.telemetry.location.LocationEngine;
import com.mapbox.services.commons.models.Position;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NavigationActivity extends AppCompatActivity implements NavigationListener{

    AutoCompleteTextView places;
    MyPlacesAdapter adapter;
    NavigationView navigationView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.Theme_AppCompat_NoActionBar);
       // setTheme(R.style.NavigationViewLight);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        navigationView = findViewById(R.id.navigationView);
        navigationView.onCreate(savedInstanceState);
        Point origin = Point.fromLngLat(-122.800880, 49.032459);
        Point destination = Point.fromLngLat(-122.814289, 49.116873);
        MapboxNavigation navigation = new MapboxNavigation(this, getResources().getString(R.string.access_token));
        boolean simulateRoute = true;

// Create a NavigationViewOptions object to package everything together
        NavigationViewOptions options = NavigationViewOptions.builder()
                .origin(origin)
                .destination(destination)
                .awsPoolId(null)
                .shouldSimulateRoute(simulateRoute)
                .navigationListener(this)
                .build();

// Call this method with Context from within an Activity
        NavigationLauncher.startNavigation(this, options);

    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        navigationView.onLowMemory();
    }

    @Override
    public void onBackPressed() {
        // If the navigation view didn't need to do anything, call super
      /*  if (!navigationView.onBackPressed()) {
            super.onBackPressed();
        }*/
        Log.e("OnBack","Cancel");
        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        navigationView.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        navigationView.onRestoreInstanceState(savedInstanceState);
    }


    @Override
    public void onCancelNavigation() {
        Log.e("OnBack","NAvCancel");
    }

    @Override
    public void onNavigationFinished() {
        Log.e("OnBack","NAvCancelFinish ");
    }

    @Override
    public void onNavigationRunning() {

    }
}
