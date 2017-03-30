package com.example.vladimir.vksearch;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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

    private static int ofset = 0;

    private static Context context;


    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photos_layout);

        Photos.context = getApplicationContext();

        user_photo = (ImageView)findViewById(R.id.imgPhoto);
        user = (TextView)findViewById(R.id.txtUser);
        contacts = (TextView)findViewById(R.id.txtContacts);

//        final String city = "Riga";

        btn_like = (Button)findViewById(R.id.btn_like);
        btn_dislike = (Button)findViewById(R.id.btn_dislike);

        Log.e("VK_Photos_fired", "onCreate");

        Random r = new Random();

        if ( ofset == 0 )
            ofset = r.nextInt(80 - 1) + 1;
        // get user from VK.com
        Photos.getUsers("Riga", ofset);
        ofset++;
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
