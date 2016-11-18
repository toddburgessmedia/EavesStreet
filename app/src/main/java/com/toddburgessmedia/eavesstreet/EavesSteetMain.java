package com.toddburgessmedia.eavesstreet;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class EavesSteetMain extends AppCompatActivity {

    public final static String TAG = "EavesStreet";

    static final int GET_AUTH_CODE = 111;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    String accessToken;
    long time;

    EavesStreetMainFragment fragment;


    @Override
    public void onStart() {
        super.onStart();

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eaves_steet_main);

        prefs = getSharedPreferences("eavesstreet", MODE_PRIVATE);
        editor = prefs.edit();

        if (!accessTokenValid()) {
            authenticateUser();
            return;
        }

        createFragment();

    }

    private void createFragment() {
        fragment = new EavesStreetMainFragment();
        Bundle bundle = new Bundle();
        bundle.putString("access_token",accessToken);
        bundle.putLong("time",time);
        bundle.putString("clientID",getString(R.string.clientid));
        fragment.setArguments(bundle);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.main_framelayout, fragment, "fragment");
                transaction.commit();
            }
        });

    }

    public void authenticateUser() {
        Intent i = new Intent(this, EmpireAvenueAuthActivity.class);
        startActivityForResult(i, GET_AUTH_CODE);
    }

    @Subscribe
    public void authenticateUser(EavesStreetMainFragment.UnAuthorizedMessage message) {
        Intent i = new Intent(this, EmpireAvenueAuthActivity.class);
        startActivityForResult(i, GET_AUTH_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == GET_AUTH_CODE) {
            Log.d(TAG, "onActivityResult: " + data.getStringExtra("access_token"));
            if (resultCode == RESULT_OK) {
                editor.putString("access_token", data.getStringExtra("access_token"));
                editor.putLong("time", (System.currentTimeMillis() / 1000) + 5184000);
                editor.apply();
                getPrefValues();
                createFragment();
                updateWidget();
            }
        }
    }

    private void updateWidget() {

        int ids[] = AppWidgetManager.getInstance(this).getAppWidgetIds(new ComponentName(this,EavesStreetWidget.class));

        if (ids.length == 0) {
            return;
        }

        Toast.makeText(this, R.string.eavesmain_toast_update, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, EavesStreetIntentService.class);
        intent.putExtra("access_token", accessToken);
        intent.putExtra("client_id", getString(R.string.clientid));
        startService(intent);
    }


    private boolean accessTokenValid() {

        getPrefValues();

        if (accessToken.equals("invalid")) {
            return false;
        } else if ((time == -1) || ((System.currentTimeMillis()/1000) > time)) {
            return false;
        }

        return true;
    }

    private void getPrefValues() {
        accessToken = prefs.getString("access_token", "invalid");
        time = prefs.getLong("time",-1);
    }
}
