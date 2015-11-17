package com.mikedaguillo.redditunderground2;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.mikedaguillo.redditunderground2.data.RedditDatabaseContract;
import com.mikedaguillo.redditunderground2.data.RedditDatabaseHelper;

import java.util.ArrayList;

public class CacheSubreddits extends AppCompatActivity {

    ListView downloadSubredditsView;
    Button cacheButton;
    SharedPreferences appSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache_subreddits);

        // Bind the controls
        downloadSubredditsView = (ListView) findViewById(R.id.download_subreddits_listview);
        downloadSubredditsView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        cacheButton = (Button) findViewById(R.id.cache_button);

        // Retrieve the settings
        appSettings = this.getSharedPreferences("app_settings", Context.MODE_PRIVATE);

        // Set the button listeners
        cacheButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        setupDisplay();
    }

    private void setupDisplay() {
        // Check the database for this users subreddits
        RedditDatabaseHelper helper = new RedditDatabaseHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor subredditsCursor = db.query(
                RedditDatabaseContract.Subreddit.TABLE_NAME,
                new String[] { RedditDatabaseContract.Subreddit.COLUMN_NAME_DISPLAY_NAME },
                RedditDatabaseContract.Subreddit.COLUMN_NAME_SUBSCRIBED_USER + "=?",
                new String[]{appSettings.getString(getString(R.string.CurrentUser), "null")},
                null,
                null,
                RedditDatabaseContract.Subreddit.COLUMN_NAME_DISPLAY_NAME + " ASC");

        if (subredditsCursor.getCount() == 0)
        {   // The current user has no saved subreddits. Prompt them to download them if they have an internet connection
            recreate();
        }

        // We have the subreddits, display them
        ArrayList<String> subreddits = new ArrayList<String>();
        while (subredditsCursor.moveToNext())
            subreddits.add(subredditsCursor.getString(subredditsCursor.getColumnIndexOrThrow(RedditDatabaseContract.Subreddit.COLUMN_NAME_DISPLAY_NAME)));

        subredditsCursor.close();
        db.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, android.R.id.text1, subreddits);
        downloadSubredditsView.setAdapter(adapter);
    }

    // Async class designed to cache subreddit data from reddit
    private class CacheSubredditData extends AsyncTask<Void, Void, Void>
    {
        ArrayList<String> _subredditsToCache;

        public CacheSubredditData(ArrayList<String> subredditsToCache)
        {
            _subredditsToCache = subredditsToCache;
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Establish a connection to reddit and retrieve the top listing for each subreddit

            return null;
        }
    }
}
