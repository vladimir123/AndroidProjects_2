package com.example.vladimir.vksearch;

import android.annotation.SuppressLint;
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

import java.util.Date;

/**
 * Created by Vladimir on 21.02.2017.
 */

public class Logged extends Activity {

    private String s_city, name, surname, photo_url, m_phone, h_phone;
    private Integer cnt;
    private Date las_seen;

    private JSONObject jUsers;
    private JSONObject userObject;
    private JSONObject object;
    private JSONArray lastSeenObj;
    private JSONArray usersArray, l_seenArray;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        s_city = "Riga";
        cnt = 2;
        intent = new Intent(Logged.this, Photos.class);

        VKRequest search_users = new VKRequest("users.search", VKParameters.from("count", cnt, "hometown", s_city, "sex", 1, "status", 6, VKApiConst.FIELDS, "photo_max_orig, contacts, last_seen"));

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

                    Log.d("VK_FOUND_USERS_ARRAY", String.valueOf(usersArray));

                    intent.putExtra("userArray", usersArray.toString());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Logged.this.getApplicationContext().startActivity(intent);
                }
                catch (JSONException je)
                {
                    je.printStackTrace();
                }
            }
        });
    }

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
}
