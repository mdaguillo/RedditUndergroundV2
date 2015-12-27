package com.mikedaguillo.redditunderground2.data.api.json;

import java.util.ArrayList;

/**
 * Java representation of JSON listing from reddit
 */
public final class RedditListing {
    public String kind;
    public ListingData data;

    public class ListingData
    {
        public String modhash;
        public ArrayList<JSONRedditPost> children;
    }
}
