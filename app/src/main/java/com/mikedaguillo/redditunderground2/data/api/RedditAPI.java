package com.mikedaguillo.redditunderground2.data.api;

/**
 * Class which houses various api connection strings
 */
public final class RedditAPI {
    // Connection string skeleton
    public static String REDDIT_LOGIN_STRING = "https://www.reddit.com/api/login/{username}";
    public static String REDDIT_GET_SUBSCRIBED_SUBREDDITS_STRING = "https://www.reddit.com/subreddits/mine/subscriber.json";
    public static String REDDIT_SUBREDDIT_LISTING_HOT = "https://www.reddit.com/r/{subreddit}/hot.json";
}