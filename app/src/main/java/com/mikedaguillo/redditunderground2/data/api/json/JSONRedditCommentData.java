package com.mikedaguillo.redditunderground2.data.api.json;

/**
 * Java representation of the data object within a reddit JSON comment object
 */
public class JSONRedditCommentData {
    String id; // excludes the t#
    String subreddit_id; // full name ex: t5_2q12g
    JSONRedditCommentListing replies; // Data is recursive. A comment can contain its own listing of comments
    int gilded;
    String author;
    String parent_id; // Full name. Can be an original post or another comment
    String link_id; // Full name. The id of the post this comment is under. If this is equal to parent_id, the comment is a first level reply
    int score;
    String body;
    int ups;
    int downs;
    String name; // The full id ex: t1_cy7t3pp
    int created_utc; // post date
}
