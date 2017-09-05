package com.example.navde.demodriver;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.navde.demodriver.models.BeansPickAddress;
import com.example.navde.demodriver.models.DirectionResults;
import com.example.navde.demodriver.models.Route;
import com.example.navde.demodriver.models.Steps;
import com.example.navde.demodriver.network.NetworkService;
import com.example.navde.demodriver.service.DefaultChatterBoxCallback;
import com.example.navde.demodriver.service.binder.ChatterBoxClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements View.OnClickListener,OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = MapsActivity.class.getSimpleName();
    SupportMapFragment mapFragment;
    private GoogleMap mMap;

    CustomTextView txtCurrentLocation;
    RelativeLayout rlCurrentLocation,rlSelectMain;
    private List<LatLng> polyLineList;
    private Marker marker1,marker2,marker3,marker4,marker5;
    boolean IS_ADDRESS_SEARCHED=false;// check is address search is in process or not to avoid multiple calls
    String addressText;
    ProgressBar customProgressBar;
    private Address addressObj;
    private Float zoomLevel=17f;//initial map zoom level
    private Location mLastLocation,mCurrentLoc;//Represents last known location
    ImageView imgCenterPin;
    SwitchCompat btnPick;
    private BeansPickAddress locationObj,oldLocationObj;
    private ChatterBoxClient chatterBoxServiceClient;       //To access service methods
    private float v;
    BeansAPNS beansAPNS;
    int progressBarStatus=0;
    private double driverLat=49.035231,driverLng=-122.795683;
    LatLng startPostion,endPosition;
    LatLng startLat,startLng,endLat,endLng;
    boolean IsStartSet=false,IsEndSet=false;
    Resources resources;
    private GoogleApiClient mGoogleApiClient;//Provides the entry point to Google Play services.
    private Handler handler;
    private Runnable progressUpdater;
    private AddressResultReceiver mResultReceiver;//Receiver registered with this activity to get the response from FetchAddressIntentService.
    private LatLng startPosition1,startPosition2, startPosition3,startPosition4,startPosition5;
    private int index, next;
    private LatLng sydney;
    private Button button;
    String url1="49.03475205301813,-122.80113101005554|49.03475205301813,-122.80068039894104|49.03444959709725,-122.80039072036743|49.03401349462516,-122.80037999153137|49.03384467973859,-122.8006911277771|49.03408383399211,-122.80110955238342|49.03458324063788,-122.80110955238342";
    String url2="49.03506857468707,-122.80115246772766|49.03587745424663,-122.80116319656372|49.03655971634473,-122.80116319656372|49.03655971634473,-122.80261158943176|49.03638387649291,-122.80413508415222|49.03574381418296,-122.80401706695557|49.03488569574625,-122.80338406562805|49.034745019180384,-122.80247211456299|49.03475205301813,-122.80115246772766";
    String url3="49.031129494946306,-122.80116319656372|49.03219870457269,-122.8011417388916|49.03331009548515,-122.80120611190796|49.034322987095955,-122.80120611190796|49.03475908685489,-122.80118465423584";
    String url4="49.032944324001825,-122.80037999153137|49.03359145633461,-122.80036926269531|49.034365190465515,-122.80039072036743|49.03475205301813,-122.80043363571167|49.03476612069063,-122.80100226402283|49.034745019180384,-122.80256867408752|49.03433705488978,-122.80373811721802|49.03369696624291,-122.8043282032013";
    ArrayList<LatLng> firstRouteList,secondRouteList,thirdRouteList,fourthRouteList;
    private LocationRequest mLocationRequest;
    private PolylineOptions polylineOptions, blackPolylineOptions;
    private Polyline blackPolyline, greyPolyLine;
    RoadsPresenterInteractor presenter;
    int i=0,j=0,k=0,l=0,m=0;
    boolean isMarkerRotating=false;
    NetworkService service;

    /**
     * Service to connect with Pubnub
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            chatterBoxServiceClient = (ChatterBoxClient) service;
            initListenerPubnub();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

    };

    @Override
    public void onStop() {
        super.onStop();
        if(mGoogleApiClient!=null)
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, mLocationCallback);
                mGoogleApiClient.disconnect();
            }
        if(mCurrentLoc!=null)
            saveLastLoc(mCurrentLoc.getLatitude(),mCurrentLoc.getLongitude(),this);
        else {
            if(mLastLocation!=null)
                saveLastLoc(mLastLocation.getLatitude(),mLastLocation.getLongitude(),this);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        resources=getResources();
        mResultReceiver = new AddressResultReceiver(new Handler());
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(3 * 1000).setNumUpdates(1); // 1 second, in milliseconds; // 1 second, in milliseconds
        service = ApplicationData.getInstance().getNetworkService();
        presenter=new RoadsPresenter(this,service);
        presenter.getRoute(url1,1);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        polyLineList = new ArrayList<>();
        imgCenterPin= (ImageView) findViewById(R.id.imgCenterPin);
        customProgressBar= (ProgressBar) findViewById(R.id.customProgressBar);
        btnPick= (SwitchCompat) findViewById(R.id.btnPick);
        rlSelectMain= (RelativeLayout) findViewById(R.id.rlSelectMain);
        rlCurrentLocation= (RelativeLayout) findViewById(R.id.rlCurrentLocation);
        txtCurrentLocation= (CustomTextView) findViewById(R.id.txtCurrentLocation);
        txtCurrentLocation.setOnClickListener(this);
        mapFragment.getMapAsync(MapsActivity.this);

        btnPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mMap.addMarker(new MarkerOptions().position(new LatLng(startPostion.latitude,startPostion.longitude))
                        .flat(true)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.client_pin_centered)));
                imgCenterPin.setBackgroundResource(R.drawable.destination_flag);
                IsStartSet=true;
            }
        });

      /*  String requestUrl = null;
        try {
            requestUrl = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "mode=driving&"
                    + "transit_routing_preference=less_driving&"
                    + "origin=" + latitude + "," + longitude + "&"
                    + "destination=" + destination + "&"
                    + "key=" + "AIzaSyDIaU9M7cN8z7LG2zJjWmfPWebtSClJSRQ";
            Log.d(TAG, requestUrl);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                    requestUrl, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, response + "");
                            try {
                                JSONArray jsonArray = response.getJSONArray("routes");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject route = jsonArray.getJSONObject(i);
                                    JSONObject poly = route.getJSONObject("overview_polyline");
                                    String polyline = poly.getString("points");
                                    polyLineList = decodePoly(polyline);
                                    Log.d(TAG, polyLineList + "");
                                }
                                //Adjusting bounds
                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                for (LatLng latLng : polyLineList) {
                                    builder.include(latLng);
                                }
                                LatLngBounds bounds = builder.build();
                                CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 2);
                                mMap.animateCamera(mCameraUpdate);

                                polylineOptions = new PolylineOptions();
                                polylineOptions.color(Color.GRAY);
                                polylineOptions.width(5);
                                polylineOptions.startCap(new SquareCap());
                                polylineOptions.endCap(new SquareCap());
                                polylineOptions.jointType(ROUND);
                                polylineOptions.addAll(polyLineList);
                                greyPolyLine = mMap.addPolyline(polylineOptions);

                                blackPolylineOptions = new PolylineOptions();
                                blackPolylineOptions.width(5);
                                blackPolylineOptions.color(Color.BLACK);
                                blackPolylineOptions.startCap(new SquareCap());
                                blackPolylineOptions.endCap(new SquareCap());
                                blackPolylineOptions.jointType(ROUND);
                                blackPolyline = mMap.addPolyline(blackPolylineOptions);

                                mMap.addMarker(new MarkerOptions()
                                        .position(polyLineList.get(polyLineList.size() - 1)).flat(true));

                                ValueAnimator polylineAnimator = ValueAnimator.ofInt(0, 100);
                                polylineAnimator.setDuration(2000);
                                polylineAnimator.setInterpolator(new LinearInterpolator());
                                polylineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                        List<LatLng> points = greyPolyLine.getPoints();
                                        int percentValue = (int) valueAnimator.getAnimatedValue();
                                        int size = points.size();
                                        int newPoints = (int) (size * (percentValue / 100.0f));
                                        List<LatLng> p = points.subList(0, newPoints);
                                        blackPolyline.setPoints(p);
                                    }
                                });
                                polylineAnimator.start();
                                marker = mMap.addMarker(new MarkerOptions().position(sydney)
                                        .flat(true)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car)));
                                handler = new Handler();
                                index = -1;
                                next = 1;
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (index < polyLineList.size() - 1) {
                                            index++;
                                            next = index + 1;
                                        }
                                        if (index < polyLineList.size() - 1) {
                                            startPosition = polyLineList.get(index);
                                            endPosition = polyLineList.get(next);
                                        }
                                        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
                                        valueAnimator.setDuration(3000);
                                        valueAnimator.setInterpolator(new LinearInterpolator());
                                        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                            @Override
                                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                                v = valueAnimator.getAnimatedFraction();
                                                lng = v * endPosition.longitude + (1 - v)
                                                        * startPosition.longitude;
                                                lat = v * endPosition.latitude + (1 - v)
                                                        * startPosition.latitude;
                                                LatLng newPos = new LatLng(lat, lng);
                                                marker.setPosition(newPos);
                                                marker.setAnchor(0.5f, 0.5f);
                                                //marker.setRotation(getBearing(startPosition, newPos));
                                                rotateMarker(marker,Float.parseFloat(String.valueOf(getBearing(startPosition, newPos))));
                                                marker.setPosition(newPos);
                                                *//*mMap.moveCamera(CameraUpdateFactory
                                                        .newCameraPosition
                                                                (new CameraPosition.Builder()
                                                                        .target(newPos)
                                                                        .zoom(15.5f)
                                                                        .build()));*//*
                                            }
                                        });
                                        valueAnimator.start();
                                        handler.postDelayed(this, 3000);
                                    }
                                }, 3000);


                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, error + "");
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.rlMainRequestTaxiLay:
                break;

            case R.id.txtCurrentLocation:
                  Intent intent=new Intent(MapsActivity.this,FindAddress.class);
                startActivityForResult(intent,1);
                break;


        }
    }

    /**
     * Method to initialize pubnub listener. Called only once from this base activity.
     */
    public void initListenerPubnub()
    {
        if(chatterBoxServiceClient!=null)
        {
            chatterBoxServiceClient.initListener(chatListener);
        }

    }

    /**
     * Callback for pubnub chat listener
     */

    private DefaultChatterBoxCallback chatListener =new DefaultChatterBoxCallback(){

        @Override
        public void onMessage(BeansMessage message) {
            super.onMessage(message);

            Log.e("Message","AYA"+message.getMessage());

        }
    };
    private void initView()
    {
        rlSelectMain.setVisibility(View.VISIBLE);
        mMap.clear();
        imgCenterPin.setVisibility(View.VISIBLE);
        imgCenterPin.setBackgroundResource(R.drawable.client_pin_centered);
        IsStartSet=false;
        IsEndSet=false;
    }
    /*private float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);

        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }
*/

    private void rotateMarker(final Marker marker, final float toRotation) {
        if(!isMarkerRotating) {
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            final float startRotation = marker.getRotation();
            final long duration = 1000;

            final Interpolator interpolator = new LinearInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    isMarkerRotating = true;

                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed / duration);

                    float rot = t * toRotation + (1 - t) * startRotation;

                    marker.setRotation(-rot > 180 ? rot / 2 : rot);
                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    } else {
                        isMarkerRotating = false;
                    }
                }
            });
        }
    }

    private double getBearing(LatLng latLng1,LatLng latLng2) {

        double PI = 3.14159;
        double lat1 = latLng1.latitude * PI / 180;
        double long1 = latLng1.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return brng;
    }

    static void animateUserMarkerToGB(final Marker marker, final LatLng finalPosition) {
        final LatLng startPosition = marker.getPosition();
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();

        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final float durationInMs = 2500;

        handler.post(new Runnable() {
            long elapsed;
            float t;
            float v;

            @Override
            public void run() {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;
                v = interpolator.getInterpolation(t);
                marker.setPosition(new LatLngInterpolator.Spherical().interpolate(v, startPosition, finalPosition));
                //rotateMarker(marker,Float.parseFloat(String.valueOf(getBearing(startPosition, finalPosition))),v,startPosition,finalPosition);
                // Repeat till progress is complete.
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    // fragment.IS_USER_ANIM=false;
                }
            }
        });
    }

    private void rotateMarker(final Marker marker, final LatLng destination, final float rotation) {

        if (marker != null) {

            final LatLng startPosition = marker.getPosition();
            final float startRotation = marker.getRotation();

            final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.Spherical();
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(2500); // duration 3 second
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {

                    try {
                        float v = animation.getAnimatedFraction();
                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, destination);
                        float bearing = computeRotation(v, startRotation, rotation);

                        marker.setRotation(bearing);
                        marker.setPosition(newPosition);

                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
            valueAnimator.start();
        }
    }
    private static float computeRotation(float fraction, float start, float end) {
        float normalizeEnd = end - start; // rotate start to 0
        float normalizedEndAbs = (normalizeEnd + 360) % 360;

        float direction = (normalizedEndAbs > 180) ? -1 : 1; // -1 = anticlockwise, 1 = clockwise
        float rotation;
        if (direction > 0) {
            rotation = normalizedEndAbs;
        } else {
            rotation = normalizedEndAbs - 360;
        }

        float result = fraction * rotation + start;
        return (result + 360) % 360;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==2)
        {
            rlSelectMain.setVisibility(View.GONE);
            beansAPNS=data.getParcelableExtra("addressInfo");
            Log.e("STRATRT",beansAPNS.getStartLatitude()+"   "+beansAPNS.getStartLongitude());
            Marker markerStart = mMap.addMarker(new MarkerOptions().position(new LatLng(beansAPNS.getStartLatitude(),beansAPNS.getStartLongitude()))
                    .flat(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.client_pin_centered)));
            Marker markerend = mMap.addMarker(new MarkerOptions().position(new LatLng(beansAPNS.getEndLatitude(),beansAPNS.getEndLongitude()))
                    .flat(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.destination_flag)));
            startPostion=new LatLng(beansAPNS.getStartLatitude(),beansAPNS.getStartLongitude());
            endPosition=new LatLng(beansAPNS.getEndLatitude(),beansAPNS.getEndLongitude());
            showPolyAnim(beansAPNS.getStartLatitude(),beansAPNS.getStartLongitude(),beansAPNS.getEndLatitude(),beansAPNS.getEndLongitude());
        }

    }

    private void showPolyAnim(double startLat,double startLong,double endLat,double endLng)
    {
        LatLngBounds.Builder b = new LatLngBounds.Builder();
        b.include(new LatLng(startLat,startLong));
        b.include(new LatLng(endLat,endLng));
        LatLngBounds bounds = b.build();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels - getResources().getDisplayMetrics().heightPixels / 2;
        int padding = (int) (width * 0.10);
        //Change the padding as per needed
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        mMap.animateCamera(cu);
        service.changeApiBaseUrl("http://maps.googleapis.com");
        presenter.getDirection(startLat+","+startLong,endLat+","+endLng);
    }

    private void startAnim(ArrayList<LatLng> routelist){
        if(mMap != null) {
            MapAnimator.getInstance().animateRoute(mMap, routelist);
        } else {
            Toast.makeText(getApplicationContext(), "Map not ready", Toast.LENGTH_LONG).show();
        }
    }

    protected void onDirectionResult(DirectionResults directionResults)
    {
        ArrayList<LatLng> routelist = new ArrayList<LatLng>();
        routelist.add(startPostion);
        if(directionResults.getRoutes().size()>0){
            List<LatLng> decodelist;
            Route routeA = directionResults.getRoutes().get(0);
            Log.i("zacharia", "Legs length : "+routeA.getLegs().size());
            if(routeA.getLegs().size()>0){
                List<Steps> steps= routeA.getLegs().get(0).getSteps();
                Log.i("zacharia","Steps size :"+steps.size());
                Steps step;
                com.example.navde.demodriver.models.Location location;
                String polyline;
                for(int i=0 ; i<steps.size();i++){
                    step = steps.get(i);
                    location =step.getStart_location();
                    routelist.add(new LatLng(location.getLat(), location.getLng()));
                    Log.i("zacharia", "Start Location :" + location.getLat() + ", " + location.getLng());
                    polyline = step.getPolyline().getPoints();
                    decodelist = PolyUtil.decode(polyline);
                    routelist.addAll(decodelist);
                    location =step.getEnd_location();
                    routelist.add(new LatLng(location.getLat() ,location.getLng()));
                    Log.i("zacharia","End Location :"+location.getLat() +", "+location.getLng());
                }
            }
        }

        routelist.add(endPosition);
        startAnim(routelist);
    }

    protected LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if((locationResult!=null)&&(locationResult.getLocations().size()>0))
            {
                Location location=locationResult.getLocations().get(0);
                mCurrentLoc=location;
                initCamera( location );
                // It is possible that the user presses the button to get the address before the
                // GoogleApiClient object successfully connects. In such a case, mAddressRequested
                // is set to true, but no attempt is made to fetch the address (see
                // fetchAddressButtonHandler()) . Instead, we start the intent service here if the
                // user has requested an address, since we now have a connection to GoogleApiClient.
                startIntentService(null,location);

            }
        }


    };
    /**
     * Builds a GoogleApiClient. Uses {@code #addApi} to request the LocationServices API.
     */
    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(MapsActivity.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        if(mGoogleApiClient!=null)
            mGoogleApiClient.connect();

    }

    private void startAnim()
    {
        /*new Thread(new Runnable() {
            public void run() {
                while (progressBarStatus < 100) {

                    // process some tasks
                    progressBarStatus = doSomeTasks();

                    // your computer is too fast, sleep 1 second
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Update the progress bar
                    progressBarHandler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressBarStatus);
                        }
                    });
                }

                // ok, file is downloaded,
                if (progressBarStatus >= 100) {

                    // sleep 2 seconds, so that you can see the 100%
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // close the progress bar dialog
                    customProgressBar.dismiss();
                }
            }
        }).start();
*/

    }
    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        // Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
       /* try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                // Determine whether a Geocoder is available.
                if (!Geocoder.isPresent()) {
                    Toast.makeText(MapsActivity.this, R.string.no_geocoder_available, Toast.LENGTH_LONG).show();
                    return;
                }
                initCamera( mLastLocation );
                // It is possible that the user presses the button to get the address before the
                // GoogleApiClient object successfully connects. In such a case, mAddressRequested
                // is set to true, but no attempt is made to fetch the address (see
                // fetchAddressButtonHandler()) . Instead, we start the intent service here if the
                // user has requested an address, since we now have a connection to GoogleApiClient.
                startIntentService(null,mLastLocation);
                mCurrentLoc=mLastLocation;
            }
            else
            {
                if(!(PreferenceManager.getDefaultSharedPreferences(
                        MapsActivity.this).getString(resources.getString(R.string.loc_lat_lastsaved), "").equals("")))
                {
                    Double lat=Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(
                            MapsActivity.this).getString(resources.getString(R.string.loc_lat_lastsaved), ""));
                    Double lng=Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(
                            MapsActivity.this).getString(resources.getString(R.string.loc_lng_lastsaved), ""));
                    mCurrentLoc=new Location("");
                    mCurrentLoc.setLatitude(lat);
                    mCurrentLoc.setLongitude(lng);
                    initCamera( mCurrentLoc );
                    startIntentService(null,mCurrentLoc);
                }
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,mLocationCallback,null).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                    }
                });
            }
        }
        catch(SecurityException e)
        {
            e.printStackTrace();
        }*/

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    /**
     * Initialize camera view
     * @param location Location to show on mapview
     */
    private void initCamera( Location location ) {
        try {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng( location.getLatitude(),
                    location.getLongitude()), zoomLevel));
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.getUiSettings().setMapToolbarEnabled(false);
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);


        }
        catch(SecurityException e)
        {
            e.printStackTrace();
        }


    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected void startIntentService(LatLng latlng, Location loc) {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(MapsActivity.this, FetchAddressIntentService.class);
        // Pass the result receiver as an extra to the service.
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        if(loc==null)
        {
            Location targetLocation = new Location("");//provider name is unecessary
            targetLocation.setLatitude(latlng.latitude);//your coords of course
            targetLocation.setLongitude(latlng.longitude);
            // Pass the location data as an extra to the service.
            intent.putExtra(Constants.LOCATION_DATA_EXTRA, targetLocation);
            mLastLocation=targetLocation;
        }
        else {
            // Pass the location data as an extra to the service.
            intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        }


        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        final double latitude = 28.671246;
        double longitude = 77.317654;
        mMap.setTrafficEnabled(false);
        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // Add a marker in Home and move the camera
        sydney = new LatLng(driverLat,driverLng);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        marker4 = mMap.addMarker(new MarkerOptions().position(sydney)
                .flat(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_driver_on)));
        marker4.setAnchor(0.5f, 0.5f);
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                .target(sydney)
                .zoom(17)
                .build()));
        Location location1=new Location("a");
        location1.setLongitude(49.10449219998637);
        location1.setLatitude(-122.80433737625849);

        Location location2=new Location("b");
        location2.setLongitude(49.10668426706855);
        location2.setLatitude(-122.80515432357788);


        buildGoogleApiClient();


        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

            @Override
            public void onCameraChange(CameraPosition arg0) {
                if (IS_ADDRESS_SEARCHED == false)
                    if(!IsStartSet)
                    {
                        startPostion=arg0.target;

                    }
                    else if(!IsEndSet)
                    {
                        endPosition=arg0.target;
                    }
                startIntentService(arg0.target, null);

                IS_ADDRESS_SEARCHED = false;
            }
        });
    }

    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         *  Receives data sent from FetchAddressIntentService and updates the UI in WelcomeMainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            /*mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            displayAddressOutput();*/
            locationObj=resultData.getParcelable("address");
            addressText="";
            addressObj=resultData.getParcelable("addressText");
            if(addressObj!=null)
            {
                for (int i=0;i<3;i++)
                {
                    if(addressObj.getAddressLine(i)!=null)
                        addressText=addressText+addressObj.getAddressLine(i)+"\n";
                }
                showMapData();
            }

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                //showToast(getString(R.string.address_found));
            }
            // Reset. Enable the Fetch Address button and stop showing the progress bar.
           /* mAddressRequested = false;
            updateUIWidgets();*/
        }
    }

    // Updates user interface for map
    private void showMapData()
    {
        try {
            Log.e("Add",addressText);

            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.getUiSettings().setMapToolbarEnabled(false);
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
        catch(SecurityException e)
        {
            e.printStackTrace();
        }

    }

    private void saveLastLoc(Double lastLat,Double lastLng,Activity activity)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(resources.getString(R.string.loc_lat_lastsaved), String.valueOf(lastLat));
        editor.putString(resources.getString(R.string.loc_lng_lastsaved), String.valueOf(lastLng));
        editor.apply();
    }


}
