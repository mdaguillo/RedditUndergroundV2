package com.mikedaguillo.redditunderground2.utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.File;

/**
 * A general application level manager
 */
public final class ApplicationManager {
    private final static String TAG = "ApplicationManager";

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

    public static Bitmap GetThumbnailFromFile(String filePath)
    {
        return BitmapFactory.decodeFile(filePath);
    }

    public static void SendUserToActivity(Context callingContext, Class destinationClass, String intentKey, String intentData)
    {
        Intent intent = new Intent(callingContext, destinationClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (intentData != null)
            intent.putExtra(intentKey, intentData);
        callingContext.startActivity(intent);
    }

    /**
     * Returns the directory where thumbnail images are stored. If the directory does not exist yet, it creates it.
     */
    public static File GetThumbnailStorageDirectory(Context context)
    {
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "thumbnails");
        if (!file.exists() && !file.mkdirs())
            Log.e(TAG, "Failed to create the thumbnail directory");

        return file;
    }

    /**
     * Returns the directory where the fully downloaded images are stored. If the directory does not yet exist, it creates it.
     */
    public static File GetImageStorageDirectory(Context context)
    {
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "images");
        if (!file.exists() && !file.mkdirs())
            Log.e(TAG, "Failed to create the images directory");

        return file;
    }
}
