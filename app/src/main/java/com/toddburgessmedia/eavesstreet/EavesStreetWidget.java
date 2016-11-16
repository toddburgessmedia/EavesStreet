package com.toddburgessmedia.eavesstreet;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class EavesStreetWidget extends AppWidgetProvider {

    public final String ACTION = "com.toddburgessmedia.eavesstreet.UPDATE";
    public final String ERROR = "com.toddburgessmedia.eavesstreet.ERROR";
    public final String NETWORKERROR = "com.toddburgessmedia.eavesstreet.ERROR";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, Intent intent) {

        double change = intent.getDoubleExtra("change", 0.0);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.eaves_street_widget);

        views.setTextViewText(R.id.widget_ticker, "(e)" + intent.getStringExtra("ticker"));
        views.setTextViewText(R.id.widget_close,
                String.format("%.2f",intent.getDoubleExtra("close",0.0d))+"e");
        views.setTextViewText(R.id.widget_change,
                String.format("%.2f",Math.abs(change)));

        if (change >= 0) {
            views.setTextColor(R.id.widget_change, Color.BLACK);
            views.setViewVisibility(R.id.widget_change_up, View.VISIBLE);
            views.setViewVisibility(R.id.widget_change_down, View.GONE);
        } else {
            views.setTextColor(R.id.widget_change, Color.RED);
            views.setViewVisibility(R.id.widget_change_up, View.GONE);
            views.setViewVisibility(R.id.widget_change_down, View.VISIBLE);
        }




        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    static void updateAppWidgetError(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, String errormsg) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.eaves_street_widget);

        views.setTextViewText(R.id.widget_ticker, errormsg);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


        @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        SharedPreferences prefs = context.getSharedPreferences("eavesstreet", Context.MODE_PRIVATE);
        String accessToken = prefs.getString("access_token","invalid");
        String clientID = context.getString(R.string.clientid);

        Intent intent = new Intent(context,EavesStreetIntentService.class);
        intent.putExtra("access_token", accessToken);
        intent.putExtra("client_id", clientID);
        context.startService(intent);

        // There may be multiple widgets active, so update all of them

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        super.onReceive(context, intent);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        ComponentName name = new ComponentName(context, EavesStreetWidget.class);
        int[] appIds = manager.getAppWidgetIds(name);

        Log.d(EavesSteetMain.TAG, "onReceive: we got a message");
        Log.d(EavesSteetMain.TAG, "onReceive: " + intent.getAction());
        if (intent.getAction().equals(ACTION)) {
            for (int appWidgetId : appIds) {
                updateAppWidget(context, manager, appWidgetId,intent);
            }
        } else if (intent.getAction().equals(ERROR)) {
            createErrorNotification(context);
            for (int appWidgetId : appIds) {
                updateAppWidgetError(context, manager, appWidgetId, "Authentication Error");
            }
        } else if (intent.getAction().equals(NETWORKERROR)) {
            for (int appWidgetId : appIds) {
                updateAppWidgetError(context, manager, appWidgetId, "Network Error");
            }
        }

    }

    private void createErrorNotification (Context context) {

        Intent resultIntent = new Intent(context, EavesSteetMain.class);
        PendingIntent pi = PendingIntent.getActivity(context,0,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getString(R.string.app_name) + " Error")
                .setContentText("You must re-authenticate with Empire Avenue")
                .setAutoCancel(true)
                .setContentIntent(pi);

        NotificationManager mgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mgr.notify(1,builder.build());

    }

}

