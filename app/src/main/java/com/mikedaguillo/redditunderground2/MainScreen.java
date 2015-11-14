package com.mikedaguillo.redditunderground2;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import com.mikedaguillo.redditunderground2.utility.ConnectionManager;

import java.io.IOException;

public class MainScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView textView = (TextView) findViewById(R.id.textView);
        setSupportActionBar(toolbar);

        final ConnectionManager connectionManager = new ConnectionManager();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userConnectionString = connectionManager.GetConnectionUrl();

                // First make sure the user is connected to the network
                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    // fetch data
                    LoginToReddit loginToReddit = new LoginToReddit(connectionManager);
                    loginToReddit.doInBackground();
                } else {
                    // display error
                    textView.setText("You must be connected to a network to login.");
                }
            }
        });
    }

    private class LoginToReddit extends AsyncTask<String, Void, String>
    {
        private ConnectionManager cm;

        public LoginToReddit(ConnectionManager connectionManager)
        {
            cm = connectionManager;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                return cm.ConnectToReddit(cm.GetConnectionUrl(), "BetaRhoOmega", "Coffeeiphone1");
            } catch (IOException e) {
                e.printStackTrace();
                return "Unable to connect to reddit";
            }
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            textView.setText(result);
        }

    }
}
