package com.toddburgessmedia.eavesstreet;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class EavesStreetWidget extends AppWidgetProvider {




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

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        SharedPreferences prefs = context.getSharedPreferences("eavesstreet", Context.MODE_PRIVATE);
        String accessToken = prefs.getString("access_token","invalid");
        String clientID = context.getString(R.string.clientid);

        Intent intent = new Intent(context,EavesStreetIntentService.class);
        intent.putExtra("access_token", accessToken);
        intent.putExtra("client_id", clientID);
        intent.putExtra("appIds", appWidgetIds);
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

        if (intent.getIntArrayExtra("appIds") == null) {
            return;
        }

        AppWidgetManager manager = AppWidgetManager.getInstance(context);

        Log.d(EavesSteetMain.TAG, "onReceive: we got a message");

        for (int appWidgetId : intent.getIntArrayExtra("appIds")) {
            updateAppWidget(context, manager, appWidgetId,intent);
        }
    }


}

