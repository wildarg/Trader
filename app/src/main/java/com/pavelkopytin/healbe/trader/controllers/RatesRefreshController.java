package com.pavelkopytin.healbe.trader.controllers;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import com.pavelkopytin.healbe.trader.provider.TraderProviderContract;
import com.pavelkopytin.healbe.trader.rest.JsonRatesApi;
import com.pavelkopytin.healbe.trader.rest.JsonRatesApiHelper;
import com.pavelkopytin.healbe.trader.rest.resources.Rate;

/*
 * Created by Wild on 07.06.2015.
 */
public class RatesRefreshController {

    private static final String API_KEY = "jr-f6047194fdd5ff60782db7c9da0dbfaf";
    private static final long REFRESH_INTERVAL = 20000;
    private final Handler handler;
    private final ProgressCallback progressCallback;
    private final ContentResolver resolver;
    private final JsonRatesApi api;
    private final Context context;
    private RefreshRatesTask2 refreshTask;
    private boolean stopRefreshing;

    public RatesRefreshController(Context context, ProgressCallback callback) {
        handler = new Handler();
        progressCallback = callback;
        this.context = context;
        resolver = context.getContentResolver();
        api = JsonRatesApiHelper.getInstance().getApi();
    }

    public interface ProgressCallback {
        void startProgress();
        void stopProgress();
    }

    public void startRefresh() {
        stopRefreshing = false;
        refreshTask = new RefreshRatesTask2();
        refreshTask.execute();
    }

    public void stopRefresh() {
        stopRefreshing = true;
        if (refreshTask != null)
            refreshTask.cancel(true);
    }

    private class RefreshRatesTask2 extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressCallback.startProgress();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Cursor c = resolver.query(TraderProviderContract.Pairs.CONTENT_URI, null, null, null, null);
            try {
                while (c.moveToNext()) {
                    updatePair(c);
                }
            } catch (final Exception e) {
                e.printStackTrace();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Error on rate update: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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
            resolver.update(pairUri, values, null, null);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!isCancelled()) {
                progressCallback.stopProgress();
                if (!stopRefreshing)
                    sheduleRefresh();
            }
        }
    }

    private void sheduleRefresh() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!stopRefreshing) {
                    refreshTask = new RefreshRatesTask2();
                    refreshTask.execute();
                }
            }
        }, REFRESH_INTERVAL);
    }
}
