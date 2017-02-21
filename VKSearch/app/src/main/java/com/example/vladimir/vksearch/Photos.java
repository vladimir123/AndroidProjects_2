package com.example.vladimir.vksearch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Vladimir on 21.02.2017.
 */

public class Photos extends Activity {
    private ArrayList<String> photo_url = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photos_layout);

        //getting photos from search query
        Intent intent = getIntent();
        photo_url.add(intent.getStringExtra("photo_url"));

        ImageView photo = (ImageView)findViewById(R.id.photo1);

        for (int i=0; i<photo_url.size(); i++)
        {
            Picasso.with(getApplicationContext()).load(photo_url.get(i)).into(photo);

            Log.d("VK_PHOTO_URL", photo_url.get(i));
        }

    }
}
