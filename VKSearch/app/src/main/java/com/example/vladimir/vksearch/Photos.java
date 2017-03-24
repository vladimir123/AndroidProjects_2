package com.example.vladimir.vksearch;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Vladimir on 21.02.2017.
 */

public class Photos extends Activity {
//    private ArrayList<String> photo_url = new ArrayList<>();

    public String photo, full_name, h_phone, m_phone;

    private String usersArray;
    private JSONArray users;
    private JSONObject object;

    Intent intent;

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

//        recreate();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photos_layout);

        //getting photos from search query

        intent = getIntent();
        usersArray = intent.getStringExtra("userArray");

        final ImageView user_photo = (ImageView)findViewById(R.id.imgPhoto);
        final TextView user = (TextView)findViewById(R.id.txtUser);
        final TextView contacts = (TextView)findViewById(R.id.txtContacts);

        Button btn_like = (Button)findViewById(R.id.btn_like);
        Button btn_dislike = (Button)findViewById(R.id.btn_dislike);


        try
        {
            users = new JSONArray(usersArray);

            Log.e("VK_PHOTO_USER_OBJECT", String.valueOf(users));

            for (int i = 0; i < users.length(); i++)
            {
                object = users.getJSONObject(i);

//                Log.e("VK_USER_OBJECT", String.valueOf(object));

                full_name = object.getString("first_name") + " " + object.getString("last_name");
                photo = object.getString("photo_max_orig");

                if ( object.has("home_phone") )
                    h_phone = object.getString("home_phone");
                else
                    h_phone = " - ";

                if ( object.has("mobile_phone") )
                    m_phone = object.getString("mobile_phone");
                else
                    m_phone = " - ";

                user.setText( full_name );
                Picasso.with(getApplicationContext())
                        .load(photo)
                        .placeholder(R.drawable.progress_animation)
                        .into(user_photo);

                contacts.setText("Phone: "+h_phone+"\r\n"+"Mobile: "+m_phone);
            }

        }
        catch(JSONException jException)
        {
            jException.printStackTrace();
        }

        btn_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("VK_USER_ARRAY_ON_CLICK", String.valueOf(users.length()));
            }
        });

    }
}
