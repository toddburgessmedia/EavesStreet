package com.toddburgessmedia.eavesstreet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class EavesSteetMain extends AppCompatActivity {

    public final static String TAG = "EavesStreet";

    static final int GET_AUTH_CODE = 111;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    String accessToken;
    long time;

    EavesStreetMainFragment fragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eaves_steet_main);

        prefs = getSharedPreferences("eavesstreet", MODE_PRIVATE);
        editor = prefs.edit();

        if (!accessTokenValid()) {
            Intent i = new Intent(this, EmpireAvenueAuthActivity.class);
            startActivityForResult(i, GET_AUTH_CODE);
        }

        fragment = new EavesStreetMainFragment();
        Bundle bundle = new Bundle();
        bundle.putString("access_token",accessToken);
        bundle.putLong("time",time);
        bundle.putString("clientID",getString(R.string.clientid));
        fragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_framelayout, fragment, "fragment");
        transaction.commit();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == GET_AUTH_CODE) {
            Log.d(TAG, "onActivityResult: " + data.getStringExtra("access_token"));
            if (resultCode == RESULT_OK) {
                editor.putString("access_token", data.getStringExtra("access_token"));
                editor.putLong("time", (System.currentTimeMillis() / 1000) + 5184000);
                editor.apply();
            }
        }
    }

    private boolean accessTokenValid() {

        accessToken = prefs.getString("access_token", "invalid");
        time = prefs.getLong("time",-1);

        if (accessToken.equals("invalid")) {
            return false;
        } else if ((time == -1) || ((System.currentTimeMillis()/1000) > time)) {
            return false;
        }

        return true;
    }
}
