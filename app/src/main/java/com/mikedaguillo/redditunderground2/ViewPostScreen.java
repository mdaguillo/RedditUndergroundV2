package com.mikedaguillo.redditunderground2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mikedaguillo.redditunderground2.data.database.RedditDatabaseHelper;
import com.mikedaguillo.redditunderground2.data.database.model.RedditPost;
import com.mikedaguillo.redditunderground2.utility.ApplicationManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ViewPostScreen extends AppCompatActivity {
    private final String TAG = "ViewPostScreen";
    private ProgressBar progressBar;
    private ListView postListView;
    private RedditDatabaseHelper dbhelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post_screen);

        // Display the loading bar
        progressBar = (ProgressBar) findViewById(R.id.post_view_progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        // Bind the controls
        postListView = (ListView) findViewById(R.id.commentsListView);

        // Retrieve the post id from the intent
        Intent intent = getIntent();
        String postId = intent.getStringExtra("postId");
        Log.d(TAG, "Viewing post with id: " + postId);

        // Set up the view
        dbhelper = new RedditDatabaseHelper(this);
        PostAndCommentListAdapter adapter = new PostAndCommentListAdapter(postId, dbhelper);
        postListView.setAdapter(adapter);

        progressBar.setVisibility(View.INVISIBLE);
    }

    class PostAndCommentListAdapter extends BaseAdapter {
        RedditPost originalPost;

        public PostAndCommentListAdapter(String postId, RedditDatabaseHelper databaseHelper)
        {
            originalPost = databaseHelper.GetPostById(postId);
        }

        @Override
        public int getCount() { return 1; }

        @Override
        public Object getItem(int position) { return null; }

        @Override
        public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null)
            {
                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                // The first item is always the original post
                if (position == 0)
                {
                    view = inflater.inflate(R.layout.original_post_list_item, parent, false);
                }
                else
                {
                    // comments
                }
            }

            // Bind the data to the layout controls
            if (position == 0)
            {
                // Original Post
                TextView author = (TextView)view.findViewById(R.id.opAuthor);
                TextView subreddit = (TextView)view.findViewById(R.id.opSubreddit);
                TextView createdDate = (TextView)view.findViewById(R.id.opCreated);
                TextView score = (TextView)view.findViewById(R.id.opScore);
                TextView title = (TextView)view.findViewById(R.id.opTitle);
                ImageView thumbnail = (ImageView)view.findViewById(R.id.opThumbnail);
                TextView numComments = (TextView)view.findViewById(R.id.opNumComments);
                TextView selfText = (TextView)view.findViewById(R.id.opSelfText);

                author.setText(originalPost.GetPostAuthor());
                subreddit.setText(originalPost.GetSubredditDisplayName());

                // Format and set the post date
                Date postedDate = new Date((long)originalPost.GetCreated()*1000);
                DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                createdDate.setText(df.format(postedDate));

                score.setText(originalPost.GetPostScore() + "");
                title.setText(originalPost.GetPostTitle());

                // Set the thumbnail
                Bitmap thumbnailImage = ApplicationManager.GetThumbnailFromFile(originalPost.GetThumbnailLocation());
                if (thumbnailImage != null) {
                    thumbnail.setImageBitmap(thumbnailImage);
                    thumbnail.setVisibility(View.VISIBLE);
                }
                else {
                    thumbnail.setVisibility(View.GONE);
                }

                numComments.setText("Comments: " + originalPost.GetNumComments());

                // Set the self text if we need to
                if (originalPost.isSelf())
                {
                    selfText.setText(originalPost.GetSelfText());
                    selfText.setVisibility(View.VISIBLE);
                }
            }

            return view;
        }
    }
}
