package com.example.vizassist;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.vizassist.imagepipeline.ImageActions;
import com.example.vizassist.utilities.HttpUtilities;

import org.json.JSONException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.sql.ClientInfoStatus;
import java.sql.Struct;
import java.sql.Time;

import org.apache.http.client.ClientProtocolException;


public class MainActivity extends AppCompatActivity {

    private static final String UPLOAD_HTTP_URL = ""; // removed for security reasons

    private static final int IMAGE_CAPTURE_CODE = 1;
    private static final int SELECT_IMAGE_CODE = 2;


    private static final int CAMERA_PERMISSION_REQUEST = 1001;

    private MainActivityUIController mainActivityUIController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivityUIController = new MainActivityUIController(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivityUIController.resume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_capture: {
                mainActivityUIController.updateResultView(getString(R.string.result_placeholder));
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    mainActivityUIController.askForPermission(
                            Manifest.permission.CAMERA, CAMERA_PERMISSION_REQUEST);
                } else {
                    ImageActions.startCameraActivity(this, IMAGE_CAPTURE_CODE);
                }
                break;
            }
            case R.id.action_gallery: {
                mainActivityUIController.updateResultView(getString(R.string.result_placeholder));
                ImageActions.startGalleryActivity(this, SELECT_IMAGE_CODE);
                break;
            }
            default: {
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // for extensibility switch is used here
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    ImageActions.startCameraActivity(this, IMAGE_CAPTURE_CODE);
                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Bitmap bitmap = null;
            if (requestCode == IMAGE_CAPTURE_CODE) {
                bitmap = (Bitmap) data.getExtras().get("data");
                mainActivityUIController.updateImageViewWithBitmap(bitmap);
            } else if (requestCode == SELECT_IMAGE_CODE) {
                Uri selectedImage = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),
                            selectedImage);
                    mainActivityUIController.updateImageViewWithBitmap(bitmap);
                } catch (IOException e) {
                    mainActivityUIController.showErrorDialogWithMessage(
                            R.string.reading_error_message);
                }
            }
            if (bitmap != null) {
                final Bitmap bitMapToUpload = bitmap;
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        uploadImage(bitMapToUpload);
                    }
                });
                thread.start();
            }
        }
    }

    private void uploadImage(Bitmap bitmap) {
        try {
            HttpURLConnection conn = HttpUtilities.makeHttpPostConnectionToUploadImage(bitmap,
                    UPLOAD_HTTP_URL);
            conn.connect();
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                mainActivityUIController.updateResultView(HttpUtilities.parseOCRResponse(conn));
            } else {
                mainActivityUIController.showInternetError();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            mainActivityUIController.showInternetError();
        } catch (JSONException e) {
            e.printStackTrace();
            mainActivityUIController.showInternetError();
        }
    }
}
