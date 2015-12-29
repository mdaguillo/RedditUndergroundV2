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

import com.mikedaguillo.redditunderground2.data.api.json.JSONRedditPost;
import com.mikedaguillo.redditunderground2.data.database.RedditDatabaseContract;
import com.mikedaguillo.redditunderground2.data.database.RedditDatabaseHelper;
import com.mikedaguillo.redditunderground2.data.api.json.RedditListing;
import com.mikedaguillo.redditunderground2.utility.ApplicationManager;
import com.mikedaguillo.redditunderground2.utility.ConnectionManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class DownloadSubredditsScreen extends AppCompatActivity {

    ListView downloadSubredditsView;
    Button cacheButton;
    SharedPreferences appSettings;
    ArrayList<String> subredditsToCache;
    String sessionCookie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_subreddits_screen);

        // Bind the controls
        downloadSubredditsView = (ListView) findViewById(R.id.download_subreddits_listview);
        downloadSubredditsView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        cacheButton = (Button) findViewById(R.id.cache_button);
        subredditsToCache = new ArrayList<>();

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
                CacheSubredditData async = new CacheSubredditData(subredditsToCache, sessionCookie, v.getContext());
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
                RedditDatabaseContract.Subreddit.COLUMN_NAME_SUBSCRIBED_USER + " LIKE ?",
                new String[]{ "%" + appSettings.getString(getString(R.string.CurrentUser), "null") + "%" },
                null,
                null,
                RedditDatabaseContract.Subreddit.COLUMN_NAME_DISPLAY_NAME + " ASC");

        if (subredditsCursor.getCount() == 0)
        {   // The current user has no saved subreddits. Prompt them to download them if they have an internet connection
            // recreate();
        }

        // We have the subreddits, display them
        ArrayList<String> subreddits = new ArrayList<String>();
        while (subredditsCursor.moveToNext())
        {
            String subredditDisplayName = subredditsCursor.getString(subredditsCursor.getColumnIndexOrThrow(RedditDatabaseContract.Subreddit.COLUMN_NAME_DISPLAY_NAME));
            subreddits.add(subredditDisplayName);
        }

        subredditsCursor.close();
        db.close();
        helper.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, android.R.id.text1, subreddits);
        downloadSubredditsView.setAdapter(adapter);
    }

    // Async class designed to cache subreddit data from reddit
    private class CacheSubredditData extends AsyncTask<Void, ProgressUpdates, Void>
    {
        private final String TAG = "CacheSubredditData";
        private ArrayList<String> _subredditsToCache;
        private String _sessionCookie;
        private ProgressDialog _downloadDialog;

        public CacheSubredditData(ArrayList<String> subredditsToCache, String sessionCookie, Context context)
        {
            _subredditsToCache = subredditsToCache;
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
                // Create the database objects we'll need
                RedditDatabaseHelper dbHelper = new RedditDatabaseHelper(getApplicationContext());
                db = dbHelper.getWritableDatabase();

                // Create the object we'll use to update the progress dialog
                ProgressUpdates progressUpdates = new ProgressUpdates();

                // Loop through the selected subreddits and download listing info from reddit
                for (int i = 0; i < _subredditsToCache.size(); i++)
                {
                    String subreddit = _subredditsToCache.get(i);
                    progressUpdates.UpdateMessage = "Locally caching posts for subreddit: " + subreddit;
                    progressUpdates.UpdatePercent = 0;
                    publishProgress(progressUpdates);

                    RedditListing listing = ConnectionManager.RetrievePostListing(subreddit, _sessionCookie);
                    if (listing == null || listing.data == null || listing.data.children == null)
                    {
                        Log.e(TAG, "An error occurred while attempting to retrieve listing data from subreddit: " + subreddit + ". Listing or data returned null.");
                        progressUpdates.UpdatePercent = 100;
                        publishProgress(progressUpdates);
                        continue;
                    }

                    // Now insert the data from the listing into the db
                    HashMap<String, Object> redditPostValues = new HashMap<>();
                    int listingSize = listing.data.children.size();
                    for (int j = 0; j < listingSize; j++)
                    {
                        JSONRedditPost post = listing.data.children.get(j);
                        if (post != null && post.data != null)
                        {
                            Log.d(TAG, "Testing whether the truncate function works: " + post.data.GetSubredditTruncatedId());

                            redditPostValues.put(RedditDatabaseContract.RedditPost.COLUMN_NAME_REDDITPOST_ID, post.data.id);
                            redditPostValues.put(RedditDatabaseContract.RedditPost.COLUMN_NAME_REDDITPOST_FULL_NAME, post.data.name);
                            redditPostValues.put(RedditDatabaseContract.RedditPost.COLUMN_NAME_SUBREDDIT_ID, post.data.GetSubredditTruncatedId());
                            redditPostValues.put(RedditDatabaseContract.RedditPost.COLUMN_NAME_SUBREDDIT_FULL_NAME, post.data.subreddit_id);
                            redditPostValues.put(RedditDatabaseContract.RedditPost.COLUMN_NAME_SUBREDDIT_DISPLAY_NAME, post.data.subreddit);
                            redditPostValues.put(RedditDatabaseContract.RedditPost.COLUMN_NAME_AUTHOR, post.data.author);
                            redditPostValues.put(RedditDatabaseContract.RedditPost.COLUMN_NAME_TITLE, post.data.title);
                            redditPostValues.put(RedditDatabaseContract.RedditPost.COLUMN_NAME_SCORE, post.data.score);
                            redditPostValues.put(RedditDatabaseContract.RedditPost.COLUMN_NAME_CREATED, post.data.created_utc);
                            redditPostValues.put(RedditDatabaseContract.RedditPost.COLUMN_NAME_SELFTEXT, post.data.selftext);
                            redditPostValues.put(RedditDatabaseContract.RedditPost.COLUMN_NAME_URL, post.data.url);
                            redditPostValues.put(RedditDatabaseContract.RedditPost.COLUMN_NAME_NUM_COMMENTS, post.data.num_comments);
                            redditPostValues.put(RedditDatabaseContract.RedditPost.COLUMN_NAME_IS_OVER_18, post.data.over_18);
                            redditPostValues.put(RedditDatabaseContract.RedditPost.COLUMN_NAME_IS_STICKIED, post.data.stickied);
                            redditPostValues.put(RedditDatabaseContract.RedditPost.COLUMN_NAME_IS_SELF, post.data.is_self);

                            // If there's a thumbnail, we attempt to download it and save it to file.
                            // We save the path to the file in the database
                            String imageName = post.data.id + "_" + "thumbnail";
                            File thumbnailsFolder = ApplicationManager.GetThumbnailStorageDirectory(getApplicationContext());
                            String imagePath = ConnectionManager.DownloadImageAndStoreInFile(post.data.thumbnail, thumbnailsFolder, imageName);

                            // If there's an image, we also make an attempt to download it and save it to file
                            // TODO: Download the full image file and store it on disk

                            redditPostValues.put(RedditDatabaseContract.RedditPost.COLUMN_NAME_THUMBNAIL, imagePath);

                            long success = dbHelper.InsertIgnoreRedditPostRow(db, redditPostValues);
                            Log.d(TAG, "Post row number: " + success);
                        }
                        else
                        {
                            Log.e(TAG, "Error: JSONRedditPost object or JSONRedditPost data object came back null");
                        }

                        progressUpdates.UpdatePercent = (int)(((float)(j+1)/listingSize) * 100);
                        publishProgress(progressUpdates);
                        // Empty the values for the next post
                        redditPostValues.clear();
                    }

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
        protected void onProgressUpdate(ProgressUpdates... updates)
        {
            String message = updates[0].UpdateMessage;
            int progress = updates[0].UpdatePercent;

            if (!message.equals(""))
                _downloadDialog.setMessage(message);

            _downloadDialog.setProgress(progress);
        }

        @Override
        protected void onPostExecute(Void v)
        {
            super.onPostExecute(v);
            _downloadDialog.dismiss();
        }
    }

    public class ProgressUpdates
    {
        public ProgressUpdates() {}

        public String UpdateMessage;
        public int UpdatePercent;
    }
}
