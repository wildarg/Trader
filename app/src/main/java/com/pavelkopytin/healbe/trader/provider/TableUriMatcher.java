package com.pavelkopytin.healbe.trader.provider;

/*
 * Created by Wild on 07.06.2015.
 */
import android.content.UriMatcher;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class TableUriMatcher {

    public static final int URI_RECORDS_TYPE = 0;
    public static final int URI_RECORD_ID_TYPE = 1;
    private UriMatcher tableMatcher;
    private UriMatcher viewMatcher;
    private UriMatcher uriTypeMatcher;
    private String authority;
    private List<String> tables = new ArrayList<>();
    private List<String> views = new ArrayList<>();

    public TableUriMatcher(String authority) {
        this.authority = authority;
        tableMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriTypeMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        viewMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    }

    public void addTable(String name) {
        if (!tables.contains(name)) {
            tables.add(name);
        }
        int ind = tables.indexOf(name);
        tableMatcher.addURI(authority, name, ind);
        tableMatcher.addURI(authority, name + "/#", ind);
        uriTypeMatcher.addURI(authority, name, URI_RECORDS_TYPE);
        uriTypeMatcher.addURI(authority, name + "/#", URI_RECORD_ID_TYPE);
    }

    public void addView(String name) {
        if (!views.contains(name)) {
            views.add(name);
        }
        int ind = views.indexOf(name);
        viewMatcher.addURI(authority, name, ind);
        viewMatcher.addURI(authority, name + "/#", ind);
        uriTypeMatcher.addURI(authority, name, URI_RECORDS_TYPE);
        uriTypeMatcher.addURI(authority, name + "/#", URI_RECORD_ID_TYPE);
    }

    public String getTableName(Uri uri) {
        int ind = tableMatcher.match(uri);
        if (ind >= 0)
            return tables.get(ind);
        return null;
    }

    public String getViewName(Uri uri) {
        int ind = viewMatcher.match(uri);
        if (ind >= 0)
            return views.get(ind);
        return null;
    }

    public String getType(Uri uri) {
        String name = getTableName(uri);
        if (name == null) name = getViewName(uri);
        if (name == null) throw new IllegalArgumentException("Unknown uri " + uri);

        if (isUriRecordIdType(uri))
            return "vnd.android.cursor.item/vnd." + authority + "." + name;
        return "vnd.android.cursor.dir/vnd." + authority + "." + name;
    }

    public boolean isUriRecordIdType(Uri uri) {
        int ind = uriTypeMatcher.match(uri);
        return ind == URI_RECORD_ID_TYPE;
    }
}
