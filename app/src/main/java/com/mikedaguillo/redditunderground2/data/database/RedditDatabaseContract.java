package com.mikedaguillo.redditunderground2.data.database;

import android.provider.BaseColumns;

/**
 * Data contract for the type of data stored in the sqlite database
 **/
public final class RedditDatabaseContract {
    public RedditDatabaseContract() {}

    public static final String TEXT_TYPE = " TEXT";
    public static final String INTEGER_TYPE = " INTEGER";
    public static final String REAL_TYPE = " REAL";
    public static final String UNIQUE = " UNIQUE";
    public static final String FOREIGN_KEY = " FOREIGN KEY";
    public static final String REFERENCES = " REFERENCES ";
    public static final String COMMA_SEP = ",";

    // Subreddit table
    public static final String SQL_CREATE_TABLE_SUBREDDIT =
            "CREATE TABLE " + Subreddit.TABLE_NAME + " (" +
            Subreddit._ID + INTEGER_TYPE + " PRIMARY KEY," +
            Subreddit.COLUMN_NAME_SUBREDDIT_ID + TEXT_TYPE + COMMA_SEP +
            Subreddit.COLUMN_NAME_DISPLAY_NAME + TEXT_TYPE + COMMA_SEP +
            Subreddit.COLUMN_NAME_URL + TEXT_TYPE + COMMA_SEP +
            Subreddit.COLUMN_NAME_SUBSCRIBED_USER + TEXT_TYPE + COMMA_SEP +
            UNIQUE + "(" + Subreddit.COLUMN_NAME_SUBREDDIT_ID + COMMA_SEP + Subreddit.COLUMN_NAME_SUBSCRIBED_USER + ")" +
            " )";

    public static final String SQL_DELETE_TABLE_SUBREDDIT =
            "DROP TABLE IF EXISTS " + Subreddit.TABLE_NAME + ";";


    // Post table
    public static final String SQL_CREATE_TABLE_REDDITPOST =
            "CREATE TABLE " + RedditPost.TABLE_NAME + " ("
            + RedditPost._ID + INTEGER_TYPE + " PRIMARY KEY,"
            + RedditPost.COLUMN_NAME_REDDITPOST_ID + TEXT_TYPE + COMMA_SEP
            + RedditPost.COLUMN_NAME_SUBREDDIT_DISPLAY_NAME + TEXT_TYPE + COMMA_SEP
            + RedditPost.COLUMN_NAME_AUTHOR + TEXT_TYPE + COMMA_SEP
            + RedditPost.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP
            + RedditPost.COLUMN_NAME_SCORE + INTEGER_TYPE + COMMA_SEP
            + RedditPost.COLUMN_NAME_CREATED + REAL_TYPE + COMMA_SEP
            + RedditPost.COLUMN_NAME_SELFTEXT + TEXT_TYPE + COMMA_SEP
            + RedditPost.COLUMN_NAME_URL + TEXT_TYPE + COMMA_SEP
            + RedditPost.COLUMN_NAME_THUMBNAIL + TEXT_TYPE + COMMA_SEP
            + RedditPost.COLUMN_NAME_NUM_COMMENTS + INTEGER_TYPE + COMMA_SEP
            + RedditPost.COLUMN_NAME_IS_OVER_18 + INTEGER_TYPE + COMMA_SEP
            + RedditPost.COLUMN_NAME_IS_STICKIED + INTEGER_TYPE + COMMA_SEP
            + RedditPost.COLUMN_NAME_IS_SELF + INTEGER_TYPE + COMMA_SEP
            + UNIQUE + "(" + RedditPost.COLUMN_NAME_REDDITPOST_ID + ")"
            + FOREIGN_KEY + "(" + RedditPost.COLUMN_NAME_SUBREDDIT_DISPLAY_NAME + ")" + REFERENCES + Subreddit.TABLE_NAME + "(" + Subreddit.COLUMN_NAME_DISPLAY_NAME + ")"
            + " )";

    public static final String SQL_DELETE_TABLE_REDDITPOST =
            "DROP TABLE IF EXISTS " + RedditPost.TABLE_NAME + ";";

    // Inner class defines table contents
    public static abstract class Subreddit implements BaseColumns
    {
        public static final String TABLE_NAME = "subreddit";
        public static final String COLUMN_NAME_SUBREDDIT_ID = "subreddit_id";
        public static final String COLUMN_NAME_DISPLAY_NAME = "display_name";
        public static final String COLUMN_NAME_URL = "url";
        public static final String COLUMN_NAME_SUBSCRIBED_USER = "subscribed_user";
    }

    public static abstract class RedditPost implements BaseColumns
    {
        public static final String TABLE_NAME = "redditpost";
        public static final String COLUMN_NAME_REDDITPOST_ID = "redditpost_id";
        public static final String COLUMN_NAME_SUBREDDIT_DISPLAY_NAME = "display_name"; // Foreign key
        public static final String COLUMN_NAME_AUTHOR = "author";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_SCORE = "score";
        public static final String COLUMN_NAME_CREATED = "created";
        public static final String COLUMN_NAME_SELFTEXT = "selftext";
        public static final String COLUMN_NAME_URL = "url";
        public static final String COLUMN_NAME_THUMBNAIL = "thumbnail";
        public static final String COLUMN_NAME_NUM_COMMENTS = "num_comments";
        public static final String COLUMN_NAME_IS_OVER_18 = "is_over_18";
        public static final String COLUMN_NAME_IS_STICKIED = "is_stickied";
        public static final String COLUMN_NAME_IS_SELF = "is_self";
    }
}
