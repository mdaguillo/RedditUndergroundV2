package com.mikedaguillo.redditunderground2.data.api.json;

/**
 * This is returned when you request a list of comments from a post. The first object is the actual post, the second object is this.
 */
public class JSONRedditCommentListing {
    String kind;
    JSONRedditCommentListingData data;
}
