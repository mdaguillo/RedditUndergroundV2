package com.mikedaguillo.redditunderground2.data.database;

import android.provider.BaseColumns;

import org.w3c.dom.Text;

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
            Subreddit.COLUMN_NAME_SUBREDDIT_FULL_NAME + TEXT_TYPE + COMMA_SEP +
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
            + RedditPost.COLUMN_NAME_REDDITPOST_FULL_NAME + TEXT_TYPE + COMMA_SEP
            + RedditPost.COLUMN_NAME_SUBREDDIT_DISPLAY_NAME + TEXT_TYPE + COMMA_SEP
            + RedditPost.COLUMN_NAME_SUBREDDIT_ID + TEXT_TYPE + COMMA_SEP
            + RedditPost.COLUMN_NAME_SUBREDDIT_FULL_NAME + TEXT_TYPE + COMMA_SEP
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

    // Comment Table
    public static final String SQL_CREATE_TABLE_REDDITCOMMENT =
            "CREATE TABLE " + RedditComment.TABLE_NAME + " ("
            + RedditComment._ID + INTEGER_TYPE + " PRIMARY KEY,"
            + RedditComment.COLUMN_NAME_REDDITCOMMENT_ID + TEXT_TYPE + COMMA_SEP
            + RedditComment.COLUMN_NAME_REDDITCOMMENT_FULLNAME + TEXT_TYPE + COMMA_SEP
            + RedditComment.COLUMN_NAME_REDDITPOST_FULLNAME + TEXT_TYPE + COMMA_SEP
            + RedditComment.COLUMN_NAME_REDDITCOMMENT_PARENT_ID + TEXT_TYPE + COMMA_SEP
            + RedditComment.COLUMN_NAME_REDDITCOMMENT_AUTHOR + TEXT_TYPE + COMMA_SEP
            + RedditComment.COLUMN_NAME_REDDITCOMMENT_BODY + TEXT_TYPE + COMMA_SEP
            + RedditComment.COLUMN_NAME_REDDITCOMMENT_UPS + INTEGER_TYPE + COMMA_SEP
            + RedditComment.COLUMN_NAME_REDDITCOMMENT_DOWNS + INTEGER_TYPE + COMMA_SEP
            + RedditComment.COLUMN_NAME_REDDITCOMMENT_SCORE + INTEGER_TYPE + COMMA_SEP
            + RedditComment.COLUMN_NAME_REDDITCOMMENT_CREATED + REAL_TYPE + COMMA_SEP
            + RedditComment.COLUMN_NAME_REDDITCOMMENT_GILDED + INTEGER_TYPE + COMMA_SEP
            + UNIQUE + "(" + RedditComment.COLUMN_NAME_REDDITCOMMENT_ID + ")"
            + FOREIGN_KEY + "(" + RedditComment.COLUMN_NAME_REDDITPOST_FULLNAME + ")" + REFERENCES + RedditPost.TABLE_NAME + "(" + RedditPost.COLUMN_NAME_REDDITPOST_FULL_NAME + ")"
            + " )";

    public static final String SQL_DELETE_TABLE_REDDITCOMMENT =
            "DROP TABLE IF EXISTS " + RedditComment.TABLE_NAME + ";";

    // Inner class defines table contents
    public static abstract class Subreddit implements BaseColumns
    {
        public static final String TABLE_NAME = "subreddit";
        public static final String COLUMN_NAME_SUBREDDIT_ID = "subreddit_id";
        public static final String COLUMN_NAME_SUBREDDIT_FULL_NAME = "subreddit_full_name"; // This is the same as subreddit_id but with the kind prepended ex t5_2qi2g
        public static final String COLUMN_NAME_DISPLAY_NAME = "display_name";
        public static final String COLUMN_NAME_URL = "url";
        public static final String COLUMN_NAME_SUBSCRIBED_USER = "subscribed_user";
    }

    public static abstract class RedditPost implements BaseColumns
    {
        public static final String TABLE_NAME = "redditpost";
        public static final String COLUMN_NAME_REDDITPOST_ID = "redditpost_id";
        public static final String COLUMN_NAME_REDDITPOST_FULL_NAME = "redditpost_full_name"; // same as id but prepended with t#_
        public static final String COLUMN_NAME_SUBREDDIT_DISPLAY_NAME = "display_name"; // Foreign key
        public static final String COLUMN_NAME_SUBREDDIT_ID = "subreddit_id";
        public static final String COLUMN_NAME_SUBREDDIT_FULL_NAME = "subreddit_full_name";
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

    public static abstract class RedditComment implements BaseColumns
    {
        public static final String TABLE_NAME = "redditcomment";
        public static final String COLUMN_NAME_REDDITCOMMENT_ID = "redditcomment_id";
        public static final String COLUMN_NAME_REDDITCOMMENT_FULLNAME = "redditcomment_full_name";
        public static final String COLUMN_NAME_REDDITPOST_FULLNAME = "redditpost_full_name";
        public static final String COLUMN_NAME_REDDITCOMMENT_PARENT_ID = "parent_id";
        public static final String COLUMN_NAME_REDDITCOMMENT_AUTHOR = "author";
        public static final String COLUMN_NAME_REDDITCOMMENT_BODY = "body";
        public static final String COLUMN_NAME_REDDITCOMMENT_UPS = "ups";
        public static final String COLUMN_NAME_REDDITCOMMENT_DOWNS = "downs";
        public static final String COLUMN_NAME_REDDITCOMMENT_SCORE = "score";
        public static final String COLUMN_NAME_REDDITCOMMENT_CREATED = "created";
        public static final String COLUMN_NAME_REDDITCOMMENT_GILDED = "gilded";
    }
}
