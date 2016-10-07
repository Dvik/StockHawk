package com.sam_chordas.android.stockhawk.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.sam_chordas.android.stockhawk.API.StockAPI;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.client.StockDataClient;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.model.Example;
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

    TextView stockSymTv, bidPriceTv, emptyView;
    LineChart chart;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);

        initViews();

        String name = getIntent().getStringExtra(QuoteColumns.NAME);
        stockSymTv.setText(name);

        String bidPrice = getIntent().getStringExtra(QuoteColumns.BIDPRICE);
        bidPriceTv.setText(getString(R.string.bid_price, bidPrice));

        String symbol = getIntent().getStringExtra(QuoteColumns.SYMBOL);

        String stockQuery = "select * from yahoo.finance.historicaldata where symbol= '"
                + symbol + "' and startDate = '" + getEndDate() + "' and endDate ='" + getStartDate() + "'";

        StockAPI apiService =
                StockDataClient.getClient().create(StockAPI.class);

        Call<Example> call;

        call = apiService.getPriceOverTime(stockQuery);

        call.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {
                progressBar.setVisibility(View.GONE);
                chart.setVisibility(View.VISIBLE);
                chart.setNoDataText(getString(R.string.loading_chart));
                if (response.body().getStockResponse().getCount() > 0) {
                    List<Quote> quoteList = response.body().getStockResponse().getResults().getQuote();
                    ArrayList<String> labels = new ArrayList<String>();
                    List<Entry> entries = new ArrayList<Entry>();
                    for (Quote quote : quoteList) {
                        entries.add(new Entry(getActualTime(quote.getDate()), Float.parseFloat(quote.getHigh())));
                        labels.add(quote.getDate());
                        Log.d("Label", quote.getDate());
                    }
                    LineDataSet dataSet = new LineDataSet(entries, "Stock Price Over Time");
                    LineData lineData = new LineData(dataSet);
                    lineData.setValueTextColor(ContextCompat.getColor(StockDetailActivity.this, android.R.color.white));
                    XAxis xAxis = chart.getXAxis();
                    xAxis.setTextColor(ContextCompat.getColor(StockDetailActivity.this, android.R.color.white));
                    xAxis.setDrawGridLines(false);


                    YAxis yAxisLeft = chart.getAxisLeft();
                    yAxisLeft.setTextColor(ContextCompat.getColor(StockDetailActivity.this, android.R.color.white));
                    yAxisLeft.setDrawGridLines(false);

                    YAxis yAxisRight = chart.getAxisRight();
                    yAxisRight.setTextColor(ContextCompat.getColor(StockDetailActivity.this, android.R.color.white));
                    yAxisRight.setDrawGridLines(false);
                    yAxisRight.setDrawLabels(false);
                    yAxisRight.setDrawTopYLabelEntry(false);

                    chart.setData(lineData);

                    chart.setDescription(getString(R.string.last_10, quoteList.get(0).getDate(),
                            quoteList.get(quoteList.size() - 1).getDate()));
                    chart.setDescriptionColor(ContextCompat.getColor(StockDetailActivity.this, android.R.color.white));

                    chart.invalidate();
                }
            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);

            }
        });

    }

    public void initViews() {
        stockSymTv = (TextView) findViewById(R.id.stock_sym_tv);
        bidPriceTv = (TextView) findViewById(R.id.stock_price_tv);
        chart = (LineChart) findViewById(R.id.chart);
        progressBar = (ProgressBar) findViewById(R.id.progress_chart);
        emptyView = (TextView) findViewById(R.id.empty_view);

    }

    public String getStartDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(cal.getTime());
    }

    public String getEndDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -20);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(cal.getTime());
    }

    public Float getActualTime(String dateString) {
        Date todayDate = new Date();
        try {
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sd.parse(dateString);
            return Float.parseFloat(String.valueOf(Math.abs((((date.getTime() - todayDate.getTime()) / 1000) / 3600) / 24)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0f;
    }
}
