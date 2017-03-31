package com.example.vladimir.vksearch;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Vladimir on 31.03.2017.
 */

public class GetCity implements LocationListener
{

    private static Context context;

    public static Context getAppContext()
    {
        return GetCity.context;
    }

    @Override
    public void onLocationChanged(Location loc)
    {
//        Toast.makeText(getAppContext(),"Location changed: Lat: " + loc.getLatitude() + " Lng: "+ loc.getLongitude(), Toast.LENGTH_SHORT).show();
        String longitude = "Longitude: " + loc.getLongitude();
        Log.v("VK_GET_CITY_NAME_LONG", longitude);
        String latitude = "Latitude: " + loc.getLatitude();
        Log.v("VK_GET_CITY_NAME_LAT", latitude);

        /*------- To get city name from coordinates -------- */
        String cityName = null;
        Geocoder gcd = new Geocoder(getAppContext(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(loc.getLatitude(),
                    loc.getLongitude(), 1);
            if (addresses.size() > 0) {
                System.out.println(addresses.get(0).getLocality());
                cityName = addresses.get(0).getLocality();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        String s = longitude + "\n" + latitude + "\n\nMy Current City is: " + cityName;
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
