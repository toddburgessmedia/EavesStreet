package com.toddburgessmedia.eavesstreet;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.toddburgessmedia.eavesstreet.retrofit.EAProfile;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class EavesStreetIntentService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.toddburgessmedia.eavesstreet.action.FOO";
    private static final String ACTION_BAZ = "com.toddburgessmedia.eavesstreet.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.toddburgessmedia.eavesstreet.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.toddburgessmedia.eavesstreet.extra.PARAM2";

    public EavesStreetIntentService() {
        super("EavesStreetIntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, EavesStreetIntentService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) {
            return;
        }

        Log.d(EavesSteetMain.TAG, "onHandleIntent: intent fired up");

        String accessToken = intent.getStringExtra("access_token");
        String clientID = intent.getStringExtra("client_id");
        int[] appIds = intent.getIntArrayExtra("appIds");
        Log.d(EavesSteetMain.TAG, "onHandleIntent: " + appIds.toString());

        EavesStreetPresenter presenter = new EavesStreetPresenter(clientID, accessToken, this,appIds);
        presenter.fetchEAProfile();
    }

    public void updateService(EAProfile profile, int[] appIds) {

        Log.d(EavesSteetMain.TAG, "updateService: " + appIds.toString());

        Intent intent = new Intent("com.toddburgessmedia.eavesstreet.UPDATE");
        intent.putExtra("ticker", profile.getTicker());
        intent.putExtra("close", profile.getClose());
        intent.putExtra("change", profile.getChange());
        intent.putExtra("appIds", appIds);
        sendBroadcast(intent);

    }
}
