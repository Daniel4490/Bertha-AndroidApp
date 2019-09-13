package com.au.berthaau;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;

import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.au.berthaau.Fragments.GraphFragments.GraphFragment;
import com.au.berthaau.Fragments.OverviewFragment;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;

import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {


    private static final int MY_PERMISSIONS_REQUEST_LOCATIONS = 1;

    private BottomNavigationView bottomNavigationView;

    private SettingsClient settingsClient;

    private final Fragment overviewFragment = new OverviewFragment();
    private final Fragment graphFragment = new GraphFragment();

    private final FragmentManager fragmentManager = getSupportFragmentManager();

    private Fragment activeFragment = overviewFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager.beginTransaction().add(R.id.main_container, graphFragment).hide(graphFragment).commit();
        fragmentManager.beginTransaction().add(R.id.main_container, overviewFragment).commit();

        // Bottom Navbar init
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_oversigt:
                        fragmentManager.beginTransaction().hide(activeFragment).show(overviewFragment).commit();
                        activeFragment = overviewFragment;
                        Log.d("NAVBAR", "oversigt selected");
                        break;
                    case R.id.action_grafer:
                        fragmentManager.beginTransaction().hide(activeFragment).show(graphFragment).commit();
                        activeFragment = graphFragment;
                        break;
                    case R.id.action_guide:

                        Intent intent = new Intent(MainActivity.this, LocationService.class);
                        try{
                            startService(intent);
                        }
                        catch (Exception e) {
                            Log.e("EXCEPTION", "EXCEPTION CAUGHT" + e);
                            stopService(intent);
                            e.printStackTrace();
                        }
                        break;
                }
                return true;
            }
        });



        // Create new instance of SettingsClient. Used to check if device has correct location settings, and configure location requests
        settingsClient = new SettingsClient(this);


        // Check for user permission and request permissions if not yet accepted
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATIONS
            );
        }

        if (checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS, Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_REQUEST_LOCATIONS
            );
            return;
        }



        setupLocationSettings();
    }



    @Override
    protected void onResume() {
        super.onResume();




        //TelephonyManager tMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        //if (checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
        //    return;
        //}
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        //    longitude.setText(tMgr.getImei());
        //}


        //Toast.makeText(this, tMgr.getDeviceId(), Toast.LENGTH_LONG).show();
    }

    public void setupLocationSettings() {
        Task<LocationSettingsResponse> task = settingsClient.buildLocationSettingsRequest();

        task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    // All location settings are satisfied. The client can initialize location
                    // requests here.

                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                // TODO: Handle situation where user cancels. i value = request check for settings
                                resolvable.startResolutionForResult(
                                        MainActivity.this,
                                        1);

                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            } catch (ClassCastException e) {
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.

                            break;
                    }
                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted


                } else {
                    Toast.makeText(this, "App needs permission to access location to function", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }



}
