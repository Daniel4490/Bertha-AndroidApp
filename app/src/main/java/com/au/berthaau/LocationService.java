package com.au.berthaau;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.au.berthaau.HttpHelpers.HttpGetAsyncTask;
import com.au.berthaau.HttpHelpers.HttpPostAsyncTask;
import com.au.berthaau.Models.KorrigeretData;
import com.au.berthaau.Models.Position;
import com.au.berthaau.Models.Sensorer;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CancellationException;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

public class LocationService extends Service {

    private final Handler handler = new Handler();


    // Constants for notification/channel creation
    private final int NOTIFICATION_ID = 123;
    private final String CHANNEL_ID = "LokationerChannel";
    private final String CHANNEL_ID_NAME = "Lokationer";

    private NotificationManager notificationManager;

    // Instancefields used for FusedLocation
    private FusedLocationProviderClient fusedLocationClient;

    private static final LocationRequest locationRequest =  LocationRequest.create();
    private LocationCallback locationCallback;

    // Constant value defining the interval for collection position data (in milliseconds). Match this value with sensor interval.
    private final int SENSOR_MEASUREMENT_INTERVAL = (1000 * 60) * 5;

    // HTTP
    private String baseUrl = "http://envs-atair-web.au.dk/berthaapi/api";
    private Gson gson;

    // Device information
    private long deviceId;
    private String sensorId;
    private Date latestTimestampSensor;

    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Service doesn't provide binding so return null
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize
        this.gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:sss").create();

        // Because this service should be started with startForegroundService, the application must create an ongoing notification and call startForeground within 5 seconds
        // Also creates a channel if Android version of device requires it
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID_NAME,
                        NotificationManager.IMPORTANCE_HIGH);
                channel.setSound(null, null);
                channel.setShowBadge(false);
                notificationManager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
                notificationManager.deleteNotificationChannel(CHANNEL_ID);
                notificationManager.createNotificationChannel(channel);
            }

            Notification notification = createNotification(getApplicationContext(), CHANNEL_ID);

            if (notification == null) {
                notification = new NotificationCompat.Builder(this, CHANNEL_ID).build();
            }

            startForeground(NOTIFICATION_ID, notification);

        } catch (Exception e) {
            e.printStackTrace();
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        // Identifies and retrieves a unique deviceId - IMEI
        // International Mobile Equipment Identity (IMEI): the Unique Number to identify GSM, WCDMA mobile phones as well as some satellite phones.
        // The IMEI is unique for each and every device and remains unique for the device even if the application is re-installed or if the device is rooted or factory reset.
        TelephonyManager tMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            Log.d("PERMISSIONS", "PHONE permissions missing");
            stopForeground(true);
            stopSelf();
        }
        try{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                deviceId = Long.parseLong(tMgr.getImei());
        }
        else{
            deviceId = Long.parseLong(tMgr.getDeviceId());
        }
        }
        catch (CancellationException e) {
            Log.e("Cant get ID ",  e.getMessage());
            throw e;
        }

        init(deviceId);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                    Location location = locationResult.getLastLocation();

                    // Timestamp should match format in DB
                    long currentTimeInMillisUTC = System.currentTimeMillis() - (((1000*60) *60) *2);
                    String currentDateandTimeUTC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(currentTimeInMillisUTC));

                    // Insert GPS-data into DB through webservice
                    Position postData = new Position(currentDateandTimeUTC, location.getLongitude(), location.getLatitude(), sensorId);
                    String jsonPostData = gson.toJson(postData);
                    HttpPostAsyncTask task = new HttpPostAsyncTask(jsonPostData);
                    task.execute(baseUrl + "/position");

                    notificationManager.notify(NOTIFICATION_ID,createNotification(getApplicationContext(), CHANNEL_ID, location));


                    // Process in callback might have taken some time so it should be taken into account on interval for next request.
                    locationRequest.setInterval(SENSOR_MEASUREMENT_INTERVAL - ((System.currentTimeMillis() - (((1000*60) *60) *2)) - currentTimeInMillisUTC));

                    // Interval can't be updated in a sensible way after location updates has been started, so previous location update request is removed and a new one is initiated with a delay
                    fusedLocationClient.removeLocationUpdates(locationCallback);

                    getLatestTimestampSensor(sensorId);
                    //handler.postDelayed(runnableStartLocationUpdates, SENSOR_MEASUREMENT_INTERVAL - ((System.currentTimeMillis() - (((1000*60) *60) *2)) - currentTimeInMillisUTC));


                    Log.d("JSON", jsonPostData);
                    Log.d("INTERVAL","New GPS data captured " + "Longitude: " + location.getLongitude() + " Latitude: " + location.getLatitude());
            }
        };

        // If the system kills the service after onStartCommand() returns,
        // recreate the service and call onStartCommand() with the last intent that was delivered to the service.
        return START_REDELIVER_INTENT;
    }

    // All active processes must be stopped before service process can be destroyed
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("TAG", "onDestroy called!");
        notificationManager.cancelAll();
        stopLocationUpdates();
    }


    // Initial notification creation without location data
    private Notification createNotification(Context context, String channelid) {
        try {
            return new NotificationCompat.Builder(context,channelid)
                    .setContentTitle("Indsamler lokationsdata")
                    .setOnlyAlertOnce(true)
                    .setOngoing(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Overload method for showing notification with location data when data has been retrieved
    private Notification createNotification(Context context, String channelid, Location location) {
        try {
            return new NotificationCompat.Builder(context,channelid)
                    .setContentTitle("Indsamler lokationsdata")
                    .setOnlyAlertOnce(true)
                    .setOngoing(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText("Seneste lokation" + "\nLængdegrad: " + location.getLongitude() + "\nBreddegrad: " + location.getLatitude()))
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void init(long id){
        HttpGetCorrespondingSensorIdTask task = new HttpGetCorrespondingSensorIdTask();
        task.execute(baseUrl + "/sensors/" + id);

        locationRequest.setInterval(SENSOR_MEASUREMENT_INTERVAL);

        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        Log.d("TAG", baseUrl +"/sensors/" +id);
        Log.d("TAG", "Got to init call");

    }


    private void getLatestTimestampSensor(String sensorId){
        Log.d("TAG", "Got to GetLatestTimestampSensor call " + baseUrl + "/korrigeretdata/" + sensorId + "/latest");

        HttpGetLatestTimestampTask latestTimestampTask = new HttpGetLatestTimestampTask();
        latestTimestampTask.execute(baseUrl + "/korrigeretdata/" + sensorId + "/latest");
    }





    // Synchronizes data recording interval with corresponding sensor for device.
    // Note that this is dependant on the Sensor and Phone using a service that provides the same time.
    private void setLocationRequestSync() {
        int timeDifference;

        Log.d("TAG", "Got to setLocationRequestSync call");

        // Matches DB timezone (UTC)
        long deviceCurrentTimeUTC = System.currentTimeMillis() - ((1000*60) *60) * 2;

        if (latestTimestampSensor.before(new Date(deviceCurrentTimeUTC))){
            timeDifference = (int) (deviceCurrentTimeUTC - latestTimestampSensor.getTime());
            Log.d("TAG", "latest timestamp sensor: " + latestTimestampSensor.getTime()+  " " + "system current time: " + deviceCurrentTimeUTC + ". Time difference: " + (timeDifference / 1000 / 60) + " minutes");
        }
        else{
            timeDifference = (int) (latestTimestampSensor.getTime() - deviceCurrentTimeUTC);
        }

        // If time difference is negative it might be because latest sensor data has not yet been inserted into database. Service should wait and try again
        if(SENSOR_MEASUREMENT_INTERVAL - timeDifference < 0){
            fusedLocationClient.removeLocationUpdates(locationCallback);
            Log.d("TAG", "Data missing from db. Delaying start");
            handler.postDelayed(runnableDelayLocationUpdates, 2000);
        }else{
            Log.d("TAG", "Request interval " + locationRequest.getFastestInterval() + " " + locationRequest.getInterval() + ". Try delay " + (SENSOR_MEASUREMENT_INTERVAL - timeDifference));
            handler.postDelayed(runnableStartLocationUpdates, SENSOR_MEASUREMENT_INTERVAL - timeDifference);
        }


    }


    private void startLocationUpdates() {

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            stopForeground(true);
            stopSelf();
            return;
        }


        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                null /* Looper */);

    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }


    private Runnable runnableStartLocationUpdates = new Runnable() {
        @Override
        public void run() {
            startLocationUpdates();
        }
    };


    private Runnable runnableDelayLocationUpdates = new Runnable() {
        @Override
        public void run() {
            getLatestTimestampSensor(sensorId);
        }
    };



    private class HttpGetCorrespondingSensorIdTask extends HttpGetAsyncTask{
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            // TODO: Response should not be in an array (only a single object is returned - webservice issue)
            Log.d("TAG", "Corresponding sensor id object " + s);
            Sensorer[] correspondingSensor = gson.fromJson(s, Sensorer[].class);

            getLatestTimestampSensor(correspondingSensor[0].getSensorId());

            sensorId = correspondingSensor[0].getSensorId();

            // Store phones corresponding sensorID so it can be used in other activities when requesting data from Web service
            SharedPreferences.Editor editor = getSharedPreferences("MyPref", MODE_PRIVATE).edit();
            editor.putString("sensorID", sensorId);
            editor.apply();

            Log.d("TAG", "Got corresponding sensor id: " + correspondingSensor[0].getSensorId());
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
            Log.d("TAG","HttpCorrespondingTask cancelled " + s);
            stopForeground(true);
            stopSelf();
        }
    }

    private class HttpGetLatestTimestampTask extends HttpGetAsyncTask{

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            KorrigeretData latestKorrigeretData = gson.fromJson(s, KorrigeretData.class);
            latestTimestampSensor = latestKorrigeretData.getTidspunkt();



            // latest sensor data can be used for display in app so its stored in Shared preferences
            SharedPreferences.Editor editor = getSharedPreferences("latestSensorReading", MODE_PRIVATE).edit();

            editor.putFloat("no2Value", (float) latestKorrigeretData.getNO2());
            editor.putFloat("o3Value", (float) latestKorrigeretData.getO3());
            editor.putFloat("pm25Value", (float) latestKorrigeretData.getPM25());
            editor.putFloat("pm10Value", (float) latestKorrigeretData.getPM10());
            editor.apply();

            setLocationRequestSync();
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
            Log.d("TAG","HttpGetLatestTimestamp cancelled " + s);
            stopForeground(true);
            stopSelf();
        }
    }



}


