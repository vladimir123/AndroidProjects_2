package com.example.vladimir.vksearch;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;

/**
 * Created by Vladimir on 21.02.2017.
 */

public class Photos extends Activity {
//    private ArrayList<String> photo_url = new ArrayList<>();

    public String photo, full_name, h_phone, m_phone;

//    private String usersArray;
//    private JSONArray users;
//    private JSONObject object;

    private static JSONObject jUsers, object;
    private static JSONObject userObject;
    private static JSONArray usersArray;
    private static ArrayList<String> users;

    private static int offset = 0;

    Intent intent;
    Object get;

    static int ofset = 0;

    @Override
    protected void onPause() {
        super.onPause();

        this.finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Toast.makeText(getApplicationContext(), "OnResume fired", Toast.LENGTH_LONG).show();

//        recreate();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Toast.makeText(getApplicationContext(), "OnStart fired", Toast.LENGTH_LONG).show();




//        SearchUsers.getUser("Riga", i);
/*
        GetUser gu = new GetUser();
        try {
            get = gu.execute("Riga", i).get();
//            Log.e("VK_PHOTOS", String.valueOf( gu.execute("Riga", i).get() == null ));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Log.e("VK_ON_PHOTOS_GET_USER", String.valueOf(get));

        i++;
*/
//        recreate();
    }

//TODO: peredelat zagruzku v funkcii + dodelat like fotkam

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photos_layout);

        //getting photos from search query

//        intent = getIntent();
//        Log.e("VK_PHOTOS_FROM_SEARCH", intent.getStringExtra("first_name"));

//        usersArray = intent.getStringExtra("userArray");

        final ImageView user_photo = (ImageView)findViewById(R.id.imgPhoto);
        final TextView user = (TextView)findViewById(R.id.txtUser);
        final TextView contacts = (TextView)findViewById(R.id.txtContacts);
        final String city = "Riga";

        Button btn_like = (Button)findViewById(R.id.btn_like);
        Button btn_dislike = (Button)findViewById(R.id.btn_dislike);

        Log.e("VK_Photos_fired", "onCreate");

        Random r = new Random();

        if ( ofset == 0 )
            ofset = r.nextInt(80 - 1) + 1;

        btn_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("VK_Photos_fired", "onClick Like");

                try
                {
                    VKRequest search_users = new VKRequest("users.search", VKParameters.from("count", 1, "hometown", city, "sex", 1, "status", 6, "offset", ofset, VKApiConst.FIELDS, "photo_max_orig, contacts, last_seen, photo_id"));

                    search_users.executeWithListener(new VKRequest.VKRequestListener() {
                        @SuppressLint("LongLogTag")
                        @Override
                        public void onComplete(VKResponse response) {
                            super.onComplete(response);

                            object = null;

                            try
                            {
                                jUsers = new JSONObject(response.responseString);
                                userObject = jUsers.getJSONObject("response");
                                usersArray = userObject.getJSONArray("items");

                                Log.e("VK_FOUND_USERS_ARRAY", String.valueOf(usersArray));
                                for (int i = 0; i < usersArray.length(); i++)
                                {
                                    object = usersArray.getJSONObject(i);
                                    Log.e("VK_CONCRETE_USER_OBJECT", String.valueOf(object));

                                    user.setText( object.getString("first_name")+" "+object.getString("last_name") );
                                    Picasso.with(getApplicationContext())
                                            .load(object.getString("photo_max_orig"))
                                            .placeholder(R.drawable.progress_animation)
                                            .into(user_photo);

                                    if ( object.has("mobile_phone") )
                                        h_phone = object.getString("mobile_phone");
                                    else
                                        h_phone = " - ";
                                    if ( object.has("home_phone") )
                                        m_phone = object.getString("home_phone");
                                    else
                                        m_phone = " - ";

                                    contacts.setText("Phone: "+h_phone+"\r\n"+"Mobile: "+m_phone);

                                    String photo_id = object.getString("photo_id").split("_")[1];
                                    String u_id = object.getString("id");

                                    Log.e("VK_PHOTO_ID", String.valueOf(Integer.parseInt(photo_id)));

                                    //stavim like na foto
                                    setLike("photo", u_id, Integer.parseInt(photo_id));
                                }
                            }
                            catch (JSONException je)
                            {
                                je.printStackTrace();
                            }
                        }
                    });

                    ofset++;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        btn_dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("VK_Photos_fired", "onClick DisLike");

                try
                {
                    VKRequest search_users = new VKRequest("users.search", VKParameters.from("count", 1, "hometown", city, "sex", 1, "status", 6, "offset", ofset, VKApiConst.FIELDS, "photo_max_orig, contacts, last_seen"));

                    search_users.executeWithListener(new VKRequest.VKRequestListener() {
                        @SuppressLint("LongLogTag")
                        @Override
                        public void onComplete(VKResponse response) {
                            super.onComplete(response);

                            object = null;

                            try
                            {
                                jUsers = new JSONObject(response.responseString);
                                userObject = jUsers.getJSONObject("response");
                                usersArray = userObject.getJSONArray("items");

                                Log.e("VK_FOUND_USERS_ARRAY", String.valueOf(usersArray));
                                for (int i = 0; i < usersArray.length(); i++)
                                {
                                    object = usersArray.getJSONObject(i);
                                    Log.e("VK_CONCRETE_USER_OBJECT", String.valueOf(object));

                                    user.setText( object.getString("first_name")+" "+object.getString("last_name") );
                                    Picasso.with(getApplicationContext())
                                            .load(object.getString("photo_max_orig"))
                                            .placeholder(R.drawable.progress_animation)
                                            .into(user_photo);

                                    if ( object.has("mobile_phone") )
                                        h_phone = object.getString("mobile_phone");
                                    else
                                        h_phone = " - ";
                                    if ( object.has("home_phone") )
                                        m_phone = object.getString("home_phone");
                                    else
                                        m_phone = " - ";

                                    contacts.setText("Phone: "+h_phone+"\r\n"+"Mobile: "+m_phone);


                                }
                            }
                            catch (JSONException je)
                            {
                                je.printStackTrace();
                            }
                        }
                    });

                    ofset++;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });


        try
        {
            VKRequest search_users = new VKRequest("users.search", VKParameters.from("count", 1, "hometown", city, "sex", 1, "status", 6, "offset", ofset, VKApiConst.FIELDS, "photo_max_orig, contacts, last_seen, photo_id"));

            search_users.executeWithListener(new VKRequest.VKRequestListener() {
                @SuppressLint("LongLogTag")
                @Override
                public void onComplete(VKResponse response) {
                    super.onComplete(response);

                    object = null;

                    try
                    {
                        jUsers = new JSONObject(response.responseString);
                        userObject = jUsers.getJSONObject("response");
                        usersArray = userObject.getJSONArray("items");
//                    users = new ArrayList<String>();

                        Log.e("VK_FOUND_USERS_ARRAY", String.valueOf(usersArray));
                        for (int i = 0; i < usersArray.length(); i++)
                        {
                            object = usersArray.getJSONObject(i);
                            Log.e("VK_CONCRETE_USER_OBJECT", String.valueOf(object));

                            //TODO: tut delat otobrazenie

                            user.setText( object.getString("first_name")+" "+object.getString("last_name") );
                            Picasso.with(getApplicationContext())
                                    .load(object.getString("photo_max_orig"))
                                    .placeholder(R.drawable.progress_animation)
                                    .into(user_photo);

                            if ( object.has("mobile_phone") )
                                h_phone = object.getString("mobile_phone");
                            else
                                h_phone = " - ";
                            if ( object.has("home_phone") )
                                m_phone = object.getString("home_phone");
                            else
                                m_phone = " - ";

                            contacts.setText("Phone: "+h_phone+"\r\n"+"Mobile: "+m_phone);

//                        Log.e("VK_INSIDE_USER_SEARCH", String.valueOf(users));

                        }
                    }
                    catch (JSONException je)
                    {
                        je.printStackTrace();
                    }
                }
            });

            ofset++;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public static String setLike(String type, String owner_id, int item_id ) {
        final String[] res = {""};
        VKRequest request = new VKRequest("likes.add", VKParameters.from("type", type, "owner_id", owner_id, "item_id", item_id));
        request.executeSyncWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    JSONObject jsonObject = response.json.getJSONObject("response");
                    res[0] = jsonObject.getString("likes");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return res[0];
    }



}
