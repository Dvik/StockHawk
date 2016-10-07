package com.sam_chordas.android.stockhawk.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.sam_chordas.android.stockhawk.API.StockAPI;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.client.StockDataClient;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.model.Quote;
import com.sam_chordas.android.stockhawk.model.StockResponse;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StockDetailActivity extends AppCompatActivity {

    TextView stockSymTv,bidPriceTv;
    LineChart chart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);

        initViews();

        String name = getIntent().getStringExtra(QuoteColumns.NAME);
        stockSymTv.setText(name);

        String bidPrice = getIntent().getStringExtra(QuoteColumns.BIDPRICE);
        bidPriceTv.setText(getString(R.string.bid_price,bidPrice));

        String symbol = getIntent().getStringExtra(QuoteColumns.SYMBOL);
        StringBuilder urlStringBuilder = new StringBuilder();
        try {
            urlStringBuilder.append(URLEncoder.encode("select * from yahoo.finance.quotes where symbol "
                    + "= \'"+symbol+"\') and startDate =\'"+getEndDate()+"\'and endDate =\'"
                    +getStartDate()+"\'", "UTF-8"));
            urlStringBuilder.append("&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables."
                    + "org%2Falltableswithkeys&callback=");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String stockQuery = urlStringBuilder.toString();

        StockAPI apiService =
                StockDataClient.getClient().create(StockAPI.class);

        Call<StockResponse> call;

        call = apiService.getPriceOverTime(stockQuery);

        call.enqueue(new Callback<StockResponse>() {
            @Override
            public void onResponse(Call<StockResponse> call, Response<StockResponse> response) {
                List<Quote> quoteList = response.body().getResults().getQuote();
                List<Entry> entries = new ArrayList<Entry>();
                for(Quote quote:quoteList)
                {
                    Log.d("Vlaues",String.valueOf(getActualTime(quote.getDate()))+" "+quote.getHigh());
                    entries.add(new Entry(getActualTime(quote.getDate()),Float.parseFloat(quote.getHigh())));
                }
                LineDataSet dataSet = new LineDataSet(entries,"Stock Price Over Time");
                LineData lineData = new LineData(dataSet);
                chart.setData(lineData);
                chart.invalidate();
            }

            @Override
            public void onFailure(Call<StockResponse> call, Throwable t) {

            }
        });

    }

    public void initViews()
    {
        stockSymTv = (TextView) findViewById(R.id.stock_sym_tv);
        bidPriceTv = (TextView) findViewById(R.id.stock_price_tv);
        chart = (LineChart) findViewById(R.id.chart);

    }

    public String getStartDate()
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(cal.getTime());
    }

    public String getEndDate()
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -10);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(cal.getTime());
    }

    public Float getActualTime(String dateString) {
        Date todayDate = new Date();
        try {
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sd.parse(dateString);
            return Float.parseFloat(String.valueOf(date.getTime() - todayDate.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0f;
    }
}
