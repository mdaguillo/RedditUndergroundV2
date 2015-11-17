package com.mikedaguillo.redditunderground2.data;

/**
 * Java representation of a JSON reddit post
 */
public class RedditPost {
    public String kind;
    public PostData data;

    class PostData {
        public String id; // Ex: 3t254u
        public String author;
        public String selftext;
        public String thumbnail;
        public String url;
        public String title;
        public int score; // upvotes - downvotes
        public int num_comments;
        public Boolean over_18;
        public Boolean stickied;
        public Boolean is_self;
        public Float created_utc;

    }
}
