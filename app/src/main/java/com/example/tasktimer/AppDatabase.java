package com.example.tasktimer;

 /*Basic database class for the application.
  The only class that should use this is {@Link AppProvider}.*/



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Date;

class AppDatabase extends SQLiteOpenHelper {
    private static final String TAG = "AppDatabase";

    public static final String DATABASE_NAME = "TaskTimer.db";
    public static final int DATABASE_VERSION = 3;

    //Implement AppDatabase as a Singleton
    private static AppDatabase instance = null;

    private AppDatabase(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "AppDatabase: constructor");
    }

/**
     * Get an instance os the app's singleton database helper object
     *
     * @param context the content providers context.
     * @return a SQLite database helper object*/



    static AppDatabase getInstance(Context context){
        if ( instance == null){
            Log.d(TAG, "getInstance: creating new instance");
            instance = new AppDatabase(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: starts");
        String sSQL; // Use a string variable to facilitate logging
sSQL = "CREATE TABLE Tasks (_id INTEGER PRIMARY KEY NOT NULL, Name TEXT NOT NULL, Description TEXT, SortOrder INTEGER, CategoryID INTEGER);";

        sSQL = "CREATE TABLE " + TaskContract.TABLE_NAME + " ("
                + TaskContract.Columns._ID + " INTEGER PRIMARY KEY NOT NULL, "
                + TaskContract.Columns.TASKS_NAME + " TEXT NOT NULL, "
                + TaskContract.Columns.TASKS_DESCRIPTION + " TEXT, "
                + TaskContract.Columns.TASKS_SORTORDER + " INTEGER);";
        Log.d(TAG, sSQL);
        db.execSQL(sSQL);

        addTimingsTable(db);
        addDurationsView(db);

        Log.d(TAG, "onCreate: ends");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: starts");
        switch (oldVersion) {
            case 1:
                // upgrade logic from version 1
                addTimingsTable(db);
                // fall through, to include version 2 upgrade logic as well
            case 2:
                // upgrade logic from version 2
                addDurationsView(db);
                break;

            default:
                throw new IllegalStateException("onUpgrade() with unknown newVersion: " + newVersion);
        }
        Log.d(TAG, "onUpgrade: ends");
    }

    private void addTimingsTable(SQLiteDatabase db){
        String sSQL = "CREATE TABLE " + TimingsContract.TABLE_NAME + " ("
                + TimingsContract.Columns._ID + " INTEGER PRIMARY KEY NOT NULL, "
                + TimingsContract.Columns.TIMINGS_TASK_ID + " INTEGER NOT NULL, "
                + TimingsContract.Columns.TIMINGS_START_TIME + " INTEGER, "
                + TimingsContract.Columns.TIMINGS_DURATION + " INTEGER);";
        Log.d(TAG, sSQL);
        db.execSQL(sSQL);

sSQL = "CREATE TRIGGER Remove_Task"
                + " AFTER DELETE ON " + TaskContract.TABLE_NAME
                + " FOR EACH ROW"
                + " BEGIN"
                + "DELETE FROM " + TimingsContract.TABLE_NAME
                + " WHERE " + TimingsContract.Columns.TIMINGS_TASK_ID
                + " = OLD." + TaskContract.Columns._ID
                + ";"
                + " END;";

        Log.d(TAG, sSQL);
        db.execSQL(sSQL);
    }
    private void addDurationsView(SQLiteDatabase db){

CREATE VIEW vwTaskDurations AS
        SELECT  Timings._id,
        Tasks.Name,
        Tasks.Description,
        Timings.StartTime,
                Date(Timings.StartTime, 'unixepoch') AS StartDate,
                SUM(Timings.Duration) AS Duration
                FROM Tasks INNER JOIN Timings
                ON Tasks._id = Timings.TaskId
                GROUP BY Tasks._Id, StartDate;


        String sSQL = "CREATE VIEW" + DurationsContract.TABLE_NAME
                + "AS SELECT " + TimingsContract.TABLE_NAME + "." + TimingsContract.Columns._ID + ","
                + TaskContract.TABLE_NAME + "." + TaskContract.Columns.TASKS_NAME + ", "
                + TaskContract.TABLE_NAME + "." + TaskContract.Columns.TASKS_DESCRIPTION + ", "
                + TimingsContract.TABLE_NAME + "." + TaskContract.Columns.TIMINGS_START_TIME + ", "
                + "DATE(" + TimingsContract.TABLE_NAME + "." + TimingsContract.Columns.TIMINGS_START_TIME + ", 'unixepoch')"
                + "AS " + DurationsContract.Columns.DURATIONS_START_DATE + ", "
                + "SUM(" + TimingsContract.TABLE_NAME + "." + TimingsContract.Columns.TIMINGS_DURATION + ")"
                + "AS" + DurationsContract.Columns.DURATIONS_DURATION
                + "FROM " + TaskContract.TABLE_NAME + "JOIN " + TimingsContract.TABLE_NAME
                + "ON "  + TaskContract.TABLE_NAME + "." + TaskContract.Columns._ID + " = "
                + TimingsContract.TABLE_NAME + "." + TimingsContract.Columns.TIMINGS_TASK_ID
                + "GROUP BY" + DurationsContract.Columns.DURATIONS_START_DATE + ", " + DurationsContract.Columns.DURATIONS_NAME
                + ";";
        Log.d(TAG, sSQL);
        db.execSQL(sSQL);
    }
}
