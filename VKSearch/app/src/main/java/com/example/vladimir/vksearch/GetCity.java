package com.example.vladimir.vksearch;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by Vladimir on 10.04.2017.
 */

public class GetCity implements LocationListener {

    private final Context mContext;
    double latitude, longitude;
    Location location;

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

    public void getLocation() {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);

        //noinspection MissingPermission
        locationManager.requestLocationUpdates(provider, 0, 0, this);

        if (locationManager != null) {
            //noinspection MissingPermission
            location = locationManager.getLastKnownLocation(provider);
            updateGPSCoords();
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
        /*------- To get city name from coordinates -------- */
        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(latitude, longitude, 1);

            Log.e("VK_CITY", String.valueOf(addresses));

            return addresses;
        }
        catch (IOException e) {
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
