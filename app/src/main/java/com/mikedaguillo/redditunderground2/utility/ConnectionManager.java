package com.mikedaguillo.redditunderground2.utility;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.gson.Gson;
import com.mikedaguillo.redditunderground2.data.Listing;
import com.mikedaguillo.redditunderground2.data.RedditDatabaseContract;
import com.mikedaguillo.redditunderground2.data.RedditDatabaseHelper;
import com.mikedaguillo.redditunderground2.data.RedditPost;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

public final class ConnectionManager {

    private static final String TAG = "ConnectionManager";

    public ConnectionManager() {
    }

    /**
     * Replaces the connection string skeleton with the desired username to login to
     *
     * @param username
     * @return A connection url with
     */
    public static String GetConnectionUrl(String username) {
        return RedditAPI.REDDIT_LOGIN_STRING.replace("{username}", username);
    }

    /**
     * Attempts to login to reddit
     *
     * @param username
     * @param password
     * @return
     * @throws IOException
     */
    public static RedditLoginJSON LoginToReddit(String username, String password) throws IOException {
        OutputStream outputStream;
        InputStream inputStream = null;

        try {
            // Set up the connection url
            String connectionUrl = GetConnectionUrl(username);
            URL url = new URL(connectionUrl);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // Write post params
            Hashtable<String, String> postParams = new Hashtable<>();
            postParams.put("api_type", "json");
            postParams.put("user", username);
            postParams.put("passwd", password);
            String postParamsString = WritePostParams(postParams);

            // Write to the output stream
            outputStream = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            writer.write(postParamsString);
            writer.flush();
            writer.close();
            outputStream.close();

            // Perform the login
            connection.connect();

            // Now read the results from reddit
            inputStream = connection.getInputStream();
            StringBuilder sb = new StringBuilder();
            BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = rd.readLine()) != null)
                sb.append(line);

            String redditReturnValues = sb.toString();
            Log.d(TAG, "Reddit return values: " + redditReturnValues);

            // Lets convert the string to a json object
            Gson gson = new Gson();
            ConnectionManager.RedditLoginJSON redditReturnJSON = gson.fromJson(redditReturnValues, ConnectionManager.RedditLoginJSON.class);

            return redditReturnJSON;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error occurred attempting to login to reddit.", e);

            return null;
        } finally {
            if (inputStream != null)
                inputStream.close();
        }
    }

    public static RedditSubredditsJSON GetCurrentUsersSubscribedSubreddits(String sessionCookie) {
        InputStream inputStream = null;
        try {
            // Construct subreddits url
            URL connectionUrl = new URL(RedditAPI.REDDIT_GET_SUBSCRIBED_SUBREDDITS_STRING);
            HttpsURLConnection connection = (HttpsURLConnection) connectionUrl.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Cookie", "reddit_session=" + sessionCookie + "; secure_session=1");

            // Connect
            connection.connect();

            // Read the results
            inputStream = connection.getInputStream();
            String returnValues = ReadInputStream(inputStream);

            if (returnValues == null)
            {
                // Error occurred reading the input stream
                Log.e(TAG, "Error occurred while reading input stream for users subreddits");
                return null;
            }

            Log.d(TAG, "Subreddits return values: " + returnValues);

            Gson gson = new Gson();
            RedditSubredditsJSON subredditsJSON = gson.fromJson(returnValues, RedditSubredditsJSON.class);
            return subredditsJSON;
        } catch (IOException e) {
            Log.e(TAG, "Error occurred while attempting to retrieve user subreddits.", e);
            return null;
        }
    }

    private static String ReadInputStream(InputStream inputStream) throws IOException
    {
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null)
                sb.append(line);

            return sb.toString();
        }
        catch (IOException ex) {
            Log.e(TAG, "Error occurred reading the input stream.", ex);
            return null;
        }
        finally
        {
            if (inputStream != null)
                inputStream.close();
        }
    }

    /**
     * Converts a hashtable of key value pairs to an ampersand separated string post params
     *
     * @param postParams
     * @return Ampersand separated string of post params
     */
    private static String WritePostParams(Hashtable<String, String> postParams) {
        StringBuilder postParamStringBuilder = new StringBuilder();
        Set<String> paramEntries = postParams.keySet();
        boolean first = true;
        for (String key : paramEntries) {
            if (first)
                first = false;
            else
                postParamStringBuilder.append("&");

            postParamStringBuilder.append(key + "=" + postParams.get(key));
        }

        return postParamStringBuilder.toString();
    }


    public static boolean IsConnectedToNetwork(Activity activity) {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) return false;
        NetworkInfo.State network = info.getState();
        return (network == NetworkInfo.State.CONNECTED);
    }

    public static boolean CacheSubreddit(String subredditToCache, String subredditId, RedditDatabaseHelper dbHelper, SQLiteDatabase db, String sessionCookie) throws IOException {
        InputStream inputStream;
        try
        {
            // Get connection string
            String connectionString = RedditAPI.REDDIT_SUBREDDIT_LISTING_HOT.replace("{subreddit}", subredditToCache);
            URL connectionUrl = new URL(connectionString);
            HttpsURLConnection connection = (HttpsURLConnection) connectionUrl.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Cookie", "reddit_session=" + sessionCookie + "; secure_session=1");

            // Connect
            connection.connect();

            // Read the results
            inputStream = connection.getInputStream();
            String returnValues = ReadInputStream(inputStream);
            if (returnValues == null)
            {
                Log.e(TAG, "Error occurred while attempting to read input stream for subreddit: " + subredditToCache);
                return false;
            }

            Log.d(TAG, "Listing return values: " + returnValues);

            // Deserialize the json
            Gson gson = new Gson();
            Listing listing = gson.fromJson(returnValues, Listing.class);

            // Check to make sure the data returned is valid
            if (listing == null || listing.data == null || listing.data.children == null)
            {
                Log.e(TAG, "Error: Listing json data came back as null, or the data or children are null.");
                return false;
            }

            // Now insert the data from the listing into the db
            HashMap<String, Object> redditPostValues = new HashMap<>();
            for (RedditPost post : listing.data.children)
            {
                if (post != null && post.data != null)
                {
                    redditPostValues.put(RedditDatabaseContract.RedditPost.COLUMN_NAME_REDDITPOST_ID, post.data.id);
                    redditPostValues.put(RedditDatabaseContract.RedditPost.COLUMN_NAME_SUBREDDIT_ID, subredditId);
                    redditPostValues.put(RedditDatabaseContract.RedditPost.COLUMN_NAME_AUTHOR, post.data.author);
                    redditPostValues.put(RedditDatabaseContract.RedditPost.COLUMN_NAME_TITLE, post.data.title);
                    redditPostValues.put(RedditDatabaseContract.RedditPost.COLUMN_NAME_SCORE, post.data.score);
                    redditPostValues.put(RedditDatabaseContract.RedditPost.COLUMN_NAME_CREATED, post.data.created_utc);
                    redditPostValues.put(RedditDatabaseContract.RedditPost.COLUMN_NAME_SELFTEXT, post.data.selftext);
                    redditPostValues.put(RedditDatabaseContract.RedditPost.COLUMN_NAME_URL, post.data.url);
                    redditPostValues.put(RedditDatabaseContract.RedditPost.COLUMN_NAME_THUMBNAIL, post.data.thumbnail);
                    redditPostValues.put(RedditDatabaseContract.RedditPost.COLUMN_NAME_NUM_COMMENTS, post.data.num_comments);
                    redditPostValues.put(RedditDatabaseContract.RedditPost.COLUMN_NAME_IS_OVER_18, post.data.over_18);
                    redditPostValues.put(RedditDatabaseContract.RedditPost.COLUMN_NAME_IS_STICKIED, post.data.stickied);
                    redditPostValues.put(RedditDatabaseContract.RedditPost.COLUMN_NAME_IS_SELF, post.data.is_self);

                    dbHelper.InsertIgnoreRedditPostRow(db, redditPostValues);
                }
                else
                {
                    Log.e(TAG, "Error: RedditPost object or RedditPost data object came back null");
                }
                // Empty the values for the next post
                redditPostValues.clear();
            }

            return true;
        }
        catch (IOException ex)
        {
            Log.e(TAG, "Error making connection to listing", ex);
            return false;
        }
    }

    private static class RedditAPI {
        // Connection string skeleton
        public static String REDDIT_LOGIN_STRING = "https://www.reddit.com/api/login/{username}";
        public static String REDDIT_GET_SUBSCRIBED_SUBREDDITS_STRING = "https://www.reddit.com/subreddits/mine/subscriber.json";
        public static String REDDIT_SUBREDDIT_LISTING_HOT = "https://www.reddit.com/r/{subreddit}/hot.json";
    }

    public static class RedditLoginJSON {
        public RedditLoginJSON() {
        }

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

    public static class RedditSubredditsJSON {
        public RedditSubredditsJSON() {
        }

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
}
