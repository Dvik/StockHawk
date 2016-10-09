package com.sam_chordas.android.stockhawk.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DetailWidgetRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }

                final long identityToken = Binder.clearCallingIdentity();

                data = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                        new String[] { "Distinct " + QuoteColumns.SYMBOL ,
                        QuoteColumns.BIDPRICE , QuoteColumns.NAME, QuoteColumns._ID},
                        QuoteColumns.SYMBOL+" IS NOT NULL) GROUP BY ("+QuoteColumns.SYMBOL,
                        null, null);

                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_list_item);

                String symbol = data.getString(data.getColumnIndex(QuoteColumns.SYMBOL));
                String bidPrice = data.getString(data.getColumnIndex(QuoteColumns.BIDPRICE));
                String name = data.getString(data.getColumnIndex(QuoteColumns.NAME));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    setRemoteContentDescription(views, getString(R.string.a11y_stock_price, bidPrice),
                            getString(R.string.a11y_stock_symbol, symbol));
                }

                views.setTextViewText(R.id.stock_symbol, symbol);
                views.setTextViewText(R.id.bid_price, bidPrice);

                Bundle extras = new Bundle();
                extras.putString(QuoteColumns.NAME, name);
                extras.putString(QuoteColumns.SYMBOL, symbol);
                extras.putString(QuoteColumns.BIDPRICE, bidPrice);

                final Intent intent = new Intent();
                intent.putExtras(extras);
                views.setOnClickFillInIntent(R.id.widget_list, intent);
                return views;
            }

            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            private void setRemoteContentDescription(RemoteViews views, String bidPriceDesc, String symbolDesc) {
                views.setContentDescription(R.id.stock_symbol, symbolDesc);
                views.setContentDescription(R.id.stock_symbol, bidPriceDesc);
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return Long.parseLong(data.getString(data.getColumnIndex(QuoteColumns._ID)));
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}