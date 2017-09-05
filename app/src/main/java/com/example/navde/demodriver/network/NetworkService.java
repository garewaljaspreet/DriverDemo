package com.example.navde.demodriver.network;

/**
 * Created by navdeepg on 3/1/2017.
 */

import android.content.Context;
import android.preference.PreferenceManager;

import com.example.navde.demodriver.BeansMain;
import com.example.navde.demodriver.models.DirectionResults;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by navdeepg
 */

public class NetworkService {
    public static String baseUrl ="https://roads.googleapis.com";
    private NetworkAPI networkAPI;
    private OkHttpClient okHttpClient;
    Context mContext;
    public int SERVER_RESPONSE_OK=200;
    public int SERVER_RESPONSE_CREATED=201;
    public int SERVER_RESPONSE_BID_CONFLICT=409;
    public int SERVER_RESPONSE_UNPROCESS_ENTITY=422;
    public int SERVER_RESPONSE_TRIP_CANCELLED=410;
    public int SERVER_RESPONSE_OK_NOCONTENT=204;

    public NetworkService(Context mContext){
        this(baseUrl,mContext);
    }

    public NetworkService(String baseUrl, Context mContext){
        this.mContext=mContext;
        okHttpClient = buildClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        networkAPI = retrofit.create(NetworkAPI.class);
    }

    /**
     * Method to return the API interface.
     * @return
     */
    public NetworkAPI getAPI(){
        return  networkAPI;
    }


    /**
     * Method to build and return an OkHttpClient so we can set/get
     * headers quickly and efficiently.
     * @return
     */
    public OkHttpClient buildClient(){

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor logging=new HttpLoggingInterceptor();
        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());
                // Do anything with response here
                //if we ant to grab a specific cookie or something..
                return response;
            }
        });
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(logging);
        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request=null;
                if(PreferenceManager.getDefaultSharedPreferences(
                        mContext).getString("API_KEY", "").equals(""))
                    request = chain.request().newBuilder().addHeader("Accept", "application/json")
                            .addHeader("Content-Type", "application/json")
                            .build();
                return chain.proceed(request);
            }
        });

        return  builder.build();
    }

    public void changeApiBaseUrl(String newApiBaseUrl) {
        baseUrl = newApiBaseUrl;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        networkAPI = retrofit.create(NetworkAPI.class);
    }

    /**
     * all the Service alls to use for the retrofit requests.
     */
    public interface NetworkAPI {

        @GET("/v1/snapToRoads")//get list of completed trips
        Call<BeansMain> getPath(@Query("path") String path, @Query("interpolate") boolean interpolate, @Query("key") String key);

        @GET("/maps/api/directions/json")//get list of completed trips
        Call<DirectionResults> getDirection(@Query("origin") String origin, @Query("destination") String destination, @Query("path") String path);
        /*@GET("api/v1/rest/aggregate/trips/past-for-customer/{id}")//get list of completed trips
        Call<ArrayList<BeansTripsAggregate>> getPastTrips(@Path("id") String id);

        @GET("api/v1/rest/aggregate/trips/current-for-customer/{id}")//get list of Trips
        Call<ArrayList<BeansTripsAggregate>> getAllTrips(@Path("id") String id);

        @GET("/api/v1/rest/trips/{id}/bids")//get list of available bids
        Call<BidsListResponse> getBidsList(@Path("id") String id);

        @GET("api/v1/rest/customers/{id}/undeleted-addresses")//get list of saved addresses
        Call<ArrayList<BeansPickAddress>> getAddress(@Path("id") String id);

        @GET("api/v1/action/braintree/json-token/{braintreeToken}")//get list of saved addresses
        Call<TokenModel> getPaymentToken(@Path("braintreeToken") String braintreeToken);

        @GET("api/v1/rest/customers/{id}/undeleted-vehicles")//get list of saved vehicles
        Call<ArrayList<BeansVehicleMain>> getVehicles(@Path("id") String id);

        @POST("api/v1/rest/vehicles")//to create new vehicle
        Call<ResponseBody> addVehicle(@Body NewVehicleModel model);

        @POST("api/v1/rest/trips")//to create new trip
        Call<ResponseBody> createNewTrip(@Body NewTripModel model);

        @POST("api/v1/rest/customers/{userId}/redeem-credit")//to redeem credits
        Call<BeansRedeemAmount> redeemCredit(@Body RedeemCreditModel model, @Path("userId") String userId);

        @Multipart
        @POST("api/v1/rest/vehicles/{vehicleId}/photo")//to add new vehicle image
        Call<ResponseBody> addVehicleImage(@Part("photo\"; filename=\"vehicle.jpg\"") RequestBody file, @Path("vehicleId") String vehicleId);

        @PATCH("api/v1/rest/customers/{userId}")//to create new vehicle
        Call<ResponseBody> updateProfile(@Body ProfileModel model, @Path("userId") String userId);

        @Multipart
        @POST("api/v1/rest/customers/{userId}/photo")//to upload profile image
        Call<ResponseBody> uploadProfileImage(@Part("photo\"; filename=\"profile.jpg\"") RequestBody file, @Path("userId") String userId);

        @GET("api/v1/rest/customers/{userId}/photo")//Get Trip Info
        Call<ResponseBody> getProfileImage(@Path("userId") String userId);

        @GET("api/v1/rest/aggregate/trips/{tripId}")//Get Trip Info
        Call<BeansTripsAggregate> getTripInfo(@Path("tripId") String tripId);

        @GET("api/v1/rest/trips/{tripId}")//Get Rating information for trip
        Call<BeansRating> getRatingInfo(@Path("tripId") String tripId);

        @PATCH("api/v1/rest/trips/{tripId}/rate")//submit user provided river rating
        Call<ResponseBody> sendDriverRating(@Body DriverRatingModel model, @Path("tripId") String tripId);

        @PATCH("api/v1/rest/addresses/{addressId}")//Delete address
        Call<ResponseBody> deleteAddress(@Body DeleteAddressModel model, @Path("addressId") String addressId);

        @PATCH("/api/v1/rest/trips/{tripId}/book-trip")//Delete address
        Call<ResponseBody> acceptBid(@Body AcceptBidModel model, @Path("tripId") String tripId);

        @PATCH("api/v1/rest/vehicles/{vehicleId}")//Delete Vehicle
        Call<ResponseBody> deleteVehicle(@Body DeleteVehicleModel model, @Path("vehicleId") String vehicleId);

        @POST("/api/v1/rest/addresses")//to create new address
        Call<ResponseBody> addAddress(@Body AddUpdateAddressModel model);

        @PATCH("/api/v1/rest/addresses/{addressId}")//to create new address
        Call<ResponseBody> updateAddress(@Body AddUpdateAddressModel model, @Path("addressId") String addressId);*/


    }

}
