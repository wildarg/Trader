package com.pavelkopytin.healbe.trader.rest;

import com.google.gson.JsonObject;
import com.pavelkopytin.healbe.trader.rest.resources.Rate;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/*
 * Created by Wild on 07.06.2015.
 */
public interface JsonRatesApi {

    @GET("/currencies.json")
    void getCurrencies(Callback<JsonObject> callback);

    @GET("/get")
    Rate getRate(@Query("from") String from, @Query("to") String to, @Query("apiKey") String apiKey);

}
