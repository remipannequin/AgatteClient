/*
 * This file is part of AgatteClient.
 *
 * AgatteClient is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AgatteClient is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AgatteClient.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2014 Rémi Pannequin (remi.pannequin@gmail.com).
 */

package com.agatteclient.alarm.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.agatteclient.MainActivity;

/**
 * Content Provider to interact with the list of alarms
 *
 * Created by Rémi Pannequin on 17/09/14.
 */
public class AlarmProvider extends ContentProvider {


    private AlarmDbHelper db_helper;
    private static final UriMatcher sURLMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static final int ALARMS = 1;
    private static final int ALARMS_ID = 2;

    @Override
    public boolean onCreate() {
        this.db_helper = new AlarmDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sort) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        // Generate the body of the query
        int match = sURLMatcher.match(uri);
        switch (match) {
            case ALARMS:
                qb.setTables(AlarmContract.Alarm.TABLE_NAME);
            break;
            case ALARMS_ID:
                qb.setTables(AlarmContract.Alarm.TABLE_NAME);
                qb.appendWhere(AlarmContract.Alarm._ID);
                qb.appendWhere(uri.getPathSegments().get(1));
            break;
            default:
                throw new IllegalArgumentException("Unknown URL " + uri);
        }
        SQLiteDatabase db = db_helper.getReadableDatabase();
        Cursor ret = qb.query(db, projection, selection, selectionArgs, null, null, sort);
        if (ret == null) {
            Log.e(MainActivity.LOG_TAG, "AlarmProvider.query() failed");//NON-NLS
        } else {
            ret.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return ret;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        if (sURLMatcher.match(uri) != ALARMS) {
            throw new IllegalArgumentException("Cannot insert into URL: " + uri);
        }
        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }
        if (!values.containsKey(AlarmContract.Alarm.COLUMN_NAME_HOUR)) {
            values.put(AlarmContract.Alarm.COLUMN_NAME_HOUR, 0);
        }
        if (!values.containsKey(AlarmContract.Alarm.COLUMN_NAME_MINUTE)) {
        values.put(AlarmContract.Alarm.COLUMN_NAME_MINUTE, 0);
        }
        if (!values.containsKey(AlarmContract.Alarm.COLUMN_NAME_DAYS)) {
        values.put(AlarmContract.Alarm.COLUMN_NAME_DAYS, 0);
        }
        if (!values.containsKey(AlarmContract.Alarm.COLUMN_NAME_ENABLED)) {
            values.put(AlarmContract.Alarm.COLUMN_NAME_ENABLED, 0);
        }


        SQLiteDatabase db = db_helper.getWritableDatabase();
        long rowId = db.insert(AlarmContract.Alarm.TABLE_NAME, null, values);
        if (rowId < 0) {
            throw new SQLException("Failed to insert row into " + uri);
        }
        Log.v(MainActivity.LOG_TAG, "Added alarm rowId = " + rowId);//NON-NLS
        Uri newUrl = ContentUris.withAppendedId(AlarmContract.Alarm.CONTENT_URI, rowId);
        getContext().getContentResolver().notifyChange(newUrl, null);
        return newUrl;
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = db_helper.getWritableDatabase();
        int count;
        switch (sURLMatcher.match(uri)) {
            case ALARMS:
                count = db.delete(AlarmContract.Alarm.TABLE_NAME, where, whereArgs);
            break;
            case ALARMS_ID:
                String segment = uri.getPathSegments().get(1);
                //Long.parseLong(segment);
                if (TextUtils.isEmpty(where)) {
                    where = "_id=" + segment;//NON-NLS
                } else {
                    where = "_id=" + segment + " AND (" + where + ")";//NON-NLS
                }
                count = db.delete(AlarmContract.Alarm.TABLE_NAME, where, whereArgs);
            break;
            default:
                throw new IllegalArgumentException("Cannot delete from URL: " + uri);//NON-NLS
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count;
        long rowId;
        int match = sURLMatcher.match(uri);
        SQLiteDatabase db = db_helper.getWritableDatabase();
        switch (match) {
            case ALARMS_ID:
                String segment = uri.getPathSegments().get(1);
                rowId = Long.parseLong(segment);
                count = db.update(AlarmContract.Alarm.TABLE_NAME, values, AlarmContract.Alarm._ID + rowId, null);
            break;
            default:
                throw new UnsupportedOperationException("Cannot update URL: " + uri);

        }
        Log.v(MainActivity.LOG_TAG, "*** notifyChange() rowId: " + rowId + " url " + uri);//NON-NLS
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
