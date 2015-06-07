package com.pavelkopytin.healbe.trader;

import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.pavelkopytin.healbe.trader.provider.TraderProviderContract;

/*
 * Created by Wild on 06.06.2015.
 */
public class PairEditActivity extends AppCompatActivity {

    private static final int SELECT_FROM_CURRENCY_REQUEST = 1;
    private static final int SELECT_TO_CURRENCY_REQUEST = 2;
    private static final String FROM_CURRENCY_NAME = "from_currency_name";
    private static final String TO_CURRENCY_NAME = "to_currency_name";
    private String fromCurrencyName;
    private String toCurrencyName;
    private AppCompatButton fromButton;
    private AppCompatButton toButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pair);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fromButton = (AppCompatButton) findViewById(R.id.fromButton);
        fromButton.setOnClickListener(new SelectFromCurrencyButtonClickListener());
        toButton = (AppCompatButton) findViewById(R.id.toButton);
        toButton.setOnClickListener(new SelectToCurrencyButtonClickListener());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        fromCurrencyName = savedInstanceState.getString(FROM_CURRENCY_NAME);
        toCurrencyName = savedInstanceState.getString(TO_CURRENCY_NAME);
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_pair, menu);
        MenuItem saveMenuItem = menu.findItem(R.id.action_save);
        saveMenuItem.setVisible(!(TextUtils.isEmpty(fromCurrencyName) || TextUtils.isEmpty(toCurrencyName)));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                savePair();
                return true;
        }
        return false;
    }

    private void savePair() {
        if (!pairExists()) {
            ContentValues values = new ContentValues();
            values.put(TraderProviderContract.Pairs.FROM, fromCurrencyName);
            values.put(TraderProviderContract.Pairs.TO, toCurrencyName);
            final AsyncQueryHandler handler = new AsyncQueryHandler(getContentResolver()) {
                @Override
                protected void onInsertComplete(int token, Object cookie, Uri uri) {
                    super.onInsertComplete(token, cookie, uri);
                    PairEditActivity.this.finish();
                }
            };
            handler.startInsert(0, null, TraderProviderContract.Pairs.CONTENT_URI, values);
        } else {
            Toast.makeText(this, getResources().getString(R.string.pair_exists_message), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean pairExists() {
        String where = String.format("%s = ? and %s = ?", TraderProviderContract.Pairs.FROM, TraderProviderContract.Pairs.TO);
        String[] args = new String[] {fromCurrencyName, toCurrencyName};
        Cursor c = getContentResolver().query(TraderProviderContract.Pairs.CONTENT_URI, null, where, args, null);
        boolean exists = c.moveToFirst();
        c.close();
        return exists;
    }

    private class SelectFromCurrencyButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(PairEditActivity.this, SelectCurrencyActivity.class);
            intent.putExtra(SelectCurrencyActivity.EXTRA_CURRENCY_NAME, fromCurrencyName);
            startActivityForResult(intent, SELECT_FROM_CURRENCY_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_FROM_CURRENCY_REQUEST && resultCode == RESULT_OK) {
            fromCurrencyName = data.getStringExtra(SelectCurrencyActivity.EXTRA_CURRENCY_NAME);
            invalidateView();
        } else if (requestCode == SELECT_TO_CURRENCY_REQUEST && resultCode == RESULT_OK) {
            toCurrencyName = data.getStringExtra(SelectCurrencyActivity.EXTRA_CURRENCY_NAME);
            invalidateView();
        }
    }

    private void invalidateView() {
        fromButton.setText(fromCurrencyName);
        toButton.setText(toCurrencyName);
        invalidateOptionsMenu();
    }

    private class SelectToCurrencyButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(PairEditActivity.this, SelectCurrencyActivity.class);
            intent.putExtra(SelectCurrencyActivity.EXTRA_CURRENCY_NAME, toCurrencyName);
            startActivityForResult(intent, SELECT_TO_CURRENCY_REQUEST);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(FROM_CURRENCY_NAME, fromCurrencyName);
        outState.putString(TO_CURRENCY_NAME, toCurrencyName);
    }
}
