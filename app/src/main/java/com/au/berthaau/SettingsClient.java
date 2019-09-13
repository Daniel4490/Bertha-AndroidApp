package com.au.berthaau;

import android.content.Context;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.tasks.Task;


public class SettingsClient {

    private static final LocationRequest RequestHighAccuracy =  LocationRequest.create();
    private static final LocationRequest RequestBalancedPowerAccuracy = LocationRequest.create();

    private Context context;

    public SettingsClient(Context context){

        this.context = context;


        // High accuracy location - Request the most accurate locations available with 5 seconds interval
        RequestHighAccuracy.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        RequestHighAccuracy.setInterval(1000 * 5);

        // Balanced accuracy location -  receive updates at a specified interval, and can receive them faster when available, but still a low power impact (interval every min if possible or every 5 min)
        // This style of request is appropriate for many location aware applications, including background usage
        RequestBalancedPowerAccuracy.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        RequestBalancedPowerAccuracy.setFastestInterval(1000 * 60);
        RequestBalancedPowerAccuracy.setInterval(1000* 60 * 5);


    }


    public Task<LocationSettingsResponse> buildLocationSettingsRequest(){

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(RequestHighAccuracy)
                .addLocationRequest(RequestBalancedPowerAccuracy);


        return LocationServices.getSettingsClient(context).checkLocationSettings(builder.build());

    }
}
