package com.mikedaguillo.redditunderground2;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mikedaguillo.redditunderground2.data.database.RedditDatabaseHelper;
import com.mikedaguillo.redditunderground2.utility.ApplicationManager;

public class SettingsScreen extends AppCompatActivity {

    private ListView settingsView;
    private TextView currentUserLabel;
    private String[] settingOptions = new String[] { "Clear all data" };
    private SharedPreferences appSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        // Instantiate the sharedPreferences
        appSettings = this.getSharedPreferences("app_settings", Context.MODE_PRIVATE);

        // Bind the controls
        settingsView = (ListView) findViewById(R.id.options);
        currentUserLabel = (TextView) findViewById(R.id.current_user_label);

        // Determine login state
        if (appSettings.getBoolean(getString(R.string.LoginState), false))
        {
            String currentUser = appSettings.getString(getString(R.string.CurrentUser), "");
            currentUserLabel.setText("Current user: " + currentUser);
            currentUserLabel.setVisibility(View.VISIBLE);
        }
        else
        {   // logged out
            currentUserLabel.setText("");
            currentUserLabel.setVisibility(View.GONE);
        }

        // Set up the list view adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, settingOptions);
        settingsView.setAdapter(adapter);
        settingsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemValue = (String) settingsView.getItemAtPosition(position);

                switch(itemValue)
                {
                    case "Clear all data":
                        ApplicationManager.CreateAndShowAlertDialog(SettingsScreen.this, "Clear Data", "Are you certain you want to delete all subreddit data currently saved?", "Delete", "Cancel", new ApplicationManager.DialogCallback() {
                            @Override
                            public void execute() {
                                Context context = getApplicationContext();
                                // Clear out the current logged in user settings
                                RedditDatabaseHelper dbHelper = new RedditDatabaseHelper(context);

                                Toast returnMessage;
                                if (dbHelper.DeleteAllData(getApplicationContext()))
                                    returnMessage = Toast.makeText(context, "Data deleted successfully!", Toast.LENGTH_LONG);
                                else
                                    returnMessage = Toast.makeText(context, "An error occurred while deleting data. Please try again.", Toast.LENGTH_LONG);

                                dbHelper.close();
                                returnMessage.show(); // Show the message
                            }
                        });
                        break;
                    default:
                        break;
                }
            }
        });
    }
}
