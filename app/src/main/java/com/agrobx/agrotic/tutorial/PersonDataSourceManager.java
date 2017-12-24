package com.agrobx.agrotic.tutorial;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class PersonDataSourceManager {
    public static final String TABLE_PERSON_NAME = "persons";
    public static final String COLUMN_FIRST_NAME = "first_name";
    public static final String COLUMN_LAST_NAME = "last_name";
    public static final String COLUMN_ROW_ID = "row_id";

    public static final String PERSON_TABLE_SQL_CREATE =
            "CREATE TABLE " + TABLE_PERSON_NAME + " (" +
                    COLUMN_LAST_NAME + " TEXT PRIMARY KEY, " +
                    COLUMN_FIRST_NAME + " TEXT NOT NULL );";

    public static final String PERSON_TABLE_SQL_DROP =
            "DROP TABLE IF EXISTS " + TABLE_PERSON_NAME + ";";

    private DataBaseHandler dataBaseHandler;
    private SQLiteDatabase dataBase;

    //on recupere le databasehandler
    public PersonDataSourceManager(Context context) {
        dataBaseHandler = DataBaseHandler.getInstance(context);
    }

    // pour ouvrir la database
    public void open() {
        dataBase = dataBaseHandler.getWritableDatabase();
    }

    // et la fermer
    public void close() {
        dataBase.close();
    }


    public long add(PersonDataModel personDataModel) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_FIRST_NAME, personDataModel.getFirstName());
        values.put(COLUMN_LAST_NAME, personDataModel.getLastName());
        long rowId = -1;
//rowId = dataBase.insertOrThrow(TABLE_PERSON_NAME, null, values);
        try {
            rowId = dataBase.insertOrThrow(TABLE_PERSON_NAME, null, values);
        } catch (SQLException e) {
            Log.e("SQLite", "add", e);
        }
        return rowId;
    }

    public long update(PersonDataModel personDataModel) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_FIRST_NAME, personDataModel.getFirstName());
        String where = COLUMN_LAST_NAME + " = ?";
        String[] whereArgs = {personDataModel.getLastName() + ""};
        long rowId = dataBase.update(TABLE_PERSON_NAME, values, where, whereArgs);
        if (rowId == 0) {
            Log.w("SQLite", "update:" + personDataModel.toString());
        }
        return rowId;
    }

    public long remove(PersonDataModel personDataModel) {
        String where = COLUMN_LAST_NAME + " = ?";
        String[] whereArgs = {personDataModel.getLastName() + ""};
        int rowIds = dataBase.delete(TABLE_PERSON_NAME, where, whereArgs);
        if (rowIds == 0) {
            Log.w("SQLite", "update:" + personDataModel.toString());
        }
        return rowIds;
    }

    public int getCount() {
        Cursor cursor = dataBase.rawQuery("SELECT * FROM " + TABLE_PERSON_NAME, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }


    public PersonDataModel get(String _lastName) {
        PersonDataModel personDataModel = null;
        Cursor cursor = dataBase.rawQuery("SELECT * FROM " + TABLE_PERSON_NAME + " WHERE = " + _lastName, null);
        if (cursor.moveToFirst()) {
            personDataModel = new PersonDataModel(null, _lastName, null);

            personDataModel.setFirstName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIRST_NAME))
            );
            cursor.close();
        }
        return personDataModel;
    }


    public ArrayList<PersonDataModel> getAll() {
        ArrayList<PersonDataModel> personDataModels = new ArrayList<PersonDataModel>();
        Cursor cursor = dataBase.rawQuery("SELECT * FROM " + TABLE_PERSON_NAME, null);
/*
for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
{
}
*/
        while (cursor.moveToNext()) {
            PersonDataModel personDataModel = new PersonDataModel
                    (

                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIRST_NAME)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_NAME)),
                            null
                    );
            personDataModels.add(personDataModel);
        }
        cursor.close();
        return personDataModels;
    }


}