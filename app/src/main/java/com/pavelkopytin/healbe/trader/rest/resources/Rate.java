package com.pavelkopytin.healbe.trader.rest.resources;

/*
 * Created by Wild on 07.06.2015.
 */
public class Rate {

    //{"utctime":"2015-06-07T13:30:02+02:00","from":"USD","to":"RUB","rate":"56.29499800"}

    String from;
    String to;
    float rate;

    public float getRate() {
        return rate;
    }
}
