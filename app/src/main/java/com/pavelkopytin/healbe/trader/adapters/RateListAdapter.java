package com.pavelkopytin.healbe.trader.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.pavelkopytin.healbe.trader.R;
import com.pavelkopytin.healbe.trader.provider.TraderProviderContract;

/*
 * Created by Wild on 07.06.2015.
 */
public class RateListAdapter extends CursorAdapter {

    public RateListAdapter(Context context) {
        super(context, null, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.rates_row, parent, false);
        view.setTag(new RatesRowViewHolder(view));
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        RatesRowViewHolder h = (RatesRowViewHolder) view.getTag();
        h.populate(cursor);
    }

    private class RatesRowViewHolder {

        TextView fromCurrencyText;
        TextView toCurrencyText;
        TextView rateInfoText;

        public RatesRowViewHolder(View view) {
            fromCurrencyText = (TextView) view.findViewById(R.id.fromCurrencyTextView);
            toCurrencyText = (TextView) view.findViewById(R.id.toCurrencyTextView);
            rateInfoText = (TextView) view.findViewById(R.id.rateInfo);
        }

        public void populate(Cursor c) {
            fromCurrencyText.setText(c.getString(c.getColumnIndex(TraderProviderContract.Pairs.FROM)));
            toCurrencyText.setText(c.getString(c.getColumnIndex(TraderProviderContract.Pairs.TO)));
            float firstRate = c.getFloat(c.getColumnIndex(TraderProviderContract.Pairs.FIRST_RATE));
            float lastRate = c.getFloat(c.getColumnIndex(TraderProviderContract.Pairs.LAST_RATE));
            rateInfoText.setText(String.format("%,.4f (%+.4f)", lastRate, lastRate - firstRate));
        }
    }
}
