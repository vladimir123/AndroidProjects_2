package com.example.vladimir.vksearch;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;
import com.vk.sdk.util.VKUtil;

import java.util.Arrays;

public class MainActivity extends Activity {

    private String[] scope = new String[]{VKScope.PHOTOS, VKScope.MESSAGES, VKScope.WALL};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*
        setContentView(R.layout.activity_main);
        String[] fingerprints = VKUtil.getCertificateFingerprint(this, this.getPackageName());

        TextView finger = (TextView)findViewById(R.id.txt_debug);
        finger.setText(String.valueOf(Arrays.asList(fingerprints)));

        Log.e("VK_FINGERPRINTS", String.valueOf(Arrays.asList(fingerprints)));
*/

//        if (!VKSdk.isLoggedIn())
            VKSdk.login(this, scope);
        Log.e("VK_MAINACTIVITY", "onCreate fired");
    }

    @Override
    protected void onStart() {
        super.onStart();
        setVisible(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                // Пользователь успешно авторизовался
                Log.d("VK_LOGIN", "Access granted");
                //Transfer VK data to other activity
                Intent intent = new Intent(MainActivity.this, Photos.class);
                intent.putExtra("res", res.accessToken);
                startActivity(intent);
                finish();
            }
            @Override
            public void onError(VKError error) {
                // Произошла ошибка авторизации (например, пользователь запретил авторизацию)
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
