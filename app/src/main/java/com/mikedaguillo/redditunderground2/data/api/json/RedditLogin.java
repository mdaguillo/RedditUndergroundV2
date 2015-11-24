package com.mikedaguillo.redditunderground2.data.api.json;

/**
 * Java representation of the JSON returned when logging into reddit
 */
public final class RedditLogin {
    public JsonObject json;

    public class JsonObject {
        public String[][] errors;
        public DataObject data;
    }

    public class DataObject {
        public Boolean need_https;
        public String modhash;
        public String cookie;
    }
}
