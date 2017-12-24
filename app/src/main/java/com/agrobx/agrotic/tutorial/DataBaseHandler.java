package com.agrobx.agrotic.tutorial;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHandler extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "tutorial.db";
    private static final int DATABASE_VERSION = 1;

    private static DataBaseHandler dbhInstance;

    // permet d'avoir un unique objet pour ouvrir/fermer notre DB, pattern clasique de programmation
    public static synchronized DataBaseHandler getInstance(Context context) {
        if (dbhInstance == null) {
            dbhInstance = new DataBaseHandler(context.getApplicationContext());
        }
        return dbhInstance;
    }

    public static synchronized void closeInstance() {
        if (dbhInstance != null) {
            dbhInstance.close();
            dbhInstance = null;
        }
    }

    private DataBaseHandler(Context context) {
        super(context, PersonDataSourceManager.TABLE_PERSON_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PersonDataSourceManager.PERSON_TABLE_SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(PersonDataSourceManager.PERSON_TABLE_SQL_DROP);
        onCreate(db);
    }
}