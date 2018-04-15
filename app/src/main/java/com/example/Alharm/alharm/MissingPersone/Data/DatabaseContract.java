package com.example.Alharm.alharm.MissingPersone.Data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by 450 G1 on 05/03/2018.
 */

public class DatabaseContract {

    public static String TABLE_RESTAURANT = "MissingPeopleTable";

    public static final class TableColumns implements BaseColumns {
        public static String TABLE_NAME = "MissingPeopleTable";
        public static final String _ID = "id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_FIREBASE_ID = "FIREBASE_ID";
        public static final String COLUMN_IMAGE= "image";
        public static final String COLUMN_LAT = "lat";
        public static final String COLUMN_LONG = "long";
        public static final String COLUMN_STATE = "state";

        public static final String COLUMN_PHONE = "phone";
    }

    public static final String CONTENT_AUTHORITY = "com.example.Alharm.alharm.MissingPersone.Data";
    public static final Uri CONTENT_URI = new Uri.Builder().scheme("content")
            .authority(CONTENT_AUTHORITY)
            .appendPath(TABLE_RESTAURANT)
            .build();


}
