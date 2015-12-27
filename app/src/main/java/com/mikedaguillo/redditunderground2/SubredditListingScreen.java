package com.mikedaguillo.redditunderground2;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mikedaguillo.redditunderground2.data.RedditPostListItem;
import com.mikedaguillo.redditunderground2.data.database.RedditDatabaseHelper;
import com.mikedaguillo.redditunderground2.utility.ApplicationManager;

import java.util.ArrayList;

public class SubredditListingScreen extends AppCompatActivity {
    private String subredditName;
    private ProgressBar progressBar;
    private RedditDatabaseHelper dbHelper;
    private ListView listingView;

    private final String TAG = "SubredditListingScreen";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subreddit_listing_screen);

        // Display the loading bar
        progressBar = (ProgressBar) findViewById(R.id.subreddit_listing_progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        // Bind the rest of the controls
        listingView = (ListView) findViewById(R.id.subreddit_listing_view);

        // Get the subreddit passed from the other activity
        Intent intent = getIntent();
        subredditName = intent.getStringExtra("subreddit");

        // Setup the view
        dbHelper = new RedditDatabaseHelper(this);
        RedditPostAdapter postAdapter = new RedditPostAdapter(subredditName, dbHelper);
        listingView.setAdapter(postAdapter);
        listingView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Retrieve the post id and send the user to the post view
                RedditPostListItem clickedPost = (RedditPostListItem)listingView.getItemAtPosition(position);
                ApplicationManager.SendUserToActivity(getApplicationContext(), ViewPostScreen.class, "postId", clickedPost.postId);
            }
        });

        progressBar.setVisibility(View.INVISIBLE);
    }

    class RedditPostAdapter extends BaseAdapter {
        ArrayList<RedditPostListItem> savedPosts;

        public RedditPostAdapter(String subredditName, RedditDatabaseHelper dbHelper)
        {
            savedPosts = dbHelper.GetPostsForSubreddit(subredditName);
        }

        @Override
        public int getCount() {
            return savedPosts.size();
        }

        @Override
        public Object getItem(int position) {
            return savedPosts.get(position);
        }

        @Override
        public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null)
            {
                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.reddit_post_list_item, parent, false);
            }

            TextView postTitle = (TextView)view.findViewById(R.id.postTitle);
            TextView postAuthor = (TextView)view.findViewById(R.id.postAuthor);
            TextView postSubReddit = (TextView)view.findViewById(R.id.postSubReddit);
            TextView postComments = (TextView)view.findViewById(R.id.postComments);
            ImageView postThumbnail = (ImageView)view.findViewById(R.id.thumbnail);

            RedditPostListItem post = savedPosts.get(position);

            postTitle.setText(post.title);
            postAuthor.setText(post.author);
            postSubReddit.setText(post.subreddit);
            postComments.setText("Comments: " + post.numComments);
            if (post.thumbnail != null) {
                postThumbnail.setImageBitmap(post.thumbnail);
                postThumbnail.setVisibility(View.VISIBLE);
            }
            else {
                postThumbnail.setVisibility(View.GONE);
            }

            return view;
        }
    }
}
