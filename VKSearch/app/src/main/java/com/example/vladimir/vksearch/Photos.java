package com.example.vladimir.vksearch;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.view.View.VISIBLE;

/**
 * Created by Vladimir on 21.02.2017.
 */

public class Photos extends Activity {

    public static String full_name;
    public static String[] photo_id;
    public static String user_id;
    public static String current_city = null;

    private static JSONObject jUsers, object, jLikes;
    private static JSONObject userObject;
    private static JSONArray usersArray;

    private static ImageView user_photo;
    private static TextView user;
    private static TextView contacts;
    private static Button btn_like, btn_dislike;

    public static int ofset = 0;
    static Boolean permGranted = false;

    public static String getCurrent_city() {
        return current_city;
    }

    public static void setCurrent_city(String current_city) {
        Photos.current_city = current_city;
    }
/*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
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
*/
    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photos_layout);

        user_photo = (ImageView) findViewById(R.id.imgPhoto);
        user = (TextView) findViewById(R.id.txtUser);
        contacts = (TextView) findViewById(R.id.txtContacts);

        btn_like = (Button) findViewById(R.id.btn_like);
        btn_dislike = (Button) findViewById(R.id.btn_dislike);

        Log.e("VK_Photos_fired", "onCreate");


        try
        {

            //on emulator need to disable GPS class

            Log.e("VK_PHOTOS_GEOCODER", String.valueOf(Geocoder.isPresent()));

            GetCity city = new GetCity(getApplicationContext());
            List<Address> ci = city.getCity(getApplicationContext());

//            Toast.makeText(getApplicationContext(), "CITY ON CREATE => "+ci.get(0), Toast.LENGTH_LONG).show();

            current_city = ci.get(0).getLocality();

            city.stopGPS();

        }
        catch(NullPointerException ne)
        {
            ne.printStackTrace();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }


        //get random number for random user from search function
        Random r = new Random();
        if ( ofset == 0 )
            ofset = r.nextInt(80 - 1) + 1;

        getUsers(ofset, current_city);
        ofset++;

    }

    public void setLike(String type, String owner_id, String item_id ) {

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
    public void getUsers(int ofset, String cityName)
    {
//        Log.e("VK_GETUSERS_OFFSET", String.valueOf(ofset));
//        Log.e("VK_GETUSERS_CURRENTCITY", cityName);

        final Map<String,String> rez = new HashMap<>();

        VKRequest search_users = new VKRequest("users.search", VKParameters.from("count", 1, "hometown", cityName, "sex", 1, "status", 6, "offset", ofset, VKApiConst.FIELDS, "photo_max_orig, contacts, last_seen, photo_id"));

        //show `Loading` spinner
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Loading data");
        progress.setCancelable(false);
        progress.show();

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

                    if (usersArray != null)
                    {
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
                        //show data in content
                        getUser(rez);
                        //hide 'Loading' spinner
                        progress.dismiss();
//                        progressBar.setVisibility(View.INVISIBLE);
                    }
                    else
                    {
                        Intent photos = new Intent(getApplicationContext(), Photos.class);
                        getApplicationContext().startActivity(photos);
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

    public void getUser(Map<String, String> data)
    {
        try {
            full_name = data.get("first_name") + " " + data.get("last_name");

            user.setText(full_name);
            Picasso.with(getApplicationContext())
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
                    Log.e("VK_LIKE_CLICKED_CITY", current_city);

                    setLike("photo", user_id, photo_id[1]);
//                    Photos.getUsers("Riga", ofset);

                    getUsers(ofset, current_city);
                    ofset++;
                }
            });

            btn_dislike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("VK_Photos_fired", "onClick DisLike");
//                    Log.e("VK_DISLIKE_CLICKED_CITY", current_city);

                    try
                    {
                        getUsers(ofset, current_city);
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