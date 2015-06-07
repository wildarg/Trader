package com.pavelkopytin.healbe.trader.tasks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import com.pavelkopytin.healbe.trader.provider.TraderProviderContract;
import com.pavelkopytin.healbe.trader.rest.JsonRatesApi;
import com.pavelkopytin.healbe.trader.rest.JsonRatesApiHelper;
import com.pavelkopytin.healbe.trader.rest.resources.Rate;

/*
 * Created by Wild on 07.06.2015.
 */
public class RefreshRatesTask extends AsyncTask<Void, Void, Void> {

    private static final String API_KEY = "jr-f6047194fdd5ff60782db7c9da0dbfaf";
    private final Context context;
    private final JsonRatesApi api;

    public RefreshRatesTask(Context context) {
        this.context = context;
        api = JsonRatesApiHelper.getInstance().getApi();
    }

    @Override
    protected Void doInBackground(Void... params) {
        Cursor c = context.getContentResolver().query(TraderProviderContract.Pairs.CONTENT_URI, null, null, null, null);
        while (c.moveToNext()) {
            updatePair(c);
        }
        c.close();
        return null;
    }

    private void updatePair(Cursor c) {
        Uri pairUri = Uri.withAppendedPath(TraderProviderContract.Pairs.CONTENT_URI,
                c.getString(c.getColumnIndex(TraderProviderContract.Pairs._ID)));
        String from = c.getString(c.getColumnIndex(TraderProviderContract.Pairs.FROM));
        String to = c.getString(c.getColumnIndex(TraderProviderContract.Pairs.TO));
        float firstRate = c.getFloat(c.getColumnIndex(TraderProviderContract.Pairs.FIRST_RATE));
        Rate rateInfo = api.getRate(from, to, API_KEY);

        ContentValues values = new ContentValues();
        values.put(TraderProviderContract.Pairs.LAST_RATE, rateInfo.getRate());
        if (firstRate == 0)
            values.put(TraderProviderContract.Pairs.FIRST_RATE, rateInfo.getRate());
        context.getContentResolver().update(pairUri, values, null, null);
    }
}
