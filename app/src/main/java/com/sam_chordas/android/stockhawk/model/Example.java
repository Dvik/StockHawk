package com.sam_chordas.android.stockhawk.model;

/**
 * Created by Divya on 10/7/2016.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Example {

    @SerializedName("stockResponse")
    @Expose
    private StockResponse stockResponse;

    /**
     *
     * @return
     * The stockResponse
     */
    public StockResponse getStockResponse() {
        return stockResponse;
    }

    /**
     *
     * @param stockResponse
     * The stockResponse
     */
    public void setStockResponse(StockResponse stockResponse) {
        this.stockResponse = stockResponse;
    }

}