package com.example.vladimir.vksearch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Vladimir on 21.02.2017.
 */

public class Logged extends Activity {

    public String query;
    public double latitude;
    public double longitude;
    public Integer cnt;
    public Integer radius;

    private JSONObject jPhotoos, photoObject, object;
    private JSONArray photosArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        query = "female";
        latitude = 56.9132463;
        longitude = 24.1752813;
        cnt = 3;
        radius = 5000;

        VKRequest search_photos = new VKRequest("photos.search", VKParameters.from("q", query, "lat", latitude, "long", longitude, "count", cnt, "radius", radius));

        final Intent intent = new Intent(Logged.this, Photos.class);

        search_photos.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);

                Log.d("VK_RESPONSE_PHOTO", response.responseString);

                try
                {
                    jPhotoos = new JSONObject(response.responseString);
                    photoObject = jPhotoos.getJSONObject("response");
                    photosArray = photoObject.getJSONArray("items");

                    for ( int i=0; i<photosArray.length(); i++ )
                    {
                        object = photosArray.getJSONObject(i);

                        //send photo urls to display activity
                        intent.putExtra("photo_url", object.getString("photo_130"));
                        Logged.this.getApplicationContext().startActivity(intent);

                        Log.d("VK_SINGLE_PHOTO", object.getString("photo_130"));
                    }

                    Log.d("VK_ONE_PHOTO", String.valueOf(photosArray));
                }
                catch(JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
}
