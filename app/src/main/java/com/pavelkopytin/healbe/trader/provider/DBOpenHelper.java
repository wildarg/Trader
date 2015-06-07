package com.pavelkopytin.healbe.trader.provider;

/*
 * Created by Wild on 07.06.2015.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.HashMap;
import java.util.Map;

public class DBOpenHelper extends SQLiteOpenHelper {

    private Map<String, String> tables = new HashMap<>();

    public DBOpenHelper(Context context, String name, int version) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (String sql : tables.values()) {
            db.execSQL(sql);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (String tableName : tables.keySet()) {
            db.execSQL("drop table if exists " + tableName);
            db.execSQL(tables.get(tableName));
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (String tableName : tables.keySet()) {
            db.execSQL("drop table if exists " + tableName);
            db.execSQL(tables.get(tableName));
        }
    }

    public void addTable(String tableName, String[] fields) {
        if (!tables.containsKey(tableName))
            tables.put(tableName, createTableSql(tableName, fields));
    }

    private String createTableSql(String tableName, String[] fields) {
        StringBuilder sql = new StringBuilder();
        sql.append("create table ");
        sql.append(tableName);
        sql.append("(");
        sql.append(BaseColumns._ID);
        sql.append(" integer primary key autoincrement");
        for (String field: fields) {
            sql.append(",");
            sql.append(field);
        }
        sql.append(")");
        return sql.toString();
    }
}
