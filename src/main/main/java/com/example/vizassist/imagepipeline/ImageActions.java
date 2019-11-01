package com.example.vizassist.imagepipeline;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaSession2Service;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.content.Context;

import androidx.core.content.FileProvider;

/**
 * Actions to get images from either the device back camera or photo gallery
 */
public class ImageActions {
    public static String currentPhotoPath;

    /**
     * Create a image file
     */
    private static File createImageFile(Context context) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir,
                imageFileName +
                ".jpg"
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * Start the built-in back camera to capture a still image.
     * @param activity origin activity in which the intent will be from.
     * @param requestCode request code to get result when the camera activity is dismissed.
     */
    public static void startCameraActivity(Activity activity, int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(activity.getApplicationContext().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile(activity);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(activity.getApplicationContext(),
                        "com.example.vizassist.fileprovider",
                        photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                activity.startActivityForResult(intent, requestCode);
            }
        }
    }

    /**
     * Start photo gallery image picker to select a saved image.
     * @param activity origin activity in which the intent will be from.
     * @param requestCode request code to get result when the gallery activity is dismissed.
     */
    public static void startGalleryActivity(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent, requestCode);
    }
}
