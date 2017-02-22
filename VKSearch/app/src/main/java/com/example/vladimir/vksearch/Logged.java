package com.example.vladimir.vksearch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
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

    private String s_city, name, surname, photo_url;
    private Integer cnt;

    private JSONObject jUsers, userObject, object;
    private JSONArray usersArray;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        s_city = "Riga";
        cnt = 1;
        intent = new Intent(Logged.this, Photos.class);

        VKRequest search_users = new VKRequest("users.search", VKParameters.from("count", cnt, "hometown", s_city, "sex", 1, "status", 6, VKApiConst.FIELDS, "photo_max_orig, contacts, last_seen"));

        search_users.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);

                try
                {
                    jUsers = new JSONObject(response.responseString);
                    userObject = jUsers.getJSONObject("response");
                    usersArray = userObject.getJSONArray("items");

                    for( int i=0; i<usersArray.length(); i++ )
                    {
                        object = usersArray.getJSONObject(i);

                        name = object.getString("first_name");
                        surname = object.getString("last_name");
                        photo_url = object.getString("photo_max_orig");

                        try {
                            intent.putExtra("name", name);
                            intent.putExtra("surname", surname);
                            intent.putExtra("photo", photo_url);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            Logged.this.getApplicationContext().startActivity(intent);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }

                    Log.d("VK_FOUND_USERS", String.valueOf(object));
                }
                catch (JSONException je)
                {
                    je.printStackTrace();
                }
            }
        });
    }
}
