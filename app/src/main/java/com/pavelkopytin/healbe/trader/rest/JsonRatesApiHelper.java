package com.pavelkopytin.healbe.trader.rest;

import retrofit.RestAdapter;

/*
 * Created by Wild on 07.06.2015.
 */

public class JsonRatesApiHelper {

    private static JsonRatesApiHelper instance;
    private final JsonRatesApi api;

    public synchronized static JsonRatesApiHelper getInstance() {
        if (instance == null)
            instance = new JsonRatesApiHelper();
        return instance;
    }

    private JsonRatesApiHelper() {
        final RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint("http://jsonrates.com/")
                .build();
        api = adapter.create(JsonRatesApi.class);
    }

    public JsonRatesApi getApi() {
        return api;
    }
}
