package vladimir.com.vkmessenger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKBatchRequest;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vladimir on 14.08.2016.
 */
public class Logged extends Activity {

    private String name;
    private String surname;
    private String friend_full;

    private ArrayList<String> online_ = new ArrayList<>();;

    private ArrayList<String> friendArray = new ArrayList<String>();
    private ArrayList<String> photo_url = new ArrayList<String>();
    private ArrayList<String> friend_id = new ArrayList<>();

    ListView list;
    List<Boolean> flag = new ArrayList<Boolean>(); //online flag

    JSONArray itemArr, online_mobile;
    JSONObject object, jObj_friend, jObj_online;
    JSONObject respObj;

    FriendListView adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logged_activity);

//TODO remove all test information(e.g toast/textboxes...);

        Toast.makeText(getApplicationContext(), "USER WAS LOGGED IN BEFORE", Toast.LENGTH_LONG).show();

        //VKRequests creating
        VKRequest request_friend = VKApi.friends().get(VKParameters.from(VKApiConst.FIELDS, "photo_50, first_name, last_name, online"));
        VKRequest request_online = VKApi.friends().getOnline(VKParameters.from(VKApiUser.FIELD_ONLINE_MOBILE, null, 1));

        //executing VKRequests
        VKBatchRequest batch = new VKBatchRequest(request_friend, request_online);
        batch.executeWithListener(new VKBatchRequest.VKBatchRequestListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onComplete(VKResponse[] responses) {
                super.onComplete(responses);

                Log.d("VK_RESPONSE[1]", responses[1].responseString);//online
                Log.d("VK_RESPONSE[0]", responses[0].responseString);//friends

                try
                {
                    jObj_friend = new JSONObject(responses[0].responseString);
                    respObj = jObj_friend.getJSONObject("response"); //get top level of response
                    itemArr = respObj.getJSONArray("items"); //get second level of response

                    jObj_online = new JSONObject(responses[1].responseString);
                    online_mobile = jObj_online.getJSONArray("response"); //get id's from response

                    for (int i=0; i < itemArr.length(); i++)
                    {
                        object = itemArr.getJSONObject(i);

                        Log.d("VK_FRIENDARRAY", String.valueOf(itemArr));

                        name = object.getString("first_name");
                        surname = object.getString("last_name");
                        friend_full = name + " " + surname;

                        friendArray.add(friend_full);
                        photo_url.add(object.getString("photo_50"));

                        friend_id.add(object.getString("id"));

//TODO: CHECKOUT FOR ONLINE USERES

                        //online friend (Desktop + mobile)
                        Log.d("VK_ONLINE_FRIEND_DESKTOP", name + " " + surname + " is online =>" + object.getString("online"));
//                        if ( object.getString("online") == "1" || online_mobile.length() > 0 )
//                        {
                            for (int j = 0; j < online_mobile.length(); j++) {
                                Log.d("VK_ONLINE_FRIEND", name + " " + surname + " is online =>" + String.valueOf(friend_id.get(i).equals(online_mobile.getString(j))));

//                                Log.d("VK_ONLINE_MOBILE", String.valueOf(online_mobile.get(j)));
                                Log.d( "VK_FRIEND_ONLINE_MOBILE", String.valueOf(online_mobile.getInt(j)));

                                online_ .add(online_mobile.getString(j));

//                                online_.add(String.valueOf(online_mobile.getString(j)));

//                                if (
//                                        friend_id.get(i).equals(online_mobile.getString(j)) ||
//                                        object.getString("online") == "1"
//                                   )
//                                    flag.add(true);
//                                else
//                                    flag.add(false);
                            }
//                        }
//                    Log.d("VK_FLAG", String.valueOf(flag));
                    adapter = new FriendListView(Logged.this, photo_url, friendArray, friend_id, online_);
                    }
                    Log.d("VK_ONLINE_MOBILE", String.valueOf(online_mobile));

                    //set data into custom ListView
                    list = (ListView) findViewById(R.id.listView);
                    list.setAdapter(adapter);

                    Log.d("VK_JOBJ_FRIENDS", String.valueOf(friendArray));
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);

                Log.d("VK_BATCH_ERROR", error.errorMessage + " " + error.errorReason);
            }
        });
    }
}
