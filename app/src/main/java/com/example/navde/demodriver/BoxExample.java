package com.example.navde.demodriver;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import static com.google.android.gms.location.places.AutocompleteFilter.TYPE_FILTER_GEOCODE;

/**
 * Created by navdeepg on 2017-08-29.
 */

public class BoxExample extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks{

    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.testmap);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        if(mGoogleApiClient!=null)
            mGoogleApiClient.connect();

        EditText edText=(EditText)findViewById(R.id.edText);

        edText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0)
                {
                    /*PendingResult<AutocompletePredictionBuffer> result =
                            Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient, s.toString(), null, null);

                    result.setResultCallback(new ResultCallback<AutocompletePredictionBuffer>() {
                        @Override public void onResult(@NonNull AutocompletePredictionBuffer autocompletePredictions) {
                            // Iterate through results and display them in list or other UI

                            Iterator<AutocompletePrediction> iterator = autocompletePredictions.iterator();
                            ArrayList resultList = new ArrayList<>(autocompletePredictions.getCount());
                            while (iterator.hasNext()) {
                                AutocompletePrediction prediction = iterator.next();
                                // Only include U.S. cities
                                // States only have 1 comma (e.g. AK, United States). Don't include states.
                               Log.e("Result",prediction.getPlaceId()+"  "+prediction.getFullText(null).toString());


                            }

                        }
                    });*/

                    final PendingResult<PlaceBuffer> placeResult =
                            Places.GeoDataApi.getPlaceById(mGoogleApiClient,"EiMxNjg4IDE1MiBTdHJlZXQsIFN1cnJleSwgQkMsIENhbmFkYQ");
                    placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                        @Override
                        public void onResult(PlaceBuffer places) {
                            if (places.getStatus().isSuccess()) {
                                Log.e("Success"," "+places.get(0).getLatLng().longitude+"    "+places.get(0).getLatLng().latitude+"   ");
                            } else {
                                Log.e("Error","Test  ");
                            }
                        }
                    });


                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("RESULTTT","FAILLED");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        String query = "Main Street";

        LatLng pointA = new LatLng(40.020451, -105.274679);
        LatLng pointB = new LatLng(40.012004, -105.289957);
        LatLngBounds bounds = new LatLngBounds.Builder().include(pointA).include(pointB).build();

        AutocompleteFilter filter = new AutocompleteFilter.Builder()
                .setTypeFilter(TYPE_FILTER_GEOCODE)
                .build();

        PendingResult<AutocompletePredictionBuffer> result =
                Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient, query, bounds, null);

        result.setResultCallback(new ResultCallback<AutocompletePredictionBuffer>() {
            @Override public void onResult(@NonNull AutocompletePredictionBuffer result) {
                Log.e("RESULTTT",result.get(0).toString());
                // Iterate through results and display them in list or other UI
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
