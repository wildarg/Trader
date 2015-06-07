package com.pavelkopytin.healbe.trader;

import android.app.LoaderManager;
import android.content.AsyncQueryHandler;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.pavelkopytin.healbe.trader.adapters.RateListAdapter;
import com.pavelkopytin.healbe.trader.controllers.RatesRefreshController;
import com.pavelkopytin.healbe.trader.provider.TraderProviderContract;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private RateListAdapter adapter;
    private boolean refreshing = false;
    private RatesRefreshController refreshController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        findViewById(R.id.add_pair_button).setOnClickListener(new AddPairButtonClickListener());

        SwipeListView list = (SwipeListView) findViewById(R.id.swipe_listview);
        list.setSwipeListViewListener(new SwipeListener());
        adapter = new RateListAdapter(this);
        list.setAdapter(adapter);

        refreshController = new RatesRefreshController(this, new ShowProgressCallback());
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshController.startRefresh();
    }

    @Override
    protected void onPause() {
        super.onPause();
        refreshController.stopRefresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (refreshing) {
            menu.findItem(R.id.menu_refresh).setActionView(R.layout.menu_progress);
        } else {
            menu.findItem(R.id.menu_refresh).setActionView(null);
            menu.findItem(R.id.menu_refresh).setVisible(false);
        }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, TraderProviderContract.Pairs.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    private class AddPairButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(MainActivity.this, PairEditActivity.class));
        }
    }

    private class SwipeListener extends BaseSwipeListViewListener {

        @Override
        public void onDismiss(int[] reverseSortedPositions) {
            super.onDismiss(reverseSortedPositions);
            Cursor c = (Cursor) adapter.getItem(reverseSortedPositions[0]);
            Uri uri = Uri.withAppendedPath(TraderProviderContract.Pairs.CONTENT_URI,
                    c.getString(c.getColumnIndex(TraderProviderContract.Pairs._ID)));
            deletePair(uri);
        }
    }

    private void deletePair(Uri uri) {
        final AsyncQueryHandler h = new AsyncQueryHandler(getContentResolver()) { };
        h.startDelete(0, null, uri, null, null);
    }


    private class ShowProgressCallback implements RatesRefreshController.ProgressCallback {
        @Override
        public void startProgress() {
            refreshing = true;
            invalidateOptionsMenu();
        }

        @Override
        public void stopProgress() {
            refreshing = false;
            invalidateOptionsMenu();
        }
    }
}
