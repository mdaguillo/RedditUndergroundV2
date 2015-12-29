package com.mikedaguillo.redditunderground2.data.api.json;

import java.util.ArrayList;

/**
 * The data object in a listing of comments
 */
public class JSONRedditCommentListingData {
    String modhash;
    ArrayList<JSONRedditComment> children;
}
