package com.mikedaguillo.redditunderground2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mikedaguillo.redditunderground2.data.database.RedditDatabaseHelper;
import com.mikedaguillo.redditunderground2.utility.ApplicationManager;
import com.mikedaguillo.redditunderground2.utility.ConnectionManager;
import com.mikedaguillo.redditunderground2.data.api.json.JSONRedditSubreddits;
import com.mikedaguillo.redditunderground2.data.api.json.RedditLogin;

import java.io.IOException;

/**
 * A login screen that offers login via email/password to reddit.
 * Upon completion of login, the login cookie is stored in the shared preferences of the app,
 * and the static Reddit object is populated with information about the user's account.
 */
public class LoginScreen extends AppCompatActivity {
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mUserNameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    // Settings
    private SharedPreferences appSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        // Set up the login form.
        mUserNameView = (EditText) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);

        Button mRedditLoginButon = (Button) findViewById(R.id.reddit_login_button);
        mRedditLoginButon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ApplicationManager.HideKeyboard(LoginScreen.this, view);
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        // Instantiate the connection manager and settings
        appSettings = this.getSharedPreferences("app_settings", Context.MODE_PRIVATE);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUserNameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUserNameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check network connection
        if (!ConnectionManager.IsConnectedToNetwork(this))
        {
            Toast.makeText(LoginScreen.this, "You must be connected to the internet in order to login to reddit.", Toast.LENGTH_LONG).show();
            return;
        }

        // Check for a valid password
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            mUserNameView.setError(getString(R.string.error_field_required));
            focusView = mUserNameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(username, password);
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String TAG = "UserLoginTask";
        private final String mUsername;
        private final String mPassword;

        UserLoginTask(String username, String password) {
            mUsername = username;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            SQLiteDatabase db = null;

            try {
                // Try and login
                RedditLogin redditReturnJSON = ConnectionManager.LoginToReddit(mUsername, mPassword);

                // Check if we got a valid response
                if (redditReturnJSON == null)
                {
                    Log.e(TAG, "Error while logging in. The return string could not be converted to a json object");
                    return false;
                }

                // Check if we got any errors
                if (redditReturnJSON.json.errors.length > 0)
                {
                    Log.e(TAG, "Error while logging in: " + redditReturnJSON.json.errors.toString());
                    return false;
                }

                // Check the return data
                if (redditReturnJSON.json.data == null)
                {   // No data returned
                    Log.e(TAG, "No data returned from reddit");
                    return false;
                }

                // Check to make sure we got a session cookie
                if (redditReturnJSON.json.data.cookie == null || redditReturnJSON.json.data.cookie.equals(""))
                {   // No session cookie
                    Log.e(TAG, "No session cookie retrieved");
                    return false;
                }

                // No errors, store user login data in the shared preferences
                SharedPreferences.Editor editor = appSettings.edit();
                editor.putBoolean(getString(R.string.LoginState), true);
                editor.putString(getString(R.string.CurrentUser), mUsername);
                editor.putString(getString(R.string.SessionCookie), redditReturnJSON.json.data.cookie);
                editor.commit();

                // Now lets try and retrieve the user's subreddits
                JSONRedditSubreddits subscribedSubreddits = ConnectionManager.GetCurrentUsersSubscribedSubreddits(redditReturnJSON.json.data.cookie);

                if (subscribedSubreddits == null)
                {
                    Log.e(TAG, "Subscribed subreddits object is null");
                    return false;
                }

                // Retrieve the database helper and a writeable database
                RedditDatabaseHelper helper = new RedditDatabaseHelper(getApplicationContext());
                db = helper.getWritableDatabase();

                if (subscribedSubreddits.data == null || subscribedSubreddits.data.children == null)
                {
                    Log.e(TAG, "Subscribed subreddits object has no data object or children object");
                    return false;
                }

                // Loop through the returned data and insert into the db
                for (int i = 0; i < subscribedSubreddits.data.children.length; i++)
                {
                    JSONRedditSubreddits.SubredditDataObject subredditData = subscribedSubreddits.data.children[i].data;
                    helper.InsertIgnoreSubredditRow(
                            db,
                            subredditData.id,
                            subredditData.name,
                            subredditData.display_name,
                            subredditData.url,
                            mUsername
                    );
                }
            } catch (IOException e) {
                Log.e(TAG, "An error occurred while attempting to login to reddit: ", e);
                return false;
            }
            finally {
                if (db != null)
                    db.close();
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
                // Send the user back to the main screen
                Intent intent = new Intent(getApplicationContext(), MainScreen.class);
                startActivity(intent);
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

