package com.mikedaguillo.redditunderground2.utility;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.Hashtable;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

public final class ConnectionManager {

    private static final String TAG = "ConnectionManager";

    public ConnectionManager() { }

    /**
     * Replaces the connection string skeleton with the desired username to login to
     * @param username
     * @return A connection url with
     */
    public static String GetConnectionUrl(String username) { return RedditAPI.REDDIT_LOGIN_STRING.replace("{username}", username); }

    /**
     * Attempts to login to reddit
     * @param username
     * @param password
     * @return
     * @throws IOException
     */
    public static RedditLoginJSON LoginToReddit(String username, String password) throws IOException
    {
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
        }
        finally {
            if (inputStream != null)
                inputStream.close();
        }
    }

    public static RedditSubredditsJSON GetCurrentUsersSubscribedSubreddits (String currentUser, String sessionCookie) throws IOException
    {
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
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null)
                sb.append(line);

            String returnValues = sb.toString();
            Log.d(TAG, "Subreddits return values: " + returnValues);

            Gson gson = new Gson();
            RedditSubredditsJSON subredditsJSON = gson.fromJson(returnValues, RedditSubredditsJSON.class);
            return subredditsJSON;
        }
        catch (IOException e)
        {
            Log.e(TAG, "Error occurred while attempting to retrieve user subreddits.", e);
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
     * @param postParams
     * @return Ampersand separated string of post params
     */
    private static String WritePostParams(Hashtable<String, String> postParams)
    {
        StringBuilder postParamStringBuilder = new StringBuilder();
        Set<String> paramEntries = postParams.keySet();
        boolean first = true;
        for (String key : paramEntries)
        {
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

    private static class RedditAPI {
        // Connection string skeleton
        public static String REDDIT_LOGIN_STRING = "https://www.reddit.com/api/login/{username}";
        public static String REDDIT_GET_SUBSCRIBED_SUBREDDITS_STRING = "https://www.reddit.com/subreddits/mine/subscriber.json";
    }

    public static class RedditLoginJSON {
        public RedditLoginJSON() {}

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
        public RedditSubredditsJSON() {}

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
