package com.mikedaguillo.redditunderground2.utility;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.gson.Gson;
import com.mikedaguillo.redditunderground2.data.api.*;
import com.mikedaguillo.redditunderground2.data.api.json.RedditListing;
import com.mikedaguillo.redditunderground2.data.api.json.RedditLogin;
import com.mikedaguillo.redditunderground2.data.api.json.JSONRedditSubreddits;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
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

    public ConnectionManager() {
    }

    /**
     * Attempts to login to reddit
     *
     * @param username
     * @param password
     * @return
     * @throws IOException
     */
    public static RedditLogin LoginToReddit(String username, String password) throws IOException {
        OutputStream outputStream;
        InputStream inputStream = null;

        try {
            // Set up the connection url
            String connectionUrl = RedditAPI.REDDIT_LOGIN_STRING.replace("{username}", username);
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
            RedditLogin redditReturnJSON = gson.fromJson(redditReturnValues, RedditLogin.class);

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

    public static JSONRedditSubreddits GetCurrentUsersSubscribedSubreddits(String sessionCookie) {
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
            JSONRedditSubreddits subredditsJSON = gson.fromJson(returnValues, JSONRedditSubreddits.class);
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

    public static RedditListing RetrievePostListing(String subreddit, String sessionCookie) throws IOException {
        InputStream inputStream;
        try
        {
            // Get connection string
            String connectionString = RedditAPI.REDDIT_SUBREDDIT_LISTING_HOT.replace("{subreddit}", subreddit);
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
                Log.e(TAG, "Error occurred while attempting to read input stream for subreddit: " + subreddit);
                return null;
            }

            Log.d(TAG, "Listing return values: " + returnValues);

            // Deserialize the json
            Gson gson = new Gson();
            RedditListing listing = gson.fromJson(returnValues, RedditListing.class);
            return listing;
        }
        catch (IOException ex)
        {
            Log.e(TAG, "Error making connection to listing", ex);
            return null;
        }
    }

    /**
     * Takes a url to an image, downloads the image and saves it to file on the phone.
     * Returns the filepath to the image if successful (or null otherwise).
     */
    public static String DownloadImageAndStoreInFile(String imageUrl, File storagePath, String fileName)
    {
        String filepath;
        InputStream inputStream = null;
        try
        {
            // If there is no thumbnail short circuit
            if (imageUrl.equals("") || !imageUrl.startsWith("http"))
                return null;

            // Attempt to make a connection
            URL connectionUrl = new URL(imageUrl);
            inputStream = connectionUrl.openStream();

            // Determine the image extension
            fileName = fileName + imageUrl.substring(imageUrl.lastIndexOf('.'));
            // Now open an output stream we'll write to
            File image = new File(storagePath, fileName);
            OutputStream outputStream = new FileOutputStream(image);
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            BufferedOutputStream bos = new BufferedOutputStream(outputStream);
            int i;
            while ((i = bis.read()) != -1)
            {
                bos.write(i);
            }
            bos.flush();
            bos.close();
            outputStream.close();

            // Return the file path
            Log.d(TAG, "File written to: " + image.getAbsolutePath());
            filepath = image.getAbsolutePath();
            return filepath;

        }
        catch (IOException ex)
        {
            Log.e(TAG, "An error occurred while downloading an image from the url " + imageUrl, ex);
            if (inputStream != null)
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            return null;
        }
    }
}
