package vladimir.com.vkmessenger;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

/**
 * Created by vladimir on 14.08.2016.
 */
public class SplashClass extends Activity {

    public static Boolean isShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        if ( isOnline() ) {
            Log.d("VK_SPLASSHER", "Phone isOnline!!");

            VKSdk.wakeUpSession(this, new VKCallback<VKSdk.LoginState>() {
                @Override
                public void onResult(VKSdk.LoginState res) {
                    switch (res) {
                        case LoggedOut:
                            startActivity(new Intent(SplashClass.this, MainActivity.class));
                            finish();
                            break;
                        case LoggedIn:
                            Intent intent = new Intent(SplashClass.this, Logged.class);
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

    //if phone is offline, show dialog to Internet services and recreate activity
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
