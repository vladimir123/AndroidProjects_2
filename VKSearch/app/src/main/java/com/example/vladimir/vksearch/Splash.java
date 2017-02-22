package com.example.vladimir.vksearch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import static com.vk.sdk.VKSdk.LoginState.LoggedIn;
import static com.vk.sdk.VKSdk.LoginState.LoggedOut;
import static com.vk.sdk.VKSdk.LoginState.Pending;
import static com.vk.sdk.VKSdk.LoginState.Unknown;

/**
 * Created by Vladimir on 21.02.2017.
 */

public class Splash extends Activity {

    public static Boolean isShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ( isOnline() ) {
            Log.d("VK_SPLASSHER", "Phone isOnline!!");

            VKSdk.wakeUpSession(this, new VKCallback<VKSdk.LoginState>() {
                @Override
                public void onResult(VKSdk.LoginState res) {

                    Log.d("VK_SPLASHER_RES", String.valueOf(res));

                    switch (res) {
                        case LoggedOut:
                            startActivity(new Intent(Splash.this, MainActivity.class));
                            finish();
                            break;
                        case LoggedIn:
                            Intent intent = new Intent(Splash.this, Logged.class);
                            startActivity(intent);
                            finish();
                            break;
                        case Pending:
                            break;
                        case Unknown:
                            break;
                    }
                }

                @Override
                public void onError(VKError error) {
                    Log.d("VK_SplashError", error.errorMessage);
                }
            });
        }
        else
        {
            Log.d("VK_SPLASSHER", "Phone isOfline!!");

            showNoConnectionDialog(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("VK_SPLASH", "OnResume");
        Log.d("VK_Splasher", "isShown => "+isShown);

        if ( isShown ) {
            try {
                if (isOnline()) {
                    Log.d("VK_SPLASHER", "Activity recreated");

                    this.recreate();
                    finish();
                }
            } catch (Exception ex) {
                Log.d("VK_SPLASH_RESTART", ex.getMessage());
            }
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static void showNoConnectionDialog(Context ctx1)
    {
        final Context ctx = ctx1;
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setCancelable(true);
        builder.setMessage(R.string.no_connection);
        builder.setTitle(R.string.no_connection_title);
        builder.setPositiveButton(R.string.settings_button_text, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which)
            {
                ctx.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
            }
        });

        builder.setNegativeButton(R.string.cancel_button_text, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                return;
            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            public void onCancel(DialogInterface dialog) {
                return;
            }
        });

        builder.show();
        isShown = true;
    }
}
