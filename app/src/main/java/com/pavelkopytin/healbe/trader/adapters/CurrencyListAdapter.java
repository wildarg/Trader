package com.pavelkopytin.healbe.trader.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pavelkopytin.healbe.trader.R;
import com.pavelkopytin.healbe.trader.provider.TraderProviderContract;

/*
 * Created by Wild on 07.06.2015.
 */
public class CurrencyListAdapter extends CursorAdapter {

    private final String selectedName;

    public CurrencyListAdapter(Context context, String selectedName) {
        super(context, null, 0);
        this.selectedName = selectedName;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.currency_item, parent, false);
        view.setTag(new CurrencyViewHolder(view));
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        CurrencyViewHolder h = (CurrencyViewHolder) view.getTag();
        h.populate(cursor);
    }

    private class CurrencyViewHolder {
        TextView nameText;
        TextView descriptionText;
        ImageView checkedImage;

        public CurrencyViewHolder(View view) {
            nameText = (TextView) view.findViewById(R.id.nameTextView);
            descriptionText = (TextView) view.findViewById(R.id.descriptionTextView);
            checkedImage = (ImageView) view.findViewById(R.id.checkedImage);
        }

        public void populate(Cursor c) {
            String name = c.getString(c.getColumnIndex(TraderProviderContract.Currencies.NAME));
            nameText.setText(name);
            descriptionText.setText(c.getString(c.getColumnIndex(TraderProviderContract.Currencies.DESCRIPTION)));
            if (name.equals(selectedName))
                checkedImage.setVisibility(View.VISIBLE);
            else
                checkedImage.setVisibility(View.INVISIBLE);
        }
    }
}
