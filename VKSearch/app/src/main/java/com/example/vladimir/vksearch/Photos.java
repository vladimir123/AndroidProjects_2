package com.example.vladimir.vksearch;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import static com.example.vladimir.vksearch.Photos.ofset;

/**
 * Created by Vladimir on 21.02.2017.
 */

public class Photos extends Activity {

    public static String full_name;
    public static String[] photo_id;
    public static String user_id;

    private static JSONObject jUsers, object, jLikes;
    private static JSONObject userObject;
    private static JSONArray usersArray;

    private static ImageView user_photo;
    private static TextView user;
    private static TextView contacts;
    private static Button btn_like, btn_dislike;

    public static int ofset = 0;

    private static Context context;

    private LocationManager locationManager;
    private String provider;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

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

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photos_layout);

        Photos.context = getApplicationContext();

        user_photo = (ImageView) findViewById(R.id.imgPhoto);
        user = (TextView) findViewById(R.id.txtUser);
        contacts = (TextView) findViewById(R.id.txtContacts);

        btn_like = (Button) findViewById(R.id.btn_like);
        btn_dislike = (Button) findViewById(R.id.btn_dislike);

        Log.e("VK_Photos_fired", "onCreate");
        try
        {
            //GPS
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            LocationListener locationListener = new MyLocationListener();
            //noinspection MissingPermission
            Criteria criteria = new Criteria();
            provider = locationManager.getBestProvider(criteria, false);
            //noinspection MissingPermission
            locationManager.requestLocationUpdates(provider, 5000, 0, locationListener);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static Context getAppContext()
    {
        return Photos.context;
    }

    public static void setLike(String type, String owner_id, String item_id ) {

        Log.e("VK_SET_LIKES", "Likes fired!"+" type => "+type+" user => "+owner_id+" photo => "+item_id);

        VKRequest request = new VKRequest("likes.add", VKParameters.from("type", type, "owner_id", owner_id, "item_id", item_id));

        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try
                {
                    jLikes = new JSONObject(response.responseString);
                    Log.e("VK_LIKES_RESPONSE", String.valueOf(jLikes));
                }
                catch(JSONException lex)
                {
                    lex.printStackTrace();
                }
            }
        });

        return;
    }

    @SuppressLint("LongLogTag")
    public static void getUsers(String city, int ofset)
    {
        Log.e("VK_GETUSERS_OFFSET", String.valueOf(ofset));

        final Map<String,String> rez = new HashMap<>();

        VKRequest search_users = new VKRequest("users.search", VKParameters.from("count", 1, "hometown", city, "sex", 1, "status", 6, "offset", ofset, VKApiConst.FIELDS, "photo_max_orig, contacts, last_seen, photo_id"));

        search_users.executeWithListener(new VKRequest.VKRequestListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);

                try
                {
                    jUsers = new JSONObject(response.responseString);
                    userObject = jUsers.getJSONObject("response");
                    usersArray = userObject.getJSONArray("items");

                    Log.e("VK_FOUND_USERS_ARRAY", String.valueOf(usersArray));

                    if (usersArray != null) {
                        for (int i = 0; i < usersArray.length(); i++) {
                            object = usersArray.getJSONObject(i);

                            Log.e("VK_CONCRETE_USER_OBJECT", String.valueOf(object));


                            rez.put("first_name", object.getString("first_name"));
                            rez.put("last_name", object.getString("last_name"));
                            rez.put("photo_max_orig", object.getString("photo_max_orig"));
                            rez.put("photo_id", object.getString("photo_id"));
                            rez.put("user_id", object.getString("id"));

                            if (object.has("home_phone"))
                                rez.put("home_phone", object.getString("home_phone"));
                            else
                                rez.put("home_phone", " - ");

                            if (object.has("mobile_phone"))
                                rez.put("mobile_phone", object.getString("mobile_phone"));
                            else
                                rez.put("mobile_phone", " - ");
                        }


                        Photos.getUser(rez);
                    }
                    else
                    {
                        Intent photos = new Intent(Photos.getAppContext(), Photos.class);
                        Photos.getAppContext().startActivity(photos);
                    }
                }
                catch (JSONException je)
                {
                    je.printStackTrace();
                }
            }
        });
        return;
    }

    public static void getUser(Map<String, String> data)
    {
        try {
            full_name = data.get("first_name") + " " + data.get("last_name");

            user.setText(full_name);
            Picasso.with(Photos.getAppContext())
                    .load(data.get("photo_max_orig"))
                    .placeholder(R.drawable.progress_animation)
                    .into(user_photo);

            contacts.setText("Phone: " + data.get("home_phone") + "\r\n" +"Mobile: " + data.get("mobile_phone"));

            photo_id = data.get("photo_id").split("_");
            user_id = data.get("user_id");

            btn_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("VK_Photos_fired", "onClick Like");

                    Photos.setLike("photo", user_id, photo_id[1]);
                    Photos.getUsers("Riga", ofset);
                    ofset++;
                }
            });

            btn_dislike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("VK_Photos_fired", "onClick DisLike");

                    try {
                        Photos.getUsers("Riga", ofset);
                        ofset++;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return;
    }

}

class MyLocationListener implements LocationListener {

    @Override
    public void onLocationChanged(Location loc) {
        Toast.makeText(Photos.getAppContext(),"Location changed: Lat: " + loc.getLatitude() + " Lng: "+ loc.getLongitude(), Toast.LENGTH_SHORT).show();
        String longitude = "Longitude: " + loc.getLongitude();
        Log.e("VK_GET_CITY", longitude);
        String latitude = "Latitude: " + loc.getLatitude();
        Log.e("VK_GET_CITY", latitude);

        /*------- To get city name from coordinates -------- */
        String cityName = null;
        Geocoder gcd = new Geocoder(Photos.getAppContext(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);

            Log.e("VK_CITY", String.valueOf(addresses));

            if (addresses.size() > 0) {
                System.out.println(addresses.get(0).getLocality());
                cityName = addresses.get(0).getLocality();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        String s = longitude + "\n" + latitude + "\n\nMy Current City is: " + cityName;

        Log.e("VK_CITY_NAME", s);

        //get random number for random user from search function
        Random r = new Random();
        if ( ofset == 0 )
            ofset = r.nextInt(80 - 1) + 1;

        Toast.makeText(Photos.getAppContext(), "City => "+cityName, Toast.LENGTH_LONG).show();

        // get user from VK.com
//        Photos.getUsers("Riga", ofset);
        Photos.getUsers(cityName, ofset);
        ofset++;

    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}
