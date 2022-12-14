package com.fms.fusion.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.util.Size;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int TF_OD_API_INPUT_SIZE = 300;
    private static final boolean TF_OD_API_IS_QUANTIZED = true;
    private static final String TF_OD_API_MODEL_FILE = "detect.tflite";
    private static final String TF_OD_API_LABELS_FILE = "labelmap.txt";
    private static final DetectorMode MODE = DetectorMode.TF_OD_API;
    // Minimum detection confidence to track a detection.
    private static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.5f;
    psf



    private Detector detector;



    private Bitmap croppedBitmap = null;


    private boolean hasPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(Manifest.
                    permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }
    public static Bitmap getBitmapFromAssetsFolder(Context context, String fileName) {
        Bitmap bitmap = null;
        try
        {
            InputStream istr=context.getAssets().open(fileName);
            bitmap= BitmapFactory.decodeStream(istr);
        }
        catch (IOException e)
        {
            System.out.println("Error: " + e);
            System.exit(0);
        }
        return bitmap;
    }
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(Manifest.
                    permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(
                        MainActivity.this,
                        "Camera permission is required for this demo",
                        Toast.LENGTH_LONG)
                        .show();

                requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }}

    private enum DetectorMode {
        TF_OD_API;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!hasPermission())
            requestPermission();
        Bitmap bitmap = getBitmapFromAssetsFolder(this,"person.png");
        try {
            detector =
                    TFLiteObjectDetectionAPIModel.create(
                            this,
                            TF_OD_API_MODEL_FILE,
                            TF_OD_API_LABELS_FILE,
                            TF_OD_API_INPUT_SIZE,
                            TF_OD_API_IS_QUANTIZED);
        } catch (IOException e) {
            e.printStackTrace();
        }
        bitmap= Bitmap.createScaledBitmap(
                bitmap, 300, 300, false);
        final List<Detector.Recognition> results = detector.recognizeImage(bitmap);
        float minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
        switch (MODE) {
            case TF_OD_API:
                minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
                break;
        }
        final List<Detector.Recognition> mappedRecognitions =
                new ArrayList<Detector.Recognition>();
        for (final Detector.Recognition result : results) {
            final RectF location = result.getLocation();
                if (location != null && result.getConfidence() >= minimumConfidence) {
                    result.setLocation(location);
                    mappedRecognitions.add(result);
                    Log.e(TAG, "onCreate: "+result.getTitle() );
                }
        }


    }

    private static final String TAG = "MainActivity";
}