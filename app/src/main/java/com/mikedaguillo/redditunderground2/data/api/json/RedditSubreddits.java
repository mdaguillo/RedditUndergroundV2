package com.mikedaguillo.redditunderground2.data.api.json;

/**
 * Java representation of the json returned from the api call:
 * reddit.com/subreddits/mine/subscriber
 */
public final class RedditSubreddits {
    public String kind;
    public DataObject data;

    public class DataObject {
        public String modhash;
        public SubredditObject[] children;
    }

    public class SubredditObject {
        public String kind;
        public SubredditDataObject data;
    }

    public class SubredditDataObject {
        public String id;
        public String display_name;
        public String url;
    }
}