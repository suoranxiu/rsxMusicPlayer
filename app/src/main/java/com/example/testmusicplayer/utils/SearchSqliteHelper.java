package com.example.testmusicplayer.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SearchSqliteHelper extends SQLiteOpenHelper {

    private String CREATE_TABLE = "create table table_search(_id integer primary key autoincrement,keyword varchar(200))";

    public SearchSqliteHelper(Context context) {
        super(context, "search_DB", null, 1);
    }
    public SearchSqliteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
