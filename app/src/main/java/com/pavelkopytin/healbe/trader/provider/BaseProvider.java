package com.pavelkopytin.healbe.trader.provider;

/*
 * Created by Wild on 07.06.2015.
 */
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.Map;

public abstract class BaseProvider extends ContentProvider {

    private DBOpenHelper dbHelper;
    private TableUriMatcher matcher;
    private Map<String, String[]> views;

    @Override
    public boolean onCreate() {
        dbHelper = new DBOpenHelper(getContext(), getDBName(), getDBVersion());
        matcher = new TableUriMatcher(getAuthority());
        initDBStructure();
        return true;
    }

    private void initDBStructure() {
        Map<String, String[]> dbMap = getDBMap();
        for (String tableName: dbMap.keySet()) {
            dbHelper.addTable(tableName, dbMap.get(tableName));
            matcher.addTable(tableName);
        }
        views = getRawQueries();
        if (views != null) {
            for (String name: views.keySet()) {
                matcher.addView(name);
            }
        }
    }

    protected abstract String getDBName();
    protected abstract int getDBVersion();
    protected abstract String getAuthority();
    protected abstract Map<String, String[]> getDBMap();
    protected abstract Map<String, String[]> getRawQueries();

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String tableName = matcher.getTableName(uri);
        if (tableName == null) {
            return viewQuery(uri, selectionArgs);
        }
        String where = selection;
        if (matcher.isUriRecordIdType(uri))
            where = addIdCondition(where, uri.getLastPathSegment());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(tableName, projection, where, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    private Cursor viewQuery(Uri uri, String[] selectionArgs) {
        String viewName = matcher.getViewName(uri);
        if (viewName == null) throw new IllegalArgumentException("Unknown uri " + uri);

        String[] viewParams = views.get(viewName);
        String sql = viewParams[0];
        Uri notificationUri = Uri.parse(viewParams[1]);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(sql, onGetViewSelectionArgs(selectionArgs));
        c.setNotificationUri(getContext().getContentResolver(), notificationUri);
        return c;
    }

    protected String[] onGetViewSelectionArgs(String[] selectionArgs) {
        return selectionArgs;
    }

    private String addIdCondition(String where, String id) {
        String s = BaseColumns._ID + "=" + id;
        if (!TextUtils.isEmpty(where))
            s += " and (" + where + ")";
        return s;
    }

    @Override
    public String getType(Uri uri) {
        return matcher.getType(uri);
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String tableName = matcher.getTableName(uri);
        if (tableName == null) throw new IllegalArgumentException("Unknown uri " + uri);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = db.insert(tableName, null, values);
        if (id < 0)
            throw new SQLiteException("error insert into uri " + uri);
        Uri insertedUri = Uri.withAppendedPath(uri, String.valueOf(id));
        notifyChange(uri);
        return insertedUri;
    }

    private void notifyChange(Uri uri) {
        getContext().getContentResolver().notifyChange(uri, null);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String tableName = matcher.getTableName(uri);
        if (tableName == null) throw new IllegalArgumentException("Unknown uri " + uri);

        String where = selection;
        if (matcher.isUriRecordIdType(uri))
            where = addIdCondition(where, uri.getLastPathSegment());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count = db.delete(tableName, where, selectionArgs);
        if (count > 0)
            notifyChange(uri);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String tableName = matcher.getTableName(uri);
        if (tableName == null) throw new IllegalArgumentException("Unknown uri " + uri);

        String where = selection;
        if (matcher.isUriRecordIdType(uri))
            where = addIdCondition(where, uri.getLastPathSegment());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count = db.update(tableName, values, where, selectionArgs);
        if (count > 0)
            notifyChange(uri);
        return count;
    }

    @Override
    public int bulkInsert(Uri uri, @NonNull ContentValues[] values) {
        String tableName = matcher.getTableName(uri);
        if (tableName == null) throw new IllegalArgumentException("Unknown uri " + uri);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int cnt = 0;
        try {
            db.beginTransaction();
            db.delete(tableName, null, null);
            for (ContentValues val: values) {
                long id = db.insert(tableName, null, val);
                if (id < 0)
                    throw new SQLiteException("error insert into uri " + uri);
                cnt++;
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        if (cnt > 0)
            notifyChange(uri);
        return cnt;
    }
}
