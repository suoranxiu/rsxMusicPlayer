package com.example.testmusicplayer.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 用来操作历史纪录的
 *
 */
public class RecordsSqliteHelper extends SQLiteOpenHelper {

    private String CREATE_RECORDS_TABLE = "create table table_records(_id integer primary key autoincrement,keyword varchar(200))";

    public RecordsSqliteHelper(Context context) {
        super(context, "records_DB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_RECORDS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
