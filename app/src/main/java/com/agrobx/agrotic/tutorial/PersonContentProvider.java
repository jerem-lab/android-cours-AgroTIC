package com.agrobx.agrotic.tutorial;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

import java.util.Arrays;
import java.util.HashSet;

import static com.agrobx.agrotic.tutorial.PersonContentProvider.Contract.MIME_DIR;
import static com.agrobx.agrotic.tutorial.PersonContentProvider.Contract.MIME_ITEM;
import static com.agrobx.agrotic.tutorial.PersonContentProvider.Contract.SORT_ORDER_DEFAULT;

/**
 * Created by Jérémy on 13/12/2017.
 */

public class PersonContentProvider extends ContentProvider {
    private DataBaseHandler dataBaseHandler;
    private static final int DIR = 10;
    private static final int ITEM = 20;

    private static final String AUTHORITY = "com.agrobx.agrotic.tutorial.personcontentprovider";
    private static final String BASE_PATH = PersonDataSourceManager.TABLE_PERSON_NAME;

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, DIR);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", ITEM);
    }

    public static class Contract implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" +
                BASE_PATH);
        public static final String MIME_DIR =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + AUTHORITY + "." + BASE_PATH;
        public static final String MIME_ITEM =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + AUTHORITY + "." + BASE_PATH;
        public static final String COLUMN_FIRST_NAME = PersonDataSourceManager.COLUMN_FIRST_NAME;
        public static final String COLUMN_LAST_NAME = PersonDataSourceManager.COLUMN_LAST_NAME;
        public static final String KEY_COL_ID = PersonDataSourceManager.COLUMN_ROW_ID;
        public static final String SORT_ORDER_DEFAULT = PersonDataSourceManager.COLUMN_LAST_NAME + " ASC";
    }

    private void checkColumns(String[] projection) {
        String[] available =
                {
                        Contract.KEY_COL_ID,
                        Contract.COLUMN_FIRST_NAME,
                        Contract.COLUMN_LAST_NAME

                };
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }

    @Override
    public boolean onCreate() {
        dataBaseHandler = DataBaseHandler.getInstance(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Log.i("mytag", uri.toString());
        SQLiteDatabase database = dataBaseHandler.getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        checkColumns(projection);
        queryBuilder.setTables(PersonDataSourceManager.TABLE_PERSON_NAME);
        if (sortOrder == null) {
            sortOrder = SORT_ORDER_DEFAULT;
        }
        int uriType = sURIMatcher.match(uri);
        Log.i("CP", String.valueOf(uriType));
        switch (uriType) {
            case DIR: {
                break;
            }
            case ITEM: {
                queryBuilder.appendWhere(PersonDataSourceManager.COLUMN_ROW_ID + "=" +
                        uri.getLastPathSegment());
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown URI: " + uri);
            }
        }
        Cursor cursor = queryBuilder.query(database, projection, selection, selectionArgs, null,
                null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.i("mytag", uri.toString());
        SQLiteDatabase database = dataBaseHandler.getWritableDatabase();
        long rowId = database.insertOrThrow(PersonDataSourceManager.TABLE_PERSON_NAME, null,
                values);
        Uri _uri = ContentUris.withAppendedId(Contract.CONTENT_URI, rowId);
        getContext().getContentResolver().notifyChange(_uri, null);
        return _uri;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        Log.i("mytag", uri.toString());

        SQLiteDatabase database = dataBaseHandler.getWritableDatabase();
        int uriType = sURIMatcher.match(uri);
        int rowsUpdated = 0;
        switch (uriType) {
            case DIR: {
                rowsUpdated = database.update(PersonDataSourceManager.TABLE_PERSON_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            }
            case ITEM: {
                String rowId = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = database.update(PersonDataSourceManager.TABLE_PERSON_NAME,
                            values,
                            PersonDataSourceManager.COLUMN_ROW_ID + "=" + rowId,
                            null);
                } else {
                    rowsUpdated = database.update(PersonDataSourceManager.TABLE_PERSON_NAME,
                            values,

                            PersonDataSourceManager.COLUMN_ROW_ID + "=" + rowId
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown URI: " + uri);
            }
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.i("mytag", uri.toString());

        SQLiteDatabase database = dataBaseHandler.getWritableDatabase();
        int uriType = sURIMatcher.match(uri);
        int rowsDeleted = 0;
        switch (uriType) {
            case DIR: {
                rowsDeleted = database.delete(PersonDataSourceManager.TABLE_PERSON_NAME,
                        selection, selectionArgs);
                break;
            }
            case ITEM: {
                String rowId = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {

                    rowsDeleted = database.delete(PersonDataSourceManager.TABLE_PERSON_NAME,

                            PersonDataSourceManager.COLUMN_ROW_ID + "=" + rowId,
                            null);
                } else {

                    rowsDeleted = database.delete(PersonDataSourceManager.TABLE_PERSON_NAME,

                            PersonDataSourceManager.COLUMN_ROW_ID + "=" + rowId
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown URI: " + uri);
            }
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case DIR:
                return MIME_DIR;
            case ITEM:
                return MIME_ITEM;
            default:
                throw new IllegalArgumentException("Not supported URI : " + uri);
        }
    }

}

