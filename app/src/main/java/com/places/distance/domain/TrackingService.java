package com.places.distance.domain;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.places.distance.Constants;
import com.places.distance.domain.flickr.api.FlickrOAuthClient;
import com.places.distance.domain.model.ImageData;

import java.util.ArrayList;


/**
 * Service used to track the location in background, even when the application is stopped
 * Created by Aurelian Cotuna
 */

public class TrackingService extends Service {

    private static final String TAG = "TrackingService";
    private SharedPreferences mSharedPreferences;

    private static boolean sIsRunning;

    /**
     * Handles the flickr operations
     */
    private static FlickrOAuthClient sFlickrClient;

    private static PhotoSearchHandler sPhotoSearchHandler;

    private LocationManager mLocationManager = null;


    private class LocationListener implements android.location.LocationListener {

        public LocationListener(String provider) {
            Log.i(TAG, "LocationListener " + provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.i(TAG, "onLocationChanged: " + location);
            if (sPhotoSearchHandler == null || sFlickrClient == null) {
                refreshClient(getApplicationContext());
            }
            sPhotoSearchHandler.searchForLocation(location.getLatitude(), location.getLongitude());

        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.i(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.i(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.i(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand called");
        super.onStartCommand(intent, flags, startId);
        initializePreferences();
        setTrackingStatus(true);


        return START_STICKY;
    }


    private void setTrackingStatus(boolean started) {

        Intent intent = started ? new Intent(Constants.FILTER_TRACKING_STARTED) : new Intent(Constants.FILTER_TRACKING_STOPPED);

        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        sIsRunning = started;
        mSharedPreferences.edit().putBoolean(Constants.SP_TRACKING_RUNNING, started).apply();
    }

    public static boolean isRunning() {
        return sIsRunning;
    }

    /**
     * Initialize a single instance of the current {@link #mSharedPreferences} if it is not already initialized
     */
    private void initializePreferences() {
        if (mSharedPreferences == null) {
            mSharedPreferences = getSharedPreferences(Constants.SP_TRACKING_STATUS, Context.MODE_PRIVATE);
        }
    }


    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate called");
        initializeLocationManager();
        initializePreferences();
        if (sFlickrClient == null) {
            sFlickrClient = new FlickrOAuthClient(getApplicationContext());
        }

        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    Constants.GPS_LOCATION_TIME_INTERVAL,
                    Constants.GPS_LOCATION_DISTANCE_INTERVAL,
                    mLocationListeners[1]);
            Log.i(TAG, "Location updates enabled");

        } catch (SecurityException ex) {
            Log.e(TAG, "Failed to request location update", ex);
        } catch (IllegalArgumentException ex) {
            Log.e(TAG, "Network provider does not exist " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    Constants.GPS_LOCATION_TIME_INTERVAL, Constants.GPS_LOCATION_DISTANCE_INTERVAL, mLocationListeners[0]);
        } catch (SecurityException ex) {
            Log.i(TAG, "Failed to request location update ", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "GPS provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        setTrackingStatus(false);
        Log.i(TAG, "Tracking stopped");
        if (mLocationManager != null) {
            for (int listenerIndex = 0; listenerIndex < mLocationListeners.length; listenerIndex++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[listenerIndex]);

                } catch (SecurityException exception) {
                    Log.i(TAG, "Failed to remove location listener, ignoring", exception);
                } catch (Exception exception) {
                    Log.i(TAG, "Failed to remove location listener, ignoring", exception);
                }
            }
        }
    }


    /**
     * Initialize a single instance of the current {@link #mLocationManager} if it is not already initialized
     */
    private void initializeLocationManager() {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * Checks if the current client is authenticated or not
     */
    public static boolean isAuthenticated(Context context) {
        if (sFlickrClient == null) {
            sFlickrClient = new FlickrOAuthClient(context);
        }
        return sFlickrClient.isAuthenticated();
    }

    /**
     * Start the image fetching mechanism
     */
    public static void startLoadingImages(Context context) {
        if (sFlickrClient == null) {
            sFlickrClient = new FlickrOAuthClient(context);
        }

        sPhotoSearchHandler = new PhotoSearchHandler(context, sFlickrClient);
    }

    public static ArrayList<ImageData> getImageData() {
        if (sPhotoSearchHandler != null) {
            return sPhotoSearchHandler.getResults();
        }
        return null;
    }

    public static void refreshClient(Context context) {
        sFlickrClient = new FlickrOAuthClient(context);
        sPhotoSearchHandler = new PhotoSearchHandler(context, sFlickrClient);
    }
}
