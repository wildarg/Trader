package com.pavelkopytin.healbe.trader;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.pavelkopytin.healbe.trader.adapters.CurrencyListAdapter;
import com.pavelkopytin.healbe.trader.provider.TraderProviderContract;
import com.pavelkopytin.healbe.trader.rest.JsonRatesApi;
import com.pavelkopytin.healbe.trader.rest.JsonRatesApiHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/*
 * Created by Wild on 06.06.2015.
 */
public class SelectCurrencyActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String EXTRA_CURRENCY_NAME = "extra_currency_name";
    private CurrencyListAdapter adapter;
    private EditText findEditText;
    private CursorLoader loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_currency);
        findEditText = (EditText) findViewById(R.id.find);
        findEditText.addTextChangedListener(new FindTextChangeListener());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        refreshCurrencies();
        adapter = new CurrencyListAdapter(this, getIntent().getStringExtra(EXTRA_CURRENCY_NAME));
        ListView list = (ListView) findViewById(R.id.currenciesList);
        list.setOnItemClickListener(new ListItemClickListener());
        list.setAdapter(adapter);

        loader = (CursorLoader) getLoaderManager().initLoader(0, null, this);
    }

    private void refreshCurrencies() {
        JsonRatesApi api = JsonRatesApiHelper.getInstance().getApi();
        api.getCurrencies(new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                try {
                    List<ContentValues> valuesList = new ArrayList<ContentValues>();
                    JSONObject json = new JSONObject(jsonObject.toString());
                    for (Iterator<String> keys = json.keys(); keys.hasNext(); ) {
                        String key = keys.next();
                        ContentValues values = new ContentValues();
                        values.put(TraderProviderContract.Currencies.NAME, key);
                        values.put(TraderProviderContract.Currencies.DESCRIPTION, json.optString(key));
                        valuesList.add(values);
                    }
                    getContentResolver().bulkInsert(TraderProviderContract.Currencies.CONTENT_URI,
                            valuesList.toArray(new ContentValues[valuesList.size()]));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(SelectCurrencyActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, TraderProviderContract.Currencies.CONTENT_URI, null,
                String.format("%s like @find or %s like @find", TraderProviderContract.Currencies.NAME, TraderProviderContract.Currencies.DESCRIPTION),
                new String[] {getSearchText()},
                TraderProviderContract.Currencies.NAME);
    }

    private String getSearchText() {
        return "%" + findEditText.getText().toString() + "%";
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    private class FindTextChangeListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {      }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {   }

        @Override
        public void afterTextChanged(Editable s) {
            final String findText = "%" + s.toString() + "%";
            loader.setSelectionArgs(new String[] {findText});
            loader.forceLoad();
        }
    }

    private class ListItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Cursor c = (Cursor) adapter.getItem(position);
            String name = c.getString(c.getColumnIndex(TraderProviderContract.Currencies.NAME));
            Intent intent = new Intent();
            intent.putExtra(EXTRA_CURRENCY_NAME, name);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
