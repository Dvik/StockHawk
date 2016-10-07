package com.sam_chordas.android.stockhawk.API;

/**
 * Created by Divya on 10/7/2016.
 */

import com.sam_chordas.android.stockhawk.model.StockResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Divya on 9/15/2016.
 */

public interface StockAPI {

    @GET("/v1/public/yql?&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=")
    Call<StockResponse> getPriceOverTime (@Query("q") String query);

}
