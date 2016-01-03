package com.mikedaguillo.redditunderground2.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

import com.mikedaguillo.redditunderground2.data.RedditPostListItem;
import com.mikedaguillo.redditunderground2.data.database.model.RedditPost;
import com.mikedaguillo.redditunderground2.utility.ApplicationManager;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
        db.execSQL(RedditDatabaseContract.SQL_CREATE_TABLE_REDDITCOMMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(RedditDatabaseContract.SQL_DELETE_TABLE_SUBREDDIT);
        db.execSQL(RedditDatabaseContract.SQL_DELETE_TABLE_REDDITPOST);
        db.execSQL(RedditDatabaseContract.SQL_DELETE_TABLE_REDDITCOMMENT);
        onCreate(db);
    }

    public long InsertIgnoreSubredditRow(SQLiteDatabase database, String subredditId, String subredditFullName, String subredditDisplayName, String subredditUrl, String subscribedUser)
    {
        ContentValues values = new ContentValues();
        values.put(RedditDatabaseContract.Subreddit.COLUMN_NAME_SUBREDDIT_ID, subredditId);
        values.put(RedditDatabaseContract.Subreddit.COLUMN_NAME_SUBREDDIT_FULL_NAME, subredditFullName);
        values.put(RedditDatabaseContract.Subreddit.COLUMN_NAME_DISPLAY_NAME, subredditDisplayName);
        values.put(RedditDatabaseContract.Subreddit.COLUMN_NAME_URL, subredditUrl);
        values.put(RedditDatabaseContract.Subreddit.COLUMN_NAME_SUBSCRIBED_USER, subscribedUser);

        return database.insertWithOnConflict(RedditDatabaseContract.Subreddit.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public long InsertIgnoreRedditPostRow(SQLiteDatabase database, HashMap<String, Object> rowValues)
    {
        ContentValues values = new ContentValues();

        values.put(RedditDatabaseContract.RedditPost.COLUMN_NAME_REDDITPOST_ID, (String) rowValues.get(RedditDatabaseContract.RedditPost.COLUMN_NAME_REDDITPOST_ID));
        values.put(RedditDatabaseContract.RedditPost.COLUMN_NAME_REDDITPOST_FULL_NAME, (String) rowValues.get(RedditDatabaseContract.RedditPost.COLUMN_NAME_REDDITPOST_FULL_NAME));
        values.put(RedditDatabaseContract.RedditPost.COLUMN_NAME_SUBREDDIT_DISPLAY_NAME, (String) rowValues.get(RedditDatabaseContract.RedditPost.COLUMN_NAME_SUBREDDIT_DISPLAY_NAME));
        values.put(RedditDatabaseContract.RedditPost.COLUMN_NAME_SUBREDDIT_ID, (String) rowValues.get(RedditDatabaseContract.RedditPost.COLUMN_NAME_SUBREDDIT_ID));
        values.put(RedditDatabaseContract.RedditPost.COLUMN_NAME_SUBREDDIT_FULL_NAME, (String) rowValues.get(RedditDatabaseContract.RedditPost.COLUMN_NAME_SUBREDDIT_FULL_NAME));
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

    /**
     * Truncates all tables in the database
     */
    public boolean DeleteAllData(Context context)
    {
        SQLiteDatabase database = getWritableDatabase();
        try {

            // Sqlite does not have a truncate command so we need to drop any tables and then recreate them
            try {
                // Delete
                database.execSQL(RedditDatabaseContract.SQL_DELETE_TABLE_SUBREDDIT);
                database.execSQL(RedditDatabaseContract.SQL_DELETE_TABLE_REDDITPOST);

                // Recreate
                database.execSQL(RedditDatabaseContract.SQL_CREATE_TABLE_SUBREDDIT);
                database.execSQL(RedditDatabaseContract.SQL_CREATE_TABLE_REDDITPOST);
            } catch (Exception ex) {
                Log.e(TAG, "Error occurred attempting to delete data from database.", ex);
                return false;
            }

            // Now try deleting any of the photos currently saved in the apps directories
            try {
                File thumbnailDirectory = ApplicationManager.GetThumbnailStorageDirectory(context);
                FileUtils.cleanDirectory(thumbnailDirectory);
            } catch (IOException ex) {
                Log.e(TAG, "Error occurred while clearing the thumbnails directory.", ex);
                return false;
            }
        }
        finally {
            database.close();
        }

        return true;
    }

    /**
     * Returns an array list of all of the subreddits that currently have associated posts stored in the db
     */
    public ArrayList<String> GetCachedSubredditNames(SQLiteDatabase database)
    {
        Cursor cursor = null;
        try {
            // Set up the join
            String sql = "SELECT DISTINCT s." + RedditDatabaseContract.Subreddit.COLUMN_NAME_DISPLAY_NAME
                    + " FROM " + RedditDatabaseContract.Subreddit.TABLE_NAME + " s INNER JOIN "
                    + RedditDatabaseContract.RedditPost.TABLE_NAME + " p ON s." + RedditDatabaseContract.Subreddit.COLUMN_NAME_DISPLAY_NAME
                    + " = p." + RedditDatabaseContract.RedditPost.COLUMN_NAME_SUBREDDIT_DISPLAY_NAME;

            cursor = database.rawQuery(sql, new String[] {});
            if (cursor.getCount() == 0) {
                Log.e(TAG, "User has not cached any data from any subreddits");
                return new ArrayList<>();
            }

            ArrayList<String> cachedSubreddits = new ArrayList<>();
            while (cursor.moveToNext()) {
                String displayName = cursor.getString(cursor.getColumnIndexOrThrow(RedditDatabaseContract.Subreddit.COLUMN_NAME_DISPLAY_NAME));
                cachedSubreddits.add(displayName);
            }

            return cachedSubreddits;
        }
        catch (Exception e)
        {
            Log.e(TAG, "An exception occurred while attempting to query the database.", e);
            return new ArrayList<>();
        }
        finally {
            if (cursor != null)
                cursor.close();
        }
    }

    /**
     * Returns a list of saved reddit post list items for a subreddit
     */
    public ArrayList<RedditPostListItem> GetPostListItemsForSubreddit(String subredditName) {
        Cursor cursor = null;
        SQLiteDatabase database = null;
        try
        {
            database = getReadableDatabase();
            cursor = database.query(
                    RedditDatabaseContract.RedditPost.TABLE_NAME,
                    null,
                    RedditDatabaseContract.RedditPost.COLUMN_NAME_SUBREDDIT_DISPLAY_NAME + "=?",
                    new String[]{subredditName},
                    null,
                    null,
                    null,
                    null
            );
            ArrayList<RedditPostListItem> savedPosts = new ArrayList<>();
            while (cursor.moveToNext())
            {
                savedPosts.add(new RedditPostListItem(
                        cursor.getString(cursor.getColumnIndexOrThrow(RedditDatabaseContract.RedditPost.COLUMN_NAME_REDDITPOST_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(RedditDatabaseContract.RedditPost.COLUMN_NAME_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(RedditDatabaseContract.RedditPost.COLUMN_NAME_AUTHOR)),
                        cursor.getString(cursor.getColumnIndexOrThrow(RedditDatabaseContract.RedditPost.COLUMN_NAME_SUBREDDIT_DISPLAY_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(RedditDatabaseContract.RedditPost.COLUMN_NAME_NUM_COMMENTS)),
                        ApplicationManager.GetThumbnailFromFile(cursor.getString(cursor.getColumnIndexOrThrow(RedditDatabaseContract.RedditPost.COLUMN_NAME_THUMBNAIL)))
                ));
            }

            return savedPosts;
        }
        catch (Exception ex)
        {
            Log.e(TAG, "An error occurred while attempting to retrieve the stored posts for the subreddit: " + subredditName + ".", ex);
            return null;
        }
        finally
        {
            if (cursor != null)
                cursor.close();

            if (database != null)
                database.close();
        }
    }

    /**
     * Returns an object model of a Reddit Post in the database based on the post's id
     */
    public RedditPost GetPostById(String postId)
    {
        Cursor cursor = null;
        SQLiteDatabase database = null;
        try {
            database = getReadableDatabase();
            cursor = database.query(
                    RedditDatabaseContract.RedditPost.TABLE_NAME,
                    null,
                    RedditDatabaseContract.RedditPost.COLUMN_NAME_REDDITPOST_ID + "=?",
                    new String[] {postId},
                    null,
                    null,
                    null,
                    null
            );

            if (cursor.getCount() == 0)
                throw new Exception("No post in database with id: " + postId);

            cursor.moveToFirst();
            RedditPost redditPost = new RedditPost(
                    cursor.getString(cursor.getColumnIndexOrThrow(RedditDatabaseContract.RedditPost.COLUMN_NAME_REDDITPOST_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(RedditDatabaseContract.RedditPost.COLUMN_NAME_REDDITPOST_FULL_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(RedditDatabaseContract.RedditPost.COLUMN_NAME_SUBREDDIT_DISPLAY_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(RedditDatabaseContract.RedditPost.COLUMN_NAME_SUBREDDIT_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(RedditDatabaseContract.RedditPost.COLUMN_NAME_SUBREDDIT_FULL_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(RedditDatabaseContract.RedditPost.COLUMN_NAME_AUTHOR)),
                    cursor.getString(cursor.getColumnIndexOrThrow(RedditDatabaseContract.RedditPost.COLUMN_NAME_TITLE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(RedditDatabaseContract.RedditPost.COLUMN_NAME_SCORE)),
                    cursor.getFloat(cursor.getColumnIndexOrThrow(RedditDatabaseContract.RedditPost.COLUMN_NAME_CREATED)),
                    cursor.getString(cursor.getColumnIndexOrThrow(RedditDatabaseContract.RedditPost.COLUMN_NAME_SELFTEXT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(RedditDatabaseContract.RedditPost.COLUMN_NAME_URL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(RedditDatabaseContract.RedditPost.COLUMN_NAME_THUMBNAIL)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(RedditDatabaseContract.RedditPost.COLUMN_NAME_NUM_COMMENTS)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(RedditDatabaseContract.RedditPost.COLUMN_NAME_IS_OVER_18)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(RedditDatabaseContract.RedditPost.COLUMN_NAME_IS_STICKIED)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(RedditDatabaseContract.RedditPost.COLUMN_NAME_IS_SELF))
            );

            return redditPost;
        }
        catch (Exception ex) {
            Log.e(TAG, "Error occurred while attempting to retrieve post with id: " + postId, ex);
            return null;
        }
        finally {
            if (cursor != null)
                cursor.close();

            if (database != null)
                database.close();
        }
    }
}
