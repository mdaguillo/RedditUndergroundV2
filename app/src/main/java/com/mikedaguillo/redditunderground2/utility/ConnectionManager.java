package com.mikedaguillo.redditunderground2.utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.URL;
import java.security.cert.Certificate;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;

public final class ConnectionManager {

    private static final String TAG = "ConnectionManager";

    public ConnectionManager() { }

    /**
     * Replaces the connection string skeleton with the desired username to login to
     * @param username
     * @return A connection url with
     */
    public static String GetConnectionUrl(String username) { return RedditAPI.REDDIT_CONNECTION_STRING.replace("{username}", username); }

    /**
     * Attempts to login to reddit
     * @param username
     * @param password
     * @return
     * @throws IOException
     */
    public static String LoginToReddit(String username, String password) throws IOException
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
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }

            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error occurred attempting to login to reddit.", e);

            return "Unable to connect to reddit";
        }
        finally {
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
        public static String REDDIT_CONNECTION_STRING = "https://www.reddit.com/api/login/{username}";
    }
}
