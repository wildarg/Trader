package com.pavelkopytin.healbe.trader.provider;

/*
 * Created by Wild on 07.06.2015.
 */

import android.net.Uri;
import android.provider.BaseColumns;

public class TraderProviderContract {

    public static final String AUTHORITY = "com.pavelkopytin.healbe.trader.Provider";
    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    public static class Currencies implements BaseColumns {
        public static String CURRENCIES = "currencies";
        public static Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI, CURRENCIES);

        public static String NAME = "name";
        public static String DESCRIPTION = "description";

        public static String[] FIELDS = {NAME + " text", DESCRIPTION + " text"};
        private Currencies() { }
    }

    public static class Pairs implements BaseColumns {
        public static String PAIRS = "pairs";
        public static Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI, PAIRS);

        public static String FROM = "from_currency";
        public static String TO = "to_currency";
        public static String FIRST_RATE = "first_rate";
        public static String LAST_RATE = "last_rate";

        public static String[] FIELDS = {FROM + " text", TO + " text", FIRST_RATE + " real", LAST_RATE + " real"};
        private Pairs() { }

    }

}
