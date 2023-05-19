package com.example.gobang.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "Users_test.db";
    public static final int DB_VERSION = 1;
    public DBOpenHelper(Context context, CursorFactory factory){
        super(context, DB_NAME, factory, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS User_info(_id integer primary key autoincrement, account text, password text, head text NOT NULL DEFAULT '', nickname text NOT NULL DEFAULT '', game int DEFAULT 0, win int DEFAULT 0, lose int DEFAULT 0, draw int DEFAULT 0, rate double DEFAULT 0);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

    }


}
