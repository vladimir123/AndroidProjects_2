package vladimir.com.vkmessenger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.methods.VKApiMessages;
import com.vk.sdk.api.model.VKApiChat;
import com.vk.sdk.api.model.VKApiMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by gavriluks on 06.09.2016.
 */
public class Chat extends Activity {
    private String u_id, photo_url, friend_name;
    private JSONObject jObj_Out, respObj, object;
    private JSONArray itemArr;

//    private String vkUID;
    ArrayList<String> msg = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_view);

        final TextView txtView = (TextView)findViewById(R.id.txt_friendName);
        final ImageView photo = (ImageView)findViewById(R.id.img_friendPhoto);
        final ListView msg_body = (ListView)findViewById(R.id.msg_body);

        //get data from friend list
        Intent intent = getIntent();
        u_id = intent.getStringExtra("user_id");
        photo_url = intent.getStringExtra("user_photo");
        friend_name = intent.getStringExtra("user_name");

        //create VKREquest
        VKRequest requestOutMessages = new VKRequest("messages.getHistory", VKParameters.from("user_id", u_id, "count", 200));

        requestOutMessages.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);

                try {
                    jObj_Out = new JSONObject(response.responseString);
                    respObj = jObj_Out.getJSONObject("response");
                    itemArr = respObj.getJSONArray("items");

                    for (int i=0; i<itemArr.length(); i++)
                    {
                        object = itemArr.getJSONObject(i);

                        msg.add(object.getString("body"));
                    }
                    adapter = new ArrayAdapter<String>(Chat.this, android.R.layout.simple_list_item_1, msg);
                    msg_body.setAdapter(adapter);

                    Log.d("VK_MESSAGE", String.valueOf(itemArr));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        //set data into dialog window
        Picasso.with(this).load(photo_url).transform(new CircleTransform()).into(photo);
        txtView.setText(friend_name + " id=> " + u_id);
    }
}
