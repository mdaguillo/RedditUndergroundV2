package com.mikedaguillo.redditunderground2;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mikedaguillo.redditunderground2.data.RedditDatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class CachedSubredditsScreen extends AppCompatActivity {

    private ProgressBar progressBar;
    private ListView listingListView;
    private RedditDatabaseHelper dbHelper;
    private SQLiteDatabase database;
    private ArrayList<String> subredditsToDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subreddit_listing_screen);

        // Bind the controls
        listingListView = (ListView) findViewById(R.id.subreddit_listing_view);
        progressBar = (ProgressBar) findViewById(R.id.loadingBar);

        // Display the loading bar while we query the database for the current user's subreddits with content
        progressBar.setVisibility(View.VISIBLE);

        dbHelper = new RedditDatabaseHelper(this);
        database = dbHelper.getReadableDatabase();
        subredditsToDisplay = dbHelper.GetCachedSubreddits(database);

        if (subredditsToDisplay.size() == 0)
        {
            // Display a toast to tell the user they have no cached data
            Toast toast = Toast.makeText(this, "There is currently no cached data. Please return to the main screen to cache a subreddit.", Toast.LENGTH_LONG);
            toast.show();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, subredditsToDisplay);
        listingListView.setAdapter(adapter);

        // close the database
        database.close();

        progressBar.setVisibility(View.INVISIBLE);
    }
}
