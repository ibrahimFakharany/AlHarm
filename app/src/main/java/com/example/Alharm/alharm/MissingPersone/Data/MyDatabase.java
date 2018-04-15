package com.example.Alharm.alharm.MissingPersone.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 450 G1 on 05/03/2018.
 */

public class MyDatabase extends SQLiteOpenHelper {
    public static final String dbName = "missingpeopledb";
    public static final int version = 4;

    private static final String CREATE_TABLE_MISSING_PEOPLE = " create table " + DatabaseContract.TableColumns.TABLE_NAME + " ( " +
            DatabaseContract.TableColumns._ID + " INTEGER PRIMARY KEY, " +
            DatabaseContract.TableColumns.COLUMN_NAME + " TEXT, " +
            DatabaseContract.TableColumns.COLUMN_FIREBASE_ID+ " TEXT, " +
            DatabaseContract.TableColumns.COLUMN_IMAGE+ " TEXT, " +
            DatabaseContract.TableColumns.COLUMN_STATE+ " TEXT, " +
            DatabaseContract.TableColumns.COLUMN_PHONE+ " TEXT, " +
            DatabaseContract.TableColumns.COLUMN_LAT+ " DOUBLE, " +
            DatabaseContract.TableColumns.COLUMN_LONG+ " DOUBLE) ";

    public MyDatabase(Context context) {
        super(context, dbName, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_MISSING_PEOPLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TableColumns.TABLE_NAME);
        onCreate(db);
    }
}