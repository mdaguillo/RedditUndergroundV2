package com.mikedaguillo.redditunderground2.data;

import java.util.ArrayList;

/**
 * Java representation of JSON listing from reddit
 */
public final class Listing {
    public String kind;
    public ListingData data;

    public class ListingData
    {
        public String modhash;
        public ArrayList<RedditPost> children;
    }
}
