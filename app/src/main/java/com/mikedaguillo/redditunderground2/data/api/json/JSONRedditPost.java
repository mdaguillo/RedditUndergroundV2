package com.mikedaguillo.redditunderground2.data.api.json;

/**
 * Java representation of a JSON reddit post
 */
public final class JSONRedditPost {
    public String kind;
    public RedditPostData data;

    public class RedditPostData {
        public String id; // Ex: 3t254u
        public String name; // Full name ex: t3_3t254u
        public String subreddit_id; // Full name of subreddit
        public String author;
        public String selftext;
        public String thumbnail;
        public String url;
        public String title;
        public int score; // upvotes - downvotes
        public int num_comments;
        public boolean over_18;
        public boolean stickied;
        public boolean is_self;
        public float created_utc;
    }
}
