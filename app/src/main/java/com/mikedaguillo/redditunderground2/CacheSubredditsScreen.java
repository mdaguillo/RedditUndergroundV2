package com.mikedaguillo.redditunderground2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;

import com.mikedaguillo.redditunderground2.data.RedditDatabaseContract;
import com.mikedaguillo.redditunderground2.data.RedditDatabaseHelper;
import com.mikedaguillo.redditunderground2.utility.ConnectionManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CacheSubredditsScreen extends AppCompatActivity {

    ListView downloadSubredditsView;
    Button cacheButton;
    SharedPreferences appSettings;
    ArrayList<String> subredditsToCache;
    HashMap<String, String> subredditIds;
    String sessionCookie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache_subreddits);

        // Bind the controls
        downloadSubredditsView = (ListView) findViewById(R.id.download_subreddits_listview);
        downloadSubredditsView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        cacheButton = (Button) findViewById(R.id.cache_button);
        subredditsToCache = new ArrayList<>();
        subredditIds = new HashMap<>();

        // Retrieve the settings and the session cookie
        appSettings = this.getSharedPreferences("app_settings", Context.MODE_PRIVATE);
        if (appSettings.getBoolean(getString(R.string.LoginState), false))
        {
            sessionCookie = appSettings.getString(getString(R.string.SessionCookie), "");
        }
        else
        {
            Toast.makeText(this, "No session cookie found", Toast.LENGTH_LONG).show();
        }

        // Set listview listener
        downloadSubredditsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckedTextView checkedTextView = ((CheckedTextView) view);
                checkedTextView.setChecked(!checkedTextView.isChecked());

                // Add to our list
                if (checkedTextView.isChecked())
                    subredditsToCache.add(checkedTextView.getText().toString());
                else
                    subredditsToCache.remove(checkedTextView.getText().toString());

                // Enable the cache button only if there has been a selection
                if (subredditsToCache.size() > 0)
                    cacheButton.setEnabled(true);
                else
                    cacheButton.setEnabled(false);
            }
        });

        // Set the button listeners
        cacheButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CacheSubredditData async = new CacheSubredditData(subredditsToCache, subredditIds, sessionCookie, v.getContext());
                async.execute();
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
                new String[] { RedditDatabaseContract.Subreddit.COLUMN_NAME_DISPLAY_NAME, RedditDatabaseContract.Subreddit.COLUMN_NAME_SUBREDDIT_ID },
                RedditDatabaseContract.Subreddit.COLUMN_NAME_SUBSCRIBED_USER + "=?",
                new String[]{ appSettings.getString(getString(R.string.CurrentUser), "null") },
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
        {
            String subredditDisplayName = subredditsCursor.getString(subredditsCursor.getColumnIndexOrThrow(RedditDatabaseContract.Subreddit.COLUMN_NAME_DISPLAY_NAME));
            String subredditId = subredditsCursor.getString(subredditsCursor.getColumnIndexOrThrow(RedditDatabaseContract.Subreddit.COLUMN_NAME_SUBREDDIT_ID));

            subreddits.add(subredditDisplayName);
            // Also keep the ids stored in a hashmap
            subredditIds.put(subredditDisplayName, subredditId);
        }

        subredditsCursor.close();
        db.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, android.R.id.text1, subreddits);
        downloadSubredditsView.setAdapter(adapter);
    }

    // Async class designed to cache subreddit data from reddit
    private class CacheSubredditData extends AsyncTask<Void, Void, Void>
    {
        private final String TAG = "CacheSubredditData";
        private ArrayList<String> _subredditsToCache;
        private String _sessionCookie;
        private ProgressDialog _downloadDialog;
        private HashMap<String, String> _subredditIds;

        public CacheSubredditData(ArrayList<String> subredditsToCache, HashMap<String, String> subRedditIds, String sessionCookie, Context context)
        {
            _subredditsToCache = subredditsToCache;
            _subredditIds = subRedditIds;
            _sessionCookie = sessionCookie;
            _downloadDialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            _downloadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            _downloadDialog.setTitle("Downloading");
            _downloadDialog.setMessage("Writing listing data to database.");
            _downloadDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Establish a connection to reddit and retrieve the top listing for each subreddit
            SQLiteDatabase db = null;
            try
            {
                RedditDatabaseHelper dbHelper = new RedditDatabaseHelper(getApplicationContext());
                db = dbHelper.getWritableDatabase();

                // Loop through the selected subreddits and download listing info from reddit
                for (String subreddit : _subredditsToCache)
                {
                    String subredditId = _subredditIds.get(subreddit);
                    if (!ConnectionManager.CacheSubreddit(subreddit, subredditId, dbHelper, db, _sessionCookie))
                        Log.e(TAG, "An error occurred while attempting to cache listing data from subreddit: " + subreddit);
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            finally {
                if (db != null)
                    db.close();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void v)
        {
            super.onPostExecute(v);
            _downloadDialog.dismiss();
        }
    }
}
