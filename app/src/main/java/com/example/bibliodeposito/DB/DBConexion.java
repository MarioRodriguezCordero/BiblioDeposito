package com.example.bibliodeposito.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBConexion extends SQLiteOpenHelper {

    private static final String DB_NAME = "dbbibliodeposito";
    private static final int DB_VERSION = 1;

    public DBConexion(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBManager.TABLE_BOOKS_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DBManager.TABLE_BOOKS);
    }
}
