package com.mikedaguillo.redditunderground2;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mikedaguillo.redditunderground2.data.RedditDatabaseContract;
import com.mikedaguillo.redditunderground2.data.RedditDatabaseHelper;

public class DownloadSubreddits extends AppCompatActivity {

    ListView downloadSubredditsView;
    SharedPreferences appSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_subreddits);

        // Bind the controls
        downloadSubredditsView = (ListView) findViewById(R.id.download_subreddits_listview);

        // Retrieve the settings
        appSettings = this.getSharedPreferences("app_settings", Context.MODE_PRIVATE);

        // Check the database for this users subreddits
        RedditDatabaseHelper helper = new RedditDatabaseHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();

        String[] projection = new String[] { RedditDatabaseContract.Subreddit.COLUMN_NAME_DISPLAY_NAME };
        Cursor subredditsCursor = db.query(
                RedditDatabaseContract.Subreddit.TABLE_NAME,
                projection,
                RedditDatabaseContract.Subreddit.COLUMN_NAME_SUBSCRIBED_USER + "=?",
                new String[]{appSettings.getString(getString(R.string.CurrentUser), "null")},
                null,
                null,
                RedditDatabaseContract.Subreddit.COLUMN_NAME_DISPLAY_NAME + " ASC");
        subredditsCursor.moveToFirst();

        if (subredditsCursor.getCount() == 0)
        {   // The current user has no saved subreddits. Prompt them to download them if they have an internet connection
            recreate();
        }

        // We have the subreddits, display them
        String[] subreddits = new String[subredditsCursor.getCount()];
        for (int i = 0; i < subredditsCursor.getCount(); i++)
        {   // insert the display names in to the array
            subreddits[i] = subredditsCursor.getString(subredditsCursor.getColumnIndexOrThrow(RedditDatabaseContract.Subreddit.COLUMN_NAME_DISPLAY_NAME));
            subredditsCursor.moveToNext();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, subreddits);
        downloadSubredditsView.setAdapter(adapter);
    }
}
