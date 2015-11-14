package com.mikedaguillo.redditunderground2.utility;

import android.content.Intent;
import android.net.Uri;

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

public class ConnectionManager {
    private static String RedditConnectionString = "http://www.reddit.com/api/login/{username}";

    public static String GetConnectionUrl()
    {
        String userPermissionUrl = RedditConnectionString.replace("{username}", "BetaRhoOmega");
        return userPermissionUrl;
    }

    public static String ConnectToReddit(String connectionUrl, String userName, String password) throws IOException {
        OutputStream outputStream = null;
        InputStream inputStream = null;

        try
        {
            URL url = new URL(connectionUrl);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // Write post params
            Hashtable<String, String> postParams = new Hashtable<>();
            postParams.put("api_type", "json");
            postParams.put("user", userName);
            postParams.put("passwd", password);

            String postParamsString = WritePostParams(postParams);

            outputStream = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            writer.write(postParamsString);
            writer.flush();
            writer.close();
            outputStream.close();

            // Connect to reddit
            connection.connect();

            inputStream = connection.getInputStream();
            StringBuilder sb = new StringBuilder();
            BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }

            return sb.toString();
        }
        catch (Exception ex)
        {
            return "Error occurred while connecting";
        }
        finally
        {
            if (inputStream != null)
                inputStream.close();
        }
    }

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
}
