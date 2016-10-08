package com.sam_chordas.android.stockhawk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;

/**
 * Created by Divya on 10/8/2016.
 */

public class StockWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for(int appWidgetId: appWidgetIds)
        {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.stock_widget_small);
            remoteViews.setTextViewText(R.id.widget_stock_symbol,"APPL");
            remoteViews.setTextViewText(R.id.widget_stock_price,"700.43");

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            {
                remoteViews.setContentDescription(R.id.widget_stock_symbol,context.getString(R.string.a11y_stock_price,"700.43"));
                remoteViews.setContentDescription(R.id.widget_stock_symbol,context.getString(R.string.a11y_stock_symbol,"APPL"));
            }

            Intent launchIntent = new Intent(context, MyStocksActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context,0,launchIntent,0);

            remoteViews.setOnClickPendingIntent(R.id.widget,pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId,remoteViews);
        }

    }
}
