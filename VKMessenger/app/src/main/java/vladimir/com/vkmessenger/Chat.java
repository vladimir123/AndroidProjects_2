package vladimir.com.vkmessenger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by gavriluks on 06.09.2016.
 */
public class Chat extends Activity {
    private String u_id, photo_url, friend_name;
    private JSONObject jObj_Out, respObj, object;
    private JSONArray itemArr;

    private String vkUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_view);

        TextView txtView = (TextView)findViewById(R.id.txt_friendName);
        ImageView photo = (ImageView)findViewById(R.id.img_friendPhoto);

        //get data from friend list
        Intent intent = getIntent();
        u_id = intent.getStringExtra("user_id");
        photo_url = intent.getStringExtra("user_photo");
        friend_name = intent.getStringExtra("user_name");

        //create VKREquest
        VKRequest requestOutMessages = VKApi.messages().get(VKParameters.from(VKApiConst.USER_ID, u_id));

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

                        vkUID = object.getString("user_id");

                        if ( object.getString("user_id").equals(vkUID) )
                            Log.d("VK_MESSAGE_BODY", object.getString("body"));

                        Log.d("VK_MESSAGE_TO_FRIEND", String.valueOf(vkUID));
                    }

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
