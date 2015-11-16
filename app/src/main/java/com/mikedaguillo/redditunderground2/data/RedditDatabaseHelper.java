package com.mikedaguillo.redditunderground2.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Class used to read and write to the user database
 */
public class RedditDatabaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "RedditUnderground.db";

    public RedditDatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(RedditDatabaseContract.SQL_CREATE_TABLE_SUBREDDIT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(RedditDatabaseContract.SQL_DELETE_TABLE_SUBREDDIT);
        onCreate(db);
    }

    public long InsertIgnoreSubredditRow(SQLiteDatabase database, String subredditId, String subredditDisplayName, String subredditUrl, String subscribedUser)
    {
        ContentValues values = new ContentValues();
        values.put(RedditDatabaseContract.Subreddit.COLUMN_NAME_SUBREDDIT_ID, subredditId);
        values.put(RedditDatabaseContract.Subreddit.COLUMN_NAME_DISPLAY_NAME, subredditDisplayName);
        values.put(RedditDatabaseContract.Subreddit.COLUMN_NAME_URL, subredditUrl);
        values.put(RedditDatabaseContract.Subreddit.COLUMN_NAME_SUBSCRIBED_USER, subscribedUser);

        return database.insertWithOnConflict(RedditDatabaseContract.Subreddit.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }
}
