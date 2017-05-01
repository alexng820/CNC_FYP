package fyp.cnc.cnc_fyp.helper;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import fyp.cnc.cnc_fyp.Globals;
import fyp.cnc.cnc_fyp.activity.PermissionActivity;

//A service that check user's location and update time needed from the location to school

public class LocationHandler extends Service {
    private static final String TAG = LocationHandler.class.getSimpleName();
    private static final int LOCATION_INTERVAL = 15 * 60 * 1000; //15 minutes
    private static final float LOCATION_DISTANCE = 10f;
    private static final String API_KEY = "AIzaSyCfMMMtkpy5nEqI8Vm4GcHvQX_M0LpTz7w"; //API key for Google Direction API
    String latitude;
    String longitude;

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };
    private LocationManager mLocationManager = null;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
        //Check if location permission already granted, redirect to permission helper if not
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED || (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            //Get last known location
            Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            //Store last known location in global variable
            if (location != null) {
                latitude = Double.toString(location.getLatitude());
                longitude = Double.toString(location.getLongitude());
                getDirection();
            }
        } else {
            Intent intent = new Intent(this, PermissionActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocationManager != null) {
            for (LocationListener mLocationListener : mLocationListeners) {
                try {
                    mLocationManager.removeUpdates(mLocationListener);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    private void initializeLocationManager() {
        if (mLocationManager == null) {
            //Check if location permission already granted, redirect to permission helper if not
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                Intent intent = new Intent(this, PermissionActivity.class);
                startActivity(intent);
            }
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    //Get the time needed from user location to school
    public void getDirection() {
        String origin = latitude + "," + longitude;
        String destination = "The+Open+University+Of+Hong+Kong";
        String mode = "transit"; //Public transport

        String API_URL = "https://maps.googleapis.com/maps/api/directions/json?" + "origin=" + origin + "&destination=" + destination + "&mode=" + mode + "&key=" + API_KEY;

        //Tag used to cancel the request
        String tag_string_req = "req_google__direction_API";

        StringRequest strRequest = new StringRequest(Request.Method.GET, API_URL, response -> {
            try {
                //Parse the JSON response from Google Direction API
                JSONObject jsonObject = new JSONObject(response);
                JSONObject routes = jsonObject.getJSONArray("routes").getJSONObject(0);
                JSONObject legs = routes.getJSONArray("legs").getJSONObject(0);
                JSONObject duration = legs.getJSONObject("duration");
                //Update the time needed to go to school in second
                Globals.secondsToSchool = duration.getInt("value");
            } catch (JSONException e) {
                //JSON error
                e.printStackTrace();
            }
        }, error -> Log.e(TAG, "Error: " + error.getMessage()));

        //Adding request to request queue
        AppController.getInstance().addToRequestQueue(strRequest, tag_string_req);
    }

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        LocationListener(String provider) {
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            latitude = Double.toString(location.getLatitude());
            longitude = Double.toString(location.getLongitude());
            getDirection();
            mLastLocation.set(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(TAG, "onStatusChanged: " + provider);
        }
    }
}