package com.example.vladimir.vksearch;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static com.example.vladimir.vksearch.Photos.ofset;
import static com.vk.sdk.VKSdk.LoginState.LoggedIn;
import static com.vk.sdk.VKSdk.LoginState.LoggedOut;
import static com.vk.sdk.VKSdk.LoginState.Pending;
import static com.vk.sdk.VKSdk.LoginState.Unknown;

/**
 * Created by Vladimir on 21.02.2017.
 */

public class Splash extends Activity {

    public static Boolean isShown = false;
    public static String cityName;

    private LocationManager locationManager;
    private String provider;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            //GPS
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            //noinspection MissingPermission
            Criteria criteria = new Criteria();
            provider = locationManager.getBestProvider(criteria, false);
            //noinspection MissingPermission
            double latitude = locationManager.getLastKnownLocation(provider).getLatitude();
            //noinspection MissingPermission
            double longitude = locationManager.getLastKnownLocation(provider).getLongitude();

            Log.e("VK_SPLASH_GPS", "lat => "+latitude+" long => "+longitude);

            cityName = null;
            Geocoder gcd = new Geocoder(this.getApplicationContext(), Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(latitude, longitude, 1);

                Log.e("VK_CITY", String.valueOf(addresses));

                if (addresses.size() > 0) {
                    System.out.println(addresses.get(0).getLocality());
                    cityName = addresses.get(0).getLocality();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        if ( isOnline() ) {
            Log.d("VK_SPLASSHER", "Phone isOnline!!");

            VKSdk.wakeUpSession(this, new VKCallback<VKSdk.LoginState>() {
                @Override
                public void onResult(VKSdk.LoginState res) {

                    Log.d("VK_SPLASHER_RES", String.valueOf(res));

                    switch (res) {
                        case LoggedOut:
                            startActivity(new Intent(Splash.this, MainActivity.class));
                            finish();
                            break;
                        case LoggedIn:
                            Intent intent = new Intent(Splash.this, Photos.class);
                            intent.putExtra("current_city", cityName);
                            startActivity(intent);
                            finish();
                            break;
                        case Pending:
                            break;
                        case Unknown:
                            break;
                    }
                }

                @Override
                public void onError(VKError error) {
                    Log.d("VK_SplashError", error.errorMessage);
                }
            });
        }
        else
        {
            Log.d("VK_SPLASSHER", "Phone isOfline!!");

            showNoConnectionDialog(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("VK_SPLASH", "OnResume");
        Log.d("VK_Splasher", "isShown => "+isShown);

        if ( isShown ) {
            try {
                if (isOnline()) {
                    Log.d("VK_SPLASHER", "Activity recreated");

                    this.recreate();
                    finish();
                }
            } catch (Exception ex) {
                Log.d("VK_SPLASH_RESTART", ex.getMessage());
            }
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static void showNoConnectionDialog(Context ctx1)
    {
        final Context ctx = ctx1;
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setCancelable(true);
        builder.setMessage(R.string.no_connection);
        builder.setTitle(R.string.no_connection_title);
        builder.setPositiveButton(R.string.settings_button_text, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which)
            {
                ctx.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
            }
        });

        builder.setNegativeButton(R.string.cancel_button_text, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                return;
            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            public void onCancel(DialogInterface dialog) {
                return;
            }
        });

        builder.show();
        isShown = true;
    }
}