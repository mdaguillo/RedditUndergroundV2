package com.mikedaguillo.redditunderground2.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

/**
 * Class used to read and write to the user database
 */
public class RedditDatabaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "RedditUnderground.db";
    private static final String TAG = "RedditDatabaseHelper";

    public RedditDatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(RedditDatabaseContract.SQL_CREATE_TABLE_SUBREDDIT);
        db.execSQL(RedditDatabaseContract.SQL_CREATE_TABLE_REDDITPOST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(RedditDatabaseContract.SQL_DELETE_TABLE_SUBREDDIT);
        db.execSQL(RedditDatabaseContract.SQL_DELETE_TABLE_REDDITPOST);
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

    public long InsertIgnoreRedditPostRow(SQLiteDatabase database, HashMap<String, Object> rowValues)
    {
        ContentValues values = new ContentValues();

        values.put(RedditDatabaseContract.RedditPost.COLUMN_NAME_REDDITPOST_ID, (String) rowValues.get(RedditDatabaseContract.RedditPost.COLUMN_NAME_REDDITPOST_ID));
        values.put(RedditDatabaseContract.RedditPost.COLUMN_NAME_SUBREDDIT_ID, (String) rowValues.get(RedditDatabaseContract.RedditPost.COLUMN_NAME_SUBREDDIT_ID));
        values.put(RedditDatabaseContract.RedditPost.COLUMN_NAME_AUTHOR, (String) rowValues.get(RedditDatabaseContract.RedditPost.COLUMN_NAME_AUTHOR));
        values.put(RedditDatabaseContract.RedditPost.COLUMN_NAME_TITLE, (String) rowValues.get(RedditDatabaseContract.RedditPost.COLUMN_NAME_TITLE));
        values.put(RedditDatabaseContract.RedditPost.COLUMN_NAME_SCORE, (int) rowValues.get(RedditDatabaseContract.RedditPost.COLUMN_NAME_SCORE));
        values.put(RedditDatabaseContract.RedditPost.COLUMN_NAME_CREATED, (float) rowValues.get(RedditDatabaseContract.RedditPost.COLUMN_NAME_CREATED));
        values.put(RedditDatabaseContract.RedditPost.COLUMN_NAME_SELFTEXT, (String) rowValues.get(RedditDatabaseContract.RedditPost.COLUMN_NAME_SELFTEXT));
        values.put(RedditDatabaseContract.RedditPost.COLUMN_NAME_URL, (String) rowValues.get(RedditDatabaseContract.RedditPost.COLUMN_NAME_URL));
        values.put(RedditDatabaseContract.RedditPost.COLUMN_NAME_THUMBNAIL, (String) rowValues.get(RedditDatabaseContract.RedditPost.COLUMN_NAME_THUMBNAIL));
        values.put(RedditDatabaseContract.RedditPost.COLUMN_NAME_NUM_COMMENTS, (int) rowValues.get(RedditDatabaseContract.RedditPost.COLUMN_NAME_NUM_COMMENTS));
        values.put(RedditDatabaseContract.RedditPost.COLUMN_NAME_IS_OVER_18, ((boolean) rowValues.get(RedditDatabaseContract.RedditPost.COLUMN_NAME_IS_OVER_18)) ? 1 : 0);
        values.put(RedditDatabaseContract.RedditPost.COLUMN_NAME_IS_STICKIED, ((boolean) rowValues.get(RedditDatabaseContract.RedditPost.COLUMN_NAME_IS_STICKIED)) ? 1 : 0);
        values.put(RedditDatabaseContract.RedditPost.COLUMN_NAME_IS_SELF, ((boolean) rowValues.get(RedditDatabaseContract.RedditPost.COLUMN_NAME_IS_SELF)) ? 1 : 0);

        return database.insertWithOnConflict(RedditDatabaseContract.RedditPost.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public boolean DeleteAllDataInDatabase(SQLiteDatabase database)
    {
        // Sqlite does not have a truncate command so we need to drop any tables and then recreate them
        try
        {
            // Delete
            database.execSQL(RedditDatabaseContract.SQL_DELETE_TABLE_SUBREDDIT);
            database.execSQL(RedditDatabaseContract.SQL_DELETE_TABLE_REDDITPOST);

            // Recreate
            database.execSQL(RedditDatabaseContract.SQL_CREATE_TABLE_SUBREDDIT);
            database.execSQL(RedditDatabaseContract.SQL_CREATE_TABLE_REDDITPOST);

            return true;
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error occurred attempting to delete data from database.", ex);
            return false;
        }
    }
}
