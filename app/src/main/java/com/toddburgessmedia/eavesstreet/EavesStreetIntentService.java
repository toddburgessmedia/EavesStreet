package com.toddburgessmedia.eavesstreet;

import android.app.IntentService;
import android.content.Intent;

import com.toddburgessmedia.eavesstreet.retrofit.EAProfile;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class EavesStreetIntentService extends IntentService  {

    public EavesStreetIntentService() {
        super("EavesStreetIntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) {
            return;
        }

        String accessToken = intent.getStringExtra("access_token");
        String clientID = intent.getStringExtra("client_id");

        EavesStreetPresenter presenter = new EavesStreetPresenter(clientID, accessToken, this);
        presenter.fetchEAProfile();
    }

    public void update(EAProfile profile) {

        Intent intent = new Intent("com.toddburgessmedia.eavesstreet.UPDATE");
        intent.putExtra("ticker", profile.getTicker());
        intent.putExtra("close", profile.getClose());
        intent.putExtra("change", profile.getChange());
        sendBroadcast(intent);

    }

    public void onError(String errorMsg) {

        String action;
        if (errorMsg.equals("network")) {
            action = "com.toddburgessmedia.eavesstreet.NETWORKERROR";
        } else {
            action = "com.toddburgessmedia.eavesstreet.ERROR";
        }

        Intent intent = new Intent(action);
        intent.putExtra("errormsg", errorMsg);
        sendBroadcast(intent);

    }
}
