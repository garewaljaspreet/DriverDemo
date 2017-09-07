package com.example.navde.demodriver;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.navde.demodriver.event.Events;
import com.example.navde.demodriver.event.GlobalBus;
import com.example.navde.demodriver.models.BeansPickAddress;
import com.example.navde.demodriver.models.DirectionResults;
import com.example.navde.demodriver.models.Route;
import com.example.navde.demodriver.models.Steps;
import com.example.navde.demodriver.network.NetworkService;
import com.example.navde.demodriver.service.ChatterBoxService;
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

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements View.OnClickListener,OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = MapsActivity.class.getSimpleName();
    SupportMapFragment mapFragment;
    private GoogleMap mMap;
    RelativeLayout rlBottomMain,rlBottomClientDash,rlTop,rlNavigate,rlpriceInfo,rlRating,rlNav;
    CustomTextView txtCurrentLocation;
    RelativeLayout rlCurrentLocation,rlSelectMain,rlRatingSubmit;
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
    int APP_STATE=0;
    private float v;
    private double polyStartLat, polyStartLng,polyEndLat,polyEndLng;
    BeansAPNS beansAPNS;
    int progressBarStatus=0;
    private double driverLat=49.035231,driverLng=-122.795683;
    LatLng startPostion,endPosition;
    LatLng startLat,startLng,endLat,endLng;
    BeansMessage beansMessage;
    boolean IsStartSet=false,IsEndSet=false;
    Resources resources;
    CustomTextView txtRequestRide;
    private GoogleApiClient mGoogleApiClient;//Provides the entry point to Google Play services.
    private Handler handler;
    private Runnable progressUpdater;
    private AddressResultReceiver mResultReceiver;//Receiver registered with this activity to get the response from FetchAddressIntentService.
    private LatLng startPosition1,startPosition2, startPosition3,startPosition4,startPosition5;
    private int index, next;
    private LatLng sydney;
    RatingBar ratingBar;
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
    protected void onDestroy() {
        GlobalBus.getBus().unregister(this);
        unbindService(serviceConnection);
        super.onDestroy();
    }

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
        GlobalBus.getBus().register(this);
        Intent intent = new Intent(this, ChatterBoxService.class); //Bind service for Pubnub
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

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
        rlNav= (RelativeLayout) findViewById(R.id.rlNav);
        rlNav.setOnClickListener(this);
        rlSelectMain= (RelativeLayout) findViewById(R.id.rlSelectMain);
        txtRequestRide= (CustomTextView) findViewById(R.id.txtRequestRide);
        rlBottomClientDash= (RelativeLayout) findViewById(R.id.rlBottomClientDash);
        rlBottomMain= (RelativeLayout) findViewById(R.id.rlBottomMain);
        rlRating= (RelativeLayout) findViewById(R.id.rlRating);
        rlTop= (RelativeLayout) findViewById(R.id.rlTop);
        rlNavigate= (RelativeLayout) findViewById(R.id.rlNavigate);
        rlRatingSubmit= (RelativeLayout) findViewById(R.id.rlRatingSubmit);
        rlRatingSubmit.setOnClickListener(this);
        rlCurrentLocation= (RelativeLayout) findViewById(R.id.rlCurrentLocation);
        rlpriceInfo= (RelativeLayout) findViewById(R.id.rlpriceInfo);
        rlpriceInfo.setOnClickListener(this);
        txtCurrentLocation= (CustomTextView) findViewById(R.id.txtCurrentLocation);
        txtCurrentLocation.setOnClickListener(this);
        ratingBar=(RatingBar) findViewById(R.id.ratingBar);
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.parseColor("#05AA63"), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);

        ratingBar.setRating(Float.parseFloat("5.0"));
        mapFragment.getMapAsync(MapsActivity.this);

        btnPick.setChecked(true);

       btnPick.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               if(isChecked)
               {
                   txtCurrentLocation.setText("Online");
                   mMap.clear();
                   marker4 = mMap.addMarker(new MarkerOptions().position(sydney)
                           .flat(true)
                           .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_driver_on)));
                   marker4.setAnchor(0.5f, 0.5f);
                   mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                           .target(sydney)
                           .zoom(17)
                           .build()));
               }
               else
               {
                   txtCurrentLocation.setText("Offline");
                   mMap.clear();
                   marker4 = mMap.addMarker(new MarkerOptions().position(sydney)
                           .flat(true)
                           .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_driver_off)));
                   marker4.setAnchor(0.5f, 0.5f);
                   mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                           .target(sydney)
                           .zoom(17)
                           .build()));
               }
           }
       });


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

            case R.id.rlpriceInfo:

                if(APP_STATE==1)
                {

                    APP_STATE=2;
                    changeViewState(2);
                    startPostion=new LatLng(sydney.latitude,sydney.longitude);
                    endPosition=new LatLng(beansMessage.getUserStartLat(),beansMessage.getUserStartLng());
                    BeansMessage beanMessage=new BeansMessage();
                    beanMessage.setDrivarLat(sydney.latitude);
                    beanMessage.setDriverLng(sydney.longitude);
                    beanMessage.setUserStartLat(beansMessage.getUserStartLat());
                    beanMessage.setUserDestLng(beansMessage.getUserStartLng());
                    beanMessage.setUserDestLat(beansMessage.getUserDestLat());
                    beanMessage.setUserDestLng(beansMessage.getUserDestLng());
                    beanMessage.setMessage("DriverFound");
                    beanMessage.setType("Driver");
                    chatterBoxServiceClient.publishHybrid("",beanMessage);
                    mMap.addMarker(new MarkerOptions().position(endPosition)
                        .flat(true)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.client_pin_centered)));
                    setPolyInfo(startPostion.latitude,startPostion.longitude,endPosition.latitude,endPosition.longitude);
                }
                else if(APP_STATE==4)
                {
                    rlSelectMain.setVisibility(View.GONE);
                    rlBottomMain.setVisibility(View.VISIBLE);
                    rlBottomClientDash.setVisibility(View.VISIBLE);
                    rlNavigate.setBackgroundColor(resources.getColor(R.color.colorGreen));
                    txtRequestRide.setText("Start Trip");
                    rlpriceInfo.setVisibility(View.GONE);
                    rlTop.setVisibility(View.GONE);
                    rlNavigate.setVisibility(View.VISIBLE);
                    mMap.clear();

                    startPostion=endPosition;
                    endPosition=new LatLng(beansMessage.getUserDestLat(),beansMessage.getUserDestLng());
                    mMap.addMarker(new MarkerOptions().position(startPostion)
                            .flat(true)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_driver_on)));
                    mMap.addMarker(new MarkerOptions().position(endPosition)
                            .flat(true)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.client_pin_centered)));
                    setPolyInfo(startPostion.latitude,startPostion.longitude,endPosition.latitude,endPosition.longitude);

                    BeansMessage beanMessage=new BeansMessage();
                    beanMessage.setDrivarLat(startPostion.latitude);
                    beanMessage.setDriverLng(startPostion.longitude);
                    beanMessage.setUserStartLat(beansMessage.getUserStartLat());
                    beanMessage.setUserDestLng(beansMessage.getUserStartLng());
                    beanMessage.setUserDestLat(beansMessage.getUserDestLat());
                    beanMessage.setUserDestLng(beansMessage.getUserDestLng());
                    beanMessage.setMessage("TripStarted");
                    beanMessage.setType("Driver");
                    chatterBoxServiceClient.publishHybrid("",beanMessage);
                }
                else if(APP_STATE==5)
                {
                    rlSelectMain.setVisibility(View.GONE);
                    rlBottomMain.setVisibility(View.GONE);
                    rlBottomClientDash.setVisibility(View.GONE);
                    rlpriceInfo.setVisibility(View.GONE);
                    rlTop.setVisibility(View.GONE);
                    rlNavigate.setVisibility(View.GONE);
                    rlRating.setVisibility(View.VISIBLE);
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(endPosition)
                            .flat(true)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_driver_on)));
                    BeansMessage beanMessage=new BeansMessage();
                    beanMessage.setDrivarLat(beansMessage.getUserStartLat());
                    beanMessage.setDriverLng(beansMessage.getUserStartLat());
                    beanMessage.setUserStartLat(beansMessage.getUserStartLat());
                    beanMessage.setUserDestLng(beansMessage.getUserStartLng());
                    beanMessage.setUserDestLat(beansMessage.getUserDestLat());
                    beanMessage.setUserDestLng(beansMessage.getUserDestLng());
                    beanMessage.setMessage("TripCompleted");
                    beanMessage.setType("Driver");
                    chatterBoxServiceClient.publishHybrid("",beanMessage);
                }

                break;
            case R.id.rlRatingSubmit:
                rlRating.setVisibility(View.GONE);
                imgCenterPin.setVisibility(View.GONE);
                initView();
                break;

            case R.id.rlNav:
                navigateToMaps(polyStartLat,polyStartLng,polyEndLat,polyEndLng);
                break;

        }
    }

    private void changeViewState(int state)
    {
        if(state==1)
        {
            rlSelectMain.setVisibility(View.GONE);
            rlBottomMain.setVisibility(View.VISIBLE);
            rlBottomClientDash.setVisibility(View.GONE);
            rlpriceInfo.setBackgroundColor(resources.getColor(R.color.colorGreen));
            txtRequestRide.setText("Accept Trip");
            rlTop.setVisibility(View.VISIBLE);
            Drawable drawable = resources.getDrawable(R.drawable.background_extended);
            customProgressBar.setMax(50000); // Maximum Progress
            customProgressBar.setProgress(100);
            customProgressBar.setProgressDrawable(drawable);
            setProgressAnimate(customProgressBar,500);

        }
        else if(state==2)
        {
            rlSelectMain.setVisibility(View.GONE);
            rlBottomMain.setVisibility(View.VISIBLE);
            rlBottomClientDash.setVisibility(View.VISIBLE);
            rlpriceInfo.setVisibility(View.GONE);
            rlTop.setVisibility(View.GONE);
            rlNavigate.setVisibility(View.VISIBLE);

        }
        else if(state==4)
        {
            rlSelectMain.setVisibility(View.GONE);
            rlBottomMain.setVisibility(View.VISIBLE);
            rlBottomClientDash.setVisibility(View.VISIBLE);
            rlpriceInfo.setBackgroundColor(resources.getColor(R.color.colorGreen));
            txtRequestRide.setText("Start Trip");
            rlpriceInfo.setVisibility(View.VISIBLE);
            rlTop.setVisibility(View.GONE);
            rlNavigate.setVisibility(View.VISIBLE);

        }
        else if(state==5)
        {
            rlSelectMain.setVisibility(View.GONE);
            rlBottomMain.setVisibility(View.VISIBLE);
            rlBottomClientDash.setVisibility(View.VISIBLE);
            rlpriceInfo.setBackgroundColor(resources.getColor(R.color.colorGreen));
            txtRequestRide.setText("Complete Trip");
            rlpriceInfo.setVisibility(View.VISIBLE);
            rlTop.setVisibility(View.GONE);
            rlNavigate.setVisibility(View.VISIBLE);

        }
    }

    /**
     * Method to initialize pubnub listener. Called only once from this base activity.
     */
    public void initListenerPubnub()
    {
        if(chatterBoxServiceClient!=null)
        {
            chatterBoxServiceClient.subscribeChat("");
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
            Log.e("Oyeeee","callback");
            if(message.getMessage().equals("FindDriver"))
            {

                GlobalBus.getBus().post(new Events("FindDriver",message,null));

            }
            else if(message.getMessage().equals("StartTrip"))
            {
                GlobalBus.getBus().post(new Events("StartTrip",message,null));
            }
            else if(message.getMessage().equals("CompleteTrip"))
            {
                GlobalBus.getBus().post(new Events("CompleteTrip",message,null));
            }

        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Events eventInfo) {
        if(eventInfo.getEventType().equals("FindDriver"))
        {
            beansMessage=eventInfo.getMessage();
            APP_STATE=1;
           changeViewState(1);
        }
        else if(eventInfo.getEventType().equals("StartTrip"))
        {
            beansMessage=eventInfo.getMessage();
            APP_STATE=4;
            changeViewState(4);
        }
        else if(eventInfo.getEventType().equals("CompleteTrip"))
        {
            beansMessage=eventInfo.getMessage();
            APP_STATE=5;
            changeViewState(5);
        }
    }

    private void setPolyInfo(double polyStartLatitude,double polyStartLong,double polyEndLatitude,double polyEndLong)
    {
        this.polyStartLat=polyStartLatitude;
        this.polyStartLng=polyStartLong;
        this.polyEndLat=polyEndLatitude;
        this.polyEndLng=polyEndLong;
        showPolyAnim(polyStartLat,polyStartLng,polyEndLat,polyEndLng);
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

    private void navigateToMaps(double startLat,double startLong,double endLat,double endLng)
    {
        String uri = "http://maps.google.com/maps?saddr=" + startLat + "," + startLong + "&daddr=" + endLat + "," + endLng;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }

    private void initView()
    {
        rlSelectMain.setVisibility(View.VISIBLE);
        mMap.clear();
        marker4 = mMap.addMarker(new MarkerOptions().position(sydney)
                .flat(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_driver_on)));
        marker4.setAnchor(0.5f, 0.5f);
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                .target(sydney)
                .zoom(17)
                .build()));
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
       // routelist.add(startPostion);
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

       // routelist.add(endPosition);
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

    private void setProgressAnimate(ProgressBar pb, int progressTo)
    {

        ObjectAnimator animation = ObjectAnimator.ofInt(pb, "progress", pb.getProgress(), progressTo * 100);
        animation.setDuration(5000);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
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
        initView();
        Location location1=new Location("a");
        location1.setLongitude(49.10449219998637);
        location1.setLatitude(-122.80433737625849);

        Location location2=new Location("b");
        location2.setLongitude(49.10668426706855);
        location2.setLatitude(-122.80515432357788);


        buildGoogleApiClient();



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
