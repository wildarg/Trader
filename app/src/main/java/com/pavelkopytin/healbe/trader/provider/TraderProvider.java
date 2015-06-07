package com.pavelkopytin.healbe.trader.provider;

import java.util.HashMap;
import java.util.Map;

/*
 * Created by Wild on 07.06.2015.
 */
public class TraderProvider extends BaseProvider {
    private static final String DB_NAME = "trader.db";
    private static final int DB_VERSION = 2;

    @Override
    protected String getDBName() {
        return DB_NAME;
    }

    @Override
    protected int getDBVersion() {
        return DB_VERSION;
    }

    @Override
    protected String getAuthority() {
        return TraderProviderContract.AUTHORITY;
    }

    @Override
    protected Map<String, String[]> getDBMap() {
        Map<String, String[]> map = new HashMap<>();
        map.put(TraderProviderContract.Currencies.CURRENCIES, TraderProviderContract.Currencies.FIELDS);
        map.put(TraderProviderContract.Pairs.PAIRS, TraderProviderContract.Pairs.FIELDS);
        return map;
    }

    @Override
    protected Map<String, String[]> getRawQueries() {
        return null;
    }
}
