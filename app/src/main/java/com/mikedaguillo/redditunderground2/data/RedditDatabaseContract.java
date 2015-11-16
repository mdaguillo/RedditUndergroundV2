package com.mikedaguillo.redditunderground2.data;

import android.provider.BaseColumns;

/**
 * Data contract for the type of data stored in the sqlite database
 */
public final class RedditDatabaseContract {
    public RedditDatabaseContract() {}

    public static final String TEXT_TYPE = " TEXT";
    public static final String UNIQUE = " UNIQUE";
    public static final String COMMA_SEP = ",";

    // Subreddit table
    public static final String SQL_CREATE_TABLE_SUBREDDIT =
            "CREATE TABLE " + Subreddit.TABLE_NAME + " (" +
            Subreddit._ID + " INTEGER PRIMARY KEY," +
            Subreddit.COLUMN_NAME_SUBREDDIT_ID + TEXT_TYPE + COMMA_SEP +
            Subreddit.COLUMN_NAME_DISPLAY_NAME + TEXT_TYPE + COMMA_SEP +
            Subreddit.COLUMN_NAME_URL + TEXT_TYPE + COMMA_SEP +
            Subreddit.COLUMN_NAME_SUBSCRIBED_USER + TEXT_TYPE + COMMA_SEP +
            UNIQUE + "(" + Subreddit.COLUMN_NAME_SUBREDDIT_ID + COMMA_SEP + Subreddit.COLUMN_NAME_SUBSCRIBED_USER + ")" +
            " )";

    public static final String SQL_DELETE_TABLE_SUBREDDIT =
            "DROP TABLE IF EXISTS " + Subreddit.TABLE_NAME + ";";


    // Inner class defines table contents
    public static abstract class Subreddit implements BaseColumns
    {
        public static final String TABLE_NAME = "subreddit";
        public static final String COLUMN_NAME_SUBREDDIT_ID = "subreddit_id";
        public static final String COLUMN_NAME_DISPLAY_NAME = "display_name";
        public static final String COLUMN_NAME_URL = "url";
        public static final String COLUMN_NAME_SUBSCRIBED_USER = "subscribed_user";
    }
}
