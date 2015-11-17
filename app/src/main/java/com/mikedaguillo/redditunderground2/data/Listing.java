package com.mikedaguillo.redditunderground2.data;

/**
 * Java representation of JSON listing from reddit
 */
public class Listing {
    public String kind;
    public ListingData data;

    class ListingData
    {
        public String modhash;
    }
}
