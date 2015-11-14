package com.mikedaguillo.redditunderground2.utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * A general application level manager
 */
public final class ApplicationManager {

    public static void CreateAndShowAlertDialog(Activity callingActivity, String title, String message, String positiveButtonText, String negativeButtonText, final DialogCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(callingActivity);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                callback.execute();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public interface DialogCallback {
        void execute();
    }

    public static void HideKeyboard(Activity activity, View targetView)
    {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(targetView.getApplicationWindowToken(), 0);
    }
}
