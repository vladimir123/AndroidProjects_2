package com.example.vladimir.vksearch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Vladimir on 21.02.2017.
 */

public class Photos extends Activity {
//    private ArrayList<String> photo_url = new ArrayList<>();

    private String photo, full_name;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photos_layout);

        //getting photos from search query
//        photo_url.add(intent.getStringExtra("photo_url"));

        intent = getIntent();
        full_name = intent.getStringExtra("name")+" "+intent.getStringExtra("surname");
        photo = intent.getStringExtra("photo");

        ImageView user_photo = (ImageView)findViewById(R.id.imgPhoto);
        TextView user = (TextView)findViewById(R.id.txtUser);

        if (intent != null)
        {
            try
            {
                user.setText( full_name );
                Picasso.with(getApplicationContext())
                        .load(photo)
                        .placeholder(R.drawable.progress_animation)
                        .into(user_photo);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

//        for (int i=0; i<photo_url.size(); i++)
//        {
////            Picasso.with(getApplicationContext()).load(photo_url.get(i)).into(photo);
//
//            Log.d("VK_PHOTO_URL", photo_url.get(i));
//        }

    }
}
