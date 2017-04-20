package com.example.vladimir.vksearch;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.Provider;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by Vladimir on 10.04.2017.
 */

public class GetCity extends Activity implements LocationListener {

    private final Context mContext;
    double latitude, longitude;
    Location location;
//    private static String provider;
//    LocationManager locationManager;

    public double getLatitude() {

        if (location != null)
            latitude = location.getLatitude();

        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public GetCity(Context context) {
        this.mContext = context;

        getLocation();
    }

    public double getLongitude() {

        if (location != null)
            longitude = location.getLongitude();

        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {}
                else
                {
                    ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @SuppressLint("LongLogTag")
    public void getLocation() {
        try {
            LocationManager locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            Log.e("VK_GET_CITY_LOCATION_ENABLED", String.valueOf(locationManager.isProviderEnabled(LOCATION_SERVICE)));

            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, false);

            Log.e("VK_GET_CITY_PROVIDER", provider);
            Log.e("VK_GET_CITY_PROVIDER_ENABLED", String.valueOf(locationManager.isProviderEnabled(provider)));

            //noinspection MissingPermission
            locationManager.requestLocationUpdates(provider, 0, 0, this);

            if (locationManager != null) {
                //noinspection MissingPermission
                location = locationManager.getLastKnownLocation(provider);
                updateGPSCoords();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void updateGPSCoords() {
        if (location != null)
        {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    public List<Address> getCity(Context context)
    {
        Log.e("VK_GET_CITY_COORDINATES", "Lat => "+latitude+" Long => "+longitude);


        /*------- To get city name from coordinates -------- */
        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        List<Address> addresses;
        try
        {
            Toast.makeText(mContext, "Lat => "+latitude+" Long => "+longitude, Toast.LENGTH_LONG).show();

            addresses = gcd.getFromLocation(latitude, longitude, 1);

            Log.e("VK_GET_CITY", String.valueOf(addresses));

            if ( addresses.isEmpty() )
            {
                JSONObject jsonObj = new JSONObject();//parser_Json.getJSONfromURL("http://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude + "&sensor=true");
                try {
                    jsonObj.getJSONArray("http://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude + "&sensor=true");

                    Log.e("VK_GETCITY_FROM_URL", String.valueOf(jsonObj));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return addresses;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onLocationChanged(Location loc) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
