package com.mikedaguillo.redditunderground2.data;

import android.graphics.Bitmap;

/**
 * Java representation of reddit post as a list item
 * Applied to a ListView
 */
public class RedditPostListItem {
    public String postId;
    public String title;
    public String author;
    public String subreddit;
    public int numComments;
    public Bitmap thumbnail;

    public RedditPostListItem (String postId, String title, String author, String subreddit, int numComments, Bitmap thumbnail) {
        this.postId = postId;
        this.title = title;
        this.author = author;
        this.subreddit = subreddit;
        this.numComments = numComments;
        this.thumbnail = thumbnail;
    }
}
