package com.mikedaguillo.redditunderground2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mikedaguillo.redditunderground2.utility.ApplicationManager;

public class MainScreen extends AppCompatActivity {
    private ListView menuOptions;
    private TextView currentUserLabel;
    private SharedPreferences appSettings;
    private final String[] loggedOutMenuValues = new String[] { "Login to reddit" };
    private final String[] loggedInMenuValues = new String[] { "Download from my subreddits", "Logout" };
    private String[] displayValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        // Instantiate the sharedPreferences
        appSettings = this.getSharedPreferences("app_settings", Context.MODE_PRIVATE);

        // Bind the controls
        currentUserLabel = (TextView) findViewById(R.id.current_user_label);
        menuOptions = (ListView) findViewById(R.id.main_menu_options);

        // Determine login state
        if (appSettings.getBoolean(getString(R.string.LoginState), false))
        {
            displayValues = loggedInMenuValues;
            String currentUser = appSettings.getString(getString(R.string.CurrentUser), "");
            currentUserLabel.setText("Current user: " + currentUser);
            currentUserLabel.setVisibility(View.VISIBLE);
        }
        else
        {   // logged out
            displayValues = loggedOutMenuValues;
            currentUserLabel.setText("Current user: ");
            currentUserLabel.setVisibility(View.GONE);
        }

        // Set up the list view adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, displayValues);
        menuOptions.setAdapter(adapter);
        menuOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemValue = (String) menuOptions.getItemAtPosition(position);

                // Based on the value selected, send the user to a specific screen
                switch (itemValue)
                {
                    case "Login to reddit":
                        sendUserToActivity(LoginScreen.class);
                        break;
                    case "Download from my subreddits":
                        sendUserToActivity(CacheSubredditsScreen.class);
                        break;
                    case "Logout":
                        ApplicationManager.CreateAndShowAlertDialog(MainScreen.this, "Logout of Reddit", "Are you sure you want to logout?", "Logout", "Cancel", new ApplicationManager.DialogCallback() {
                            @Override
                            public void execute() {
                                // Clear out the current logged in user settings
                                SharedPreferences.Editor editor = appSettings.edit();
                                editor.remove(getString(R.string.LoginState));
                                editor.remove(getString(R.string.CurrentUser));
                                editor.remove(getString(R.string.SessionCookie));
                                editor.commit();

                                // Now refresh the current screen
                                recreate();
                            }
                        });
                    default:
                        break;
                }
            }
        });
    }

    private void sendUserToActivity(Class destinationActivityClass)
    {
        Intent intent = new Intent(this, destinationActivityClass);
        startActivity(intent);
    }
}
